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
package net.sf.regain.ui.server.taglib.input;

import net.sf.regain.util.sharedtag.taglib.SharedTagWrapperTag;

/**
 * Taglib wrapper for the shared hiddenparam tag.
 *
 * @see net.sf.regain.search.sharedlib.input.HiddenparamTag
 *
 * @author Til Schneider, www.murfman.de
 */
public class HiddenparamTag extends SharedTagWrapperTag {

  /**
   * Creates a new instance of HiddenparamTag.
   */
  public HiddenparamTag() {
    super(new net.sf.regain.search.sharedlib.input.HiddenparamTag());
  }


  /**
   * Sets the name of the request parameter whichs value should be generated.
   * 
   * @param name The name of the request parameter whichs value should be
   *        generated.
   */
  public void setName(String name) {
    getNestedTag().setParameter("name", name);
  }

}
