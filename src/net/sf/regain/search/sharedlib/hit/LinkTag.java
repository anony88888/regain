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
package net.sf.regain.search.sharedlib.hit;

import net.sf.regain.RegainException;
import net.sf.regain.search.SearchContext;
import net.sf.regain.search.SearchToolkit;
import net.sf.regain.util.sharedtag.PageRequest;
import net.sf.regain.util.sharedtag.PageWriter;

import org.apache.lucene.document.Document;

/**
 * Generates a link to the current hit's document. For the link text the title
 * is used.
 * <p>
 * Tag Parameters:
 * <ul>
 * <li><code>class</code>: The style sheet class to use for the link.</li>
 * </ul>
 *
 * @author Til Schneider, www.murfman.de
 */
public class LinkTag extends AbstractHitTag {

  /**
   * Generates the tag.
   *
   * @param out The writer where to write the code.
   * @param request The page request.
   * @param hit The current search hit.
   * @throws RegainException If there was an exception.
   */
  protected void printEndTag(PageWriter out, PageRequest request,
    Document hit)
    throws RegainException
  {
    // Get the search context
    SearchContext search = SearchToolkit.getSearchContext(request);

    String url   = search.rewriteUrl(hit.get("url"));
    String title = hit.get("title").trim();
    boolean openInNewWindow = search.getOpenUrlInNewWindow(url);

    // Rewrite the title
    // NOTE: Sometimes the title is an URL. To normal titles this will have no
    //       effect, because they don't start with an URL
    title = search.rewriteUrl(title);
    
    // Use the URL as title if there is no title.
    if (title.length() == 0) {
      title = url;
    }

    // Generate the link
    out.print("<a href=\"" + url + "\"");
    if (openInNewWindow) {
      out.print(" target=\"_blank\"");
    }
    String styleSheetClass = getParameter("class");
    if (styleSheetClass != null) {
      out.print(" class=\"" + styleSheetClass + "\"");
    }
    out.print(">" + title + "</a>");
  }

}
