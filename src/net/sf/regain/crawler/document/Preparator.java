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

import net.sf.regain.RegainException;

/**
 * Pr�pariert ein Dokument f�r die Indizierung.
 * <p>
 * Dabei werden die Rohdaten des Dokuments von Formatierungsinformation befreit,
 * es wird der Titel extrahiert und eine Zusammenfassung erstellt.
 * <p>
 * Der Ablauf der Indizierung ist dabei der folgende:
 * <ul>
 *   <li>Es wird {@link #accepts(RawDocument)} aufgerufen.</li>
 *   <li>Wenn <code>true</code> zur�ckgegeben wurde, dann wird
 *     {@link #prepare(RawDocument)} aufgerufen. Dabei extrahiert der Preparator
 *     alle wichtigen Informationen</li>
 *   <li>Diese werden nun durch beliebiges Aufrufen von {@link #getTitle()},
 *     {@link #getCleanedContent()} und {@link #getSummary()} abgefragt.</li>
 *   <li>Schlie�lich wird {@link #cleanUp()} aufgerufen und der Preparator
 *     vergisst die Informationen �ber das Dokument.</li>
 * </ul>
 *
 * @author Tilman Schneider, STZ-IDA an der FH Karlsruhe
 */
public interface Preparator {

  /**
   * Setzt den Regul�ren Ausdruck, dem eine URL entsprechen muss, damit sie von
   * diesem Pr�perator bearbeitet wird.
   * 
   * @param regex Der Regul�ren Ausdruck, dem eine URL entsprechen muss, damit
   *        sie von diesem Pr�perator bearbeitet wird.
   * @throws RegainException Wenn der Regul�re Ausdruck fehlerhaft ist.
   */
  public void setUrlRegex(String regex) throws RegainException;

  /**
   * Gibt zur�ck, ob der Pr�perator das gegebene Dokument bearbeiten kann.
   * Das ist der Fall, wenn seine URL der URL-Regex entspricht.
   *
   * @param rawDocument Das zu pr�fenden Dokuments.
   * @return Ob der Pr�perator das gegebene Dokument bearbeiten kann.
   * @see #setUrlRegex(String)
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
