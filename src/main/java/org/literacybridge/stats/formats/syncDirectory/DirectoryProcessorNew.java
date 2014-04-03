package org.literacybridge.stats.formats.syncDirectory;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.StringUtils;
import org.literacybridge.stats.api.TalkingBookDataProcessor;
import org.literacybridge.stats.formats.exceptions.CorruptFileException;
import org.literacybridge.stats.formats.ProcessingContext;
import org.literacybridge.stats.formats.SyncProcessingContext;
import org.literacybridge.stats.formats.flashData.FlashData;
import org.literacybridge.stats.formats.flashData.SystemData;
import org.literacybridge.stats.formats.formats.logFile.LogFileParser;
import org.literacybridge.stats.formats.statsFile.StatsFile;
import org.literacybridge.stats.model.SyncDirId;
import org.literacybridge.stats.processors.AbstractDirectoryProcessor;
import org.literacybridge.utils.FsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
*/
public class DirectoryProcessorNew extends AbstractDirectoryProcessor {
  protected static final Logger logger = LoggerFactory.getLogger(DirectoryProcessorNew.class);

  public static final Pattern ARCHIVED_LOG_PATTERN = Pattern.compile("log_(.*).txt");

  //Stats files don't have any "."s in them (because they have no file extensions)
  public static final Pattern STATS_FILE_PATTERN = Pattern.compile("([^.]+)");


  private ProcessingContext currProcessingContext;
  private Set<String> processedLogFiles = new HashSet<>();

  final List<TalkingBookDataProcessor> dataProcessorEventListeners;
  final Map<String, String>            categoryMap;

  public DirectoryProcessorNew(TalkingBookDataProcessor dataProcessorEventListeners, Map<String, String> categoryMap) {
    this.dataProcessorEventListeners = Lists.newArrayList(dataProcessorEventListeners);
    this.categoryMap = categoryMap;
  }


  public DirectoryProcessorNew(List<TalkingBookDataProcessor> dataProcessorEventListeners,
                               Map<String, String> categoryMap) {
    this.dataProcessorEventListeners = dataProcessorEventListeners;
    this.categoryMap = categoryMap;
  }


  @Override
  public boolean startTalkingBook(String talkingBook) throws Exception {
    super.startTalkingBook(talkingBook);

    currTalkingBook = talkingBook;

    //UNDONE(willpugh) -- ContentPackage is still not super well defined or baked into our structures.  Need to fix this, although, I think the new term is "image"
    currProcessingContext = new ProcessingContext(currTalkingBook, currVillage, currDeploymentPerDevice.deployment,
                                                  currDeploymentPerDevice.deployment, currDeploymentPerDevice.device);
    for (TalkingBookDataProcessor processor : dataProcessorEventListeners) {
      processor.onTalkingBookStart(currProcessingContext);
    }

    processedLogFiles.clear();
    return true;
  }

  @Override
  public void endTalkingBook() {
    for (TalkingBookDataProcessor processor : dataProcessorEventListeners) {
      processor.onTalkingBookEnd(currProcessingContext);
    }

    currProcessingContext = null;
    processedLogFiles.clear();
    super.endTalkingBook();
  }


  @Override
  public void processSyncDir(SyncDirId syncDirId, File syncDir) throws Exception {
    final FlashData flashData = loadFlashDataFile(syncDir);
    final SyncProcessingContext syncProcessingContext = determineProcessingContext(currDeploymentPerDevice.device, syncDir, currTalkingBook, currDeploymentPerDevice.deployment, currVillage, flashData);

    if (flashData != null) {
      processFlashData(syncProcessingContext, flashData);
    }

    processSyncDir(syncDir, syncProcessingContext, processedLogFiles, true);
  }

  /**
   * Processes a FlashData file to call all the registered callbacks.
   *
   * @param syncProcessingContext context this processing occurs in
   * @param flashData the flashdata file.  If this is null, this function will be a no-op.
   */
  public void processFlashData(final SyncProcessingContext syncProcessingContext, @Nullable final FlashData flashData) throws
                                                                                                                       IOException {
    if (flashData != null) {
      for (TalkingBookDataProcessor events : dataProcessorEventListeners) {
        events.processFlashData(syncProcessingContext, flashData);
      }
    }
  }

  /**
   * Processes a Sync directory.  This will process the following:
   * <p/>
   * <ul>
   * <li>if processInProcessLog is true:  log/log.txt</li>
   * <li>log-archive/log_*.txt</li>
   * <li>statistics/stats/*</li>
   * </ul>
   * <p/>
   * In addition, it will not processes files in processedFiles, and will add the files it processes into it when done.  Both the
   * {@code processedFiles} + {@code processInProcessLog} parameters are meant to prevent multiple counting of data when merging unsuccessful syncs.
   *
   * @param syncDir
   * @param syncProcessingContext
   * @param processedFiles
   * @param processInProcessLog
   */
  public void processSyncDir(final File syncDir, final SyncProcessingContext syncProcessingContext,
                             final Set<String> processedFiles, final boolean processInProcessLog) throws
                                                                                                  IOException {


    //Create a list of LogFileParsers that take the callback interfaces and the syncProcessingContexts.
    LogFileParser parser = new LogFileParser(dataProcessorEventListeners, syncProcessingContext, categoryMap);

    //Process the current log and the flashData files, if this is the latest dir
    if (processInProcessLog) {
      final File logFile = new File(new File(syncDir, "log"), "log.txt");
      if (logFile.canRead()) {
        processLogFile(logFile, parser, processedFiles);
      }
    }

    //Process all the Archive Files
    final File logArchives = new File(syncDir, "log-archive");
    if (logArchives.isDirectory()) {
      final Iterator<File> archivedLogFiles = FileUtils.iterateFiles(logArchives,
                                                                     new RegexFileFilter(ARCHIVED_LOG_PATTERN),
                                                                     FalseFileFilter.FALSE);
      while (archivedLogFiles.hasNext()) {
        final File archivedFile = archivedLogFiles.next();
        processLogFile(archivedFile, parser, processedFiles);
      }
    }

    //Process all the Stats files
    final File statDir = new File(new File(syncDir, "statistics"), "stats");
    if (statDir.canRead()) {
      if (statDir.isDirectory()) {
        final Iterator<File> statsFiles = FileUtils.iterateFiles(statDir, new RegexFileFilter(STATS_FILE_PATTERN),
                                                                 FalseFileFilter.FALSE);
        while (statsFiles.hasNext()) {
          runCallbacksOnStatsFile(syncProcessingContext, statsFiles.next());
        }
      } else {
        logger.error(statDir.getAbsolutePath() + " is NOT a directory.");
      }
    } else {
      logger.error("Cannot read " + statDir.getAbsolutePath());
    }
  }


  public void processLogFile(File file, LogFileParser parser, Set<String> processedFiles) {

    final String fileProcessingName = file.getParent() + "/" + file.getName();
    if (!processedFiles.contains(fileProcessingName)) {
      try {
        runCallbacksOnLogFile(file, parser);
        processedFiles.add(fileProcessingName);
      } catch (IOException ioe) {
        final String errorString = String.format("Unable to process %s.  Error=%s", file.getAbsolutePath(),
                                                 ioe.getMessage());
        logger.error(errorString, ioe);
      }

    }
  }

  static public void runCallbacksOnLogFile(File file, LogFileParser parser) throws IOException {
    FileInputStream fis = new FileInputStream(file);
    try {
      parser.parse(file.getAbsolutePath(), fis);
    } finally {
      IOUtils.closeQuietly(fis);
    }

  }

  public void runCallbacksOnStatsFile(final SyncProcessingContext syncProcessingContext, final File file) {

    try {
      FileInputStream fis = new FileInputStream(file);

      try {
        StatsFile statsFile = StatsFile.read(fis);
        for (TalkingBookDataProcessor callbacks : dataProcessorEventListeners) {
          callbacks.processStatsFile(syncProcessingContext, file.getName(), statsFile);
        }
      } catch (CorruptFileException e) {
        for (TalkingBookDataProcessor callbacks : dataProcessorEventListeners) {
          callbacks.markStatsFileAsCorrupted(syncProcessingContext, file.getName(), e.getMessage());
        }
      }
    } catch (IOException e) {
      logger.error("Could not load stats file", e);
    }

  }


  /**
   * Loads a FlashData file from a given sync directory.  This file was introduced
   * in a more recent update, so will not be around for all updates.
   *
   * @param syncDir
   * @return {@null} if the file is not there
   * @throws java.io.IOException
   */
  static public FlashData loadFlashDataFile(File syncDir) throws IOException {
    final File flashDataFile = new File(syncDir, FsUtils.FsAgnostify("statistics/stats/flashData.bin"));

    FlashData       retVal  = null;
    FileInputStream fis     = null;
    try {
      if (flashDataFile.exists()) {
        fis = new FileInputStream(flashDataFile);
        retVal = FlashData.parseFromStream(fis);

        LinkedList<String> errors = new LinkedList<>();
        if (!retVal.isValid(errors)) {
          logger.error("Flashdata file look possibly corrupt.  Errors=" + StringUtils.join(errors, "; ") + "Path=" + flashDataFile.getCanonicalPath() + " FlashData=" + retVal.toString());
        }
      }
    } finally {
      IOUtils.closeQuietly(fis);
    }

    return retVal;
  }

  /**
   * Creates a processing context.  There are two ways to do this:
   *
   * <ol>
   *   <li>Use the flashData.bin file and get the info there there.  This is the MOST reliable way to get some stat, since this info comes from the NOR flash on the
   *   device, which tends to have corruption a lot less.  The only problem is that this is a newer mechanism and so there are a lot of times this file does not exist.
   *   </li>
   *   <li>
   *     Use the filepath conventions to figure this out.  This way always exists, but this information ultimately comes from memory on the device that we have seen
   *     corruption occur in.  However, for some information such as talkingBook ID, contentUpdate and village name, this can be the best way, because we have other
   *     mechanisms to fix corruption here.
   *   </li>
   * </ol>
   *
   * @param syncDevice The name of the device this information is being synched from.  THis is NOT the TalkingBook, this is the laptop or
   *                   tablet used to sync many talking books.
   * @param syncDir    The directory this sync is occuring from.
   * @param talkingBookId the ID of the talking book, as determined from the file system
   * @param contentUpdate the update string, as determined from the file system
   * @param villageName the village name the talking book was deployed in, as determined from the file system
   * @param flashData the flashdata file, if it exists for this sync.
   * @return
   */
  static public SyncProcessingContext determineProcessingContext(String syncDevice, File syncDir, String talkingBookId,
                                                                 String contentUpdate, String villageName, @Nullable FlashData flashData) {

    SyncProcessingContext retVal;
    if (flashData != null) {

      final SystemData systemData =  flashData.getSystemData();
      retVal = new SyncProcessingContext(syncDir.getName(),
                                         talkingBookId,
                                         villageName,
                                         StringUtils.defaultIfEmpty(systemData.getContentPackage(), contentUpdate),
                                         contentUpdate,
                                         syncDevice);
    } else {
      retVal = new SyncProcessingContext(syncDir.getName(),
                                         talkingBookId,
                                         villageName,
                                         contentUpdate,
                                         contentUpdate,
                                         syncDevice);
    }

    return retVal;
  }

}