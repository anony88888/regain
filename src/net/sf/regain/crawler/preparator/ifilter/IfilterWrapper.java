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
package net.sf.regain.crawler.preparator.ifilter;

import org.apache.log4j.Logger;

/**
 * A Java wrapper around one Ifilter. 
 * 
 * @see net.sf.regain.crawler.preparator.IfilterPreparator
 * @author Til Schneider, www.murfman.de
 */
public class IfilterWrapper {

  /** The logger for this class. */
  private static Logger mLog = Logger.getLogger(IfilterWrapper.class);

  static {
    System.loadLibrary("ifilter_wrapper");
  }

  /** Used by native code. Holds the pointer to the PersistentHandler COM object */
  public long mPersistentHandler;

  /** The class ID of the Ifilter*/
  public String clsid;
  
  /**
   * Creates a new instance of IfilterWrapper.
   * 
   * @param clsid The class ID of the Ifilter, e.g.
   *        "clsid:f07f3920-7b8c-11cf-9be8-00aa004b9986"
   */
  public IfilterWrapper(String clsid) {
	  // Intialization moved to getText() method.
	  //init(clsid);
	  this.clsid = clsid;
	  mPersistentHandler = 0;
  }


  /**
   * Initializes COM.
   * <p>
   * Call this once before creating any IfilterWrapper.
   */
  public static void initCom() {
    int staThreadMode = 0x2;

    doCoInitialize(staThreadMode);
  }


  /**
   * Uninitializes COM.
   * <p>
   * Call this after you finished using the IfilterWrappers.
   */
  public static void closeCom() {
    doCoUninitialize();
  }


  /**
   * Calls the COM function CoInitializeEx.
   * 
   * @param threadModel The thread model to use.
   */
  private static native void doCoInitialize(int threadModel);


  /**
   * Calls the COM function CoUninitialize.
   */
  private static native void doCoUninitialize();


  /**
   * Initializes the Ifilter
   * 
   * @param clsid The class ID of the Ifilter, e.g.
   *        "clsid:f07f3920-7b8c-11cf-9be8-00aa004b9986"
   */
  private native void init(String clsid);


  /**
   * Gets the plain text of a file.
   * Initialization and closing of the IFilter.
   * 
   * @param fileName The name of the file too get the text for.
   * @param buffer The StringBuffer where to store the text.
   */
  public void getText(String fileName, StringBuffer buffer) {
	init(clsid);
    getText(fileName, buffer, false, mLog.isDebugEnabled(), false);
    close();
    mPersistentHandler = 0;
  }


  /**
   * Gets the plain text of a file.
   * 
   * @param fileName The name of the file too get the text for.
   * @param buffer The StringBuffer where to store the text.
   * @param showTextEndings specifies whether to include tags that show where a
   *        text block is finished for debug reasons (the text is retrieved in blocks).
   * @param showDebugMessages Whether debug messages should be printed out.
   * @param onlyThrowExceptionWhenNoTextWasFound Whether to throw an exception
   *        when an error occured, but some text already was extracted.
   *        (If no text was extracted the exception is always thrown when there
   *        is an error)
   */
  private native void getText(String fileName, StringBuffer buffer,
    boolean showTextEndings, boolean showDebugMessages,
    boolean onlyThrowExceptionWhenNoTextWasFound);


  /**
   * Closes the Ifilter.
   * <p>
   * Call this method after you are done using the IfilterWrapper.
   */
  private native void close();

  // Method has been replaced by a private close()-method, which is called by the getText()-method
  //public native void close(){}

}
