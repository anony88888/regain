/*
 * regain - A file search engine providing plenty of formats
 * Copyright (C) 2004  Til Schneider
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Til Schneider, info@murfman.de
 *
 * CVS information:
 *  $RCSfile$
 *   $Source$
 *     $Date$
 *   $Author$
 * $Revision$
 */
package net.sf.regain.ui.desktop;

import java.io.File;
import java.util.Date;

import net.sf.regain.RegainException;
import net.sf.regain.RegainToolkit;
import net.sf.regain.crawler.Crawler;
import net.sf.regain.crawler.CrawlerToolkit;
import net.sf.regain.crawler.config.CrawlerConfig;
import net.sf.regain.crawler.config.XmlCrawlerConfig;

import org.apache.log4j.Logger;

/**
 *
 * @author Til Schneider, www.murfman.de
 */
public class IndexUpdateManager {
  
  /** The logger for this class */
  private static Logger mLog = Logger.getLogger(IndexUpdateManager.class);

  /** The name of the XML file that holds the crawler configuration. */
  private static final String CRAWLER_CONFIG_FILE = "conf/CrawlerConfiguration.xml";
  
  /** The name of the file that holds the timestamp of the last index update. */
  private static final String LAST_UPDATE_FILE = "searchindex/lastupdate";
  
  /** The singleton. */
  private static IndexUpdateManager mSingleton;
  
  /** The crawler. Is <code>null</code> if there is currently no index update running. */
  private Crawler mCrawler;
  
  
  /**
   * Gets the Singleton.
   * 
   * @return The Singleton.
   */
  public static IndexUpdateManager getInstance() {
    if (mSingleton == null) {
      mSingleton = new IndexUpdateManager();
    }
    return mSingleton;
  }
  
  
  /**
   * Initializes the IndexUpdateManager.
   */
  public void init() {
    Thread checkThread = new Thread() {
      public void run() {
        checkThreadRun();
      }
    };
    checkThread.setPriority(Thread.MIN_PRIORITY);
    checkThread.start();
  }
  
  
  /**
   * The run method of the thread that checks whether an index update is
   * nessesary.
   */
  private void checkThreadRun() {
    while (true) {
      try {
        checkUpdate();
      }
      catch (Throwable thr) {
        mLog.error("Updating index failed", thr);
      }
      
      try {
        Thread.sleep(10000);
      }
      catch (InterruptedException exc) {}
    }
  }


  /**
   * Executes an index update if nessesary.
   * 
   * @throws RegainException If updating the index failed.
   */
  private synchronized void checkUpdate() throws RegainException {
    long lastUpdate = getIndexLastUpdate();
    long interval = DesktopToolkit.getDesktopConfig().getInterval();
    long nextUpdateTime = lastUpdate + interval * 1000 * 60;
    if (System.currentTimeMillis() >= nextUpdateTime) {
      // The index must be updated
      File xmlFile = new File(CRAWLER_CONFIG_FILE);
      CrawlerConfig config = new XmlCrawlerConfig(xmlFile);
      
      // Check whether to show the welcome page
      if (config.getStartUrls().length == 0) {
        // There are no start URLs defined -> Show the welcome page
        mLog.info("There is nothing configured. Showing the welcome page.");
        DesktopToolkit.openPageInBrowser("welcome.jsp");
        
        // Show the welcome page again, when the next update period is finished
        saveIndexLastUpdate();
      } else {
        // Update the index

        // Proxy settings
        CrawlerToolkit.initProxy(config);
        
        // Create and run the crawler
        TrayIconManager.getInstance().setIndexUpdateRunning(true);
        try {
          mLog.info("Starting index update on " + new Date());
          mCrawler = new Crawler(config);
          mCrawler.run(true, null);
        }
        catch (RegainException exc) {
          mLog.error("Updating the index failed", exc);
        }
        finally {
          mCrawler = null;
  
          // Save the time when the index was last updated
          saveIndexLastUpdate();
          
          // Run the garbage collector
          System.gc();
  
          TrayIconManager.getInstance().setIndexUpdateRunning(false);
        }
      }
    }
  }
  
  
  /**
   * Gets the timestamp of the last index update.
   * 
   * @return The timestamp of the last index update.
   * @throws RegainException If getting the timestamp failed.
   */
  private long getIndexLastUpdate() throws RegainException {
    File lastUpdateFile = new File(LAST_UPDATE_FILE);
    if (lastUpdateFile.exists()) {
      String lastUpdate = RegainToolkit.readStringFromFile(lastUpdateFile);
      return RegainToolkit.stringToLastModified(lastUpdate).getTime();
    } else {
      // The lastupdate file does not exist -> There was never an index created
      return -1;
    }
  }

  
  /**
   * Saves the current time as the last index update.
   */
  private void saveIndexLastUpdate() {
    File lastUpdateFile = new File(LAST_UPDATE_FILE);
    try {
      String lastUpdate = RegainToolkit.lastModifiedToString(new Date());
      CrawlerToolkit.writeToFile(lastUpdate, lastUpdateFile);
    }
    catch (RegainException exc) {
      mLog.error("Writing last update file failed", exc);
    }
  }
  
}
