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
package net.sf.regain.util.sharedtag.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import net.sf.regain.RegainToolkit;

import simple.http.Request;
import simple.http.Response;
import simple.http.load.BasicService;
import simple.http.serve.Context;

/**
 * A simpleweb Service providing JSP pages and normal files.
 *
 * @see simple.http.load.Service
 * @author Til Schneider, www.murfman.de
 */
public class SharedTagService extends BasicService {
  
  /** The base directory where the provided files are located. */
  private File mBaseDir;
  
  /** The parser to use for parsing JSP pages */
  private ExecuterParser mParser;
  
  
  /**
   * Creates a new instance of SharedTagService.
   * 
   * @param context The context of this service.
   */
  public SharedTagService(Context context) {
    super(context);
    
    mParser = new ExecuterParser();
  }

  
  /**
   * Processes a request.
   * 
   * @param req The request.
   * @param resp The response.
   * @throws Exception If executing the JSP page failed.
   */
  public void process(Request req, Response resp) throws Exception {
    // Check whether this request comes from localhost
    boolean localhost = req.getInetAddress().isLoopbackAddress();
    if (! localhost) {
      // This request does not come from localhost -> Send 403 Forbidden
      handle(req, resp, 403);
    }
    
    String fileName = context.getRequestPath(req.getURI());
    
    if (mBaseDir == null) {
      mBaseDir = new File(context.getBasePath());
    }
    
    File file = new File(mBaseDir, fileName);
    if (file.exists()) {
      if (file.isDirectory()) {
        processDirectory(req, resp, file);
      }
      else if (file.getName().endsWith(".jsp")) {
        String jspCode = RegainToolkit.readStringFromFile(file);
        Executer root = mParser.parse(jspCode);
        SharedTagResource resource = new SharedTagResource(context, root);
        resource.handle(req, resp);
      }
      else {
        processFile(req, resp, file);
      }
    } else {
      handle(req, resp, 404);
    }
  }


  /**
   * Processes a directory listing request.
   * 
   * @param req The request.
   * @param resp The response.
   * @param dir The directory to list.
   * @throws Exception If executing the JSP page failed.
   */
  private void processDirectory(Request req, Response resp, File dir)
    throws Exception
  {
    resp.set("Content-Type", "text/html");
    
    PrintStream out = resp.getPrintStream();
    out.print("<html><head><title>" + dir.getName() + "</title></head><body>");
    String[] childArr = dir.list();
    for (int i = 0; i < childArr.length; i++) {
      out.print("<a href=\"" + childArr[i] + "\">" + childArr[i] + "</a><br>");
    }
    out.print("</body></html>");
    out.close();
  }

  
  /**
   * Processes a file request.
   * 
   * @param req The request.
   * @param resp The response.
   * @param file The to send.
   * @throws Exception If executing the JSP page failed.
   */
  private void processFile(Request req, Response resp, File file)
    throws Exception
  {
    OutputStream out = null;
    FileInputStream in = null;
    try {
      out = resp.getOutputStream();
      in = new FileInputStream(file);
      RegainToolkit.pipe(in, out);
      in.close();
      out.close();
    }
    finally {
      if (in != null) {
        try { in.close(); } catch (IOException exc) {}
      }
      if (out != null) {
        try { out.close(); } catch (IOException exc) {}
      }
    }
  }
  
}
