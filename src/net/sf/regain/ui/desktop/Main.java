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

  /**
   * The main entry point.
   * 
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    SimplePageRequest.setInitParameter("configFile", "conf/SearchConfiguration.xml");

    try {
      FileContext context = new FileContext(new File("web"));
      
      MapperEngine engine = new MapperEngine(context);
      
      engine.load("SharedTagService", SharedTagService.class.getName());
      engine.link("*", "SharedTagService");
      
      ProtocolHandler handler = HandlerFactory.getInstance(engine);
      
      Connection connection = ConnectionFactory.getConnection(handler);

      System.out.println("Listening on port 88...");
      connection.connect(new ServerSocket(88));
    }
    catch (Exception exc) {
      exc.printStackTrace();
      System.exit(1);
    }
  }
  
}
