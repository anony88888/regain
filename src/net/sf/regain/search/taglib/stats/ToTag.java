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
package net.sf.regain.search.taglib.stats;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspWriter;

import net.sf.regain.RegainException;
import net.sf.regain.search.ExtendedJspException;
import net.sf.regain.search.SearchContext;
import net.sf.regain.search.SearchToolkit;
import net.sf.regain.search.taglib.AbstractSimpleTag;


/**
 * Generiert die Nummer des letzen Treffers, der auf dieser Seite gezeigt wird.
 *
 * @author Til Schneider, www.murfman.de
 */
public class ToTag extends AbstractSimpleTag {

  /**
   * Generiert den Tag.
   *
   * @param out Der JspWriter auf den der Taginhalt geschrieben werden soll.
   */
  public void printEndTag(JspWriter out)
    throws IOException, ExtendedJspException
  {
    ServletRequest request = pageContext.getRequest();

    SearchContext search;
    try {
      search = SearchToolkit.getSearchContextFromPageContext(pageContext);
    } catch (RegainException exc) {
      throw new ExtendedJspException("Error creating search context", exc);
    }

    int fromResult = SearchToolkit.getIntParameter(request, PARAM_FROM_RESULT, 0);
    int maxResults = SearchToolkit.getIntParameter(request, PARAM_MAX_RESULTS, 25);

    int toResult = fromResult + maxResults - 1;
    if (toResult >= search.getHitCount()) {
      toResult = search.getHitCount() - 1;
    }

    out.print(toResult + 1);
  }

}
