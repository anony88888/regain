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
package net.sf.regain.search.taglib.hit;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import net.sf.regain.RegainToolkit;
import net.sf.regain.search.ExtendedJspException;

import org.apache.lucene.document.Document;


/**
 * Generiert die Größe des aktuellen Trefferdokuments. Dabei wird immer die
 * größtmögliche Einheit angezeigt.
 *
 * @author Til Schneider, www.murfman.de
 */
public class SizeTag extends AbstractHitTag {

  /**
   * Generiert den Tag.
   *
   * @param out Der JspWriter auf den der Taginhalt geschrieben werden soll.
   * @param hit Der aktuelle Suchtreffer.
   */
  protected void printEndTag(JspWriter out, Document hit)
    throws IOException, ExtendedJspException
  {
    String sizeAsString = hit.get("size");
    if (sizeAsString != null) {
      try {
        int size = Integer.parseInt(sizeAsString);

        out.print(RegainToolkit.bytesToString(size));
      } catch (NumberFormatException exc) {
        throw new ExtendedJspException("Field size is not a number: "
          + sizeAsString);
      }
    }
  }

}
