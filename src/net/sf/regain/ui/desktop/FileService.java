package net.sf.regain.ui.desktop;

import java.io.File;
import java.net.URLDecoder;

import net.sf.regain.RegainException;
import net.sf.regain.RegainToolkit;
import net.sf.regain.crawler.config.CrawlerConfig;
import net.sf.regain.crawler.config.StartUrl;
import net.sf.regain.crawler.config.XmlCrawlerConfig;
import net.sf.regain.util.sharedtag.simple.SharedTagService;
import simple.http.Request;
import simple.http.Response;
import simple.http.load.BasicService;
import simple.http.serve.Context;

/**
 * A simpleweb service providing files. For security reasons this service only
 * provides files that are in the index.
 *
 * @author Til Schneider, www.murfman.de
 */
public class FileService extends BasicService {
  
  // TODO: Change this service to use the index rather than the crawler config.
  
  /** The file that holds the crawler configuration. */
  private File mCrawlerConfigFile;
  
  /**
   * The time when the crawler configuration was last modified. Used to decide
   * whether the configuration has to be reloaded.
   */
  private long mCrawlerConfigLastModified;
  
  /**
   * The crawler configuration. The configuration is used to decide whether a
   * file access is allowed or not.
   */
  private CrawlerConfig mConfig;
  

  /**
   * Creates a new instance of FileService.
   * 
   * @param context The context of this service.
   */
  public FileService(Context context) {
    super(context);
    
    mCrawlerConfigFile = new File("conf/CrawlerConfiguration.xml");
    mCrawlerConfigLastModified = -1;
  }


  /**
   * Processes a request.
   * 
   * @param req The request.
   * @param resp The response.
   * @throws Exception If executing the JSP page failed.
   */
  public void process(Request req, Response resp) throws Exception {
    String filename = req.getURI();
    if (filename.startsWith("/file/")) {
      filename = filename.substring(6);
    }
    filename = URLDecoder.decode(filename);
    System.out.println("filename: '" + filename + "'");

    // Check whether this request comes from localhost
    boolean localhost = req.getInetAddress().isLoopbackAddress();
    if (! localhost) {
      // This request does not come from localhost -> Send 403 Forbidden
      handle(req, resp, 403);
    }
    
    // Check the filename
    if (isAllowed(filename)) {
      // This file is allowed -> Send it
      SharedTagService.processFile(req, resp, new File(filename));
    } else {
      // This file is not allowed -> Send 403 Forbidden
      handle(req, resp, 403);
    }
  }

  
  /**
   * Decides whether a file access is allowed or not.
   * 
   * @param filename The name of the file to check.
   * @return Whether a file access is allowed or not.
   * @throws RegainException If loading the crawler config failed.
   */
  private boolean isAllowed(String filename) throws RegainException {
    CrawlerConfig config = getCrawlerConfig();
    
    String url = RegainToolkit.fileToUrl(new File(filename));
    
    boolean ignoreCase = File.separator.endsWith("\\");
    if (ignoreCase) {
      url = url.toLowerCase();
    }
    
    // Check the white list
    StartUrl[] startlist = config.getStartUrls();
    boolean isAllowed = false;
    for (int i = 0; i < startlist.length; i++) {
      String urlPrefix = startlist[i].getUrl();
      urlPrefix = RegainToolkit.replace(urlPrefix, " ", "%20");
      if (ignoreCase) {
        urlPrefix = urlPrefix.toLowerCase();
      }

      System.out.println("url:       '" + url + "'");
      System.out.println("urlPrefix: '" + urlPrefix + "'");
      if (url.startsWith(urlPrefix)) {
        isAllowed = true;
        break;
      }
    }
    if (! isAllowed) {
      return false;
    }
    
    // Check the black list
    String[] blacklist = config.getUrlPrefixBlackList();
    for (int i = 0; i < blacklist.length; i++) {
      String urlPrefix = blacklist[i];
      urlPrefix = RegainToolkit.replace(urlPrefix, " ", "%20");
      if (ignoreCase) {
        urlPrefix = urlPrefix.toLowerCase();
      }

      if (url.startsWith(urlPrefix)) {
        return false;
      }
    }
    
    // All tests passed
    return true;
  }
  
  
  /**
   * Loads the crawler configuration if nessesary.
   * 
   * @return The crawler configuration.
   * @throws RegainException If loading the crawler configuration failed.
   */
  private CrawlerConfig getCrawlerConfig() throws RegainException {
    long lastModified = mCrawlerConfigFile.lastModified();
    if (mCrawlerConfigLastModified != lastModified) {
      // The config was modified -> Load it
      mConfig = new XmlCrawlerConfig(mCrawlerConfigFile);
      
      mCrawlerConfigLastModified = lastModified;
    }
    
    return mConfig;
  }
  
  
}
