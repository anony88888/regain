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
package net.sf.regain.crawler.document;

import java.util.Map;

import net.sf.regain.RegainException;
import net.sf.regain.crawler.config.PreparatorConfig;

/**
 * Prepares a document for indexing.
 * <p>
 * This is done by extracting the raw text from a document. In other words the
 * document is stripped from formating information. Specific text parts like a
 * title or a summary may be extracted as well.
 * <p>
 * The procedure of preparation is the following:
 * <ul>
 *   <li>First {@link #init(String, PreparatorConfig)} is called.</li>
 *   <li>For each document {@link #accepts(RawDocument)} is called.<br>
 *     If <code>true</code> was returned the actual preparation of the document
 *     is made:
 *     <ul>
 *       <li>{@link #prepare(RawDocument)} is called. The preparator extracts
 *         now all nessesary information.</li>
 *       <li>This information is retrieved by arbitrary calls of
 *         {@link #getCleanedContent()}, {@link #getHeadlines()},
 *         {@link #getPath()}, {@link #getSummary()} and {@link #getTitle()}.</li>
 *       <li>After all information for this document was retrieved,
 *         {@link #cleanUp()} is called. The preparator should release all
 *         information about the current document in order to prepare the
 *         next one.</li>
 *     </ul>
 *   </li>
 *   <li>After all documents have been prepared, {@link #close()} is called.
 * </ul>
 *
 * @author Til Schneider, www.murfman.de
 */
public interface Preparator {

  /**
   * Initializes the preparator.
   *
   * @param regex The regular expression a URL must match to, to be prepared by
   *        this preparator.
   * @param config The configuration for this preparator.
   * @throws RegainException When the regular expression or the configuration
   *         has an error.
   */
  public void init(String regex, PreparatorConfig config) throws RegainException;

  /**
   * Gibt zur�ck, ob der Pr�perator das gegebene Dokument bearbeiten kann.
   * Das ist der Fall, wenn seine URL der URL-Regex entspricht.
   *
   * @param rawDocument Das zu pr�fenden Dokuments.
   * @return Ob der Pr�perator das gegebene Dokument bearbeiten kann.
   * @see #init(String, PreparatorConfig)
   */
  public boolean accepts(RawDocument rawDocument);

  /**
   * Pr�pariert ein Dokument f�r die Indizierung.
   *
   * @param rawDocument Das zu pr�pariernde Dokument.
   *
   * @throws RegainException Wenn die Pr�paration fehl schlug.
   */
  public void prepare(RawDocument rawDocument) throws RegainException;

  /**
   * Gibt den Titel des Dokuments zur�ck.
   * <p>
   * Falls kein Titel extrahiert werden konnte, wird <CODE>null</CODE>
   * zur�ckgegeben.
   *
   * @return Der Titel des Dokuments.
   */
  public String getTitle();

  /**
   * Gibt den von Formatierungsinformation befreiten Inhalt des Dokuments zur�ck.
   *
   * @return Der ges�uberte Inhalt.
   */
  public String getCleanedContent();

  /**
   * Gibt eine Zusammenfassung f�r das Dokument zur�ck.
   * <p>
   * Falls es keine Zusammenfassung m�glich ist, wird <CODE>null</CODE>
   * zur�ckgegeben.
   *
   * @return Eine Zusammenfassung f�r das Dokument zur�ck.
   */
  public String getSummary();

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
  public String getHeadlines();

  /**
   * Gibt den Pfad zur�ck, �ber den das Dokument zu erreichen ist.
   * <p>
   * Falls kein Pfad verf�gbar ist, wird <code>null</code> zur�ckgegeben.
   *
   * @return Der Pfad, �ber den das Dokument zu erreichen ist.
   */
  public PathElement[] getPath();

  /**
   * Gets additional fields that should be indexed.
   * <p>
   * These fields will be indexed and stored.
   * 
   * @return The additional fields or <code>null</code>.
   */
  public Map getAdditionalFields();

  /**
   * Gibt alle Ressourcen frei, die f�r die Informationen �ber das Dokument
   * reserviert wurden.
   * <p>
   * Wird am Ende der Bearbeitung eines Dokumebts aufgerufen, also nachdem die
   * Getter abgefragt wurden.
   */
  public void cleanUp();

  /**
   * Gibt alle Ressourcen frei, die von diesem Pr�parator genutzt wurden.
   * <p>
   * Wird ganz am Ende des Crawler-Prozesses aufgerufen, nachdem alle Dokumente
   * bearbeitet wurden.
   *
   * @throws RegainException Wenn der Pr�parator nicht geschlossen werden konnte.
   */
  public void close() throws RegainException;

}
