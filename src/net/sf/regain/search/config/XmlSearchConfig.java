/*
 * CVS information:
 *  $RCSfile$
 *   $Source$
 *     $Date$
 *   $Author$
 * $Revision$
 */
package net.sf.regain.search.config;

import java.io.File;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sf.regain.RegainException;
import net.sf.regain.XmlToolkit;

/**
 * 
 * @author Tilman Schneider, STZ-IDA an der FH Karlsruhe
 */
public class XmlSearchConfig implements SearchConfig {

  /** All configured indexes. */
  private HashMap mIndexHash;
  

  /**
   * Creates a new instance of XmlSearchConfig.
   * 
   * @param xmlFile The XML file to read the config from.
   * @throws RegainException If reading the config failed.
   */
  public XmlSearchConfig(File xmlFile) throws RegainException {
    Document doc = XmlToolkit.loadXmlDocument(xmlFile);
    Element config = doc.getDocumentElement();

    readIndexList(config);
  }

  
  /**
   * Reads the search indexes from the config.
   *
   * @param config The configuration to read from.
   * @throws RegainException If the configration has errors.
   */
  private void readIndexList(Element config) throws RegainException {
    Node node;
    
    Node listNode = XmlToolkit.getChild(config, "indexList");
    
    // Get the node that holds the default settings for all indexes
    Node defaultNode = XmlToolkit.getChild(listNode, "defaultSettings", false);
    
    // Get the index nodes
    mIndexHash = new HashMap();
    Node[] nodeArr = XmlToolkit.getChildArr(listNode, "index");
    for (int i = 0; i < nodeArr.length; i++) {
      String name = XmlToolkit.getAttribute(nodeArr[i], "name");
      String directory = XmlToolkit.getChildText(nodeArr[i], "dir");

      node = XmlToolkit.getCascadedChild(nodeArr[i], defaultNode, "openInNewWindowRegex");
      String openInNewWindowRegex = XmlToolkit.getText(node);

      node = XmlToolkit.getCascadedChild(nodeArr[i], defaultNode, "searchFieldList", false);
      String[] searchFieldList = null;
      if (node != null) {
        searchFieldList = XmlToolkit.getTextAsWordList(node, true);
      }

      node = XmlToolkit.getCascadedChild(nodeArr[i], defaultNode, "rewriteRules", false);
      String[][] rewriteRules = readRewriteRules(node);
      
      IndexConfig indexConfig = new IndexConfig(name, directory,
          openInNewWindowRegex, searchFieldList, rewriteRules);
      mIndexHash.put(name, indexConfig);
    }
  }
  
  
  /**
   * Reads the URL rewrite rules from a node
   * 
   * @param node The node to read from.
   * @return The rewrite rules. May be null.
   * @throws RegainException If the configration has errors.
   */
  private String[][] readRewriteRules(Node node)
    throws RegainException
  {
    if (node == null) {
      return null;
    }
    
    Node[] ruleNodeArr = XmlToolkit.getChildArr(node, "rule");
    String[][] rewriteRules = new String[ruleNodeArr.length][];
    for (int i = 0; i < ruleNodeArr.length; i++) {
      String prefix = XmlToolkit.getAttribute(ruleNodeArr[i], "prefix");
      String replacement = XmlToolkit.getAttribute(ruleNodeArr[i], "replacement");
      
      // Add this rule
      rewriteRules[i] = new String[] { prefix, replacement };
    }
    
    return rewriteRules;
  }
  
  
  /**
   * Gets the configuration for an index.
   * 
   * @param indexName The name of the index to get the config for.
   * @return The configuration for the wanted index or <code>null</code> if
   *         there is no such index configured.
   */
  public IndexConfig getIndexConfig(String indexName) {
    return (IndexConfig) mIndexHash.get(indexName);
  }
  
}
