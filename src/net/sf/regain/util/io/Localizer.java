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
package net.sf.regain.util.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sf.regain.RegainToolkit;

import org.apache.log4j.Logger;

/**
 *
 * @author Til Schneider, www.murfman.de
 */
public class Localizer {
  
  /** The logger for this class. May be <code>null</code>. */  
  private static Logger mLog;
  
  /** The ResourceBundle to get the messages from. */
  private ResourceBundle mBundle;
  
  /** The prefix to put before every key. */
  private String mKeyPrefix;
  
  /**
   * Holds for a directory (File) an URLClassLoader that loads ressources from
   * that directory (URLClassLoader).
   */
  private static HashMap mFileClassLoaderHash;


  /**
   * Creates a new instance of Localizer.
   *
   * @param basedir The directory to load the resource bundle from.
   * @param basename The basename of the resource bundle to load.
   * @param locale the locale to create the localizer for.
   */
  public Localizer(File basedir, String basename, Locale locale) {
    // Try to load the localizer
    if (mLog == null) {
      try {
        mLog = Logger.getLogger(Localizer.class.getName());
      }
      catch (Exception exc) {
        // The &§%&&$ tomcat and its class loading problems!!! Grrr
        // When it is not willing to load the Logger then we've got to log to
        // System.out
      }
    }
    
    try {
      URLClassLoader loader = getClassLoader(basedir);
      mBundle = ResourceBundle.getBundle(basename, locale, loader);
    }
    catch (Throwable thr) {
      logError("ResourceBundle not found: '" + basename + "'", thr);
    }
    
    mKeyPrefix = "";
  }

  
  /**
   * Creates a new instance of Localizer.
   * 
   * @param clazz The class to create the Localizer for.
   */
  public Localizer(Class clazz) {
    this(clazz, Locale.getDefault());
  }
  

  /**
   * Creates a new instance of Localizer.
   * 
   * @param clazz The class to create the Localizer for.
   * @param locale the locale to create the localizer for.
   */
  public Localizer(Class clazz, Locale locale) {
    String className = clazz.getName();
    String packageName;
    int lastDot = className.lastIndexOf('.');
    if (lastDot == -1) {
      mKeyPrefix = className + ".";
      packageName = "";
    } else {
      mKeyPrefix = className.substring(lastDot + 1) + ".";
      packageName = className.substring(0, lastDot);
    }

    // Get the bundle
    do {
      // Try this package
      String basename = (packageName.length() == 0) ? "msg" : (packageName + ".msg");
      try {
        mBundle = ResourceBundle.getBundle(basename, locale, clazz.getClassLoader());
      }
      catch (MissingResourceException exc) {
      }
      
      // Check whether we have to check the next package
      if (mBundle == null) {
        if (packageName.length() == 0) {
          // We already tried all packages -> Give up
          logError("ResourceBundle not found for class '" + clazz + "'", null);
          return;
        }
        
        lastDot = packageName.lastIndexOf('.');
        if (lastDot == -1) {
          packageName = "";
        } else {
          packageName = packageName.substring(0, lastDot);
        }
      }
    }
    while(mBundle == null);
  }


  /**
   * Gets a class loader that loads ressources from a directory.
   * 
   * @param basedir The directory to load the ressources from.
   * @return The class loader
   * @throws MalformedURLException If the file could not be converted to an URL.
   */
  private static synchronized URLClassLoader getClassLoader(File basedir)
    throws MalformedURLException
  {
    if (mFileClassLoaderHash == null) {
      mFileClassLoaderHash = new HashMap();
    }
    
    URLClassLoader loader = (URLClassLoader) mFileClassLoaderHash.get(basedir);
    if (loader == null) {
      loader = new URLClassLoader(new URL[] { basedir.toURL() });
      mFileClassLoaderHash.put(basedir, loader);
    }
    
    return loader;
  }
  
  
  /**
   * Gets a localized message.
   *
   * @param key The key of the message.
   * @param defaultMsg The default message. (english)
   * @param arg1 The argument that should replace <CODE>{0}</CODE>.
   * @return a localized message.
   */  
  public String msg(String key, String defaultMsg, Object arg1) {
    return msg(key, defaultMsg, new Object[] { arg1 } );
  }

  
  
  /**
   * Gets a localized message.
   *
   * @param key The key of the message.
   * @param defaultMsg The default message. (english)
   * @param arg1 The argument that should replace <CODE>{0}</CODE>.
   * @param arg2 The argument that should replace <CODE>{1}</CODE>.
   * @return a localized message.
   */  
  public String msg(String key, String defaultMsg, Object arg1, Object arg2) {
    return msg(key, defaultMsg, new Object[] { arg1, arg2 } );
  }

  
  
  /**
   * Gets a localized message.
   *
   * @param key The key of the message.
   * @param defaultMsg The default message. (english)
   * @param arg1 The argument that should replace <CODE>{0}</CODE>.
   * @param arg2 The argument that should replace <CODE>{1}</CODE>.
   * @param arg3 The argument that should replace <CODE>{2}</CODE>.
   * @return a localized message.
   */  
  public String msg(String key, String defaultMsg, Object arg1, Object arg2,
    Object arg3)
  {
    return msg(key, defaultMsg, new Object[] { arg1, arg2, arg3 } );
  }
  
  
  
  /**
   * Gets a localized message.
   *
   * @param key The key of the message.
   * @param defaultMsg The default message. (english)
   * @param args The arguments that should replace the appropriate place holder.
   *        See {@link java.text.MessageFormat} for details.
   * @return a localized message.
   */  
  public String msg(String key, String defaultMsg, Object[] args) {
    String msg = msg(key, defaultMsg);
    
    // Workaround: The MessageFormat uses the ' char for quoting strings.
    //             so the "{0}" in "AB '{0}' CD" will not be replaced.
    //             In order to avoid this we quote every ' with '', so
    //             everthing will be replaced as expected.
    msg = RegainToolkit.replace(msg, "'", "''");
    
    MessageFormat format = new MessageFormat(msg, mBundle.getLocale());
    return format.format(args);    
  }


  /**
   * Gets a localized message.
   *
   * @param key The key of the message.
   * @param defaultMsg The default message. (english)
   * @return a localized message.
   */  
  public String msg(String key, String defaultMsg) {
    key = mKeyPrefix + key;
    
    String msg = null;
    if (mBundle != null) {
      try {
        msg = mBundle.getString(key);
      }
      catch (MissingResourceException exc) {}
    }
    
    if (msg == null) {
      return "[" + key + "#" + defaultMsg + "]";
    } else {
      return msg;
    }
  }


  /**
   * Logs an error.
   * 
   * @param msg The error message.
   * @param thr The error.
   */
  private void logError(String msg, Throwable thr) {
    if (mLog != null) {
      mLog.error(msg, thr);
    } else {
      System.out.println("ERROR: " + msg);
      if (thr != null) {
        thr.printStackTrace();
      }
    }
  }
  
}
