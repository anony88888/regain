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
package net.sf.regain.search.taglib.hit;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import net.sf.regain.RegainException;
import net.sf.regain.search.ExtendedJspException;
import net.sf.regain.search.SearchContext;
import net.sf.regain.search.SearchToolkit;

import org.apache.lucene.document.Document;


/**
 * Generiert einen Hyperlink auf das aktuelle Trefferdokument. Als Linktext wird
 * der Titel genutzt.
 *
 * @author Tilman Schneider, STZ-IDA an der FH Karlsruhe
 */
public class LinkTag extends AbstractHitTag {

  /** Die zu verwendende Stylesheet-Klasse. (Kann null sein) */
  private String mStyleSheetClass;


  /**
   * Setzt die zu verwendende Stylesheet-Klasse.
   *   
   * @param styleSheetClass Die zu verwendende Stylesheet-Klasse.
   */
  public void setClass(String styleSheetClass) {
    mStyleSheetClass = styleSheetClass;
  }



  /**
   * Generiert den Tag.
   *
   * @param out Der JspWriter auf den der Taginhalt geschrieben werden soll.
   * @param hit Der aktuelle Suchtreffer.
   * @throws IOException Wenn der Tag nicht geschrieben werden konnte.
   * @throws ExtendedJspException Wenn der Suchkontext nicht erstellt werden konnte.
   */
  protected void printEndTag(JspWriter out, Document hit)
    throws IOException, ExtendedJspException
  {
    SearchContext search;
    try {
      search = SearchToolkit.getSearchContextFromPageContext(pageContext);
    } catch (RegainException exc) {
      throw new ExtendedJspException("Error creating search context", exc);
    }

    String url   = hit.get("url");
    String title = hit.get("title").trim();
    boolean openInNewWindow = search.getOpenUrlInNewWindow(url);
    
    // URL nutzen, wenn kein Titel verf�gbar ist.
    if (title.length() == 0) {
      title = url;
    }
    
    out.print("<a href=\"" + url + "\"");
    if (openInNewWindow) {
      out.print(" target=\"_blank\"");
    }
    if (mStyleSheetClass != null) {
      out.print(" class=\"" + mStyleSheetClass + "\"");
    }
    out.print(">" + title + "</a>");
  }


  /**
   * Gibt die von diesem Tag genutzten Ressourcen wieder frei.
   */
  public void release() {
    super.release();

    mStyleSheetClass = null;
  }

}
