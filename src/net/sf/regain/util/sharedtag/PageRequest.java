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
package net.sf.regain.util.sharedtag;

import java.util.Enumeration;

import net.sf.regain.RegainException;

/**
 * A page request.
 *
 * @author Til Schneider, www.murfman.de
 */
public abstract class PageRequest {

  /**
   * Gets a request parameter that was given to page via GET or POST.
   * 
   * @param name The name of the parameter.
   * @return The given parameter or <code>null</code> if no such parameter was
   *         given.
   * @throws RegainException If getting the parameter failed.
   */
  public abstract String getParameter(String name) throws RegainException;

  
  /**
   * Gets a request parameter that was given to page via GET or POST.
   * 
   * @param name The name of the parameter.
   * @param mandatory Specifies whether the parameter is mandatory.
   * @return The parameter value or <code>null</code> if no such parameter was
   *         given and mandatory is <code>false</code>.
   * @throws RegainException If mandatory is <code>true</code> and the parameter
   *         was not specified.
   */
  public String getParameter(String name, boolean mandatory)
    throws RegainException
  {
    String asString = getParameter(name);
    if (mandatory && (asString == null)) {
      throw new RegainException("Page parameter '" + name + "' was not specified");
    } else {
      return asString;
    }
  }


  /**
   * Gets a request parameter and converts it to an int.
   *
   * @param name The name of the parameter.
   * @param defaultValue The value to return if the parameter is not set.
   * @throws RegainException When the parameter value is not a number.
   * @return The int value of the parameter.
   */
  public int getParameterAsInt(String name, int defaultValue) throws RegainException {
    String asString = getParameter(name);
    if (asString == null) {
      return defaultValue;
    } else {
      try {
        return Integer.parseInt(asString);
      }
      catch (NumberFormatException exc) {
        throw new RegainException("Parameter '" + name + "' must be a number: "
            + asString);
      }
    }
  }
  
  
  /**
   * Gets the names of the given parameters.
   * 
   * @return The names of the given parameters.
   * @throws RegainException If getting the parameter names failed.
   */
  public abstract Enumeration getParameterNames() throws RegainException;


  /**
   * Sets an attribute at the page context.
   * 
   * @param name The name of the attribute to set.
   * @param value The value of the attribute to set.
   */
  public abstract void setContextAttribute(String name, Object value);

  /**
   * Gets an attribute from the page context.
   * 
   * @param name The name of the attribute to get.
   * @return The attribute's value or <code>null</code> if there is no such
   *         attribute.
   */
  public abstract Object getContextAttribute(String name);
  
  /**
   * Gets an init parameter.
   * 
   * @param name The name of the init parameter.
   * @return The value of the init parameter.
   */
  public abstract String getInitParameter(String name);
  
}
