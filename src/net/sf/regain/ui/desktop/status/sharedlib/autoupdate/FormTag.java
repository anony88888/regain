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
package net.sf.regain.ui.desktop.status.sharedlib.autoupdate;

import net.sf.regain.RegainException;
import net.sf.regain.util.sharedtag.PageRequest;
import net.sf.regain.util.sharedtag.PageWriter;
import net.sf.regain.util.sharedtag.SharedTag;

/**
 *
 * @author Til Schneider, www.murfman.de
 */
public class FormTag extends SharedTag {

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
    String url = getParameter("url", true);
    int time = getParameterAsInt("time", 5);
    
    String autoupdate = request.getParameter("autoupdate");
    out.print("<form name=\"autoupdate\" action=\"" + url + "\" " +
        "style=\"display:inline;\" method=\"get\">");
    out.print("Diese Seite alle " + time + " Sekunden automatisch aktualisieren: ");
    if (autoupdate == null) {
      out.print("<input type=\"hidden\" name=\"autoupdate\" value=\"" + time + "\"/>");
      out.print("<input type=\"submit\" value=\"Einschalten\"/>");
    } else {
      out.print("<input type=\"submit\" value=\"Ausschalten\"/>");
    }
    out.print("</form>");
  }
  
}
