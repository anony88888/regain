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
package net.sf.regain.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;

import net.sf.regain.RegainException;
import net.sf.regain.RegainToolkit;
import net.sf.regain.search.config.IndexConfig;
import net.sf.regain.search.config.SearchConfig;
import net.sf.regain.search.config.XmlSearchConfig;
import net.sf.regain.util.sharedtag.PageRequest;
import net.sf.regain.util.sharedtag.PageResponse;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * A toolkit for the search JSPs containing helper methods.
 *
 * @author Til Schneider, www.murfman.de
 */
public class SearchToolkit {

  /** The name of the page context attribute that holds the SearchContext. */
  private static final String SEARCH_CONTEXT_ATTR_NAME = "SearchContext";

  /** The name of the page context attribute that holds the IndexConfig. */
  private static final String INDEX_CONFIG_CONTEXT_ATTR_NAME = "IndexConfig";
  
  /** The prefix for request parameters that contain additional field values. */
  private static final String FIELD_PREFIX = "field.";
  
  /** The configuration of the search mask. */
  private static SearchConfig mConfig;
  
  /** Holds for an extension the mime type. */
  private static HashMap mMimeTypeHash;

  
  /**
   * Gets the IndexConfig from the PageContext.
   * <p>
   * If there is no IndexConfig in the PageContext it is put in the PageContext,
   * so the next call will find it.
   * 
   * @param request The page request where the IndexConfig will be taken
   *        from or put to.
   * @return The IndexConfig for the page the context is for.
   * @throws RegainException If there is no IndexConfig for the specified index.
   */
  public static IndexConfig getIndexConfig(PageRequest request)
    throws RegainException
  {
    IndexConfig config = (IndexConfig) request.getContextAttribute(INDEX_CONFIG_CONTEXT_ATTR_NAME);
    if (config == null) {
      // Load the config (if not yet done)
      loadConfiguration(request);

      // Get the name of the index
      String indexName = request.getParameter("index");
      if (indexName == null) {
        indexName = mConfig.getDefaultIndexName();
      }
      if (indexName == null) {
        throw new RegainException("Request parameter 'index' not specified and " +
            "no default index configured");
      }
      
      // Get the configuration for that index
      config = mConfig.getIndexConfig(indexName);
      if (config == null) {
        throw new RegainException("The configuration does not contain the index '"
            + indexName + "'");
      }

      // Store the IndexConfig in the page context
      request.setContextAttribute(INDEX_CONFIG_CONTEXT_ATTR_NAME, config);
    }
    return config;
  }
  

  /**
   * Gets the SearchContext from the PageContext.
   * <p>
   * If there is no SearchContext in the PageContext it is created and put in the
   * PageContext, so the next call will find it.
   *
   * @param request The page request where the SearchContext will be taken
   *        from or put to.
   * @return The SearchContext for the page the context is for.
   * @throws RegainException If the SearchContext could not be created.
   * @see SearchContext
   */
  public static SearchContext getSearchContext(PageRequest request)
    throws RegainException
  {
    SearchContext context = (SearchContext) request.getContextAttribute(SEARCH_CONTEXT_ATTR_NAME);
    if (context == null) {
      // Get the index config
      IndexConfig indexConfig = getIndexConfig(request);

      // Get the query
      String query = request.getParameter("query");
      
      // Append the additional fields to the query
      Enumeration enum = request.getParameterNames();
      while (enum.hasMoreElements()) {
        String paramName = (String) enum.nextElement();
        if (paramName.startsWith(FIELD_PREFIX)) {
          // This is an additional field -> Append it to the query
          String fieldName = paramName.substring(FIELD_PREFIX.length());
          String fieldValue = request.getParameter(paramName).trim();
          
          if (fieldValue.length() != 0) {
            query += " " + fieldName + ":\"" + fieldValue + "\"";
          }
        }
      }
      
      // Create the SearchContext and store it in the page context
      context = new SearchContext(indexConfig, query);
      request.setContextAttribute(SEARCH_CONTEXT_ATTR_NAME, context);
    }

    return context;
  }
  
  
  /**
   * Decides whether the remote access to a file should be allowed.
   * <p>
   * The access is granted if the file is in the index.
   * 
   * @param request The request that holds the used index.
   * @param fileUrl The URL to file to check.
   * @return Whether the remote access to a file should be allowed.
   * @throws RegainException If checking the file failed.
   */
  public static boolean allowFileAccess(PageRequest request, String fileUrl)
    throws RegainException
  {
    IndexConfig config = getIndexConfig(request);
    
    IndexSearcherManager manager = IndexSearcherManager.getInstance(config.getDirectory());
    
    // Check whether the document is in the index
    Term urlTerm = new Term("url", fileUrl);
    Query query = new TermQuery(urlTerm);
    Hits hits = manager.search(query);
    
    // Allow the access if we found the file in the index
    return hits.length() > 0;
  }


  /**
   * Sends a file to the client.
   *
   * @param request The request.
   * @param response The response.
   * @param file The file to send.
   * @throws RegainException If sending the file failed.
   */
  public static void sendFile(PageRequest request, PageResponse response, File file)
    throws RegainException
  {
    long lastModified = file.lastModified();
    if (lastModified < request.getHeaderAsDate("If-Modified-Since")) {
      // The browser can use the cached file
      response.sendError(304);
    } else {
      response.setHeaderAsDate("Date", System.currentTimeMillis());
      response.setHeaderAsDate("Last-Modified", lastModified);
    
      // TODO: Make this configurable
      if (mMimeTypeHash == null) {
        // Source: http://de.selfhtml.org/diverses/mimetypen.htm
        mMimeTypeHash = new HashMap();
        mMimeTypeHash.put("html", "text/html");
        mMimeTypeHash.put("htm",  "text/html");
        mMimeTypeHash.put("gif",  "image/gif");
        mMimeTypeHash.put("jpg",  "image/jpeg");
        mMimeTypeHash.put("jpeg", "image/jpeg");
        mMimeTypeHash.put("png",  "image/png");
        mMimeTypeHash.put("js",   "text/javascript");
        mMimeTypeHash.put("txt",  "text/plain");
        mMimeTypeHash.put("pdf",  "application/pdf");
        mMimeTypeHash.put("xls",  "application/msexcel");
        mMimeTypeHash.put("doc",  "application/msword");
        mMimeTypeHash.put("ppt",  "application/mspowerpoint");
        mMimeTypeHash.put("rtf",  "text/rtf");
      }
      
      // Set the MIME type
      String filename = file.getName();
      int lastDot = filename.lastIndexOf('.');
      if (lastDot != -1) {
        String extension = filename.substring(lastDot + 1);
        String mimeType = (String) mMimeTypeHash.get(extension);
        if (mimeType != null) {
          response.setHeader("Content-Type", "mimeType/" + mimeType);
        }
      }
      
      // Send the file
      OutputStream out = null;
      FileInputStream in = null;
      try {
        out = response.getOutputStream();
        in = new FileInputStream(file);
        RegainToolkit.pipe(in, out);
      }
      catch (IOException exc) {
        throw new RegainException("Sending file failed: " + file.getAbsolutePath(), exc);
      }
      finally {
        if (in != null) {
          try { in.close(); } catch (IOException exc) {}
        }
        if (out != null) {
          try { out.close(); } catch (IOException exc) {}
        }
      }
    }
  }


  /**
   * Loads the configuration of the search mask.
   * <p>
   * If the configuration is already loaded, nothing is done.
   * 
   * @param request The page request. Used to get the "configFile" init
   *        parameter, which holds the name of the configuration file.
   * @throws RegainException If loading failed.
   */
  private static void loadConfiguration(PageRequest request)
    throws RegainException
  {
    if (mConfig == null) {
      String configFileName = request.getInitParameter("searchConfigFile");
      File configFile = new File(configFileName);
      try {
        mConfig = new XmlSearchConfig(configFile);
      }
      catch (RegainException exc) {
        throw new RegainException("Loading configuration file failed: "
            + configFile.getAbsolutePath(), exc);
      }
    }
  }
  
}
