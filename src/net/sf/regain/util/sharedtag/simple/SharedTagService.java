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
import java.util.HashMap;

import net.sf.regain.RegainToolkit;
import net.sf.regain.ui.desktop.FileService;
import simple.http.Request;
import simple.http.Response;
import simple.http.load.BasicService;
import simple.http.serve.Context;
import simple.http.serve.Resource;

/**
 * A simpleweb Service providing JSP pages and normal files.
 *
 * @see simple.http.load.Service
 * @author Til Schneider, www.murfman.de
 */
public class SharedTagService extends BasicService {
  
  /** Holds for an extension the mime type. */
  private static HashMap mMimeTypeHash;
  
  /** The base directory where the provided files are located. */
  private File mBaseDir;
  
  /** The parser to use for parsing JSP pages */
  private ExecuterParser mParser;
  
  /** The service to pass file requests to. */
  private FileService mFileService;
  
  
  /**
   * Creates a new instance of SharedTagService.
   * 
   * @param context The context of this service.
   */
  public SharedTagService(Context context) {
    super(context);
    
    mParser = new ExecuterParser();
    
    // TODO: Find out, how simpleweb calls another service 
    mFileService = new FileService(context);
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
    
    if (fileName.startsWith("/file/")) {
      // TODO: Just a hack. Normally should there be a way simpleweb calls this
      //       service.
      mFileService.process(req, resp);
      return;
    }
    
    if (mBaseDir == null) {
      mBaseDir = new File(context.getBasePath());
    }
    
    File file = new File(mBaseDir, fileName);
    if (file.exists()) {
      if (file.isDirectory()) {
        processDirectory(req, resp, file);
      }
      else if (file.getName().endsWith(".jsp")) {
        Executer root = mParser.parse(mBaseDir, fileName);
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
    processFile(this, req, resp, file);
  }
  
  
  /**
   * Processes a file request.
   * 
   * @param statusCodeHandler The resource to use for handling a status code.
   * @param req The request.
   * @param resp The response.
   * @param file The to send.
   * @throws Exception If executing the JSP page failed.
   */
  public static void processFile(Resource statusCodeHandler, Request req,
    Response resp, File file)
    throws Exception
  {
    long lastModified = file.lastModified();
    if (lastModified < req.getDate("If-Modified-Since")) {
      // The browser can use the cached file
      statusCodeHandler.handle(req, resp, 304);
    } else {
      resp.setDate("Date", System.currentTimeMillis());
      resp.setDate("Last-Modified", lastModified);
    
      // TODO: Make this configurable
      if (mMimeTypeHash == null) {
        // Source: http://de.selfhtml.org/diverses/mimetypen.htm
        mMimeTypeHash = new HashMap();
        mMimeTypeHash.put("html", "text/html");
        mMimeTypeHash.put("htm", "text/html");
        mMimeTypeHash.put("txt", "text/plain");
        mMimeTypeHash.put("pdf", "application/pdf");
        mMimeTypeHash.put("xls", "application/msexcel");
        mMimeTypeHash.put("doc", "application/msword");
        mMimeTypeHash.put("ppt", "application/mspowerpoint");
        mMimeTypeHash.put("rtf", "text/rtf");
      }
      
      // Set the MIME type
      String filename = file.getName();
      int lastDot = filename.lastIndexOf('.');
      if (lastDot != -1) {
        String extension = filename.substring(lastDot + 1);
        String mimeType = (String) mMimeTypeHash.get(extension);
        if (mimeType != null) {
          resp.set("Content-Type", "mimeType/" + mimeType);
        }
      }
      
      // Send the file
      OutputStream out = null;
      FileInputStream in = null;
      try {
        out = resp.getOutputStream();
        in = new FileInputStream(file);
        RegainToolkit.pipe(in, out);
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

}
