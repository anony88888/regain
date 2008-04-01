package net.sf.regain.util.modifyindex;

import org.apache.lucene.document.Field;

public class ModifyDocumentField {
	private String fieldName;
	private boolean stored = false;
	private boolean indexed = false;
	private boolean tokenized = false;
	private boolean termVectorStored = false;
	private String text;
	private String boost;
	
	public String getBoost() {
		return boost;
	}
	public void setBoost(String boost) {
		this.boost = boost;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public boolean isIndexed() {
		return indexed;
	}
	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}
	public boolean isStored() {
		return stored;
	}
	public void setStored(boolean stored) {
		this.stored = stored;
	}
	public boolean isTermVectorStored() {
		return termVectorStored;
	}
	public void setTermVectorStored(boolean termVectorStored) {
		this.termVectorStored = termVectorStored;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isTokenized() {
		return tokenized;
	}
	public void setTokenized(boolean tokenized) {
		this.tokenized = tokenized;
	}
    
}
