/*
 * regain - A file search engine providing plenty of formats
 * Copyright (C) 2004-2008 Til Schneider, Thomas Tesche
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
 * Contact: Til Schneider, info@murfman.de, Thomas Tesche, regain@thtesche.com
 *
 * CVS information:
 *  $RCSfile$
 *   $Source$
 *     $Date: 2007-11-01 12:32:25 +0100 (Thu, 01 Nov 2007) $
 *   $Author: til132 $
 * $Revision: 256 $
 */
package net.sf.regain.ui.server.taglib.hit;

import net.sf.regain.util.sharedtag.taglib.SharedTagWrapperTag;

/**
 * Taglib wrapper for the shared ContentTag.
 *
 * @see net.sf.regain.search.sharedlib.hit.ContentTag
 *
 * @author Thomas Tesche, www.thtesche.com
 */
public class ContentTag extends SharedTagWrapperTag {

  /**
   * Creates a new instance of ContentTag.
   */
  public ContentTag() {
    super(new net.sf.regain.search.sharedlib.hit.ContentTag());
  }
  
}