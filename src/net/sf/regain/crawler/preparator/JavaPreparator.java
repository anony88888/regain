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
 *     $Date: 2008-03-16 20:50:37 +0100 (So, 16 Mär 2008) $
 *   $Author: thtesche $
 * $Revision: 281 $
 */
package net.sf.regain.crawler.preparator;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;
import net.sf.regain.RegainException;
import net.sf.regain.crawler.document.AbstractPreparator;
import net.sf.regain.crawler.document.RawDocument;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Prepares Java source code for indexing
 * <p>
 * The following information will be extracted:
 * class name, member names, comments (javadoc) 
 *
 * @author Thomas Tesche, cluster:Consult, http://www.thtesche.com/
 */
public class JavaPreparator extends AbstractPreparator {

  /**
   * Creates a new instance of JavaPreparator.
   *
   * @throws RegainException If creating the preparator failed.
   */
  public JavaPreparator() throws RegainException {
    super(new String[]{"text/java"});
  }

  /**
   * Prepares the document for indexing
   *
   * @param rawDocument the document
   *
   * @throws RegainException if preparation goes wrong
   */
  public void prepare(RawDocument rawDocument) throws RegainException {

    // Create new parsing with Java Language Specification 3
    ASTParser parser = ASTParser.newParser(AST.JLS3);
    CompilationUnit compileUnit = null;
    Vector<String> contentParts = new Vector<String>();
    Vector<String> titleParts = new Vector<String>();

    try {
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setSource(rawDocument.getContentAsString().toCharArray());
      // Compile the source code
      compileUnit = (CompilationUnit) parser.createAST(null);

      // Iterate over all type declarations
      ListIterator<TypeDeclaration> typesIter = compileUnit.types().listIterator();
      while (typesIter.hasNext()) {
        TypeDeclaration _class = typesIter.next();
        // Class/Interface name
        String class_interface = _class.isInterface() ? "Interface: " : "Class: ";
        titleParts.add(class_interface + _class.getName().getIdentifier());
        contentParts.add(class_interface + _class.getName().getIdentifier());
        // Superclass name
        SimpleType _superClass = (SimpleType) _class.getSuperclassType();
        if( _superClass!=null ){
          contentParts.add("Superclass: " + _superClass.getName().getFullyQualifiedName());
        }
        
        // Inner classes
        TypeDeclaration[] types = _class.getTypes();
        
        
      }

      setTitle(concatenateStringParts(titleParts, Integer.MAX_VALUE));
      contentParts.add(rawDocument.getContentAsString());
      setCleanedContent(concatenateStringParts(contentParts, Integer.MAX_VALUE));

    } catch (Exception ex) {
      throw new RegainException("Error parsing Java file: " + rawDocument.getUrl(), ex);
    }

  }

  public class JClass {

    String className = null;
    ArrayList methodDeclarations = new ArrayList();
    ArrayList innerClasses = new ArrayList();
    String superClass = null;
    ArrayList interfaces = new ArrayList();
  }

  public class JMethod {

    String methodName = null;
    ArrayList parameters = new ArrayList();
    String codeBlock = null;
    String returnType = null;
  }
}
