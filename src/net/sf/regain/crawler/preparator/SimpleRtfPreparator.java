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
package net.sf.regain.crawler.preparator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import net.sf.regain.RegainException;
import net.sf.regain.RegainToolkit;
import net.sf.regain.crawler.document.AbstractPreparator;
import net.sf.regain.crawler.document.RawDocument;
import net.sf.regain.crawler.preparator.rtf.RtfFilterReader;


/**
 * Pr�pariert ein RTF-Dokument f�r die Indizierung. Dazu wird s�mtliche
 * Formatierungsinformation einfach ignoriert.
 * <p>
 * Dabei werden die Rohdaten des Dokuments von Formatierungsinformation befreit.
 *
 * @author Til Schneider, www.murfman.de
 */
public class SimpleRtfPreparator extends AbstractPreparator {

  /**
   * Creates a new instance of SimpleRtfPreparator.
   */
  public SimpleRtfPreparator() {
    super("rtf");
  }


  /**
   * Pr�pariert ein Dokument f�r die Indizierung.
   *
   * @param rawDocument Das zu pr�pariernde Dokument.
   *
   * @throws RegainException Wenn die Pr�paration fehl schlug.
   */
  public void prepare(RawDocument rawDocument) throws RegainException {
    InputStream stream = null;
    try {
      stream = rawDocument.getContentAsStream();
      RtfFilterReader reader = new RtfFilterReader(new InputStreamReader(stream));
      StringWriter writer = new StringWriter();

      RegainToolkit.pipe(reader, writer);

      stream.close();
      reader.close();
      writer.close();

      String cleanedContent = writer.toString();
      setCleanedContent(cleanedContent);
    }
    catch (Exception exc) {
      throw new RegainException("Reading RTF dokument failed: "
          + rawDocument.getUrl(), exc);
    }
    finally {
      if (stream != null) {
        try { stream.close(); } catch (Exception exc) {}
      }
    }
  }

}
