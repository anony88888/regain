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

import net.sf.regain.RegainException;
import net.sf.regain.crawler.document.RawDocument;

import org.apache.log4j.Category;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;


/**
 * Extrahiert mit Hilfe von Regul�ren Ausdr�cken ein Fragment eines Dokuments.
 * <p>
 * Mit Hilfe eines URL-Pr�fixes wird bestimmt, ob dieser Extrahierer ein
 * konkretes Dokument bearbeiten kann oder nicht.
 *
 * @author Til Schneider, www.murfman.de
 */
public class AbstractExtractor {

  /** Die Kategorie, die zum Loggen genutzt werden soll. */
  private static Category mCat = Category.getInstance(AbstractExtractor.class);

  /**
   * Der Pr�fix, den eine URL haben muss, um von diesem Extrahierer bearbeitet
   * zu werden.
   */
  private String mPrefix;

  /**
   * Der compilierte Regul�re Ausdruck, der die Stelle findet, wo das zu
   * extrahierende Fragment eines Dokuments beginnt.
   * <p>
   * Ist <code>null</code>, wenn der gesamte Anfang des Dokuments extrahiert
   * werden soll.
   */
  private RE mFragmentStartRE;

  /**
   * Der Regul�re Ausdruck, der die Stelle findet, wo das zu extrahierende
   * Fragment eines Dokuments beginnt.
   * <p>
   * Ist <code>null</code>, wenn der gesamte Anfang des Dokuments extrahiert
   * werden soll.
   */
  private String mFragmentStartRegex;

  /**
   * Der compilierte Regul�re Ausdruck, der die Stelle findet, wo das zu
   * extrahierende Fragment eines Dokuments endet.
   * <p>
   * Ist <code>null</code>, wenn das gesamte Ende des Dokuments extrahiert
   * werden soll.
   */
  private RE mFragmentEndRE;

  /**
   * Der Regul�re Ausdruck, der die Stelle findet, wo das zu extrahierende
   * Fragment eines Dokuments endet.
   * <p>
   * Ist <code>null</code>, wenn das gesamte Ende des Dokuments extrahiert
   * werden soll.
   */
  private String mFragmentEndRegex;



  /**
   * Erzeugt eine neue AbstractExtractor-Instanz.
   *
   * @param prefix Der Pr�fix den eine URL haben muss, damit das zugeh�rige
   *        Dokument von diesem HtmlContentExtractor bearbeitet wird.
   * @param fragmentStartRegex Der Regul�re Ausdruck, der die Stelle findet, wo
   *        das zu extrahierende Fragment eines Dokuments beginnt.
   *        <p>
   *        Ist <code>null</code> oder Leerstring, wenn der gesamte Anfang des
   *        Dokuments extrahiert werden soll.
   * @param fragmentEndRegex Der Regul�re Ausdruck, der die Stelle findet, wo
   *        das zu extrahierende Fragment eines Dokuments endet.
   *        <p>
   *        Ist <code>null</code> oder Leerstring, wenn das gesamte Ende des
   *        Dokuments extrahiert werden soll.
   * @throws RegainException Wenn ein Regul�rer Ausdruck einen Syntaxfehler
   *         enth�lt.
   */
  public AbstractExtractor(String prefix, String fragmentStartRegex,
    String fragmentEndRegex)
    throws RegainException
  {
    mPrefix = prefix;

    try {
      if ((fragmentStartRegex != null) && (fragmentStartRegex.length() != 0)) {
        mFragmentStartRE = new RE(fragmentStartRegex, RE.MATCH_CASEINDEPENDENT);
        mFragmentStartRegex = fragmentStartRegex;
      }
      if ((fragmentEndRegex != null) && (fragmentEndRegex.length() != 0)) {
        mFragmentEndRE = new RE(fragmentEndRegex, RE.MATCH_CASEINDEPENDENT);
        mFragmentEndRegex = fragmentEndRegex;
      }
    }
    catch (RESyntaxException exc) {
      throw new RegainException("Syntax error in regular expression", exc);
    }
  }



  /**
   * Gibt zur�ck, ob der Extrahierer das gegebene Dokument bearbeiten kann.
   * <p>
   * Dies ist der Fall, wenn die URL mit dem Pr�fix dieses Extrahierer beginnt.
   *
   * @param rawDocument Das zu pr�fenden Dokuments.
   *
   * @return Ob der Extrahierer das gegebene Dokument bearbeiten kann.
   */
  public boolean accepts(RawDocument rawDocument) {
    return rawDocument.getUrl().startsWith(mPrefix);
  }



  /**
   * Extrahiert das Fragment aus dem gegebenen Dokument.
   *
   * @param rawDocument Das Dokument, aus dem das Fragment extrahiert werden
   *        soll.
   * @return Das Fragment
   * @throws RegainException Wenn das Dokument nicht gelesen werden konnte.
   */
  protected String extractFragment(RawDocument rawDocument)
    throws RegainException
  {
    String content = rawDocument.getContentAsString();

    // Find the fragment start
    int fragmentStart = 0;
    if (mFragmentStartRE != null) {
      if (mFragmentStartRE.match(content)) {
        fragmentStart = mFragmentStartRE.getParenEnd(0);
      } else {
        mCat.warn("The regular expression '" + mFragmentStartRegex + "' had no "
          + "match for '" + rawDocument.getUrl() + "'");
      }
    }

    // Find the fragment end
    int fragmentEnd = content.length();
    if (mFragmentEndRE != null) {
      if (mFragmentEndRE.match(content, fragmentStart)) {
        fragmentEnd = mFragmentEndRE.getParenStart(0);
      } else {
        mCat.warn("The regular expression '" + mFragmentEndRegex + "' had no "
          + "match for '" + rawDocument.getUrl() + "'");
      }
    }

    if ((fragmentStart == 0) && (fragmentEnd == content.length())) {
      // Nothing to do -> So don't waste ressources
      return content;
    } else {
      return content.substring(fragmentStart, fragmentEnd);
    }
  }



}
