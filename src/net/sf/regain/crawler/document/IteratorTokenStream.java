package net.sf.regain.crawler.document;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

/**
 * A token stream reading tokens from an iterator.
 *
 * @author Til Schneider, www.murfman.de
 */
public class IteratorTokenStream extends TokenStream {

  /** An iterator providing Strings. */
  private Iterator mIter;


  /**
   * Creates a new instance of this class.
   *
   * @param iter An iterator providing Strings.
   */
  public IteratorTokenStream(Iterator iter) {
    mIter = iter;
  }


  // overridden
  public Token next() throws IOException {
    if (mIter.hasNext()) {
      String text = (String) mIter.next();
      return new Token(text, 0, text.length());
    } else {
      return null;
    }
  }

}
