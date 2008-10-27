/*
 * regain - A file search engine providing plenty of formats
 * Copyright (C) 2004-2008  Til Schneider, Thomas Tesche
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
 *     $Date: 2008-10-25 18:35:21 +0200 (Sat, 25 Oct 2008) $
 *   $Author: thtesche $
 * $Revision: 349 $
 */
package net.sf.regain;

import javax.mail.URLName;

/**
 * Toolkit for handling IMAP specific functions. We label the messages/attachments with following
 * pattern:
 * iamp-URL/foldername/message_uid_(attachment_id) (attachment is optional)
 * This is not a valid URL and has to be handled specialised
 * 
 * @author Thomas Tesche (thtesche), http://www.thtesche.com/
 */
public class ImapToolkit {

  public static boolean checkForEMailURL(String url) throws Exception {

    URLName urlName = new URLName(url);
    // are there references to a message an optional to an attachment in the message
    return urlName.getFile().matches(".*(message_[0-9]+)(_attachment_[0-9]*)*$");
   
  }


}
