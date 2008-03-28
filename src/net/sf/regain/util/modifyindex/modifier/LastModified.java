package net.sf.regain.util.modifyindex.modifier;

import java.util.Date;
import java.util.Map;

import net.sf.regain.RegainToolkit;
import net.sf.regain.util.modifyindex.ModifyDocumentField;

import org.apache.log4j.Logger;

public class LastModified implements FieldModifier {
	/** The logger for this class */
	private static Logger logger = Logger.getLogger(LastModified.class);
	private String modifyField = "last-modified";
	public Map modify(Map docFields) {
		try {
			if(logger.isDebugEnabled()){
				logger.debug("Modify field: "+modifyField);
			}
			ModifyDocumentField mdf = (ModifyDocumentField)docFields.get(modifyField);
			if(null != mdf){
				String lastModifiedAsString = mdf.getText();
				Date index = RegainToolkit.stringToLastModified(lastModifiedAsString);			
				if(logger.isDebugEnabled()){
					logger.debug("Old value: "+lastModifiedAsString);
					logger.debug("New value: "+RegainToolkit.lastModifiedToIndexString(index));
				}
				mdf.setText(RegainToolkit.lastModifiedToIndexString(index));
				docFields.put(modifyField, mdf);
			}
		} catch (Exception ex) {

		}
		return docFields;
	}

}
