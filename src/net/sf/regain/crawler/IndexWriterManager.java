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
package net.sf.regain.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import net.sf.regain.RegainException;
import net.sf.regain.RegainToolkit;
import net.sf.regain.crawler.config.CrawlerConfig;
import net.sf.regain.crawler.document.DocumentFactory;
import net.sf.regain.crawler.document.RawDocument;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Kontrolliert und kapselt die Erstellung des Suchindex.
 * <p>
 * <b>Anwendung:</b><br>
 * Rufen Sie f�r jedes Dokument {@link #addToIndex(RawDocument)} auf. Rufen Sie
 * am Ende {@link #close(boolean)} auf, um den Index zu schlie�en. Danach sind
 * keine weiteren Aufrufe von {@link #addToIndex(RawDocument)} erlaubt.
 *
 * @author Til Schneider, www.murfman.de
 */
public class IndexWriterManager {

  /** The logger for this class */
  private static Logger mLog = Logger.getLogger(IndexWriterManager.class);

  /**
   * Der Name des Index-Unterverzeichnisses, in das der neue Index gestellt
   * werden soll, sobald er fertig ist ohne dass fatale Fehler aufgetreten sind.
   * <p>
   * Die Suchmaske wird, sobald es diese Verzeichnis gibt seine Suche darauf
   * umstellen. Dabei wird es in "index" umbenannt.
   */
  private static final String NEW_INDEX_SUBDIR = "new";

  /**
   * Der Name des Index-Unterverzeichnisses, in das der neue Index gestellt
   * werden soll, sobald er fertig ist, wobei fatale Fehler sufgetreten sind.
   */
  private static final String QUARANTINE_INDEX_SUBDIR = "quarantine";

  /** Der Name des Index-Unterverzeichnisses, in dem der genutzte Index steht. */
  private static final String WORKING_INDEX_SUBDIR = "index";
  /**
   * Der Name des Index-Unterverzeichnisses, in dem der neue Index aufgebaut
   * werden soll.
   */
  private static final String TEMP_INDEX_SUBDIR = "temp";

  /**
   * Gibt an, ob die Terme sortiert in die Terme-Datei geschrieben werden soll.
   *
   * @see #writeTermFile(File, File)
   */
  private static final boolean WRITE_TERMS_SORTED = true;

  /**
   * Workaround: Unter Windows klappt das Umbenennen unmittelbar nach Schlie�en
   * des Index nicht. Wahrscheinlich sind die Filepointer auf die gerade
   * geschlossenen Dateien noch nicht richtig aufger�umt, so dass ein Umbenennen
   * des Indexverzeichnisses fehl schl�gt. Das Umbenennen wird daher regelm��ig
   * probiert, bis es entweder funktioniert oder bis der Timeout abgelaufen ist.
   */
  private static final long RENAME_TIMEOUT = 60000; // 1 min

  /**
   * Der Hinzuf�ge-Modus.
   * @see #setIndexMode(int)
   */
  private static final int ADDING_MODE = 1;
  /**
   * Der L�sch-Modus.
   * @see #setIndexMode(int)
   */
  private static final int DELETING_MODE = 2;
  /**
   * Der Beendet-Modus.
   * @see #setIndexMode(int)
   */
  private static final int FINISHED_MODE = 3;

  /** Der Analyzer, der vom IndexWriter genutzt werden soll. */
  private Analyzer mAnalyzer;

  /** Der gekapselte IndexWriter, der den eigentlichen Index erstellt. */
  private IndexWriter mIndexWriter;

  /**
   * Der gekapselte IndexReader. Wird zum L�schen von Dokumenten aus dem Index
   * ben�tigt.
   * <p>
   * Ist <code>null</code>, wenn der Index nicht aktualisiert werden soll.
   */
  private IndexReader mIndexReader;

  /**
   * Der gekapselte IndexSearcher. Wird zum Finden von Dokumenten ben�tigt.
   * <p>
   * Ist <code>null</code>, wenn der Index nicht aktualisiert werden soll.
   */
  private IndexSearcher mIndexSearcher;

  /**
   * Gibt an, ob ein bestehender Index aktualisiert wird.
   * <p>
   * Anderenfalls wird ein komplett neuer Index angelegt.
   */
  private boolean mUpdateIndex;

  /** Die DocumentFactory, die die Inhalte f�r die Indizierung aufbereitet. */
  private DocumentFactory mDocumentFactory;

  /**
   * Das Verzeichnis, in dem der Suchindex am Ende stehen soll, wenn es keine
   * fatalen Fehler gab.
   */
  private File mNewIndexDir;

  /**
   * Das Verzeichnis, in dem der Suchindex am Ende stehen soll, wenn es
   * fatale Fehler gab.
   */
  private File mQuarantineIndexDir;

  /** Das Verzeichnis, in dem der neue Suchindex aufgebaut werden soll. */
  private File mTempIndexDir;

  /** Das Verzeichnis, in dem die Analyse-Dateien erstellt werden soll. */
  private File mAnalysisDir;
  
  /**
   * The number of documents that were in the (old) index when the
   * IndexWriterManager was created.
   */
  private int mInitialDocCount;

  /** Der Profiler der das Hinzuf�gen zum Index mi�t. */
  private Profiler mAddToIndexProfiler = new Profiler("Indexed documents", "docs");

  /**
   * Enth�lt die URL und den LastUpdated-String aller Dokumente, deren Eintr�ge
   * beim Abschlie�en des Index entfernt werden m�ssen.
   * <p>
   * Die URL bildet den key, der LastUpdated-String die value.
   */
  private HashMap mUrlsToDeleteHash;



  /**
   * Erzeugt eine neue IndexWriterManager-Instanz.
   *
   * @param config Die zu verwendende Konfiguration.
   * @param updateIndex Gibt an, ob ein bereits bestehender Index aktualisiert
   *        werden soll.
   *
   * @throws RegainException Wenn der neue Index nicht vorbereitet werden konnte.
   */
  public IndexWriterManager(CrawlerConfig config, boolean updateIndex)
    throws RegainException
  {
    mUpdateIndex = updateIndex;
    
    mInitialDocCount = 0;

    File indexDir = new File(config.getIndexDir());

    if (! indexDir.exists()) {
      // NOTE: The index directory does not exist.
      //       We could just create it, but it's more savely to throw an
      //       exception. We don't wan't to destroy anything.
      throw new RegainException("The index directory " + indexDir + " does not exist");
    }

    mNewIndexDir        = new File(indexDir, NEW_INDEX_SUBDIR);
    mQuarantineIndexDir = new File(indexDir, QUARANTINE_INDEX_SUBDIR);
    mTempIndexDir       = new File(indexDir, TEMP_INDEX_SUBDIR);

    // Delete the old temp index directory if it should still exist
    if (mTempIndexDir.exists()) {
      RegainToolkit.deleteDirectory(mTempIndexDir);
    }
    // and create a new, empty one
    if (! mTempIndexDir.mkdir()) {
      throw new RegainException("Creating working directory failed: "
                                + mTempIndexDir.getAbsolutePath());
    }

    // Create the Analyzer
    // NOTE: Make shure you use the same Analyzer in the SearchContext too!
    String analyzerType = config.getAnalyzerType();
    String[] stopWordList = config.getStopWordList();
    String[] exclusionList = config.getExclusionList();
    mAnalyzer = RegainToolkit.createAnalyzer(analyzerType, stopWordList,
        exclusionList);

    // Alten Index kopieren, wenn Index aktualisiert werden soll
    if (updateIndex) {
      if (! copyExistingIndex(indexDir, analyzerType)) {
        mUpdateIndex = updateIndex = false;
      }
    }

    // Check whether we have to create a new index
    boolean createNewIndex = ! updateIndex;
    if (createNewIndex) {
      // Create a new index
      try {
        mIndexWriter = new IndexWriter(mTempIndexDir, mAnalyzer, true);
      } catch (IOException exc) {
        throw new RegainException("Creating new index failed", exc);
      }
    }

    if (updateIndex) {
      // Check whether we need a IndexSearcher
      try {
        mIndexSearcher = new IndexSearcher(mTempIndexDir.getAbsolutePath());
      } catch (IOException exc) {
        throw new RegainException("Creating IndexSearcher failed", exc);
      }

      // Force an unlock of the index (we just created a copy so this is save)
      setIndexMode(DELETING_MODE);
      try {
        IndexReader.unlock(mIndexReader.directory());
        mInitialDocCount = mIndexReader.numDocs();
      } catch (IOException exc) {
        throw new RegainException("Forcing unlock failed", exc);
      }
    }

    // Write the stopWordList and the exclusionList in a file so it can be found
    // by the search mask
    CrawlerToolkit.writeToFile(analyzerType, new File(mTempIndexDir, "analyzerType.txt"));
    CrawlerToolkit.writeListToFile(stopWordList, new File(mTempIndexDir, "stopWordList.txt"));
    CrawlerToolkit.writeListToFile(exclusionList, new File(mTempIndexDir, "exclusionList.txt"));

    // Prepare the analysis directory if wanted
    if (config.getWriteAnalysisFiles()) {
      mAnalysisDir = new File(mTempIndexDir.getAbsolutePath() + File.separator
                              + "analysis");

      if (! mAnalysisDir.mkdir()) {
        throw new RegainException("Creating analysis directory failed: "
                                  + mAnalysisDir.getAbsolutePath());
      }
    }

    mDocumentFactory = new DocumentFactory(config, mAnalysisDir);
  }


  /**
   * Gibt zur�ck, ob ein bestehender Index aktualisiert wird.
   * <p>
   * Anderenfalls wird ein komplett neuer Index angelegt.
   *
   * @return Ob ein bestehender Index aktualisiert wird.
   */
  public boolean getUpdateIndex() {
    return mUpdateIndex;
  }
  
  
  /**
   * Gets the number of documents that were in the (old) index when the
   * IndexWriterManager was created.
   * 
   * @return The initial number of documents in the index.
   */
  public int getInitialDocCount() {
    return mInitialDocCount;
  }


  /**
   * Gets the number of documents that were added to the index.
   * 
   * @return The number of documents added to the index.
   */
  public int getAddedDocCount() {
    return mAddToIndexProfiler.getMeasureCount();
  }


  /**
   * Gets the number of documents that will be removed from the index.
   * 
   * @return The number of documents removed from the index.
   */
  public int getRemovedDocCount() {
    // NOTE: We get a local pointer to the mUrlsToDeleteHash, if the hash should
    //       be set to null in the same time.
    HashMap hash = mUrlsToDeleteHash;
    return (hash == null) ? 0 : hash.size();
  }


  /**
   * Setzt den aktuellen Modus.
   * <p>
   * Es gibt folgende Modi:
   * <ul>
   *   <li>Hinzuf�ge-Modus: Hier ist der mIndexWriter und mIndexSearcher
   *     ge�ffnet, der mIndexReader ist ausgeschalten. In diesem Modus k�nnen
   *     Dokumente zum Index hinzugef�gt und gesucht werden.</li>
   *   <li>L�sch-Modus: Hier sind mIndexReader und mIndexSearcher ge�ffnet, der
   *     mIndexWriter ist ausgeschalten. In diesem Modus k�nnen Dokumente aus
   *     dem Index gel�scht und gesucht werden.</li>
   *   <li>Beendet-Modus: Hier sind alle Zugriffe auf den Index ausgeschalten:
   *     mIndexWriter, mIndexReader und mIndexSearcher. In diesem Modus kann
   *     gar nicht auf den Index zugegriffen werden.
   * </ul>
   * <p>
   * Falls der Index bereits im entsprechenden Modus ist, dann passiert nichts.
   * Diese Methode ist in diesem Fall sehr schnell.
   *
   * @param mode Der Modus, in den der Index versetzt werden soll. Muss entweder
   *        {@link #ADDING_MODE}, {@link #DELETING_MODE} oder
   *        {@link #FINISHED_MODE} sein.
   * @throws RegainException Wenn beim schlie�en oder �ffnen etwas schief ging.
   */
  private void setIndexMode(int mode) throws RegainException {
    // Close the mIndexReader in ADDING_MODE and FINISHED_MODE
    if ((mode == ADDING_MODE) || (mode == FINISHED_MODE)) {
      if (mIndexReader != null) {
        try {
          mIndexReader.close();
          mIndexReader = null;
        } catch (IOException exc) {
          throw new RegainException("Closing IndexReader failed", exc);
        }
      }
    }

    // Close the mIndexWriter in DELETING_MODE and FINISHED_MODE
    if ((mode == DELETING_MODE) || (mode == FINISHED_MODE)) {
      if (mIndexWriter != null) {
        try {
          mIndexWriter.close();
          mIndexWriter = null;
        } catch (IOException exc) {
          throw new RegainException("Closing IndexWriter failed", exc);
        }
      }
    }

    // Close the mIndexSearcher in FINISHED_MODE
    if ((mode == FINISHED_MODE) && (mIndexSearcher != null)) {
      try {
        mIndexSearcher.close();
        mIndexSearcher = null;
      } catch (IOException exc) {
        throw new RegainException("Closing IndexSearcher failed", exc);
      }
    }

    // Open the mIndexWriter in ADDING_MODE
    if ((mode == ADDING_MODE) && (mIndexWriter == null)) {
      mLog.info("Switching to index mode: adding mode");
      try {
        mIndexWriter = new IndexWriter(mTempIndexDir, mAnalyzer, false);
      } catch (IOException exc) {
        throw new RegainException("Creating IndexWriter failed", exc);
      }
    }

    // Open the mIndexReader in DELETING_MODE
    if ((mode == DELETING_MODE) && (mIndexReader == null)) {
      mLog.info("Switching to index mode: deleting mode");
      try {
        mIndexReader = IndexReader.open(mTempIndexDir);
      } catch (IOException exc) {
        throw new RegainException("Creating IndexReader failed", exc);
      }
    }

    // Tell the user, when the index is finished
    if (mode == FINISHED_MODE) {
      mLog.info("Switching to index mode: finished mode");
    }
  }


  /**
   * Kopiert den zuletzt erstellten Index in das Arbeitsverzeichnis.
   *
   * @param indexDir Das Verzeichnis, in dem der Index liegt.
   * @param analyzerType Der Analyzer-Typ, den der alte Index haben muss, um
   *        �bernommen zu werden.
   * @return Ob ein alter Index gefunden wurde.
   * @throws RegainException Wenn das Kopieren fehl schlug.
   */
  private boolean copyExistingIndex(File indexDir, String analyzerType)
    throws RegainException
  {
    // Neuesten, kompletten Index finden
    File oldIndexDir;
    if (mNewIndexDir.exists()) {
      oldIndexDir = mNewIndexDir;
    } else {
      // Es gibt keinen neuen Index -> Wir m�ssen den Index nehmen, der gerade
      // verwendet wird
      oldIndexDir = new File(indexDir, WORKING_INDEX_SUBDIR);
    }
    if (! oldIndexDir.exists()) {
      mLog.warn("Can't update index, because there was no old index. " +
        "A complete new index will be created...");
      return false;
    }

    // Analyzer-Typ des alten Index pr�fen
    File analyzerTypeFile = new File(oldIndexDir, "analyzerType.txt");
    String analyzerTypeOfIndex = RegainToolkit.readStringFromFile(analyzerTypeFile);
    if ((analyzerTypeOfIndex == null)
      || (! analyzerType.equals(analyzerTypeOfIndex.trim())))
    {
      mLog.warn("Can't update index, because the index was created using " +
        "another analyzer type (index type: '" + analyzerTypeOfIndex.trim() +
        "', configured type '" + analyzerType + "'). " +
        "A complete new index will be created...");
      return false;
    }

    // Index in Arbeitsverzeichnis kopieren
    mLog.info("Updating index from " + oldIndexDir.getAbsolutePath());
    File[] indexFiles = oldIndexDir.listFiles();
    for (int i = 0; i < indexFiles.length; i++) {
      String fileName = indexFiles[i].getName();
      if ((! indexFiles[i].isDirectory()) && (! fileName.endsWith(".txt"))) {
        // Datei ist weder Verzeichnis, noch Textdatei -> kopieren
        File target = new File(mTempIndexDir, fileName);
        RegainToolkit.copyFile(indexFiles[i], target);
      }
    }

    return true;
  }


  /**
   * F�gt ein Dokument dem Index hinzu.
   * <p>
   * Anhand der URL wird der Typ des Dokuments erkannt.
   *
   * @param rawDocument Das zu indizierende Dokument.
   *
   * @throws RegainException Wenn das Hinzuf�gen zum Index scheiterte.
   */
  public void addToIndex(RawDocument rawDocument) throws RegainException {
    // Pr�fen, ob es einen aktuellen Indexeintrag gibt
    if (mUpdateIndex) {
      boolean removeOldEntry = false;

      // Alten Eintrag suchen
      Term urlTerm = new Term("url", rawDocument.getUrl());
      Query query = new TermQuery(urlTerm);
      Document doc;
      try {
        Hits hits = mIndexSearcher.search(query);
        if (hits.length() > 0) {
          if (hits.length() > 1) {
            mLog.warn("There are duplicate entries (" + hits.length() + " in " +
              "total) for " + rawDocument.getUrl() + ". They will be removed.");
            removeOldEntry = true;
          }
          doc = hits.doc(0);
        }
        else {
          doc = null;
        }
      }
      catch (IOException exc) {
        throw new RegainException("Searching old index entry failed for "
          + rawDocument.getUrl(), exc);
      }

      // Wenn ein Dokument gefunden wurde, dann pr�fen, ob Indexeintrag aktuell ist
      if (doc != null) {
        Date docLastModified = rawDocument.getLastModified();
        if (docLastModified == null) {
          // Wir k�nnen nicht feststellen, wann das Dokument zuletzt ge�ndert
          // wurde (Das ist bei http-URLs der Fall)
          // -> Alten Eintrag l�schen und Dokument neu indizieren
          mLog.info("Don't know when the document was last modified. " +
            "Creating a new index entry...");
          removeOldEntry = true;
        } else {
          // �nderungsdatum mit dem Datum des Indexeintrages vergleichen
          String asString = doc.get("last-modified");
          if (asString != null) {
            Date indexLastModified = RegainToolkit.stringToLastModified(asString);

            long diff = docLastModified.getTime() - indexLastModified.getTime();
            if (diff > 60000L) {
              // Das Dokument ist mehr als eine Minute neuer
              // -> Der Eintrag ist nicht aktuell -> Alten Eintrag l�schen
              mLog.info("Index entry is outdated. Creating a new one... ("
                + docLastModified + " > " + indexLastModified + ")");
              removeOldEntry = true;
            } else {
              // Der Indexeintrag ist aktuell -> Wir sind fertig
              mLog.info("Index entry is already up to date");
              return;
            }
          } else {
            // Wir kennen das �nderungsdatum nicht -> Alten Eintrag l�schen
            mLog.info("Index entry has no last-modified field. Creating a new one...");
            removeOldEntry = true;
          }
        }
      }

      // Evtl. alten Eintrag l�schen
      if (removeOldEntry) {
        // Eintrag nicht sofort l�schen, sondern nur zum L�schen vormerken.
        // Siehe markForDeletion(Document)
        markForDeletion(doc);
      }
    }

    // Neuen Eintrag erzeugen
    createNewIndexEntry(rawDocument);
  }


  /**
   * Erzeugt f�r ein Dokument einen neuen Indexeintrag.
   *
   * @param rawDocument Das Dokument f�r das der Eintrag erzeugt werden soll
   * @throws RegainException Wenn die Erzeugung fehl schlug.
   */
  private void createNewIndexEntry(RawDocument rawDocument)
    throws RegainException
  {
    // Dokument erzeugen
    if (mLog.isDebugEnabled()) {
      mLog.debug("Creating document");
    }
    Document doc = mDocumentFactory.createDocument(rawDocument);

    // Dokument in den Index aufnehmen
    mAddToIndexProfiler.startMeasuring();
    try {
      setIndexMode(ADDING_MODE);
      if (mLog.isDebugEnabled()) {
        mLog.debug("Adding document to index");
      }
      mIndexWriter.addDocument(doc);
      mAddToIndexProfiler.stopMeasuring(rawDocument.getContent().length);
    }
    catch (IOException exc) {
      mAddToIndexProfiler.abortMeasuring();
      throw new RegainException("Adding document to index failed", exc);
    }
  }


  /**
   * Geht durch den Index und l�scht alle veralteten Eintr�ge.
   * <p>
   * Veraltet sind alle Eintr�ge, die entweder vom IndexWriterManager f�rs L�schen
   * vorgemerkt wurden Siehe {@link #mUrlsToDeleteHash} oder die weder im
   * urlToKeepSet stehen noch zu einem Eintrag des prefixesToKeepArr passen.
   *
   * @param urlToKeepSet Die zu verschonenden URLs
   * @param prefixesToKeepArr URL-Pr�fixe f�r zu verschonende URLs. Wenn eine
   *        URL einem dieser Pr�fixe entspricht, dann soll sie auch verschont
   *        werden.
   * @throws RegainException Wenn ein Indexeintrag entweder nicht gelesen oder
   *         nicht gel�scht werden konnte.
   */
  public void removeObsoleteEntires(HashSet urlToKeepSet,
    String[] prefixesToKeepArr)
    throws RegainException
  {
    if (! mUpdateIndex) {
      // Wir haben einen komplett neuen Index erstellt
      // -> Es kann keine Eintr�ge zu nicht vorhandenen Dokumenten geben
      // -> Wir sind fertig
      return;
    }

    setIndexMode(DELETING_MODE);
    int docCount = mIndexReader.numDocs();
    for (int docIdx = 0; docIdx < docCount; docIdx++) {
      if (! mIndexReader.isDeleted(docIdx)) {
        // Document lesen
        Document doc;
        try {
          doc = mIndexReader.document(docIdx);
        }
        catch (Throwable thr) {
          throw new RegainException("Getting document #" + docIdx
            + " from index failed.", thr);
        }

        // URL und last-modified holen
        String url = doc.get("url");
        String lastModified = doc.get("last-modified");

        // Pr�fen, ob die URL gel�scht werden soll
        boolean shouldBeDeleted;
        if (url != null) {
          // Pr�fen, ob dieser Eintrag zum L�schen vorgesehen ist
          if (isMarkedForDeletion(doc)) {
            shouldBeDeleted = true;
          }
          // Pr�fen, ob dieser Eintrag zu verschonen ist
          else if (urlToKeepSet.contains(url)) {
            shouldBeDeleted = false;
          }
          // Pr�fen, ob die URL zu einem zu-verschonen-Pr�fix passt
          else {
            shouldBeDeleted = true;
            for (int i = 0; i < prefixesToKeepArr.length; i++) {
              if (url.startsWith(prefixesToKeepArr[i])) {
                shouldBeDeleted = false;
                break;
              }
            }
          }

          if (shouldBeDeleted) {
            try {
              mLog.info("Deleting from index: " + url + " from " + lastModified);
              mIndexReader.delete(docIdx);
            }
            catch (IOException exc) {
              throw new RegainException("Deleting document #" + docIdx
                + " from index failed: " + url + " from " + lastModified, exc);
            }
          }
        }
      }
    }

    // Merkliste der zu l�schenden Eintr�ge l�schen
    mUrlsToDeleteHash = null;
  }


  /**
   * Merkt ein Dokument f�r die sp�tere L�schung vor.
   * <p>
   * Diese Methode ist Teil eines Workaround: Ein alter Eintrag, der durch einen
   * neuen ersetzt wird, wird nicht sofort gel�scht, sondern nur zur L�schung
   * vorgemerkt. Auf diese Weise wird ein seltener Fehler umgangen, der das
   * Schlie�en des IndexWriter verhindert, wenn h�ufig zwischen InderWriter und
   * IndexReader gewechselt wird.
   *
   * @param doc Das vorzumerkende Dokument.
   */
  private void markForDeletion(Document doc) {
    if (mUrlsToDeleteHash == null) {
      mUrlsToDeleteHash = new HashMap();
    }

    String url = doc.get("url");
    String lastModified = doc.get("last-modified");
    if ((url != null) || (lastModified != null)) {
      mLog.info("Marking old entry for a later deletion: " + url + " from "
        + lastModified);
      mUrlsToDeleteHash.put(url, lastModified);
    }
  }


  /**
   * Gibt zur�ck, ob ein Dokument f�r die L�schung vorgemerkt wurde.
   *
   * @param doc Das zu pr�fende Dokument.
   * @return Ob das Dokument f�r die L�schung vorgemerkt wurde.
   */
  private boolean isMarkedForDeletion(Document doc) {
    String url = doc.get("url");
    String lastModified = doc.get("last-modified");

    if ((url == null) || (lastModified == null)) {
      // url und last-modified sind Mussfelder
      // Da eines fehlt -> Dokument l�schen
      return true;
    }

    if (mUrlsToDeleteHash == null) {
      // Es sind gar keine Dokumente zum L�schen vorgemerkt
      return false;
    }

    // Pr�fen, ob es einen Eintrag f�r diese URL gibt und ob er dem
    // last-modified des Dokuments entspricht
    String lastModifiedToDelete = (String) mUrlsToDeleteHash.get(url);
    return lastModified.equals(lastModifiedToDelete);
  }


  /**
   * Gibt die Anzahl der Eintr�ge im Index zur�ck.
   *
   * @return Die Anzahl der Eintr�ge im Index.
   * @throws RegainException Wenn die Anzahl nicht ermittelt werden konnte.
   */
  public int getIndexEntryCount() throws RegainException {
    if (mIndexReader != null) {
      return mIndexReader.numDocs();
    } else {
      setIndexMode(ADDING_MODE);
      return mIndexWriter.docCount();
    }
  }


  /**
   * Optimiert und schlie�t den Index
   *
   * @param putIntoQuarantine Gibt an, ob der Index in Quarant�ne soll.
   * @throws RegainException Wenn der Index nicht geschlossen werden konnte.
   */
  public void close(boolean putIntoQuarantine) throws RegainException {
    // Ressourcen der DocumentFactory freigeben
    mDocumentFactory.close();

    // Testen, ob noch Eintr�ge f�r die L�schung vorgesehen sind
    if (mUrlsToDeleteHash != null) {
      throw new RegainException("There are still documents marked for deletion."
        + " The method removeObsoleteEntires(...) has to be called first.");
    }

    // Index optimieren und schlie�en
    try {
      setIndexMode(ADDING_MODE);
      mIndexWriter.optimize();
    }
    catch (IOException exc) {
      throw new RegainException("Finishing IndexWriter failed", exc);
    }

    // Switch to FINISHED_MODE
    setIndexMode(FINISHED_MODE);

    // Write all terms in the index into a file
    if (mAnalysisDir != null) {
      File termFile = new File(mAnalysisDir.getAbsolutePath() + File.separator
                               + "AllTerms.txt");
      writeTermFile(mTempIndexDir, termFile);
    }

    // Verzeichnis bestimmen, in das der Index kommen soll
    File targetDir;
    if (putIntoQuarantine) {
      targetDir = mQuarantineIndexDir;
    } else {
      targetDir = mNewIndexDir;
    }

    // If there is already the target directory -> delete it
    if (targetDir.exists()) {
      // We rename it before deletion so there will be no problems when the
      // search mask tries not to switch to the new index during deletion. This
      // case is very unlikely but it may happen once in 100.000 years...
      File secureDir = new File(targetDir.getAbsolutePath() + "_del");
      if (targetDir.renameTo(secureDir)) {
        RegainToolkit.deleteDirectory(secureDir);
      } else {
        // It really happend: The search mask tries to get the new index right now.
        // -> In this case we do nothing (The new index will stay in the temp dir
        //    and the next operation (renaming temp to new) will fail).
      }
    }

    // Let the new index become the working index

    // Workaround: Siehe Javadoc von RENAME_TIMEOUT
    long deadline = System.currentTimeMillis() + RENAME_TIMEOUT;
    boolean renameSucceed = false;
    while ((! renameSucceed) && (System.currentTimeMillis() < deadline)) {
      renameSucceed = mTempIndexDir.renameTo(targetDir);
      try {
        Thread.sleep(100);
      }
      catch (Exception exc) {}
    }

    if (! renameSucceed) {
      throw new RegainException("Renaming " + mTempIndexDir + " to " + targetDir
        + " failed after " + (RENAME_TIMEOUT / 1000) + " seconds!");
    }
  }


  /**
   * Erzeugt eine Datei, die alle Terme (also alle erlaubten Suchtexte) enth�lt.
   *
   * @param indexDir Das Verzeichnis, in dem der Index steht.
   * @param termFile Der Ort, wo die Datei erstellt werden soll.
   *
   * @throws RegainException Wenn die Erstellung fehlgeschlagen ist.
   */
  private void writeTermFile(File indexDir, File termFile) throws RegainException {
    IndexReader reader = null;
    FileOutputStream stream = null;
    PrintWriter writer = null;
    try {
      reader = IndexReader.open(indexDir);

      stream = new FileOutputStream(termFile);
      writer = new PrintWriter(stream);
      writer.println("This file was generated by the crawler and contains all "
                     + "terms in the index.");
      writer.println("It's no error when endings like 'e', 'en', and so on "
                     + "are missing.");
      writer.println("They have been cuttet by the GermanAnalyzer and will be "
                     + "cuttet from a search query too.");
      writer.println();

      // Write the terms
      TermEnum termEnum = reader.terms();
      int termCount;
      if (WRITE_TERMS_SORTED) {
        termCount = writeTermsSorted(termEnum, writer);
      } else {
        termCount = writeTermsSimply(termEnum, writer);
      }

      mLog.info("Wrote " + termCount + " terms into " + termFile.getAbsolutePath());
    }
    catch (IOException exc) {
      throw new RegainException("Writing term file failed", exc);
    }
    finally {
      if (reader != null) {
        try { reader.close(); } catch (IOException exc) {}
      }
      if (writer != null) {
        writer.close();
      }
      if (stream != null) {
        try { stream.close(); } catch (IOException exc) {}
      }
    }
  }



  /**
   * Schreibt die Terme so wie sie vom IndexReader kommen in den Writer.
   * <p>
   * Diese Methode braucht minimale Ressourcen.
   *
   * @param termEnum Die Aufz�hlung mit allen Termen.
   * @param writer Der Writer auf den geschrieben werden soll.
   *
   * @return Die Anzahl der Terme.
   * @throws IOException Wenn das Schreiben fehl schlug.
   */
  private int writeTermsSimply(TermEnum termEnum, PrintWriter writer)
    throws IOException
  {
    int termCount = 0;
    while (termEnum.next()) {
      Term term = termEnum.term();
      writer.println(term.text());
      termCount++;
    }

    return termCount;
  }



  /**
   * Schreibt die Terme vom IndexReader sortiert in den Writer.
   * <p>
   * Um die Terme sortieren zu k�nnen, m�ssen sie zwischengespeichert werden. Falls
   * es zu viele sind, k�nnte das schief gehen. In diesem Fall sollte man auf simples
   * Schreiben umstellen.
   *
   * @param termEnum Die Aufz�hlung mit allen Termen.
   * @param writer Der Writer auf den geschrieben werden soll.
   *
   * @return Die Anzahl der Terme.
   * @throws IOException Wenn das Schreiben fehl schlug.
   */
  private int writeTermsSorted(TermEnum termEnum, PrintWriter writer)
    throws IOException
  {
    // Put all terms in a list for a later sorting
    ArrayList list = new ArrayList();
    while (termEnum.next()) {
      Term term = termEnum.term();
      list.add(term.text());
    }

    String[] asArr = new String[list.size()];
    list.toArray(asArr);

    // Sort the terms
    Arrays.sort(asArr);

    // Write them to the writer
    for (int i = 0; i < asArr.length; i++) {
      writer.println(asArr[i]);
    }

    return asArr.length;
  }

}
