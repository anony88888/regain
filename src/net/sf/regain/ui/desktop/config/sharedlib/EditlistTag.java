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
package net.sf.regain.ui.desktop.config.sharedlib;

import net.sf.regain.RegainException;
import net.sf.regain.util.sharedtag.PageRequest;
import net.sf.regain.util.sharedtag.PageWriter;
import net.sf.regain.util.sharedtag.SharedTag;

/**
 * Generates a editable list.
 *
 * @author Til Schneider, www.murfman.de
 */
public class EditlistTag extends SharedTag {

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
    // Get the name of the edit list
    String name = getParameter("name", true);
    
    // Get the current value
    String[] currValueArr = (String[]) request.getContextAttribute("settings." + name);
    
    out.print("<select id=\"" + name + "-list\" name=\"" + name + "\" " +
        "size=\"5\" multiple");
    String styleSheetClass = getParameter("class");
    if (styleSheetClass != null) {
      out.print(" class=\"" + styleSheetClass + "\"");
    }
    out.print(">");
    for (int i = 0; i < currValueArr.length; i++) {
      out.print("<option>" + currValueArr[i] + "</option>");
    }
    out.print("</select><br/>");
    
    out.print("<input type=\"text\" size=\"20\" id=\"" + name + "-entry\"/>");
    out.print("<button type=\"button\" onClick=\"addToList('" + name + "')\">Hinzuf&uuml;gen</button>");
    out.print("<button type=\"button\" onClick=\"removeFromList('" + name + "')\">Entfernen</button>");
  }

}
