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

import java.util.HashMap;
import java.util.Map;

import net.sf.regain.RegainException;
import net.sf.regain.crawler.config.PreparatorConfig;
import net.sf.regain.crawler.document.PathElement;
import net.sf.regain.crawler.document.Preparator;
import net.sf.regain.crawler.document.RawDocument;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;


/**
 * Abstrakte Implementierung eines Präperators.
 * <p>
 * Implementiert die Getter-Methoden und übernimmt das Aufräumen zwischen zwei
 * Präparationen (Siehe {@link #cleanUp()}).
 * <p>
 * Kindklassen können die Werte über die geschützten (protected) Setter-Methoden
 * setzen.
 *
 * @author Til Schneider, www.murfman.de
 */
public abstract class AbstractPreparator implements Preparator {

  /**
   * The regular expression a URL must match to, to be prepared by this
   * preparator.
   */
  private RE mUrlRegex;
  /** Der gefundene Titel. */
  private String mTitle;
  /** Der gesäuberte Inhalt. */
  private String mCleanedContent;
  /** Die Zusammenfassung des Dokuments. */
  private String mSummary;
  /** Die extrahierten Überschriften. Kann <code>null</code> sein */
  private String mHeadlines;
  /** Der Pfad, über den das Dokument zu erreichen ist. */
  private PathElement[] mPath;
  /** The additional fields that should be indexed. */
  private HashMap mAdditionalFieldMap;


  /**
   * Erzeugt eine neue Instanz.
   */
  public AbstractPreparator() {
  }


  /**
   * Initializes the preparator.
   *
   * @param regex The regular expression a URL must match to, to be prepared by
   *        this preparator.
   * @param config The configuration for this preparator.
   * @throws RegainException If the regular expression or the configuration
   *         has an error.
   */
  public void init(String regex, PreparatorConfig config) throws RegainException {
    try {
      mUrlRegex = new RE(regex);
    }
    catch (RESyntaxException exc) {
      throw new RegainException("URL-Regex for preparator " + getClass().getName()
        + " has wrong syntax: '" + regex + "'", exc);
    }
    
    readConfig(config);
  }

  
  /**
   * Reads the configuration for this preparator.
   * <p>
   * Does nothing by default. May be overridden by subclasses to actual read the
   * config.
   * 
   * @param config The configuration
   * @throws RegainException If the configuration has an error.
   */
  protected void readConfig(PreparatorConfig config) throws RegainException {
  }


  /**
   * Gibt zurück, ob der Präperator das gegebene Dokument bearbeiten kann.
   * Das ist der Fall, wenn seine URL der URL-Regex entspricht.
   *
   * @param rawDocument Das zu prüfenden Dokuments.
   * @return Ob der Präperator das gegebene Dokument bearbeiten kann.
   * @see #init(String, PreparatorConfig)
   */
  public boolean accepts(RawDocument rawDocument) {
    return mUrlRegex.match(rawDocument.getUrl());
  }



  /**
   * Gibt den Titel des Dokuments zurück.
   * <p>
   * Falls kein Titel extrahiert werden konnte, wird <CODE>null</CODE>
   * zurückgegeben.
   *
   * @return Der Titel des Dokuments.
   */
  public String getTitle() {
    return mTitle;
  }



  /**
   * Setzt den Titel des Dokuments, das gerade präpariert wird.
   *
   * @param title Der Titel.
   */
  protected void setTitle(String title) {
    mTitle = title;
  }



  /**
   * Gibt den von Formatierungsinformation befreiten Inhalt des Dokuments zurück.
   *
   * @return Der gesäuberte Inhalt.
   */
  public String getCleanedContent() {
    return mCleanedContent;
  }



  /**
   * Setzt von Formatierungsinformation befreiten Inhalt des Dokuments, das
   * gerade präpariert wird.
   *
   * @param cleanedContent Der gesäuberte Inhalt.
   */
  protected void setCleanedContent(String cleanedContent) {
    mCleanedContent = cleanedContent;
  }



  /**
   * Gibt eine Zusammenfassung für das Dokument zurück.
   * <p>
   * Da eine Zusammenfassung nicht einfach möglich ist, wird <CODE>null</CODE>
   * zurückgegeben.
   *
   * @return Eine Zusammenfassung für das Dokument
   */
  public String getSummary() {
    return mSummary;
  }



  /**
   * Setzt die Zusammenfassung des Dokuments, das gerade präpariert wird.
   *
   * @param summary Die Zusammenfassung
   */
  protected void setSummary(String summary) {
    mSummary = summary;
  }



  /**
   * Gibt die Überschriften des Dokuments zurück.
   * <p>
   * Es handelt sich dabei nicht um die Überschrift des Dokuments selbst,
   * sondern lediglich um Unter-Überschriften, die in dem Dokument verwendendet
   * werden. Mit Hilfe dieser Überschriften läßt sich eine bessere Relevanz
   * berechnen.
   * <p>
   * Wenn keine Überschriften gefunden wurden, dann wird <code>null</code>
   * zurückgegeben.
   *
   * @return Die Überschriften des Dokuments.
   */
  public String getHeadlines() {
    return mHeadlines;
  }



  /**
   * Setzt die Überschriften, in im Dokument, das gerade präpariert wird,
   * gefunden wurden.
   *
   * @param headlines Die Zusammenfassung
   */
  protected void setHeadlines(String headlines) {
    mHeadlines = headlines;
  }



  /**
   * Gibt den Pfad zurück, über den das Dokument zu erreichen ist.
   * <p>
   * Falls kein Pfad verfügbar ist, wird <code>null</code> zurückgegeben.
   *
   * @return Der Pfad, über den das Dokument zu erreichen ist.
   */
  public PathElement[] getPath() {
    return mPath;
  }



  /**
   * Setzt den Pfad, über den das Dokument zu erreichen ist.
   *
   * @param path Der Pfad, über den das Dokument zu erreichen ist.
   */
  public void setPath(PathElement[] path) {
    mPath = path;
  }


  /**
   * Gets additional fields that should be indexed.
   * <p>
   * These fields will be indexed and stored.
   * 
   * @return The additional fields or <code>null</code>.
   */
  public Map getAdditionalFields() {
    return mAdditionalFieldMap;
  }


  /**
   * Adds an additional field to the current document.
   * <p>
   * This field will be indexed and stored.
   * 
   * @param fieldName The name of the field.
   * @param fieldValue The value of the field.
   */
  public void addAdditionalField(String fieldName, String fieldValue) {
    if (mAdditionalFieldMap == null) {
      mAdditionalFieldMap = new HashMap();
    }
    mAdditionalFieldMap.put(fieldName, fieldValue);
  }
  

  /**
   * Gibt alle Ressourcen frei, die für die Informationen über das Dokument
   * reserviert wurden.
   */
  public void cleanUp() {
    mTitle = null;
    mCleanedContent = null;
    mSummary = null;
    mHeadlines = null;
    mPath = null;
    mAdditionalFieldMap = null;
  }


  /**
   * Frees all resources reserved by the preparator.
   * <p>
   * Is called at the end of the crawler process after all documents were
   * processed.
   * 
   * @throws RegainException If freeing the resources failed.
   */
  public void close() throws RegainException {
  }

}
