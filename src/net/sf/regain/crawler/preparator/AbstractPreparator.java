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
package net.sf.regain.crawler.preparator;

import net.sf.regain.RegainException;
import net.sf.regain.crawler.document.PathElement;
import net.sf.regain.crawler.document.Preparator;
import net.sf.regain.crawler.document.RawDocument;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;


/**
 * Abstrakte Implementierung eines Pr�perators.
 * <p>
 * Implementiert die Getter-Methoden und �bernimmt das Aufr�umen zwischen zwei
 * Pr�parationen (Siehe {@link #cleanUp()}).
 * <p>
 * Kindklassen k�nnen die Werte �ber die gesch�tzten (protected) Setter-Methoden
 * setzen. 
 *
 * @author Tilman Schneider, STZ-IDA an der FH Karlsruhe
 */
public abstract class AbstractPreparator implements Preparator {

  /**
   * Der Regul�re Ausdruck, dem eine URL entsprechen muss, damit sie von
   * diesem Pr�perator bearbeitet wird.
   */
  private RE mUrlRegex;
  /** Der gefundene Titel. */
  private String mTitle;
  /** Der ges�uberte Inhalt. */
  private String mCleanedContent;
  /** Die Zusammenfassung des Dokuments. */
  private String mSummary;
  /** Die extrahierten �berschriften. Kann <code>null</code> sein */
  private String mHeadlines;
  /** Der Pfad, �ber den das Dokument zu erreichen ist. */
  private PathElement[] mPath;
  
  
  
  /**
   * Erzeugt eine neue Instanz.
   */
  public AbstractPreparator() {
  }



  /**
   * Setzt den Regul�ren Ausdruck, dem eine URL entsprechen muss, damit sie von
   * diesem Pr�perator bearbeitet wird.
   * 
   * @param regex Der Regul�ren Ausdruck, dem eine URL entsprechen muss, damit
   *        sie von diesem Pr�perator bearbeitet wird.
   * @throws RegainException Wenn der Regul�re Ausdruck fehlerhaft ist.
   */
  public void setUrlRegex(String regex) throws RegainException {
    try {
      mUrlRegex = new RE(regex);
    }
    catch (RESyntaxException exc) {
      throw new RegainException("URL-Regex for preparator " + getClass().getName()
        + " has wrong syntax: '" + regex + "'", exc);
    }
  }



  /**
   * Gibt zur�ck, ob der Pr�perator das gegebene Dokument bearbeiten kann.
   * Das ist der Fall, wenn seine URL der URL-Regex entspricht.
   *
   * @param rawDocument Das zu pr�fenden Dokuments.
   * @return Ob der Pr�perator das gegebene Dokument bearbeiten kann.
   * @see #setUrlRegex(String)
   */  
  public boolean accepts(RawDocument rawDocument) {
    return mUrlRegex.match(rawDocument.getUrl());
  }



  /**
   * Gibt den Titel des Dokuments zur�ck.
   * <p>
   * Falls kein Titel extrahiert werden konnte, wird <CODE>null</CODE>
   * zur�ckgegeben.
   *
   * @return Der Titel des Dokuments.
   */  
  public String getTitle() {
    return mTitle;
  }
  
  
  
  /**
   * Setzt den Titel des Dokuments, das gerade pr�pariert wird.
   * 
   * @param title Der Titel.
   */
  protected void setTitle(String title) {
    mTitle = title;
  }



  /**
   * Gibt den von Formatierungsinformation befreiten Inhalt des Dokuments zur�ck.
   *
   * @return Der ges�uberte Inhalt.
   */  
  public String getCleanedContent() {
    return mCleanedContent;
  }



  /**
   * Setzt von Formatierungsinformation befreiten Inhalt des Dokuments, das
   * gerade pr�pariert wird.
   * 
   * @param cleanedContent Der ges�uberte Inhalt.
   */
  protected void setCleanedContent(String cleanedContent) {
    mCleanedContent = cleanedContent;
  }



  /**
   * Gibt eine Zusammenfassung f�r das Dokument zur�ck.
   * <p>
   * Da eine Zusammenfassung nicht einfach m�glich ist, wird <CODE>null</CODE>
   * zur�ckgegeben.
   *
   * @return Eine Zusammenfassung f�r das Dokument
   */  
  public String getSummary() {
    return mSummary;
  }



  /**
   * Setzt die Zusammenfassung des Dokuments, das gerade pr�pariert wird.
   * 
   * @param summary Die Zusammenfassung
   */
  protected void setSummary(String summary) {
    mSummary = summary;
  }



  /**
   * Gibt die �berschriften des Dokuments zur�ck.
   * <p>
   * Es handelt sich dabei nicht um die �berschrift des Dokuments selbst,
   * sondern lediglich um Unter-�berschriften, die in dem Dokument verwendendet
   * werden. Mit Hilfe dieser �berschriften l��t sich eine bessere Relevanz
   * berechnen. 
   * <p>
   * Wenn keine �berschriften gefunden wurden, dann wird <code>null</code>
   * zur�ckgegeben.
   *
   * @return Die �berschriften des Dokuments.
   */  
  public String getHeadlines() {
    return mHeadlines;
  }



  /**
   * Setzt die �berschriften, in im Dokument, das gerade pr�pariert wird,
   * gefunden wurden.
   * 
   * @param headlines Die Zusammenfassung
   */
  protected void setHeadlines(String headlines) {
    mHeadlines = headlines;
  }



  /**
   * Gibt den Pfad zur�ck, �ber den das Dokument zu erreichen ist.
   * <p>
   * Falls kein Pfad verf�gbar ist, wird <code>null</code> zur�ckgegeben.
   * 
   * @return Der Pfad, �ber den das Dokument zu erreichen ist.
   */
  public PathElement[] getPath() {
    return mPath;
  }



  /**
   * Setzt den Pfad, �ber den das Dokument zu erreichen ist.
   * 
   * @param path Der Pfad, �ber den das Dokument zu erreichen ist.
   */
  public void setPath(PathElement[] path) {
    mPath = path;
  }



  /**
   * Gibt alle Ressourcen frei, die f�r die Informationen �ber das Dokument
   * reserviert wurden.
   */
  public void cleanUp() {
    mTitle = null;
    mCleanedContent = null;
    mSummary = null;
    mHeadlines = null;
    mPath = null;
  }


  /**
   * Gibt alle Ressourcen frei, die von diesem Pr�parator genutzt wurden.
   * <p>
   * Wird ganz am Ende des Crawler-Prozesses aufgerufen, nachdem alle Dokumente
   * bearbeitet wurden.
   */
  public void close() throws RegainException {
  }

}
