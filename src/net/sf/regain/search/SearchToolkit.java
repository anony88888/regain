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
import java.util.Enumeration;

import net.sf.regain.RegainException;
import net.sf.regain.search.config.IndexConfig;
import net.sf.regain.search.config.SearchConfig;
import net.sf.regain.search.config.XmlSearchConfig;
import net.sf.regain.util.sharedtag.PageRequest;

/**
 * A toolkit for the search JSPs containing helper methods.
 *
 * @author Til Schneider, www.murfman.de
 */
public class SearchToolkit {

  /** The name of the page context attribute that holds the SearchContext. */
  private static final String SEARCH_CONTEXT_ATTR_NAME = "SearchContext";

  /** The prefix for request parameters that contain additional field values. */
  private static final String FIELD_PREFIX = "field.";
  
  /** The configuration of the search mask. */
  private static SearchConfig mConfig;



  /**
   * Gets the SearchContext from the PageContext.
   * <p>
   * If there is no SearchContext in the PageContext it is created and put in the
   * PageContext, so the next call will find it.
   *
   * @param request The page request where the SearchContext will be taken
   *        from or put to.
   * @return The SearchContext for the page the context is for.
   *
   * @throws RegainException When the SearchContext could not be created.
   * @see SearchContext
   */
  public static SearchContext getSearchContext(PageRequest request)
    throws RegainException
  {
    SearchContext context = (SearchContext) request.getContextAttribute(SEARCH_CONTEXT_ATTR_NAME);
    if (context == null) {
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
      IndexConfig indexConfig = mConfig.getIndexConfig(indexName);
      if (indexConfig == null) {
        throw new RegainException("The configuration does not contain the index '"
            + indexName + "'");
      }

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
      String configFileName = request.getInitParameter("configFile");
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
