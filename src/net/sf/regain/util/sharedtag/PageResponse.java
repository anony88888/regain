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
package net.sf.regain.util.sharedtag;

import net.sf.regain.RegainException;

/**
 * A page response.
 *
 * @author Til Schneider, www.murfman.de
 */
public interface PageResponse {

  /**
   * Prints text to a page.
   * 
   * @param text The text to print.
   * @throws RegainException If printing failed.
   */
  public void print(String text) throws RegainException;

  /**
   * Redirects the request to another URL.
   * 
   * @param url The URL to redirect to.
   * @throws RegainException If redirecting failed.
   */
  public void sendRedirect(String url) throws RegainException;

}
