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
package net.sf.regain.crawler.preparator.html;

import java.util.ArrayList;

import net.sf.regain.RegainException;
import net.sf.regain.crawler.CrawlerToolkit;
import net.sf.regain.crawler.document.PathElement;
import net.sf.regain.crawler.document.RawDocument;
import net.sf.regain.crawler.preparator.HtmlPreparator;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;


/**
 * Extrahiert aus einem HTML-Dokument den Pfad, �ber den es zu erreichen ist.
 * 
 * @author Tilman Schneider, STZ-IDA an der FH Karlsruhe
 */
public class HtmlPathExtractor extends AbstractExtractor {

  /**
   * Der Regul�re Ausdruck, der ein Pfadelement findet.
   */  
  private RE mPathElementRE;
  
  /**
   * Die Gruppe, die die URL im Regul�re Ausdruck findet.
   */  
  private int mPathElementUrlGroup; 

  /**
   * Die Gruppe, die den Titel im Regul�re Ausdruck findet.
   */  
  private int mPathElementTitleGroup;



  /**
   * Erzeugt eine neue HtmlPathExtractor-Instanz.
   *
   * @param prefix Der Pr�fix den eine URL haben muss, damit das zugeh�rige
   *        Dokument von diesem HtmlPathExtractor bearbeitet wird.
   * @param pathStartRegex Der Regul�re Ausdruck, der die Stelle findet,
   *        wo die Pfadangabe beginnt.
   *        <p>
   *        Ist <code>null</code> oder Leerstring, wenn der Pfad am Anfang des
   *        HTML-Dokuments beginnt.
   * @param pathEndRegex Der Regul�re Ausdruck, der die Stelle findet,
   *        wo die Pfadangabe endet.
   *        <p>
   *        Ist <code>null</code> oder Leerstring, wenn der Pfad am Ende des
   *        HTML-Dokuments endet.
   * @param pathElementRegex Der Regul�re Ausdruck, der ein Pfadelement findet.
   * @param pathElementUrlGroup Die Gruppe, die die URL im Regul�re Ausdruck
   *        findet.
   * @param pathElementTitleGroup Die Gruppe, die den Titel im Regul�re Ausdruck
   *        findet.
   * @throws RegainException Wenn ein Regul�rer Ausdruck einen Syntaxfehler
   *         enth�lt.
   */
  public HtmlPathExtractor(String prefix, String pathStartRegex,
    String pathEndRegex, String pathElementRegex, int pathElementUrlGroup,
    int pathElementTitleGroup)
    throws RegainException
  {
    super(prefix, pathStartRegex, pathEndRegex);

    try {
      mPathElementRE = new RE(pathElementRegex, RE.MATCH_CASEINDEPENDENT);
    }
    catch (RESyntaxException exc) {
      throw new RegainException("Syntax error in regular expression", exc);
    }

    mPathElementUrlGroup = pathElementUrlGroup;
    mPathElementTitleGroup = pathElementTitleGroup;
  }



  /**
   * Extrahiert aus dem gegebenen HTML-Dokument den Pfad �ber den es zu
   * erreichen ist. 
   *
   * @param rawDocument Das Dokument, aus dem der Pfad extrahiert werden soll. 
   * @return Der Pfad �ber den das Dokument zu erreichen ist oder
   *         <code>null</code>, wenn kein Pfad gefunden wurde. 
   * @throws RegainException Wenn das Dokument nicht gelesen werden konnte.
   */
  public PathElement[] extractPath(RawDocument rawDocument)
    throws RegainException
  {
    String pathFragment = extractFragment(rawDocument);

    ArrayList list = new ArrayList();
    
    int offset = 0;
    while (mPathElementRE.match(pathFragment, offset)) {
      String url = mPathElementRE.getParen(mPathElementUrlGroup);
      url = CrawlerToolkit.toAbsoluteUrl(url, rawDocument.getUrl());
      String title = mPathElementRE.getParen(mPathElementTitleGroup);
      title = HtmlPreparator.replaceHtmlEntities(title);
      
      list.add(new PathElement(url, title));
      
      offset = mPathElementRE.getParenEnd(0);
    }
    
    if (list.isEmpty()) {
      return null;
    } else {
      PathElement[] asArr = new PathElement[list.size()];
      list.toArray(asArr);
  
      return asArr;
    }
  }

}
