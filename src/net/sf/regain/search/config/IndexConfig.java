/*
 * CVS information:
 *  $RCSfile$
 *   $Source$
 *     $Date$
 *   $Author$
 * $Revision$
 */
package net.sf.regain.search.config;

/**
 * The configuration for one index.
 * 
 * @author Tilman Schneider, STZ-IDA an der FH Karlsruhe
 */
public class IndexConfig {

  /** Default list of index fields to search in. */
  protected static final String[] DEFAULT_SEARCH_FIELD_LIST
    = { "content", "title", "headlines" };
  
  /** The name of the index. */
  private String mName;
  
  /** The directory where the index is located. */
  private String mDirectory;
  
  /**
   * The regular expression that identifies URLs that should be opened in a new
   * window.
   */
  private String mOpenInNewWindowRegex;
  
  /**
   * The index fields to search by default.
   * <p>
   * NOTE: The user may search in other fields also using the "field:"-operator.
   * Read the
   * <a href="http://jakarta.apache.org/lucene/docs/queryparsersyntax.html">lucene query syntax</a>
   * for details.
   */
  private String[] mSearchFieldList;
  
  
  /**
   * Creates a new instance of IndexConfig.
   * 
   * @param name The name of the index.
   * @param directory The directory where the index is located.
   * @param openInNewWindowRegex The regular expression that identifies URLs
   *        that should be opened in a new window.
   * @param searchFieldList The index fields to search by default.
   */
  public IndexConfig(String name, String directory, String openInNewWindowRegex,
    String[] searchFieldList)
  {
    mName = name;
    mDirectory = directory;
    mOpenInNewWindowRegex = openInNewWindowRegex;
    mSearchFieldList = searchFieldList;
  }
  
  
  /**
   * Gets the name of the index.
   * 
   * @return The name of the index.
   */
  public String getName() {
    return mName;
  }

  
  /**
   * Gets the directory where the index is located.
   * 
   * @return The directory where the index is located.
   */
  public String getDirectory() {
    return mDirectory;
  }
  
  
  /**
   * Gets the regular expression that identifies URLs that should be opened in
   * a new window.
   * 
   * @return The regular expression that identifies URLs that should be opened
   *         in a new window.
   */
  public String getOpenInNewWindowRegex() {
    return mOpenInNewWindowRegex;
  }
  
  
  /**
   * Gets the index fields to search by default.
   * <p>
   * NOTE: The user may search in other fields also using the "field:"-operator.
   * Read the
   * <a href="http://jakarta.apache.org/lucene/docs/queryparsersyntax.html">lucene query syntax</a>
   * for details.
   * 
   * @return The index fields to search by default.
   */
  public String[] getSearchFieldList() {
    return mSearchFieldList;
  }
  
}
