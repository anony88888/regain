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

import org.apache.log4j.Logger;

import net.sf.regain.ui.desktop.config.DesktopConfig;
import net.sf.regain.ui.desktop.config.XmlDesktopConfig;
import net.sf.regain.util.ui.BrowserLauncher;

/**
 * A toolkit for the desktop search containing helper methods.
 *
 * @author Til Schneider, www.murfman.de
 */
public class DesktopToolkit implements DesktopConstants {

  /** The logger for this class */
  private static Logger mLog = Logger.getLogger(DesktopToolkit.class);
  
  /** The desktop configuration. */
  private static DesktopConfig mConfig;


  /**
   * Gets the desktop configuration.
   * 
   * @return The desktop configuration.
   */
  public static DesktopConfig getDesktopConfig() {
    if (mConfig == null) {
      File xmlFile = new File("conf/DesktopConfiguration.xml");
      mConfig = new XmlDesktopConfig(xmlFile);
    }
    return mConfig;
  }


  /**
   * Opens a page in the browser.
   * 
   * @param page The page to open.
   */
  public static void openPageInBrowser(String page) {
    String url = "http://localhost:" + DEFAULT_PORT + "/" + page;
    try {
      BrowserLauncher.openURL(url);
    }
    catch (Exception exc) {
      mLog.error("Opening browser failed", exc);
    }
  }
  
}
