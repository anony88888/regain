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
package net.sf.regain.crawler.config;


/**
 * Stellt alle zu konfigurierenden Einstellungen hardcodiert zur Verf�gung.
 *
 * @author Til Schneider, www.murfman.de
 */
public class DummyCrawlerConfig implements CrawlerConfig {

  /**
   * Gibt den Host-Namen des Proxy-Servers zur�ck. Wenn kein Host konfiguriert
   * wurde, wird <CODE>null</CODE> zur�ckgegeben.
   *
   * @return Der Host-Namen des Proxy-Servers.
   */
  public String getProxyHost() {
    return "idatmpsrv";
  }



  /**
   * Gibt den Port des Proxy-Servers zur�ck. Wenn kein Port konfiguriert wurde,
   * wird <CODE>null</CODE> zur�ckgegeben.
   *
   * @return Der Port des Proxy-Servers.
   */
  public String getProxyPort() {
    return "3128";
  }



  /**
   * Gibt den Benutzernamen f�r die Anmeldung beim Proxy-Server zur�ck. Wenn
   * kein Benutzernamen konfiguriert wurde, wird <CODE>null</CODE> zur�ckgegeben.
   *
   * @return Der Benutzernamen f�r die Anmeldung beim Proxy-Server.
   */
  public String getProxyUser() {
    return null;
  }



  /**
   * Gibt das Passwort f�r die Anmeldung beim Proxy-Server zur�ck. Wenn kein
   * Passwort konfiguriert wurde, wird <CODE>null</CODE> zur�ckgegeben.
   *
   * @return Das Passwort f�r die Anmeldung beim Proxy-Server.
   */
  public String getProxyPassword() {
    return null;
  }


  /**
   * Gibt den Timeout f�r HTTP-Downloads zur�ck. Dieser Wert bestimmt die
   * maximale Zeit in Sekunden, die ein HTTP-Download insgesamt dauern darf.
   *
   * @return Den Timeout f�r HTTP-Downloads
   */
  public int getHttpTimeoutSecs() {
    return 180;
  }


  /**
   * Gibt zur�ck, ob URLs geladen werden sollen, die weder durchsucht noch
   * indiziert werden.
   *
   * @return Ob URLs geladen werden sollen, die weder durchsucht noch indiziert
   *         werden.
   */
  public boolean getLoadUnparsedUrls() {
    return false;
  }



  /**
   * Gibt zur�ck, ob ein Suchindex erstellt werden soll.
   *
   * @return Ob ein Suchindex erstellt werden soll.
   */
  public boolean getBuildIndex() {
    return true;
  }


  /**
   * Gibt das Verzeichnis zur�ck, in dem der stehen soll.
   *
   * @return Das Verzeichnis, in dem der Suchindex stehen soll.
   */
  public String getIndexDir() {
    return "c:\\Temp\\searchIndex";
  }


  /**
   * Gibt den zu verwendenden Analyzer-Typ zur�ck.
   *
   * @return en zu verwendenden Analyzer-Typ
   */
  public String getAnalyzerType() {
    return "german";
  }


  /**
   * Gibt alle Worte zur�ck, die nicht indiziert werden sollen.
   *
   * @return Alle Worte, die nicht indiziert werden sollen.
   */
  public String[] getStopWordList() {
    return null;
  }



  /**
   * Gibt alle Worte zur�ck, die bei der Indizierung nicht vom Analyzer
   * ver�ndert werden sollen.
   *
   * @return Alle Worte, die bei der Indizierung nicht vom Analyzer
   *         ver�ndert werden sollen.
   */
  public String[] getExclusionList() {
    return null;
  }



  /**
   * Gibt zur�ck, ob Analyse-Deteien geschrieben werden sollen.
   * <p>
   * Diese Dateien helfen, die Qualit�t der Index-Erstellung zu pr�fen und
   * werden in einem Unterverzeichnis im Index-Verzeichnis angelegt.
   *
   * @return Ob Analyse-Deteien geschrieben werden sollen.
   */
  public boolean getWriteAnalysisFiles() {
    return true;
  }


  /**
   * Gibt den maximalen Prozentsatz von gescheiterten Dokumenten zur�ck. (0..1)
   * <p>
   * Ist das Verh�lnis von gescheiterten Dokumenten zur Gesamtzahl von
   * Dokumenten gr��er als dieser Prozentsatz, so wird der Index verworfen.
   * <p>
   * Gescheiterte Dokumente sind Dokumente die es entweder nicht gibt (Deadlink)
   * oder die nicht ausgelesen werden konnten.
   *
   * @return Den maximalen Prozentsatz von gescheiterten Dokumenten zur�ck.
   */
  public double getMaxFailedDocuments() {
    return 0.1;
  }


  /**
   * Gibt den Namen der Kontrolldatei f�r erfolgreiche Indexerstellung zur�ck.
   * <p>
   * Diese Datei wird erzeugt, wenn der Index erstellt wurde, ohne dass
   * fatale Fehler aufgetreten sind.
   * <p>
   * Wenn keine Kontrolldatei erzeugt werden soll, dann wird <code>null</code>
   * zur�ckgegeben.
   *
   * @return Der Name der Kontrolldatei f�r erfolgreiche Indexerstellung
   */
  public String getFinishedWithoutFatalsFileName() {
    return null;
  }


  /**
   * Gibt den Namen der Kontrolldatei f�r fehlerhafte Indexerstellung zur�ck.
   * <p>
   * Diese Datei wird erzeugt, wenn der Index erstellt wurde, wobei
   * fatale Fehler aufgetreten sind.
   * <p>
   * Wenn keine Kontrolldatei erzeugt werden soll, dann wird <code>null</code>
   * zur�ckgegeben.
   *
   * @return Der Name der Kontrolldatei f�r fehlerhafte Indexerstellung
   */
  public String getFinishedWithFatalsFileName() {
    return null;
  }


  /**
   * Gibt die StartUrls zur�ck, bei denen der Crawler-Proze� beginnen soll.
   *
   * @return Die StartUrls.
   */
  public StartUrl[] getStartUrls() {
    return new StartUrl[] {
      new StartUrl("http://www.dm-drogeriemarkt.de/CDA/Home/", true, true),
      /*
      new StartUrl("http://www.dm-drogeriemarkt.de/CDA/verteilerseite/0,2098,0-15-X,00.html", true, true),
      new StartUrl("http://www.dm-drogeriemarkt.de/CDA/verteilerseite/0,1651,0-16-X,00.html", true, true),
      new StartUrl("http://www.dm-drogeriemarkt.de/CDA/verteilerseite/0,1651,0-17-X,00.html", true, true),
      new StartUrl("http://www.dm-drogeriemarkt.de/CDA/verteilerseite/0,1651,0-18-X,00.html", true, true),
      new StartUrl("http://www.dm-drogeriemarkt.de/CDA/verteilerseite/0,1651,0-19-X,00.html", true, true),
      new StartUrl("http://www.dm-drogeriemarkt.de/CDA/verteilerseite/0,1651,0-173-X,00.html", true, true)
      */
    };
  }



  /**
   * Gibt die UrlPattern zur�ck, die der HTML-Parser nutzen soll, um URLs zu
   * identifizieren.
   *
   * @return Die UrlPattern f�r den HTML-Parser.
   */
  public UrlPattern[] getHtmlParserUrlPatterns() {
    return new UrlPattern[] {
      new UrlPattern("=\"([^\"]*\\.html)\"", 1, true,  true),
      new UrlPattern("=\"([^\"]*\\.(pdf|xls|doc|rtf|ppt))\"",  1, false, true),
      new UrlPattern("=\"([^\"]*\\.(js|css|jpg|gif|png))\"",  1, false, false)
    };
  }



  /**
   * Gibt die UrlPattern zur�ck, die der Verzeichnis-Parser nutzt, um zu
   * entscheiden, ob und wie eine Datei bearbeitet werden soll.
   *
   * @return Die UrlPattern f�r den Verzeichnis-Parser.
   */
  public UrlPattern[] getDirectoryParserUrlPatterns() {
    return new UrlPattern[] {
      new UrlPattern(".*\\.(html|pdf|xls|doc|rtf|ppt)", -1, false,  true)
    };
  }



  /**
   * Gibt die Schwarze Liste zur�ck.
   * <p>
   * Diese enth�lt Pr�fixe, die eine URL <I>nicht</I> haben darf, um bearbeitet
   * zu werden.
   *
   * @return Die Schwarze Liste.
   */
  public String[] getUrlPrefixBlackList() {
    return new String[] {
      "http://www.dm-drogeriemarkt.de/CDA/Suchen/",
      "http://www.dm-drogeriemarkt.de/CDA/content/print/"
    };
  }



  /**
   * Gibt die Wei�e Liste zur�ck.
   * <p>
   * Diese enth�lt Pr�fixe, von denen eine URL einen haben <i>mu�</i>, um
   * bearbeitet zu werden.
   *
   * @return Die Wei�e Liste
   */
  public WhiteListEntry[] getWhiteList() {
    return new WhiteListEntry[] {
      // new WhiteListEntry("http://www.dm-drogeriemarkt.de/", "dm-main"),
      new WhiteListEntry("http://www.dm-drogeriemarkt.de/CDA/Home/", "dm-home"),
      new WhiteListEntry("http://www.dm-drogeriemarkt.de/CDA/images/", "dm-images"),
      new WhiteListEntry("http://www.dm-drogeriemarkt.de/CDA/verteilerseite/0,1651,", "dm-test1"),
      new WhiteListEntry("http://www.dm-drogeriemarkt.de/CDA/content/0,1647,0-171", "dm-test2"),
    };
  }



  /**
   * Gibt die regul�ren Ausdr�cke zur�ck, auf die die URL eines Dokuments passen
   * muss, damit anstatt des wirklichen Dokumententitels der Text des Links, der
   * auf das Dokument gezeigt hat, als Dokumententitel genutzt wird.
   *
   * @return Die regul�ren Ausdr�cke, die Dokumente bestimmen, f�r die der
   *         Linktext als Titel genommen werden soll.
   */
  public String[] getUseLinkTextAsTitleRegexList() {
    return null;
  }



  /**
   * Gibt die Liste der Einstellungen f�r die Pr�peratoren zur�ck.
   *
   * @return Die Liste der Einstellungen f�r die Pr�peratoren.
   */
  public PreparatorSettings[] getPreparatorSettingsList() {
    return new PreparatorSettings[] {
      new PreparatorSettings("\\.(/|html|htm)$",
          "net.sf.regain.crawler.document.HtmlPreparator", new PreparatorConfig()),
      new PreparatorSettings("\\.xml$",
          "net.sf.regain.crawler.document.XmlPreparator", new PreparatorConfig())
    };
  }

  
  /**
   * Gets the list of the auxiliary fields.
   * 
   * @return The list of the auxiliary fields. May be null.
   */
  public AuxiliaryField[] getAuxiliaryFieldList() {
    return null;
  }

}