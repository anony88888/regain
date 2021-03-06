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
 * A matcher that checks URLs whether they match to a certain pattern.
 * 
 * @author Tilman Schneider, STZ-IDA an der FH Karlsruhe
 */
public interface UrlMatcher {

  /**
   * Checks whether a URL matches to the rules of this matcher.
   * 
   * @param url The URL to check.
   * @return Whether the given URL matches to the rules of this matcher.
   */
  public boolean matches(String url);

  /** 
   * Gets a flag whether links should be extracted from the content
   * 
   * @return Whether from a match for matches(url) possibly inclosed links should be extracted.
   */
  public boolean getShouldBeParsed();

  /** 
   * Gets a flag whether the content should be indexed.
   * 
   * @return Whether from a match for matches(url) the content from the URL should be indexed.
   */
  public boolean getShouldBeIndexed();

  /** 
   * Sets a flag whether links should be extracted from the content
   */
  public void setShouldBeParsed(boolean shouldBeParsed);

  /** 
   * Sets a flag whether the content should be indexed.
   */
  public void setShouldBeIndexed(boolean shouldBeIndexed);
}
