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
 * Ein Eintrag in der Wei�en Liste.
 * <p>
 * Die Wei�e Liste enth�lt Pr�fixe, von denen eine URL einen haben <i>mu�</i>,
 * um bearbeitet zu werden.
 * <p>
 * Des weiteren wird durch die Wei�e Liste festgelegt, welche Teile des Index
 * vom Crawler bearbeitet werden sollen.
 * 
 * @author Tilman Schneider, STZ-IDA an der FH Karlsruhe
 */
public class WhiteListEntry {

  /** Der Pr�fix, den eine URL haben muss, um bearbeitet zu werden. */
  private String mPrefix;
  
  /** Der Name dieses Indexeintrages. Kann <code>null</code> sein. */
  private String mName;
  
  /**
   * Gibt an, ob URLs vom Crawler aktualisiert werden sollen, die zu diesem
   * Eintrag passen.
   */
  private boolean mShouldBeUpdated;
  
  
  /**
   * Erzeugt eine neue Instanz.
   * 
   * @param prefix Der Pr�fix, den eine URL haben muss, um bearbeitet zu werden.
   * @param name Der Name dieses Indexeintrages. Kann <code>null</code> sein.
   */
  public WhiteListEntry(String prefix, String name) {
    mPrefix = prefix;
    mName = name;
    
    mShouldBeUpdated = true;
  }
  
  
  /**
   * Gibt den Pr�fix zur�ck, den eine URL haben muss, um bearbeitet zu werden.
   * 
   * @return Der Pr�fix, den eine URL haben muss, um bearbeitet zu werden.
   */
  public String getPrefix() {
    return mPrefix;
  }

  
  /**
   * Gibt den Namen dieses Indexeintrages zur�ck. Kann <code>null</code> sein.
   * 
   * @return Der Name. Kann <code>null</code> sein.
   */
  public String getName() {
    return mName;
  }

  
  /**
   * Gibt zur�ck, ob URLs vom Crawler aktualisiert werden sollen, die zu
   * diesem Eintrag passen.
   * 
   * @return Returns Ob passende URLs vom Crawler aktualisiert werden sollen.
   */
  public boolean shouldBeUpdated() {
    return mShouldBeUpdated;
  }

  
  /**
   * Gibt an, ob URLs vom Crawler aktualisiert werden sollen, die zu
   * diesem Eintrag passen.
   * 
   * @param shouldBeUpdated Sollen passende URLs vom Crawler aktualisiert werden?
   */
  public void setShouldBeUpdated(boolean shouldBeUpdated) {
    mShouldBeUpdated = shouldBeUpdated;
  }

}
