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
package net.sf.regain.util.sharedtag.taglib;

import java.util.Enumeration;

import javax.servlet.jsp.PageContext;

import net.sf.regain.RegainException;
import net.sf.regain.util.sharedtag.PageRequest;

/**
 * Adapter from a JSP page context to a SharedTag PageRequest.
 *
 * @author Til Schneider, www.murfman.de
 */
public class JspPageRequest extends PageRequest {
  
  /** The JSP page context to adapt. */
  private PageContext mPageContext;
  

  /**
   * Creates a new instance of JspPageRequest.
   * 
   * @param pageContext The JSP page context to adapt.
   */
  public JspPageRequest(PageContext pageContext) {
    mPageContext = pageContext;
  }

  
  /**
   * Gets a request parameter that was given to page via GET or POST.
   * 
   * @param name The name of the parameter.
   * @return The given parameter or <code>null</code> if no such parameter was
   *         given.
   */
  public String getParameter(String name) {
    return mPageContext.getRequest().getParameter(name);
  }


  /**
   * Gets all request parameters with the given name that were given to the page
   * via GET or POST.
   * 
   * @param name The name of the parameter.
   * @return The parameters or <code>null</code> if no such parameter was
   *         given.
   * @throws RegainException If getting the parameter failed.
   */
  public String[] getParameters(String name) throws RegainException {
    return mPageContext.getRequest().getParameterValues(name);
  }


  /**
   * Gets the names of the given parameters.
   * 
   * @return The names of the given parameters.
   */
  public Enumeration getParameterNames() {
    return mPageContext.getRequest().getParameterNames();
  }


  /**
   * Sets an attribute at the page context.
   * 
   * @param name The name of the attribute to set.
   * @param value The value of the attribute to set.
   */
  public void setContextAttribute(String name, Object value) {
    mPageContext.setAttribute(name, value);
  }


  /**
   * Gets an attribute from the page context.
   * 
   * @param name The name of the attribute to get.
   * @return The attribute's value or <code>null</code> if there is no such
   *         attribute.
   */
  public Object getContextAttribute(String name) {
    return mPageContext.getAttribute(name);
  }


  /**
   * Gets an init parameter.
   * 
   * @param name The name of the init parameter.
   * @return The value of the init parameter.
   */
  public String getInitParameter(String name) {
    return mPageContext.getServletContext().getInitParameter(name);
  }
  
}
