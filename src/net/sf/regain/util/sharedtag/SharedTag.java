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

import java.util.HashMap;

import net.sf.regain.RegainException;

/**
 * A tag that may be used within the taglib technology or the simpleweb
 * technology.
 *
 * @author Til Schneider, www.murfman.de
 */
public abstract class SharedTag {

  /** Specifies that the tag body should be evaluated. */
  public static final int EVAL_TAG_BODY = 1;

  /** Specifies that the tag body should be skipped. */
  public static final int SKIP_TAG_BODY = 2;

  /** The parameters for this tag. May be null. */
  private HashMap mParamMap;


  /**
   * Creates a new instance of SharedTag.
   */
  public SharedTag() {
  }
  
  
  /**
   * Gets the name of this Tag.
   * <p>
   * The name of the tag is extracted from the class name. In order to work,
   * subclasses must be named using the pattern [TagName]Tag
   * (e.g. <code>NavigationTag</code> for the tag <code>navigation</code>).
   * 
   * @return The name of this tag.
   */
  public String getTagName() {
    String className = getClass().getName();
    
    // Remove the package name
    int packageEnd = className.lastIndexOf('.');
    
    // Remove the Tag postfix
    int nameEnd = className.length();
    if (className.endsWith("Tag")) {
      nameEnd -= 3;
    }
    // NOTE: This even works, if packageEnd is -1
    String tagName = className.substring(packageEnd + 1, nameEnd).toLowerCase();
    
    int packageStart;
    while ((packageStart = className.lastIndexOf('.', packageEnd - 1)) != -1) {
      String packageName = className.substring(packageStart + 1, packageEnd);
      if (packageName.equals("sharedlib")) {
        break;
      } else {
        tagName = packageName + "_" + tagName;
        packageEnd = packageStart;
      }
    }
    
    return tagName;
  }


  /**
   * Sets a parameter.
   * 
   * @param name The parameter's name.
   * @param value The parameter's value.
   */
  public void setParameter(String name, String value) {
    if (mParamMap == null) {
      mParamMap = new HashMap();
    }
    mParamMap.put(name, value);
  }


  /**
   * Gets a parameter.
   * 
   * @param name The parameter's name.
   * @return The value of the parameter or <code>null</code> if the parameter
   *         was not set.
   */
  public String getParameter(String name) {
    if (mParamMap == null) {
      return null;
    } else {
      return (String) mParamMap.get(name);
    }
  }

  
  /**
   * Gets a parameter.
   * 
   * @param name The parameter's name.
   * @param defaultValue The value to return if the parameter was not set.
   * @return The value of the parameter or the default value if the parameter
   *         was not set.
   */
  public String getParameter(String name, String defaultValue) {
    String asString = getParameter(name);
    if (asString == null) {
      return defaultValue;
    } else {
      return asString;
    }
  }
  
  
  /**
   * Gets a parameter.
   * 
   * @param name The parameter's name.
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
      throw new RegainException("Parameter " + name + " of tag "
          + getTagName() + " was not specified");
    } else {
      return asString;
    }
  }
  
  
  /**
   * Gets a parameter and converts it to an int.
   *
   * @param name The name of the parameter.
   * @param defaultValue The value to return if the parameter is not set.
   * @throws RegainException When the parameter value is not a number.
   * @return The int value of the parameter.
   */
  public int getParameterAsInt(String name, int defaultValue)
    throws RegainException
  {
    String asString = getParameter(name);
    if (asString == null) {
      return defaultValue;
    } else {
      try {
        return Integer.parseInt(asString);
      }
      catch (NumberFormatException exc) {
        throw new RegainException("Parameter " + name + " of tag "
            + getTagName() + " must be a number: '" + asString + "'");
      }
    }
  }
  
  
  /**
   * Gets a parameter and converts it to a boolean.
   *
   * @param name The name of the parameter.
   * @param defaultValue The value to return if the parameter is not set.
   * @throws RegainException When the parameter value is not a number.
   * @return The int value of the parameter.
   */
  public boolean getParameterAsBoolean(String name, boolean defaultValue)
    throws RegainException
  {
    String asString = getParameter(name);
    if (asString == null) {
      return defaultValue;
    } else {
      if (asString.equalsIgnoreCase("true")) {
        return true;
      } else if (asString.equalsIgnoreCase("false")) {
        return false;
      } else {
        throw new RegainException("Parameter " + name + " of tag "
            + getTagName() + " must be a boolean: '" + asString + "'");
      }
    }
  }
  

  /**
   * Called when the parser reaches the start tag.
   *  
   * @param out The writer where to write the code.
   * @param request The page request.
   * @return {@link #EVAL_TAG_BODY} if you want the tag body to be evaluated or
   *         {@link #SKIP_TAG_BODY} if you want the tag body to be skipped.
   * @throws RegainException If there was an exception.
   */
  public int printStartTag(PageWriter out, PageRequest request)
    throws RegainException
  {
    return EVAL_TAG_BODY;
  }


  /**
   * Called after the body content was evaluated.
   *  
   * @param out The writer where to write the code.
   * @param request The page request.
   * @return {@link #EVAL_TAG_BODY} if you want the tag body to be evaluated
   *         once again or {@link #SKIP_TAG_BODY} if you want to print the
   *         end tag.
   * @throws RegainException If there was an exception.
   */
  public int printAfterBody(PageWriter out, PageRequest request)
    throws RegainException
  {
    return SKIP_TAG_BODY;
  }


  /**
   * Called when the parser reaches the end tag.
   *  
   * @param out The writer where to write the code.
   * @param request The page request.
   * @throws RegainException If there was an exception.
   */
  public void printEndTag(PageWriter out, PageRequest request)
    throws RegainException
  {
  }
  
  
  /**
   * Gets the String representation of this tag.
   * 
   * @return The String representation.
   */
  public String toString() {
    return getTagName();
  }

}
