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
package net.sf.regain.util.sharedtag.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import net.sf.regain.RegainException;
import net.sf.regain.util.sharedtag.PageWriter;

/**
 * Adapter from a JspWriter to a SharedTag PageWriter.
 *
 * @author Til Schneider, www.murfman.de
 */
public class JspPageWriter implements PageWriter {

  /** The JspWriter to adapt. */
  private JspWriter mJspWriter;


  /**
   * Creates a new instance of JspPageWriter.
   * 
   * @param writer The JspWriter to adapt.
   */
  public JspPageWriter (JspWriter writer) {
    mJspWriter = writer;
  }


  /**
   * Prints text to a page.
   * 
   * @param text The text to print.
   * @throws RegainException If printing failed.
   */
  public void print(String text) throws RegainException {
    try {
      mJspWriter.print(text);
    }
    catch (IOException exc) {
      throw new RegainException("Writing results failed", exc);
    }
  }

}
