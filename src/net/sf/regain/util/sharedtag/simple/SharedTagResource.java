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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.sf.regain.RegainException;
import net.sf.regain.util.sharedtag.PageRequest;
import net.sf.regain.util.sharedtag.PageWriter;
import simple.http.Request;
import simple.http.Response;
import simple.http.serve.BasicResource;
import simple.http.serve.Context;

/**
 * A simpleweb resource representing one JSP page.
 *
 * @see simple.http.serve.Resource
 * @author Til Schneider, www.murfman.de
 */
public class SharedTagResource extends BasicResource {
  
  /** The root executer holding the parsed JSP page. */
  private Executer mRootTagExecuter;
  
  
  /**
   * Creates a new instance of SharedTagResource.
   * 
   * @param context The context of this resource.
   * @param root The root executer holding the parsed JSP page.
   * @throws RegainException If parsing the JSP file failed.
   */
  public SharedTagResource(Context context, Executer root) throws RegainException {
    super(context);
    
    mRootTagExecuter = root;
  }


  /**
   * Processes a request.
   * 
   * @param req The request.
   * @param resp The response.
   * @throws Exception If executing the JSP page failed.
   */
  protected synchronized void process(Request req, Response resp) throws Exception {
    // Write the page to a buffer first
    // If an exception should be thrown the user gets a clear error message
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(stream);

    PageWriter out = new SimplePageWriter(printStream);
    PageRequest request = new SimplePageRequest(req);

    mRootTagExecuter.execute(out, request);
    
    printStream.close();
    stream.close();
    
    // The page has been generated without exception -> Send it to the user
    resp.set("Content-Type", "text/html");
    printStream = resp.getPrintStream();
    try {
      stream.writeTo(printStream);
    }
    finally {
      printStream.close();
    }
  }

}
