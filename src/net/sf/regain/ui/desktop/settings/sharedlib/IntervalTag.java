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
package net.sf.regain.ui.desktop.settings.sharedlib;

import net.sf.regain.RegainException;
import net.sf.regain.util.sharedtag.PageRequest;
import net.sf.regain.util.sharedtag.PageWriter;
import net.sf.regain.util.sharedtag.SharedTag;

/**
 * Generates a combo box with the index update interval.
 *
 * @author Til Schneider, www.murfman.de
 */
public class IntervalTag extends SharedTag {

  /** The possible choices. */
  private final String[][] CHOICES = {
    { "60",    "Eine Stunde" },
    { "1440",  "Ein Tag" },
    { "10080", "Eine Woche" },
  };


  /**
   * Called when the parser reaches the end tag.
   *  
   * @param out The writer where to write the code.
   * @param request The page request.
   * @throws RegainException If there was an exception.
   */
  public void printEndTag(PageWriter out, PageRequest request)
    throws RegainException
  {
    String currValue = (String) request.getContextAttribute("settings.interval");
    
    out.print("<select name=\"interval\">");
    for (int i = 0; i < CHOICES.length; i++) {
      String value = CHOICES[i][0];
      String name  = CHOICES[i][1];
      
      out.print("<option value=\"" + value + "\"");
      if (value.equals(currValue)) {
        out.print(" selected=\"selected\"");
      }
      out.print(">" + name + "</option>");
    }
    out.print("</select>");
  }
  
}
