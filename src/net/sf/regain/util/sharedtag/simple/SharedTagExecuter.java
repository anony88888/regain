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

import net.sf.regain.RegainException;
import net.sf.regain.util.sharedtag.PageRequest;
import net.sf.regain.util.sharedtag.PageWriter;
import net.sf.regain.util.sharedtag.SharedTag;

/**
 * An executer node that executes a SharedTag.
 *
 * @author Til Schneider, www.murfman.de
 */
public class SharedTagExecuter extends Executer {
  
  /** The shared tag that is executed by this executer. */
  private SharedTag mTag;
  

  /**
   * Creates a new instance of SharedTagExecuter.
   * 
   * @param tag The shared tag that is executed by this executer.
   */
  public SharedTagExecuter(SharedTag tag) {
    mTag = tag;
  }


  /**
   * Executes this node.
   * 
   * @param out The writer where to write the response.
   * @param request The request.
   * @throws RegainException If executing failed.
   */
  public void execute(PageWriter out, PageRequest request)
    throws RegainException
  {
    int result = mTag.printStartTag(out, request);
    while (result == SharedTag.EVAL_TAG_BODY) {
      executeChildren(out, request);
      result = mTag.printAfterBody(out, request);
    }
    
    mTag.printEndTag(out, request);
  }
  

  /**
   * Prints this tag to System.out.
   * 
   * @param prefix The prefix to put in front of every line.
   */
  public void printTag(String prefix) {
    if (childCount() == 0) {
      System.out.println(prefix + "<" + mTag.getTagName() + "/>");
    } else {
      System.out.println(prefix + "<" + mTag.getTagName() + ">");
      printChildren(prefix);
      System.out.println(prefix + "</" + mTag.getTagName() + ">");
    }
  }
  
}
