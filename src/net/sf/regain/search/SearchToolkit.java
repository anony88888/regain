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

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import net.sf.regain.RegainException;
import net.sf.regain.search.config.IndexConfig;
import net.sf.regain.search.config.SearchConfig;
import net.sf.regain.search.config.XmlSearchConfig;

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
   * @param pageContext The page context where the SearchContext will be taken
   *        from or put to.
   * @return The SearchContext for the page the context is for.
   *
   * @throws RegainException When the SearchContext could not be created.
   * @see SearchContext
   */
  public static SearchContext getSearchContextFromPageContext(PageContext pageContext)
    throws RegainException
  {
    SearchContext context = (SearchContext) pageContext.getAttribute(SEARCH_CONTEXT_ATTR_NAME);
    if (context == null) {
      // Load the config (if not yet done)
      loadConfiguration(pageContext.getServletContext());

      // Get the name of the index
      String indexName = pageContext.getRequest().getParameter("index");
      if (indexName == null) {
        throw new RegainException("Request parameter 'index' not specified");
      }
      
      // Get the configuration for that index
      IndexConfig indexConfig = mConfig.getIndexConfig(indexName);
      if (indexConfig == null) {
        throw new RegainException("The configuration does not contain the index '"
            + indexName + "'");
      }

      // Get the query
      ServletRequest request = pageContext.getRequest(); 
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
      pageContext.setAttribute(SEARCH_CONTEXT_ATTR_NAME, context);
    }

    return context;
  }


  /**
   * Gets a request parameter and converts it to an int.
   *
   * @param request The request to read the parameter from.
   * @param paramName The name of the parameter
   * @param defaultValue The value to return if the parameter is not set.
   * @throws ExtendedJspException When the parameter value is not a number.
   * @return The int value of the parameter.
   */
  public static int getIntParameter(ServletRequest request, String paramName,
    int defaultValue) throws ExtendedJspException
  {
    String asString = request.getParameter(paramName);
    if (asString == null) {
      return defaultValue;
    } else {
      try {
        return Integer.parseInt(asString);
      }
      catch (NumberFormatException exc) {
        throw new ExtendedJspException("Parameter '" + paramName
                                       + "' must be a number: " + asString);
      }
    }
  }

  
  /**
   * Loads the configuration of the search mask.
   * <p>
   * If the configuration is already loaded, nothing is done.
   * 
   * @param context The servlet context. Used to get the "configFile" init
   *        parameter, which holds the name of the configuration file.
   * @throws RegainException If loading failed.
   */
  private static void loadConfiguration(ServletContext context)
    throws RegainException
  {
    if (mConfig == null) {
      String configFileName = context.getInitParameter("configFile");
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
