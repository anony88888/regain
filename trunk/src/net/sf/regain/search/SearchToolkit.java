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
import net.sf.regain.search.access.SearchAccessController;
import net.sf.regain.search.config.DefaultSearchConfigFactory;
import net.sf.regain.search.config.IndexConfig;
import net.sf.regain.search.config.SearchConfig;
import net.sf.regain.search.config.SearchConfigFactory;
import net.sf.regain.search.results.MultipleSearchResults;
import net.sf.regain.search.results.SearchResults;
import net.sf.regain.search.results.SingleSearchResults;
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

  /** The name of the page context attribute that holds the search query. */
  private static final String SEARCH_QUERY_CONTEXT_ATTR_NAME = "SearchQuery";
  
  /** The name of the page context attribute that holds the SearchResults. */
  private static final String SEARCH_RESULTS_ATTR_NAME = "SearchResults";

  /** The name of the page context attribute that holds the IndexConfig array. */
  private static final String INDEX_CONFIG_CONTEXT_ARRAY_ATTR_NAME = "IndexConfigArr";
  
  /** The prefix for request parameters that contain additional field values. */
  private static final String FIELD_PREFIX = "field.";
  
  /** The configuration of the search mask. */
  private static SearchConfig mConfig;
  
  /** Holds for an extension the mime type. */
  private static HashMap mMimeTypeHash;

  
  /**
   * Gets the IndexConfig array from the PageContext. It contains the
   * configurations of all indexes the search query is searching on.
   * <p>
   * If there is no IndexConfig array in the PageContext it is put in the
   * PageContext, so the next call will find it.
   * 
   * @param request The page request where the IndexConfig array will be taken
   *        from or put to.
   * @return The IndexConfig array for the page the context is for.
   * @throws RegainException If there is no IndexConfig for the specified index.
   */
  public static IndexConfig[] getIndexConfigArr(PageRequest request)
    throws RegainException
  {
    IndexConfig[] configArr = (IndexConfig[]) request.getContextAttribute(INDEX_CONFIG_CONTEXT_ARRAY_ATTR_NAME);
    if (configArr == null) {
      // Load the config (if not yet done)
      loadConfiguration(request);

      // Get the names of the indexes
      String[] indexNameArr = request.getParameters("index");
      if (indexNameArr == null) {
        // There was no index specified -> Check whether we have default indexes
        // defined
        indexNameArr = mConfig.getDefaultIndexNameArr();
        if (indexNameArr == null) {
          throw new RegainException("Request parameter 'index' not specified and " +
              "no default index configured");
        }
      }
      
      // Get the configurations for these indexes
      configArr = new IndexConfig[indexNameArr.length];
      for (int i = 0; i < indexNameArr.length; i++) {
        configArr[i] = mConfig.getIndexConfig(indexNameArr[i]);
        if (configArr[i] == null) {
          throw new RegainException("The configuration does not contain the index '"
              + indexNameArr[i] + "'");
        }
      }

      // Store the IndexConfig in the page context
      request.setContextAttribute(INDEX_CONFIG_CONTEXT_ARRAY_ATTR_NAME, configArr);
    }
    return configArr;
  }
  
  
  /**
   * Gets the search query.
   * 
   * @param request The request to get the query from.
   * @return The search query.
   * @throws RegainException If getting the query failed.
   */
  public static String getSearchQuery(PageRequest request)
    throws RegainException
  {
    String queryString = (String) request.getContextAttribute(SEARCH_QUERY_CONTEXT_ATTR_NAME);
    if (queryString == null) {
      // Get the query parameter
      StringBuffer query = new StringBuffer();
      String[] queryParamArr = request.getParametersNotNull("query");
      for (int i = 0; i < queryParamArr.length; i++) {
        if (i != 0) {
          query.append(" ");
        }
        query.append(queryParamArr[i]);
      }
      
      // Append the additional fields to the query
      Enumeration enm = request.getParameterNames();
      while (enm.hasMoreElements()) {
        String paramName = (String) enm.nextElement();
        if (paramName.startsWith(FIELD_PREFIX)) {
          // This is an additional field -> Append it to the query
          String fieldName = paramName.substring(FIELD_PREFIX.length());
          String fieldValue = request.getParameter(paramName);
          
          if (fieldValue != null) {
            fieldValue = fieldValue.trim();
            if (fieldValue.length() != 0) {
              query.append(" " + fieldName + ":\"" + fieldValue + "\"");
            }
          }
        }
      }
      
      queryString = query.toString().trim();
      request.setContextAttribute(SEARCH_QUERY_CONTEXT_ATTR_NAME, queryString);
    }
    
    return queryString;
  }
  

  /**
   * Gets the SearchResults from the PageContext.
   * <p>
   * If there is no SearchResults in the PageContext it is created and put in the
   * PageContext, so the next call will find it.
   *
   * @param request The page request where the SearchResults will be taken
   *        from or put to.
   * @return The SearchResults for the page the context is for.
   * @throws RegainException If the SearchResults could not be created.
   * @see SearchResults
   */
  public static SearchResults getSearchResults(PageRequest request)
    throws RegainException
  {
    SearchResults results = (SearchResults) request.getContextAttribute(SEARCH_RESULTS_ATTR_NAME);
    if (results == null) {
      // Get the index configurations
      IndexConfig[] indexConfigArr = getIndexConfigArr(request);

      if (indexConfigArr.length == 1) {
        results = createSingleSearchResults(indexConfigArr[0], request);
      } else {
        SingleSearchResults[] childResultsArr = new SingleSearchResults[indexConfigArr.length];
        for (int i = 0; i < childResultsArr.length; i++) {
          childResultsArr[i] = createSingleSearchResults(indexConfigArr[i], request);
        }
        results = new MultipleSearchResults(childResultsArr);
      }

      // Store the SearchResults in the page context
      request.setContextAttribute(SEARCH_RESULTS_ATTR_NAME, results);
    }

    return results;
  }


  /**
   * Gets the SingleSearchResults from one index.
   * 
   * @param indexConfig The config of the index to search in.
   * @param request The request that initiated the search.
   * @return The SingleSearchResults for the index. 
   * @throws RegainException If searching failed.
   */
  private static SingleSearchResults createSingleSearchResults(
    IndexConfig indexConfig, PageRequest request)
    throws RegainException
  {
    // Get the query
    String query = getSearchQuery(request);

    // Get the groups the current user has reading rights for
    String[] groupArr = null;
    SearchAccessController accessController = indexConfig.getSearchAccessController();
    if (accessController != null) {
      groupArr = accessController.getUserGroups(request);
      
      // Check the Group array
      RegainToolkit.checkGroupArray(accessController, groupArr);
    }

    return new SingleSearchResults(indexConfig, query, groupArr);
  }


  /**
   * Extracts the file URL from a request path.
   * 
   * @param requestPath The request path to extract the file URL from.
   * @param encoding The encoding to use for the URL-docoding of the requestPath.
   * @return The extracted file URL.
   * @throws RegainException If extracting the file URL failed.
   * 
   * @see net.sf.regain.search.sharedlib.hit.LinkTag
   */
  public static String extractFileUrl(String requestPath, String encoding)
    throws RegainException
  {
    // FIXME: Filenames with "+" or "%C3" still don't work

    // NOTE: This is the counterpart to net.sf.regain.search.sharedlib.hit.LinkTag 

    // NOTE: Removing index GET Parameter not nessesary: We already have the requestPath
    // NOTE: Encoding "/" to "%2F" not nessesary: We would encode it in the next step anyway

    // The servlet engine has already decoded "%xx" with page encoding
    // But spaces are still "+"
    // -> Decode the spaces
    String href = RegainToolkit.replace(requestPath, "+", " ");

    // Cut off "http://domain/file/"
    int filePos = href.indexOf("file/");
    String filename = href.substring(filePos + 5);

    // Restore the double slashes
    filename = RegainToolkit.replace(filename, "\\", "/");

    // Assemble the file URL
    return RegainToolkit.fileNameToUrl(filename); 
  }


  /**
   * Decides whether the remote access to a file should be allowed.
   * <p>
   * The access is granted if the file is in the index. The access is never
   * granted for indexes that have an access controller.
   * 
   * @param request The request that holds the used index.
   * @param fileUrl The URL to file to check.
   * @return Whether the remote access to a file should be allowed.
   * @throws RegainException If checking the file failed.
   */
  public static boolean allowFileAccess(PageRequest request, String fileUrl)
    throws RegainException
  {
    IndexConfig[] configArr = getIndexConfigArr(request);
    
    // Check whether one of the indexes contains the file
    for (int i = 0; i < configArr.length; i++) {
      // NOTE: We only allow the file access if there is no access controller
      if (configArr[i].getSearchAccessController() == null) {
        String dir = configArr[i].getDirectory();
        IndexSearcherManager manager = IndexSearcherManager.getInstance(dir);
        
        // Check whether the document is in the index
        Term urlTerm = new Term("url", fileUrl);
        Query query = new TermQuery(urlTerm);
        Hits hits = manager.search(query);
        
        // Allow the access if we found the file in the index
        if (hits.length() > 0) {
          return true;
        }
      }
    }
    
    // We didn't find the file in the indexes -> File access is not allowed
    return false;
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
        
        // Source: http://framework.openoffice.org/documentation/mimetypes/mimetypes.html
        mMimeTypeHash.put("sds",  "application/vnd.stardivision.chart");
        mMimeTypeHash.put("sdc",  "application/vnd.stardivision.calc");
        mMimeTypeHash.put("sdw",  "application/vnd.stardivision.writer");
        mMimeTypeHash.put("sgl",  "application/vnd.stardivision.writer-global");
        mMimeTypeHash.put("sda",  "application/vnd.stardivision.draw");
        mMimeTypeHash.put("sdd",  "application/vnd.stardivision.impress");
        mMimeTypeHash.put("sdf",  "application/vnd.stardivision.math");
        mMimeTypeHash.put("sxw",  "application/vnd.sun.xml.writer");
        mMimeTypeHash.put("stw",  "application/vnd.sun.xml.writer.template");
        mMimeTypeHash.put("sxg",  "application/vnd.sun.xml.writer.global");
        mMimeTypeHash.put("sxc",  "application/vnd.sun.xml.calc");
        mMimeTypeHash.put("stc",  "application/vnd.sun.xml.calc.template");
        mMimeTypeHash.put("sxi",  "application/vnd.sun.xml.impress");
        mMimeTypeHash.put("sti",  "application/vnd.sun.xml.impress.template");
        mMimeTypeHash.put("sxd",  "application/vnd.sun.xml.draw");
        mMimeTypeHash.put("std",  "application/vnd.sun.xml.draw.template");
        mMimeTypeHash.put("sxm",  "application/vnd.sun.xml.math");
        mMimeTypeHash.put("odt",  "application/vnd.oasis.opendocument.text");
        mMimeTypeHash.put("ott",  "application/vnd.oasis.opendocument.text-template");
        mMimeTypeHash.put("oth",  "application/vnd.oasis.opendocument.text-web");
        mMimeTypeHash.put("odm",  "application/vnd.oasis.opendocument.text-master");
        mMimeTypeHash.put("odg",  "application/vnd.oasis.opendocument.graphics");
        mMimeTypeHash.put("otg",  "application/vnd.oasis.opendocument.graphics-template");
        mMimeTypeHash.put("odp",  "application/vnd.oasis.opendocument.presentation");
        mMimeTypeHash.put("otp",  "application/vnd.oasis.opendocument.presentation-template");
        mMimeTypeHash.put("ods",  "application/vnd.oasis.opendocument.spreadsheet");
        mMimeTypeHash.put("ots",  "application/vnd.oasis.opendocument.spreadsheet-template");
        mMimeTypeHash.put("odc",  "application/vnd.oasis.opendocument.chart");
        mMimeTypeHash.put("odf",  "application/vnd.oasis.opendocument.formula");
        mMimeTypeHash.put("odb",  "application/vnd.oasis.opendocument.database");
        mMimeTypeHash.put("odi",  "application/vnd.oasis.opendocument.image");
      }
      
      // Set the MIME type
      String filename = file.getName();
      int lastDot = filename.lastIndexOf('.');
      if (lastDot != -1) {
        String extension = filename.substring(lastDot + 1);
        String mimeType = (String) mMimeTypeHash.get(extension);
        if (mimeType != null) {
          response.setHeader("Content-Type", mimeType);
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
      // Create the factory
      String factoryClassname = request.getInitParameter("searchConfigFactoryClass");
      String factoryJarfile   = request.getInitParameter("searchConfigFactoryJar");
      if (factoryClassname == null) {
        factoryClassname = DefaultSearchConfigFactory.class.getName();
      }
      SearchConfigFactory factory = (SearchConfigFactory)
        RegainToolkit.createClassInstance(factoryClassname, SearchConfigFactory.class, factoryJarfile);
      
      // Create the config
      mConfig = factory.createSearchConfig(request);
    }
  }
  
}
