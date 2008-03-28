package net.sf.regain.util.modifyindex;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.regain.RegainException;
import net.sf.regain.XmlToolkit;
import net.sf.regain.util.modifyindex.modifier.FieldModifier;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AlterIndexConfig {
	private Map replacer;

	private String[] crawlerConfiguration;

	private String logconfig;

	private String oldIndexDir;

	private String newIndexDir;

	public AlterIndexConfig(File xmlFile) throws RegainException {
		Document doc = XmlToolkit.loadXmlDocument(xmlFile);
		Element config = doc.getDocumentElement();
		setCrawlerConfiguration(config);
		setReplacer(config);
		setLogconfig(config);
		setOldIndexDir(config);
		setNewIndexDir(config);
	}

	public String[] getCrawlerConfiguration() {
		return crawlerConfiguration;
	}

	public void setCrawlerConfiguration(Element config) throws RegainException {
		Node node = XmlToolkit.getChild(config, "configurationlist");
		if (node != null) {
			Node[] nodeArr = XmlToolkit.getChildArr(node, "config");
			crawlerConfiguration = new String[nodeArr.length];
			for (int i = 0; i < nodeArr.length; i++) {
				crawlerConfiguration[i] = XmlToolkit.getText(nodeArr[i], true);
			}
		}
	}

	public Map getReplacer() {
		return replacer;
	}

	public void setReplacer(Element config) throws RegainException {
		Node node = XmlToolkit.getChild(config, "replacelist");
		if (node != null) {
			Node[] nodeArr = XmlToolkit.getChildArr(node, "replace");
			replacer = new HashMap();
			for (int i = 0; i < nodeArr.length; i++) {
				String fieldName = XmlToolkit.getAttribute(nodeArr[i], "name", true);
				Node nodeChild = XmlToolkit.getChild(nodeArr[i], "class", true);
				String className = XmlToolkit.getText(nodeChild, true);
				try {
					FieldModifier obj = (FieldModifier) (Class.forName(className)).newInstance();
					replacer.put(fieldName, obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getLogconfig() {
		return logconfig;
	}

	public void setLogconfig(Element config) throws RegainException {
		Node node = XmlToolkit.getChild(config, "logconfig");
		if (node != null) {
			logconfig = XmlToolkit.getText(node, true);
		}
	}

	public String getNewIndexDir() {
		return newIndexDir;
	}

	public void setNewIndexDir(Element config) throws RegainException {
		Node node = XmlToolkit.getChild(config, "newindexdir");
		if (node != null) {
			newIndexDir = File.separatorChar + XmlToolkit.getText(node, true);
		}
	}

	public String getOldIndexDir() {
		return oldIndexDir;
	}

	public void setOldIndexDir(Element config) throws RegainException {
		Node node = XmlToolkit.getChild(config, "oldindexdir");
		if (node != null) {
			oldIndexDir = File.separatorChar + XmlToolkit.getText(node, true);
		}
	}

}
