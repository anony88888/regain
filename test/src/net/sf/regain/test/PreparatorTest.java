/*
 * CVS information:
 *  $RCSfile$
 *   $Source$
 *     $Date$
 *   $Author$
 * $Revision$
 */
package net.sf.regain.test;

import java.io.File;
import java.util.ArrayList;

import net.sf.regain.crawler.CrawlerToolkit;
import net.sf.regain.crawler.Profiler;
import net.sf.regain.crawler.document.RawDocument;
import net.sf.regain.crawler.preparator.AbstractPreparator;
import net.sf.regain.crawler.preparator.JacobMsPowerPointPreparator;
import net.sf.regain.crawler.preparator.JacobMsWordPreparator;
import net.sf.regain.crawler.preparator.PdfPreparator;
import net.sf.regain.crawler.preparator.PlainTextPreparator;
import net.sf.regain.crawler.preparator.PoiMsExcelPreparator;
import net.sf.regain.crawler.preparator.PoiMsWordPreparator;
import net.sf.regain.crawler.preparator.SimpleRtfPreparator;
import net.sf.regain.crawler.preparator.SwingRtfPreparator;
import net.sf.regain.crawler.preparator.XmlPreparator;

/**
 * Tests all the preparators
 * 
 * @author Tilman Schneider, www.murfman.de
 */
public class PreparatorTest {
  
  /** The profilers that measured the work of the preparators. */
  private static ArrayList mProfilerList;


  /**
   * Does the test
   * 
   * @param args The Command line arguments
   */
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Usage: [test document directory] [output directory]");
      System.exit(1);
    }
    
    File docDir = new File(args[0]);
    File outputDir = new File(args[1]);
    
    mProfilerList = new ArrayList();
    
    // testPreparator(new HtmlPreparator());
    testPreparator(docDir, outputDir, "doc", new JacobMsWordPreparator());
    testPreparator(docDir, outputDir, "doc", new PoiMsWordPreparator());
    testPreparator(docDir, outputDir, "pdf", new PdfPreparator());
    testPreparator(docDir, outputDir, "ppt", new JacobMsPowerPointPreparator());
    testPreparator(docDir, outputDir, "rtf", new SimpleRtfPreparator());
    testPreparator(docDir, outputDir, "rtf", new SwingRtfPreparator());
    testPreparator(docDir, outputDir, "txt", new PlainTextPreparator());
    // testPreparator(docDir, outputDir, "xls", new JacobMsExcelPreparator());
    testPreparator(docDir, outputDir, "xls", new PoiMsExcelPreparator());
    testPreparator(docDir, outputDir, "xml", new XmlPreparator());
    
    System.out.println();
    System.out.println("Summary:");
    for (int i = 0; i < mProfilerList.size(); i++) {
      System.out.println(" " + mProfilerList.get(i));
    }
  }

  
  /**
   * Tests one preparator
   * 
   * @param docDir The source directory where the documents are located.
   * @param outputDir The target directory where to write the extracted texts.
   * @param fileType The file type the current preperator takes.
   * @param prep The preparatos to test
   */
  private static void testPreparator(File docDir, File outputDir,
    String fileType, AbstractPreparator prep)
  {
    System.out.println("Testing preparator " + prep.getClass().getName() + "...");
    
    Profiler profiler = new Profiler(prep.getClass().getName(), "docs");
    
    File typeDir = new File(docDir, fileType);
    File prepOutputDir = new File(outputDir, prep.getClass().getName());
    if (! prepOutputDir.mkdir()) {
      System.out.println("Could not create output dir: "
        + prepOutputDir.getAbsolutePath());
      System.exit(1);
    }
    
    File[] docFileArr = typeDir.listFiles();
    String sourceUrl = CrawlerToolkit.fileToUrl(typeDir);
    for (int i = 0; i < docFileArr.length; i++) {
      if (docFileArr[i].isFile()) {
        String url = CrawlerToolkit.fileToUrl(docFileArr[i]);
        try {
          RawDocument doc = new RawDocument(url, sourceUrl, null);

          profiler.startMeasuring();
          try {
            prep.prepare(doc);
            profiler.stopMeasuring(docFileArr[i].length());
          }
          catch (Throwable thr) {
            profiler.abortMeasuring();
            throw thr;
          }

          File outFile = new File(prepOutputDir, docFileArr[i].getName() + ".txt");
          CrawlerToolkit.writeToFile(doc.getContent(), outFile);
          System.out.println("Prepared document: " + url);
        }
        catch (Throwable thr) {
          System.out.println("Preparing document failed: " + url);
          thr.printStackTrace();
        }
      }
    }
    
    try {
      prep.close();
    }
    catch (Throwable thr) {
      System.out.println("Closing preparator failed");
      thr.printStackTrace();
    }
    
    mProfilerList.add(profiler);
  }
  
}
