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
import java.net.ServerSocket;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import net.sf.regain.util.sharedtag.simple.ExecuterParser;
import net.sf.regain.util.sharedtag.simple.SharedTagService;
import net.sf.regain.util.sharedtag.simple.SimplePageRequest;
import simple.http.ProtocolHandler;
import simple.http.connect.Connection;
import simple.http.connect.ConnectionFactory;
import simple.http.load.MapperEngine;
import simple.http.serve.FileContext;
import simple.http.serve.HandlerFactory;

/**
 * Starts the desktop search.
 *
 * @author Til Schneider, www.murfman.de
 */
public class Main {
  
  /** The logger for this class */
  private static Logger mLog = Logger.getLogger(Main.class);

  
  /**
   * The main entry point.
   * 
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    // Initialize Logging
    new File("log").mkdir();
    File logConfigFile = new File("conf/log4j.properties");
    if (! logConfigFile.exists()) {
      System.out.println("ERROR: Logging configuration file not found: "
        + logConfigFile.getAbsolutePath());
      return; // Abort
    }

    PropertyConfigurator.configure(logConfigFile.getAbsolutePath());
    mLog.info("Logging initialized");

    // Initialize the search mask
    SimplePageRequest.setInitParameter("searchConfigFile", "conf/SearchConfiguration.xml");
    ExecuterParser.registerNamespace("search", "net.sf.regain.search.sharedlib");
    ExecuterParser.registerNamespace("config", "net.sf.regain.ui.desktop.config.sharedlib");
    
    // Start the index update manager
    IndexUpdateManager.getInstance().init();
    
    // Start the server
    try {
      FileContext context = new FileContext(new File("web"));
      
      MapperEngine engine = new MapperEngine(context);
      
      engine.load("SharedTagService", SharedTagService.class.getName());
      engine.link("*", "SharedTagService");
      
      ProtocolHandler handler = HandlerFactory.getInstance(engine);
      
      Connection connection = ConnectionFactory.getConnection(handler);

      mLog.info("Listening on port 88...");
      connection.connect(new ServerSocket(88));
    }
    catch (Exception exc) {
      exc.printStackTrace();
      System.exit(1);
    }
  }
  
}
