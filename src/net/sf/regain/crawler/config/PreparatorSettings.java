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
 * The settings of a preparator
 *
 * @see net.sf.regain.crawler.document.Preparator
 * @author Til Schneider, www.murfman.de
 */
public class PreparatorSettings {

  /**
   * The regular expression that identifies a URL that should be handled with
   * the preparator.
   */
  private String mUrlRegex;

  /**
   * The class name of the preparator. The class must implement
   * {@link net.sf.regain.crawler.document.Preparator Preparator}.
   */
  private String mPreparatorClassName;
  
  /**
   * The configuration of the preparator.
   */
  private PreparatorConfig mPreparatorConfig;


  /**
   * Creates a new instance of PreparatorSettings.
   * 
   * @param urlRegex The regular expression that identifies a URL that should be
   *        handled with the preparator.
   * @param preparatorClassName The class name of the preparator. The class must
   *        implement {@link net.sf.regain.crawler.document.Preparator Preparator}.
   * @param preparatorConfig The configuration of the preparator.
   */
  public PreparatorSettings(String urlRegex, String preparatorClassName,
    PreparatorConfig preparatorConfig)
  {
    mUrlRegex = urlRegex;
    mPreparatorClassName = preparatorClassName;
    mPreparatorConfig = preparatorConfig;
  }


  /**
   * Gets the regular expression that identifies a URL that should be handled
   * with the preparator.
   *
   * @return The regular expression that identifies a URL that should be handled
   *         with the preparator.
   */
  public String getUrlRegex() {
    return mUrlRegex;
  }


  /**
   * Gets the class name of the preparator.
   *
   * @return The class name of the preparator.
   */
  public String getPreparatorClassName() {
    return mPreparatorClassName;
  }

  
  /**
   * Gets the configuration of the preparator.
   * 
   * @return The configuration of the preparator.
   */
  public PreparatorConfig getPreparatorConfig() {
    return mPreparatorConfig;
  }

}
