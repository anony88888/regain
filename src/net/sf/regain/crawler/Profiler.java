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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import net.sf.regain.RegainToolkit;

import org.apache.log4j.Logger;

/**
 * Misst die Zeit und den Datendurchsatz f�r einen Verarbeitungsschritt.
 *
 * @author Til Schneider, www.murfman.de
 */
public class Profiler {

  /** The logger for this class */
  private static Logger mLog = Logger.getLogger(Profiler.class);

  /** Eine Liste mit allen erzeugten Profilern. */
  private static ArrayList mProfilerList;

  /** Der Name. */
  private String mName;
  /** Der Einheit, die gemessen wird. */
  private String mUnit;
  /** Die kummulierte Gesamtzeit. */
  private long mTotalTime;
  /** Die kummulierte Datenmenge. */
  private long mTotalBytes;
  /** Die Anzahl der Messungen. */
  private int mMeasureCount;
  /** Die Anzahl der abgebrochenen Messungen. */
  private int mAbortedMeasureCount;
  /**
   * Die Zeit, zu der die laufende Messung begonnen hat. Ist -1, wenn keine
   * Messung l�uft.
   */
  private long mMeasureStart = -1;



  /**
   * Erzeugt eine neue Profiler-Instanz und registriert sie bei der
   * Profiler-Liste.
   *
   * @param name Der Name des Verarbeitungsschrittes, der mit diesem Profiler
   *        gemessen werden sollen.
   * @param unit Die Bezeichnung der Dinge, die der Verarbeitungsschritt
   *        verarbeitet, z.B. <code>documents</code>.
   */
  public Profiler(String name, String unit) {
    mName = name;
    mUnit = unit;

    registerProfiler(this);
  }
  
  
  /**
   * Gets the number of measures.
   * 
   * @return The number of measures.
   */
  public int getMeasureCount() {
    return mMeasureCount;
  }
  
  
  /**
   * Gets the number of aborted measures.
   * 
   * @return The number of aborted measures.
   */
  public int getAbortedMeasureCount() {
    return mAbortedMeasureCount;
  }


  /**
   * Registriert einen Profiler.
   *
   * @param profiler Der zu registrierende Profiler.
   */
  private static synchronized void registerProfiler(Profiler profiler) {
    if (mProfilerList == null) {
      mProfilerList = new ArrayList();
    }

    mProfilerList.add(profiler);
  }



  /**
   * Startet eine Messung.
   */
  public void startMeasuring() {
    if (mMeasureStart != -1) {
      mLog.warn("A profiler measuring for " + mName + " was started, although "
        + "there is currently a measuring running!");
    }
    mMeasureStart = System.currentTimeMillis();
  }



  /**
   * Stoppt eine Messung.
   *
   * @param bytes Die Anzahl der verarbeiteten Bytes.
   */
  public void stopMeasuring(long bytes) {
    if (mMeasureStart == -1) {
      mLog.warn("A profiler measuring for " + mName + " was stopped, although "
        + "there was currently no measuring running!");
    } else {
      mTotalTime += System.currentTimeMillis() - mMeasureStart;
      mTotalBytes += bytes;
      mMeasureCount++;

      mMeasureStart = -1;
    }
  }



  /**
   * Bricht eine Messung ab. Eine Messung wird dann abgebrochen, wenn der
   * Verarbeitungsschritt nicht korrekt verlaufen ist, z.B. weil eine
   * Exception geworfen wurde.
   */
  public void abortMeasuring() {
    if (mMeasureStart == -1) {
      mLog.warn("A profiler measuring for " + mName + " was aborted, although "
        + "there was currently no measuring running!");
    } else {
      mMeasureStart = -1;
      mAbortedMeasureCount++;
    }
  }



  /**
   * Gibt das Resultat der Messungen als String zur�ck.
   *
   * @return Das Resultat der Messungen
   */
  public String toString() {
    // Get a current snap shot
    long totalTime = mTotalTime;
    long totalBytes = mTotalBytes;
    int measureCount = mMeasureCount;
    int abortedMeasureCount = mAbortedMeasureCount;
    
    // Calculate the results
    long averageTime = 0;
    long averageBytes = 0;
    if (measureCount > 0) {
      averageTime = totalTime / measureCount;
      averageBytes = totalBytes / measureCount;
    }

    long dataRatePerSec = 0;
    double secs = totalTime / 1000.0;
    if (secs > 0) {
      dataRatePerSec = (long) (totalBytes / secs);
    }
    
    long countsPerMinute = 0;
    if (totalTime > 0) {
      countsPerMinute = measureCount * (60 * 1000) / totalTime;
    }

    // Berechnen, wie gro� die Labels sein m�ssen
    int maxStaticLabelLength = 12;                   // "Average time"
    int maxDynamicLabelLength = 10 + mUnit.length(); // "Completed " + mUnit
    int minLabelLength = Math.max(maxStaticLabelLength, maxDynamicLabelLength);

    // Systemspeziefischen Zeilenumbruch holen
    String lineSeparator = RegainToolkit.getLineSeparator();

    // Statistik ausgeben
    StringBuffer buffer = new StringBuffer(mName + ":");
    NumberFormat numberFormat = NumberFormat.getInstance();
    if (abortedMeasureCount > 0) {
      buffer.append(lineSeparator);

      appendLabel(buffer, "Aborted " + mUnit, minLabelLength);
      buffer.append(numberFormat.format(abortedMeasureCount) + " " + mUnit + " (");

      // Ausgeben, wieviel % der Messungen fehl schlugen
      int total = abortedMeasureCount + measureCount;
      double errorPercent = (double) abortedMeasureCount / (double) total;
      buffer.append(RegainToolkit.toPercentString(errorPercent));

      buffer.append(")");
    }
    if (measureCount > 0) {
      buffer.append(lineSeparator);

      appendLabel(buffer, "Completed " + mUnit, minLabelLength);
      buffer.append(numberFormat.format(measureCount) + " " + mUnit + lineSeparator);

      appendLabel(buffer, "Total time", minLabelLength);
      buffer.append(toTimeString(totalTime) + lineSeparator);

      appendLabel(buffer, "Total data", minLabelLength);
      buffer.append(RegainToolkit.bytesToString(totalBytes) + lineSeparator);

      appendLabel(buffer, "Average time", minLabelLength);
      buffer.append(toTimeString(averageTime) + lineSeparator);

      appendLabel(buffer, "Average data", minLabelLength);
      buffer.append(RegainToolkit.bytesToString(averageBytes) + lineSeparator);

      appendLabel(buffer, "Data rate", minLabelLength);
      buffer.append(RegainToolkit.bytesToString(dataRatePerSec) + "/sec" + lineSeparator);

      appendLabel(buffer, "Output", minLabelLength);
      buffer.append(numberFormat.format(countsPerMinute) + " " + mUnit + "/min");
    }

    return buffer.toString();
  }


  /**
   * F�gt bei einem StringBuffer eine Beschriftung hinzu. Dabei werden so viele
   * Leerzeichen angeh�ngt, dass alle Beschriftungen auf selber H�he enden.
   *
   * @param buffer Der StringBuffer bei dem die Beschriftung hinzugef�gt werden
   *        soll.
   * @param label Die Beschriftung, die hinzugef�gt werden soll.
   * @param minLabelLength Die minimale L�nge der Beschriftung. (Der Rest wird
   *        mit Leerzeichen aufgef�llt).
   */
  private void appendLabel(StringBuffer buffer, String label,
    int minLabelLength)
  {
    buffer.append("  ");
    buffer.append(label);
    buffer.append(": ");

    int spaceCount = minLabelLength - label.length();
    for (int i = 0; i < spaceCount; i++) {
      buffer.append(' ');
    }
  }


  /**
   * Gibt einen f�r den Menschen gut lesbaren String f�r eine Zeit zur�ck.
   *
   * @param time Die Zeit in Millisekunden
   * @return Die Zeit als String
   */
  public String toTimeString(long time) {
    long millis = time % 1000;
    time /= 1000;
    long secs = time % 60;
    time /= 60;
    long mins = time % 60;
    time /= 60;
    long hours = time;

    if (hours != 0) {
      return hours + ":"
        + ((mins > 9) ? "" : "0") + mins + ":"
        + ((secs > 9) ? "" : "0") + secs + " h";
    }
    else if (mins != 0) {
      return mins + ":"
        + ((secs > 9) ? "" : "0") + secs + " min";
    }
    else if (secs != 0) {
      NumberFormat format = NumberFormat.getInstance();
      format.setMinimumFractionDigits(2);
      format.setMaximumFractionDigits(2);

      String asString = format.format(secs + millis / 1000.0);

      return asString + " sec";
    }
    else {
      return millis + " millis";
    }
  }



  /**
   * Gibt zur�ck, ob dieser Profiler genutzt wurde. Das ist der Fall, wenn
   * mindestens eine Messung durchgef�hrt wurde.
   *
   * @return Ob dieser Profiler genutzt wurde.
   */
  public boolean wasUsed() {
    return (mMeasureCount > 0) || (mAbortedMeasureCount > 0);
  }



  /**
   * Gibt die Resultate s�mtlicher genutzter Profiler zur�ck.
   *
   * @return Die Resultate s�mtlicher genutzter Profiler.
   */
  public static String getProfilerResults() {
    if (mProfilerList == null) {
      return "";
    }
    
    StringBuffer buffer = new StringBuffer();

    for (Iterator iter = mProfilerList.iterator(); iter.hasNext();) {
      Profiler profiler = (Profiler) iter.next();

      if (profiler.wasUsed()) {
        buffer.append(profiler);
        buffer.append(RegainToolkit.getLineSeparator());
      }
    }

    return buffer.toString();
  }

}
