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
package net.sf.regain.search.sharedlib;

import java.net.URLEncoder;

import net.sf.regain.RegainException;
import net.sf.regain.RegainToolkit;
import net.sf.regain.search.SearchConstants;
import net.sf.regain.search.SearchContext;
import net.sf.regain.search.SearchToolkit;
import net.sf.regain.util.sharedtag.PageRequest;
import net.sf.regain.util.sharedtag.PageWriter;
import net.sf.regain.util.sharedtag.SharedTag;

/**
 * This Tag creates hyperlinks to navigate through the search result pages.
 * <p>
 * Tag Parameters:
 * <ul>
 * <li><code>msgBack</code>: The message to use for labeling the back link.</li>
 * <li><code>msgForward</code>: The message to use for labeling the forward link.</li>
 * <li><code>targetPage</code>: The URL of the page where the links should point to.</li>
 * <li><code>class</code>: The style sheet class to use for the link tags.</li>
 * </ul>
 *
 * @author Til Schneider, www.murfman.de
 */
public class NavigationTag extends SharedTag implements SearchConstants {

  /** The maximum number of links to create. */
  private static final int MAX_BUTTONS = 15;

  /** Die Default-Seite, auf die die Weiter-Links zeigen sollen. */
  private static final String DEFAULT_TARGET_PAGE = "SearchOutput.jsp";


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
    String query = request.getParameter("query");
    if (query == null) {
      // Nothing to do
      return;
    }
    
    SearchContext search = SearchToolkit.getSearchContext(request);

    int fromResult = request.getParameterAsInt(PARAM_FROM_RESULT, 0);
    int maxResults = request.getParameterAsInt(PARAM_MAX_RESULTS, 25);
    int totalResults = search.getHitCount();

    int buttonCount = (int) Math.ceil((double) totalResults / (double) maxResults);
    int currButton = fromResult / maxResults;

    // The first and the last button to show
    int fromButton = 0;
    int toButton = buttonCount - 1;

    if (buttonCount > MAX_BUTTONS) {
      if (currButton < (MAX_BUTTONS / 2)) {
        // The button range starts at the first button (---X------.....)
        toButton = fromButton + MAX_BUTTONS - 1;
      }
      else if (currButton > (buttonCount - ((MAX_BUTTONS + 1) / 2))) {
        // The button range ends at the last button (.....-------X--)
        fromButton = toButton - MAX_BUTTONS + 1;
      }
      else {
        // The button range is somewhere in the middle (...----X-----..)
        toButton = currButton + (MAX_BUTTONS / 2);
        fromButton = toButton - MAX_BUTTONS + 1;
      }
    }

    String indexName = search.getIndexName();
    if (currButton > 0) {
      String msgBack = getParameter("msgBack", true);
      msgBack = RegainToolkit.replace(msgBack, "&quot;", "\"");
      printLink(out, currButton - 1, query, maxResults, indexName, msgBack);
    }
    for (int i = fromButton; i <= toButton; i++) {
      if (i == currButton) {
        // This is the current button
        out.print("<b>" + (i + 1) + "</b> ");
      } else {
        String linkText = Integer.toString(i + 1);
        printLink(out, i, query, maxResults, indexName, linkText);
      }
    }
    if (currButton < (buttonCount -1)) {
      String msgForward = getParameter("msgForward", true);
      msgForward = RegainToolkit.replace(msgForward, "'", "\"");
      printLink(out, currButton + 1, query, maxResults, indexName, msgForward);
    }
  }


  /**
   * Prints the HTML for a hyperlink.
   *
   * @param out The writer to print to.
   * @param button The index of the button to create the HTML for.
   * @param query The search query.
   * @param maxResults The maximum results.
   * @param indexName The name of the search index.
   * @param linkText The link text.
   * @throws RegainException If printing failed.
   */
  private void printLink(PageWriter out, int button, String query,
    int maxResults, String indexName, String linkText)
    throws RegainException
  {
    String targetPage = getParameter("targetPage",  DEFAULT_TARGET_PAGE);

    // Für Java 1.2.2
    String encodedQuery = URLEncoder.encode(query);
    String encodedIndexName = URLEncoder.encode(indexName);

    // Ab Java 1.3
    /*
    String encoding = pageContext.getResponse().getCharacterEncoding();
    String encodedQuery = URLEncoder.encode(query, encoding);
    String encodedIndexName = URLEncoder.encode(indexName, encoding);
    */

    out.print("<a href=\"" + targetPage + "?query=" + encodedQuery
      + "&index=" + encodedIndexName + "&maxresults=" + maxResults
      + "&fromresult=" + (button * maxResults) + "\"");
    String styleSheetClass = getParameter("class");
    if (styleSheetClass != null) {
      out.print(" class=\"" + styleSheetClass + "\"");
    }
    out.print(">" + linkText + "</a> ");
  }

}
