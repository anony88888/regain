/*
 * regain - A file search engine providing plenty of formats
 * Copyright (C) 2004-2008 Til Schneider, Thomas Tesche
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
 * Contact: Til Schneider, info@murfman.de, Thomas Tesche, regain@thtesche.com
 *
 * CVS information:
 *  $RCSfile$
 *   $Source$
 *     $Date: 2006-09-11 20:12:53 +0200 (Mon, 11 Sep 2006) $
 *   $Author: til132 $
 * $Revision: 234 $
 */
package net.sf.regain.search.sharedlib.hit;

import net.sf.regain.RegainException;
import net.sf.regain.util.sharedtag.PageRequest;
import net.sf.regain.util.sharedtag.PageResponse;

import org.apache.lucene.document.Document;

/**
 * Generates the index number on of the current hit.
 *
 * @author Thomas Tesche, www.thtesche.de
 */
public class ContentTag extends AbstractHitTag {

  /**
   * Generates the tag.
   *
   * @param request The page request.
   * @param response The page response.
   * @param hit The current search hit.
   * @param hitIndex The index of the hit.
   * @throws RegainException If there was an exception.
   */
  protected void printEndTag(PageRequest request, PageResponse response,
    Document hit, int hitIndex)
    throws RegainException {

    String content = null;
    content = hit.get("content");
    if (content != null) {
      String hitNumber = Integer.toString(hitIndex + 1);
      response.print("<input type=\"button\" class=\"button\" onclick=\"return toggleMe('hit_" +
        hitNumber + "')\" value=\"Treffer " + hitNumber + " komplett anzeigen\">");
      response.print("<div id=\"hit_" + hitNumber + "\" style=\"display:none\">" +
        "<textarea name=\"area_" + hitNumber + "\" " +
        "cols=\"150\" rows =\"13\" readonly =\"readonly\">");
      response.printNoHtml(content);
      response.print("</textarea></div><br/>");
    }
  }
}
