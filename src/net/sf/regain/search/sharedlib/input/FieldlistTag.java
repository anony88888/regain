/*
 * CVS information:
 *  $RCSfile$
 *   $Source$
 *     $Date$
 *   $Author$
 * $Revision$
 */
package net.sf.regain.search.sharedlib.input;

import net.sf.regain.RegainException;
import net.sf.regain.search.IndexSearcherManager;
import net.sf.regain.search.SearchToolkit;
import net.sf.regain.search.config.IndexConfig;
import net.sf.regain.util.sharedtag.PageRequest;
import net.sf.regain.util.sharedtag.PageResponse;
import net.sf.regain.util.sharedtag.SharedTag;

/**
 * Generates a combobox list that shows all distinct values of a field in the
 * index.
 * <p>
 * Tag Parameters:
 * <ul>
 * <li><code>field</code>: The name of the field to created the list for.</li>
 * </ul>
 * 
 * @author Tilman Schneider, STZ-IDA an der FH Karlsruhe
 */
public class FieldlistTag extends SharedTag {

  /**
   * Called when the parser reaches the end tag.
   *  
   * @param request The page request.
   * @param response The page response.
   * @throws RegainException If there was an exception.
   */
  public void printEndTag(PageRequest request, PageResponse response)
    throws RegainException
  {
    String fieldName = getParameter("field", true);

    IndexConfig indexConfig = SearchToolkit.getIndexConfig(request);
    IndexSearcherManager manager = IndexSearcherManager.getInstance(indexConfig.getDirectory());
    
    String[] fieldValues = manager.getFieldValues(fieldName);
    
    response.print("<select name=\"field." + fieldName + "\" size=\"1\">");
    response.print("<option value=\"\">- alle -</option>");
    for (int i = 0; i < fieldValues.length; i++) {
      response.print("<option>" + fieldValues[i] + "</option>");
    }
    response.print("</select");
  }

}
