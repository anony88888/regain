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
package net.sf.regain.crawler.config;

/**
 * Die Einstellungen f�r einen Pr�parator
 * 
 * @see net.sf.regain.crawler.document.Preparator
 * @author Tilman Schneider, STZ-IDA an der FH Karlsruhe
 */
public class PreparatorSettings {
  
  /**
   * Der regul�re Ausdruck, der eine URL identifiziert, die mit dem
   * Pr�parator bearbeitet werden soll.
   */
  private String mUrlRegex;

  /**
   * Der Klassenname des Pr�parators. Die Klasse muss
   * {@link net.sf.regain.crawler.document.Preparator Preparator}
   * implementieren.
   */
  private String mPreparatorClassName;
  
  
  
  /**
   * Erzeugt eine neue PreparatorSettings-Instanz
   * 
   * @param urlRegex Der regul�re Ausdruck, der eine URL identifiziert, die mit
   *        dem Pr�parator bearbeitet werden soll.
   * @param preparatorClassName Der Klassenname des Pr�parators. Die Klasse muss
   *        {@link net.sf.regain.crawler.document.Preparator Preparator}
   *        implementieren.
   */
  public PreparatorSettings(String urlRegex, String preparatorClassName) {
    mUrlRegex = urlRegex;
    mPreparatorClassName = preparatorClassName;
  }
  
  
  
  /**
   * Gibt den regul�re Ausdruck zur�ck, der eine URL identifiziert, die mit
   * dem Pr�parator bearbeitet werden soll.
   * 
   * @return Der regul�re Ausdruck, der eine URL identifiziert, die mit dem
   *         Pr�parator bearbeitet werden soll.
   */
  public String getUrlRegex() {
    return mUrlRegex;
  }
  
  
  
  /**
   * Gibt den Klassenname des Pr�parators zur�ck.
   * 
   * @return Der Klassenname des Pr�parators.
   */
  public String getPreparatorClassName() {
    return mPreparatorClassName;
  }

}
