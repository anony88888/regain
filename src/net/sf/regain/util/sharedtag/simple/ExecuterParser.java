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
package net.sf.regain.util.sharedtag.simple;

import net.sf.regain.RegainException;
import net.sf.regain.util.sharedtag.SharedTag;

import org.apache.regexp.RE;

/**
 * Parses JSP code and creates an Executer tree.
 *
 * @see Executer
 * @author Til Schneider, www.murfman.de
 */
public class ExecuterParser {
  
  /** The regex that matches a JSP tag. */
  private static RE mJspTagRegex;
  
  /** The regex that matches a parameter. */
  private static RE mParamRegex;
  
  
  /**
   * Creates a new instance of ExecuterParser.
   */
  public ExecuterParser() {
    if (mJspTagRegex == null) {
      mJspTagRegex = new RE("<([\\w/]*):(\\w*)(([^>\"]*\"[^\"]*\")*\\w*/?)>");
      mParamRegex = new RE("(\\w+)\\s*=\\s*\"([^\"]*)\"");
    }
  }
  

  /**
   * Parses the JSP code.
   * 
   * @param jspCode The JSP code to parse.
   * @return An Executer tree that can execute the JSP page.
   * @throws RegainException If parsing failed.
   */
  public synchronized Executer parse(String jspCode) throws RegainException {
    // Get the position where the real content starts
    int startPos = jspCode.indexOf("<html>");
    if (startPos == -1) {
      startPos = 0;
    }

    // Parse the content
    Executer executer = new TextExecuter("");
    int pos = parse(executer, jspCode, startPos);
    if (pos < jspCode.length()) {
      throw new RegainException("Last taglib tag is not closed! (offset: " + pos + ")");
    }
    
    return executer;
  }


  /**
   * Parses the content of an executer.
   * 
   * @param parent The executer where to add the extracted child executers.
   * @param jspCode The JSP code to parse.
   * @param pos The position where to start parsing.
   * @return The position where parsing stopped.
   * @throws RegainException If parsing failed.
   */
  private int parse(Executer parent, String jspCode, int pos)
    throws RegainException
  {
    String startNamespace = null;
    String startTagName = null;
    while (mJspTagRegex.match(jspCode, pos)) {
      // System.out.println("Pattern matches");
      
      // Add the text since the last pos
      addText(parent, jspCode, pos, mJspTagRegex.getParenStart(0));

      // There is a tag -> Extract the interesting values
      String namespace = mJspTagRegex.getParen(1);
      String tagName = mJspTagRegex.getParen(2);
      String params = mJspTagRegex.getParen(3);
      int tagEndPos = mJspTagRegex.getParenEnd(0);
      
      // Check whether this is a end tag
      if (! namespace.startsWith("/")) {
        // It's a start tag
        // System.out.println("Tag starts " + namespace + ":" + tagName);
        
        // Create the shared tag
        SharedTag tag = createSharedTag(namespace, tagName, params);
        SharedTagExecuter child = new SharedTagExecuter(tag);
        parent.addChildExecuter(child);
        
        // Check whether the start tag is closed immediately
        if (params.endsWith("/")) {
          // The start tag is closed immediately -> Set the pos to the end of the tag
          pos = tagEndPos;
        } else {
          // The start tag is not closed immediately -> Remember it
          startNamespace = namespace;
          startTagName = tagName;

          // Parse the tag content
          pos = parse(child, jspCode, tagEndPos);
        }
      } else {
        // It's a end tag
        if (startNamespace == null) {
          // System.out.println("End of parent tag");

          // There was no start tag -> It must be the end tag of the parent
          return mJspTagRegex.getParenStart(0);
        } else {
          // System.out.println("End tag " + namespace + ":" + tagName);
          
          // The current tag is finished -> Check namespace and tag name
          namespace = namespace.substring(1); // Remove the leading /
          if (! namespace.equals(startNamespace) || ! tagName.equals(startTagName)) {
            throw new RegainException("End tag " + namespace + ":" + tagName
                + " does not match to start tag " + startNamespace + ":" + startTagName);
          }

          startNamespace = null;
          startTagName = null;
          
          pos = tagEndPos;
        }
      }
      // System.out.println("Next pos " + pos);
    }

    // System.out.println("Adding trailing text");

    // There is no taglib tag any more
    // -> Add the text since the last pos to the end
    addText(parent, jspCode, pos, jspCode.length());
    return jspCode.length();
  }


  /**
   * Adds a text executer to another executer.
   * 
   * @param parent The executer where to add the TextExecuter.
   * @param jspCode The JSP code from where to extract the text.
   * @param startPos The start position of the text.
   * @param endPos The end position of the text.
   */
  private void addText(Executer parent, String jspCode, int startPos,
    int endPos)
  {
    if (startPos < endPos - 1) {
      String text = jspCode.substring(startPos, endPos);
      parent.addChildExecuter(new TextExecuter(text));
    }
  }


  /**
   * Creates a SharedTag.
   * 
   * @param namespace The namespace of the tag.
   * @param tagName The tag's name.
   * @param params The tag's parameters
   * @return The created tag.
   * @throws RegainException If creating the tag failed.
   */
  private SharedTag createSharedTag(String namespace, String tagName,
    String params)
    throws RegainException
  {
    String className = "net.sf.regain.search.sharedlib";
    
    // Add the packages (E.g. stats_size)
    int linePos;
    String cutTagName = tagName;
    while ((linePos = cutTagName.indexOf('_')) != -1) {
      className += "." + cutTagName.substring(0, linePos);
      cutTagName = cutTagName.substring(linePos + 1);
    }
    
    // Add the className
    className += "." + Character.toUpperCase(cutTagName.charAt(0))
      + cutTagName.substring(1) + "Tag";
    
    // Get the tag class
    Class tagClass;
    try {
      tagClass = Class.forName(className);
    }
    catch (ClassNotFoundException exc) {
      throw new RegainException("Class for tag " + namespace + ":" + tagName
          + " not found: " + className, exc);
    }

    // Create the tag instance
    SharedTag tag;
    try {
      tag = (SharedTag) tagClass.newInstance();
    }
    catch (Exception exc) {
      throw new RegainException("Creating tag instance for tag " + namespace
          + ":" + tagName + " could not be created: " + className, exc);
    }
    
    // Set the params
    int pos = 0;
    while (mParamRegex.match(params, pos)) {
      String name = mParamRegex.getParen(1);
      String value = mParamRegex.getParen(2);
      tag.setParameter(name, value);
      
      System.out.println("Param for " + tag + " '" + name + "'='" + value + "'");
      
      pos = mParamRegex.getParenEnd(0);
    }
    
    return tag;
  }
  
}
