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
 * <li><code>allMsg</code>: The message to show for the item that ignores this
 *     field.</li>
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
    String allMsg = getParameter("allMsg", true);

    // Get the IndexConfig
    IndexConfig[] configArr = SearchToolkit.getIndexConfigArr(request);
    if (configArr.length > 1) {
      throw new RegainException("The fieldlist tag can only be used for one index!");
    }
    IndexConfig config = configArr[0];
    
    IndexSearcherManager manager = IndexSearcherManager.getInstance(config.getDirectory());
    
    String[] fieldValues = manager.getFieldValues(fieldName);
    
    response.print("<select name=\"field." + fieldName + "\" size=\"1\">");
    response.print("<option value=\"\">" + allMsg + "</option>");
    for (int i = 0; i < fieldValues.length; i++) {
      response.print("<option>" + fieldValues[i] + "</option>");
    }
    response.print("</select>");
  }

}
