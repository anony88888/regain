package net.sf.regain.util.modifyindex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import net.sf.regain.RegainToolkit;
import net.sf.regain.crawler.config.XmlCrawlerConfig;
import net.sf.regain.util.modifyindex.modifier.FieldModifier;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermPositions;

public class AlterIndex {
	/** The logger for this class */
	private static Logger logger = Logger.getLogger(AlterIndex.class);

	public void alterIndex(String[] args) {
		// Load crawler configuration
		File xmlFile = new File(args[0]);
		AlterIndexConfig alterIndexConfig = null;
		try {
			alterIndexConfig = new AlterIndexConfig(xmlFile);
			// Initialize Logging
			File logConfigFile = new File(alterIndexConfig.getLogconfig());
			if (!logConfigFile.exists()) {
				System.out.println("ERROR: Logging configuration file not found: " + logConfigFile.getAbsolutePath());
				return; // Abort
			}
			PropertyConfigurator.configure(logConfigFile.getAbsolutePath());
			logger.info("Logging initialized");

			String[] crawlerConfigs = alterIndexConfig.getCrawlerConfiguration();
			XmlCrawlerConfig crawlerConfig = null;
			for (int i = 0; i < crawlerConfigs.length; i++) {
				// Load crawler configuration
				crawlerConfig = new XmlCrawlerConfig(new File(crawlerConfigs[i]));
				Analyzer mAnalyzer = null;
				if (null != crawlerConfig) {
					mAnalyzer = RegainToolkit.createAnalyzer(crawlerConfig.getAnalyzerType(), crawlerConfig
							.getStopWordList(), crawlerConfig.getExclusionList(), crawlerConfig
							.getUntokenizedFieldNames());
				} else {
					mAnalyzer = RegainToolkit.createAnalyzer("german", null, null, null);
				}
				IndexWriter mIndexWriter = new IndexWriter(crawlerConfig.getIndexDir()
						+ alterIndexConfig.getNewIndexDir(), mAnalyzer, true);
				logger.info("Set new index dir :" + crawlerConfig.getIndexDir() + alterIndexConfig.getNewIndexDir());
				// org.apache.lucene.index.SegmentReader;
				IndexReader mIndexReader = IndexReader.open(crawlerConfig.getIndexDir()
						+ alterIndexConfig.getOldIndexDir());
				logger.info("Use index dir :" + crawlerConfig.getIndexDir() + alterIndexConfig.getOldIndexDir());
				int maxDoc = mIndexReader.maxDoc();
				if (maxDoc > 0) {
					String[] idxFields = null;
					Collection fn = mIndexReader.getFieldNames();
					TreeSet fields = new TreeSet();
					Iterator it = fn.iterator();
					while (it.hasNext()) {
						String fld = (String) it.next();
						fields.add(fld);
					}
					idxFields = (String[]) fields.toArray(new String[0]);

					Map replacer = alterIndexConfig.getReplacer();
					for (int docNum = 0; docNum < maxDoc; docNum++) {
						logger.info("Modify " + (docNum + 1) + "/" + maxDoc);
						if (!mIndexReader.isDeleted(docNum)) {
							Document doc = mIndexReader.document(docNum);
							Map modifyDocFields = new HashMap();
							// get stored fields
							Vector sf = new Vector();
							for (int j = 0; j < idxFields.length; j++) {
								Field[] f = doc.getFields(idxFields[j]);
								if (f == null || !f[0].isStored())
									continue;
								StringBuffer sb = new StringBuffer();
								for (int k = 0; k < f.length; k++) {
									if (k > 0)
										sb.append('\n');
									sb.append(f[k].stringValue());
								}
								ModifyDocumentField modDoc = new ModifyDocumentField();
								modDoc.setFieldName(idxFields[j]);
								modDoc.setText(sb.toString());
								modDoc.setIndexed(f[0].isIndexed());
								modDoc.setStored(f[0].isStored());
								modDoc.setTokenized(f[0].isTokenized());
								modDoc.setTermVectorStored(f[0].isTermVectorStored());
								modDoc.setBoost(String.valueOf(f[0].getBoost()));
								modifyDocFields.put(idxFields[j], modDoc);
							}
							String term = null;
							GrowableStringArray terms = null;
							try {
								TreeMap docMap = new TreeMap();
								TermEnum te = mIndexReader.terms();
								TermPositions tp = mIndexReader.termPositions();
								int count = 0;
								TreeMap values = new TreeMap();
////								te.next();
								while (te.next()) {
									count++;
									if(!"content".equalsIgnoreCase(te.term().field())){
										continue;
									}
									logger.info("te.next() "+count+" "+te.term().field());
									int oldDocId = -1;
									for (int j = 0; j < maxDoc; j++) {
										tp.seek(te.term());
										if (!tp.skipTo(j) || tp.doc() != j) {
											// this term is not found in the doc
											continue;
										}
										if(tp.doc()>3){
											continue;
										}
										term = te.term().text();
										terms = (GrowableStringArray) values.get(new Integer(tp.doc()));
										if (terms == null) {
											terms = new GrowableStringArray();
											values.put(new Integer(tp.doc()), terms);
										}
										for (int k = 0; k < tp.freq(); k++) {
											int pos = tp.nextPosition();
											terms.append(pos, "|", term);
										}										
										logger.info("#### "+count+" "+j +" "+term);
									}
									
								}
//								te = mIndexReader.terms();
//								if (te.next()) {
//									if(!"content".equalsIgnoreCase(te.term().field())){
//										continue;
//									}
//									tp.seek(te.term());
//									for (int j = 0; j < maxDoc; j++) {
//										if (!tp.skipTo(j)){
//											continue;
//										}
//										logger.info(""+j);
//										term = te.term().text();
//										terms = (GrowableStringArray) values.get(new Integer(tp.doc()));
//										if (terms == null) {
//											terms = new GrowableStringArray();
//											values.put(new Integer(tp.doc()), terms);
//										}
//										for (int k = 0; k < tp.freq(); k++) {
//											int pos = tp.nextPosition();
//											terms.append(pos, "|", term);
//										}	
//									}
//								}
								for (Iterator iter = values.keySet().iterator(); iter.hasNext();) {
									Integer key = (Integer) iter.next();
									logger.info("Key: "+key+" - Value: "+((GrowableStringArray) values.get(key)).toString());
								}
//								while (te.next()) {
//									count++;
//									// skip stored fields
//									// if (sf.contains(te.term().field()))
//									// continue;
//									if(!"content".equalsIgnoreCase(te.term().field())){
//										continue;
//									}
//									tp.seek(te.term());
//									
//									int oldDocId = -1;
//									for (int j = 0; j < maxDoc; j++) {
//										tp.skipTo(j);
//										if(oldDocId==tp.doc()){
//											break;
//										}
//										logger.info(j+"-"+count+"-"+tp.doc()+"-"+te.term().text());
//										oldDocId=tp.doc();
//									}
									
//									if (!tp.skipTo(docNum) || tp.doc() != docNum) {
//										// this term is not found in the doc
//										continue;
//									}
//									term = te.term().text();
//									logger.info(te.term().field()+": "+term);
//									terms = (GrowableStringArray) docMap.get("uf_" + te.term().field());
//									if (terms == null) {
//										terms = new GrowableStringArray();
//										docMap.put("uf_" + te.term().field(), terms);
//									}
//									for (int k = 0; k < tp.freq(); k++) {
//										int pos = tp.nextPosition();
//										terms.append(pos, "|", term);
//									}
//									//break;
//								}
								String sep = " ";
//								for (int p = 0; p < idxFields.length; p++) {
									String key = "content";//idxFields[p];
									terms = (GrowableStringArray) docMap.get("uf_" + key);
									if (terms == null)
										continue;
									StringBuffer sb = new StringBuffer();
									String sNull = "null";
									int k = 0, m = 0;
									for (int j = 0; j < terms.size(); j++) {
										if (terms.get(j) == null)
											k++;
										else {
											if (sb.length() > 0)
												sb.append(sep);
											if (k > 0) {
												sb.append(sNull + "_" + k + sep);
												k = 0;
												m++;
											}
											sb.append(terms.get(j));
											m++;
											// if (m % 10 == 0 && t != null)
											// sb.append('\n');
										}
									}
									ModifyDocumentField modDoc = new ModifyDocumentField();
									modDoc.setFieldName(key);
									modDoc.setText(sb.toString());
									modDoc.setIndexed(true);
									modDoc.setBoost(String.valueOf(doc.getBoost()));
									modifyDocFields.put(key, modDoc);

									logger.info("Key: "+key+" - Value: "+sb.toString());
//								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							Iterator iter = replacer.keySet().iterator();
							while (iter.hasNext()) {
								FieldModifier modifier = (FieldModifier) replacer.get(iter.next());
								modifyDocFields = modifier.modify(modifyDocFields);
							}
							Document newDoc = new Document();
							for (int j = 0; j < idxFields.length; j++) {
								ModifyDocumentField mdf = (ModifyDocumentField)modifyDocFields.get(idxFields[j]);
								if(null != mdf){
									Field f = new Field(mdf.getFieldName(), mdf.getText(), mdf.isStored(), mdf.isIndexed(), mdf.isTokenized(), mdf.isTermVectorStored());
									String boostS = mdf.getBoost().trim();
									if (!boostS.equals("") && !boostS.equals("1.0")) {
										float boost = 1.0f;
										try {
											boost = Float.parseFloat(boostS);
										} catch (Exception e) {
											e.printStackTrace();
										}
										f.setBoost(boost);
									}
									newDoc.add(f);
								}
							}
							mIndexWriter.addDocument(newDoc);
						}
					}
					logger.info("Optimize index");
					mIndexWriter.optimize();
					logger.info("Close index");
					mIndexWriter.close();
					mIndexReader.close();
					// prefetch dateien kopieren
					logger.info("Copy all other txt files");
					File oldIndexDir = new File(crawlerConfig.getIndexDir() + alterIndexConfig.getOldIndexDir());
					File[] txts = oldIndexDir.listFiles(new FilenameFilter() {
						public boolean accept(File d, String name) {
							return name.toLowerCase().endsWith(".txt");
						}
					});
					for (int k = 0; k < txts.length; k++) {
						logger.info("Copy " + txts[k].getAbsolutePath() + " to " + crawlerConfig.getIndexDir()
								+ alterIndexConfig.getNewIndexDir());
						copy(new FileInputStream(txts[k].getAbsolutePath()), new FileOutputStream(crawlerConfig
								.getIndexDir()
								+ alterIndexConfig.getNewIndexDir() + File.separatorChar + txts[k].getName()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void copy(InputStream fis, OutputStream fos) {
		try {
			byte[] buffer = new byte[0xFFFF];
			for (int len; (len = fis.read(buffer)) != -1;)
				fos.write(buffer, 0, len);
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (1 == args.length) {
			AlterIndex alterIndex = new AlterIndex();
			alterIndex.alterIndex(args);
		} else {
			System.out.println("conf");
		}
	}

}

/**
 * Simple Vector-like implementation of growable String array.
 * 
 * @author Andrzej Bialecki &lt;ab@getopt.org&gt;
 */
class GrowableStringArray {
	public int INITIAL_SIZE = 20;

	private int size = 0;

	private String[] array = null;

	public int size() {
		return size;
	}

	/**
	 * Sets the value at specified index. If index is outside range the array is
	 * automatically expanded.
	 * 
	 * @param index
	 *            where to set the value
	 * @param value
	 */
	public void set(int index, String value) {
		if (array == null)
			array = new String[INITIAL_SIZE];
		if (array.length < index + 1) {
			String[] newArray = new String[index + INITIAL_SIZE];
			System.arraycopy(array, 0, newArray, 0, array.length);
			array = newArray;
		}
		if (index > size - 1)
			size = index + 1;
		array[index] = value;
	}

	/**
	 * Appends the separator and value at specified index. If no value exists at
	 * the specified position, this is equivalent to {@link #set(int, String)} -
	 * no separator is appended in that case.
	 * 
	 * @param index
	 *            selected position
	 * @param sep
	 *            separator
	 * @param value
	 *            value
	 */
	public void append(int index, String sep, String value) {
		String oldVal = get(index);
		if (oldVal == null) {
			set(index, value);
		} else {
			set(index, oldVal + sep + value);
		}
	}

	/**
	 * Return the value at specified index.
	 * 
	 * @param index
	 * @return
	 */
	public String get(int index) {
		if (array == null || index < 0 || index > array.length - 1)
			return null;
		return array[index];
	}

	public String toString() {
		String sep = " ";
		StringBuffer sb = new StringBuffer();
		String sNull = "null";
		int k = 0, m = 0;
		for (int j = 0; j < size(); j++) {
			if (get(j) == null)
				k++;
			else {
				if (sb.length() > 0)
					sb.append(sep);
				if (k > 0) {
					sb.append(sNull + "_" + k + sep);
					k = 0;
					m++;
				}
				sb.append(get(j));
				m++;
				// if (m % 10 == 0 && t != null)
				// sb.append('\n');
			}
		}
		return sb.toString();
	}
	
}