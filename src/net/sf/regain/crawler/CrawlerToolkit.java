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
package net.sf.regain.crawler;

import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;

import net.sf.regain.RegainException;
import net.sf.regain.RegainToolkit;
import net.sf.regain.crawler.config.CrawlerConfig;


/**
 * Enth�lt Hilfsmethoden f�r den Crawler und seine Hilfsklassen.
 *
 * @author Til Schneider, www.murfman.de
 */
public class CrawlerToolkit {

  /** The logger for this class */
  private static Logger mLog = Logger.getLogger(CrawlerToolkit.class);

  
  /**
   * Originally copied from javax.swing.JEditorPane#getStream(...).
   * <p>
   * Fetches a stream for the given URL, which is about to
   * be loaded by the <code>setPage</code> method.  By
   * default, this simply opens the URL and returns the
   * stream.  This can be reimplemented to do useful things
   * like fetch the stream from a cache, monitor the progress
   * of the stream, etc.
   * <p>
   * This method is expected to have the the side effect of
   * establishing the content type, and therefore setting the
   * appropriate <code>EditorKit</code> to use for loading the stream.
   * <p>
   * If this the stream was an http connection, redirects
   * will be followed and the resulting URL will be set as
   * the <code>Document.StreamDescriptionProperty</code> so that relative
   * URL's can be properly resolved.
   *
   * @param url the URL of the page
   *
   * @return a stream reading data from the specified URL.
   * @throws HttpStreamException if something went wrong.
   */
  public static InputStream getHttpStream(URL url) throws HttpStreamException {
    URLConnection conn = null;
    try {
      conn = url.openConnection();
      if (conn instanceof HttpURLConnection) {
        HttpURLConnection hconn = (HttpURLConnection) conn;
        // hconn.setInstanceFollowRedirects(false); // Not available in Java 1.2.2
        int response = hconn.getResponseCode();
        boolean redirect = (response >= 300 && response <= 399);

        // In the case of a redirect, we want to actually change the URL
        // that was input to the new, redirected URL
        if (redirect) {
          String loc = conn.getHeaderField("Location");
          if (loc.startsWith("http", 0)) {
            url = new URL(loc);
          } else {
            url = new URL(url, loc);
          }
          return getHttpStream(url);
        }
      }

      return conn.getInputStream();
    }
    catch (IOException exc) {
      throw HttpStreamException.createInstance("Getting HTTP connection to "
        + url.toString() + " failed", exc, conn);
    }
  }



  /**
   * L�dt ein Dokument von einem HTTP-Server herunter und gibt seinen Inhalt
   * zur�ck.
   *
   * @param url Die URL des zu ladenden Dokuments.
   *
   * @return Den Inhalt des Dokuments.
   * @throws RegainException Wenn das Laden fehl schlug.
   */
  public static byte[] loadHttpDocument(String url) throws RegainException {
    InputStream in = null;
    ByteArrayOutputStream out = null;
    try {
      in = getHttpStream(new URL(url));

      out = new ByteArrayOutputStream();

      RegainToolkit.pipe(in, out);

      out.close();
      return out.toByteArray();
    }
    catch (IOException exc) {
      throw new RegainException("Loading Dokument by HTTP failed", exc);
    }
    finally {
      if (in != null) {
        try { in.close(); } catch (Exception exc) {}
      }
      if (out != null) {
        try { out.close(); } catch (Exception exc) {}
      }
    }
  }


  /**
   * Writes data to a file
   *
   * @param data The data
   * @param file The file to write to
   *
   * @throws RegainException When writing failed
   */
  public static void writeToFile(byte[] data, File file)
    throws RegainException
  {
    FileOutputStream stream = null;
    try {
      stream = new FileOutputStream(file);
      stream.write(data);
      stream.close();
    }
    catch (IOException exc) {
      throw new RegainException("Writing file failed: " + file.getAbsolutePath(), exc);
    }
    finally {
      if (stream != null) {
        try { stream.close(); } catch (IOException exc) {}
      }
    }
  }


  /**
   * Schreibt einen String in eine Datei.
   *
   * @param text Der String.
   * @param file Die Datei, in die geschrieben werden soll.
   *
   * @throws RegainException Wenn die Erstellung der Liste fehl schlug.
   */
  public static void writeToFile(String text, File file)
    throws RegainException
  {
    writeListToFile(new String[] { text }, file);
  }


  /**
   * Schreibt eine Wortliste in eine Datei.
   *
   * @param wordList Die Wortliste.
   * @param file Die Datei, in die geschrieben werden soll.
   *
   * @throws RegainException Wenn die Erstellung der Liste fehl schlug.
   */
  public static void writeListToFile(String[] wordList, File file)
    throws RegainException
  {
    if ((wordList == null) || (wordList.length == 0)) {
      // Nothing to do
      return;
    }

    FileOutputStream stream = null;
    PrintStream printer = null;
    try {
      stream = new FileOutputStream(file);
      printer = new PrintStream(stream);

      for (int i = 0; i < wordList.length; i++) {
        printer.println(wordList[i]);
      }
    }
    catch (IOException exc) {
      throw new RegainException("Writing word list to " + file.getAbsolutePath()
        + " failed", exc);
    }
    finally {
      if (printer != null) {
        printer.close();
      }
      if (stream != null) {
        try { stream.close(); } catch (IOException exc) {}
      }
    }
  }


  /**
   * L�dt eine Datei vom Dateisystem und gibt den Inhalt zur�ck.
   *
   * @param file Die zu ladende Datei
   * @return Den Inhalt der Datei.
   * @throws RegainException Falls das Laden fehl schlug.
   */
  public static byte[] loadFile(File file) throws RegainException {
    if (file.isDirectory()) {
      throw new RegainException("Can't load a directory: "
        + file.getAbsolutePath());
    }

    FileInputStream in = null;
    ByteArrayOutputStream out = null;
    try {
      in = new FileInputStream(file);
      out = new ByteArrayOutputStream((int) file.length());

      RegainToolkit.pipe(in, out);

      return out.toByteArray();
    }
    catch (IOException exc) {
      throw new RegainException("Loading file failed " + file.getAbsolutePath(), exc);
    }
    finally {
      if (out != null) {
        try { out.close(); } catch (IOException exc) {}
      }
      if (in != null) {
        try { in.close(); } catch (IOException exc) {}
      }
    }
  }



  /**
   * Wandelt die gegebene HTTP-URL in eine absolute URL um.
   * <p>
   * Wenn die URL bereits absolut war, so wird sie unver�ndert zur�ckgegeben.
   *
   * @param url Die umzuwandelnde URL.
   * @param parentUrl Die URL auf die sich die umzuwandelnde URL bezieht. Diese
   *        URL muss absolut sein.
   *
   * @return Die absolute Version der gegebenen URL.
   */
  public static String toAbsoluteUrl(String url, String parentUrl) {
    if (! (url.startsWith("http://") || url.startsWith("file://"))) {
      // This is a relative URL

      if (parentUrl.startsWith("http://") && url.startsWith("/")) {
        // NOTE: In HTTP there are two kinds of relative URLs:
        // Some start with '/': They are absolute within the domain
        // Others don't start with '/': They are really realtive

        // This URL is absolute within the domain
        // NOTE: 7 for skipping 'http://'
        int firstSlashPos = parentUrl.indexOf('/', 7);
        if (firstSlashPos != -1) {
          String domain = parentUrl.substring(0, firstSlashPos);
          url = domain + url;
        } else {
          // The parentUrl is a domain without a path, e.g. "http://www.murfman.de"
          // -> Use the whole parentUrl
          // NOTE: url start with a /
          url = parentUrl + url;
        }
      } else {
        // This URL is really relative
        int lastSlashPos = parentUrl.lastIndexOf('/');
        // NOTE: http:// has 7 chars
        if (lastSlashPos > 7) {
          String domainWidthPath = parentUrl.substring(0, lastSlashPos + 1);
          url = domainWidthPath + url;
        } else {
          // The parentUrl is a domain without a path, e.g. "http://www.murfman.de"
          // -> Use the whole parentUrl
          url = parentUrl + "/" + url;
        }
      }
    }

    return url;
  }


  /**
   * Prints the active threads to System.out. Usefull for debugging.
   */
  public static void printActiveThreads() {
    ThreadGroup group = Thread.currentThread().getThreadGroup();
    Thread[] activeArr = new Thread[group.activeCount()];
    group.enumerate(activeArr);
    System.out.print("active threads: ");
    for (int i = 0; i < activeArr.length; i++) {
      if (i != 0) {
        System.out.print(", ");
      }
      System.out.print(activeArr[i].getName());
    }
    System.out.println();
  }


  /**
   * Initialisiert die Proxy-Einstellungen.
   *
   * @param config Die Konfiguration, aus der die Einstellungen gelesen werden
   *        sollen.
   */
  public static void initProxy(CrawlerConfig config) {
    String httpProxyHost = config.getProxyHost();
    String httpProxyPort = config.getProxyPort();
    String httpProxyUser = config.getProxyUser();
    String httpProxyPassword = config.getProxyPassword();

    String msg = "";
    if (httpProxyHost != null) {
      System.setProperty("http.proxyHost", httpProxyHost);
      msg += " host: " + httpProxyHost;
    }
    if (httpProxyPort != null) {
      System.setProperty("http.proxyPort", httpProxyPort);
      msg += " port: " + httpProxyPort;
    }
    if (httpProxyUser != null) {
      System.setProperty("http.proxyUser", httpProxyUser);
      msg += " user: " + httpProxyUser;
    }
    if (httpProxyPassword != null) {
      System.setProperty("http.proxyPassword", httpProxyPassword);
      msg += " password: (" + httpProxyPassword.length() + " characters)";
    }

    if (msg.length() != 0) {
      mLog.info("Using proxy:" + msg);
    } else {
      mLog.info("Using no proxy");
    }
  }
  
}
