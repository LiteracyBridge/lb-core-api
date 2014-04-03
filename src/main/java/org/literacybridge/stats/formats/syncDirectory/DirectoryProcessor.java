package org.literacybridge.stats.formats.syncDirectory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.StringUtils;
import org.literacybridge.stats.api.TalkingBookDataProcessor;
import org.literacybridge.stats.formats.exceptions.CorruptFileException;
import org.literacybridge.stats.formats.SyncProcessingContext;
import org.literacybridge.stats.formats.flashData.FlashData;
import org.literacybridge.stats.formats.flashData.SystemData;
import org.literacybridge.stats.formats.formats.logFile.LogFileParser;
import org.literacybridge.stats.formats.statsFile.StatsFile;
import org.literacybridge.stats.model.DeploymentId;
import org.literacybridge.stats.model.SyncDirId;
import org.literacybridge.utils.FsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Directory Structure
 * <p/>
 * Log Data:
 * 2013-03/Baazu-Jirapa/TB00037a/10m4d14h11m28s/log/log.txt
 * 2013-03/Baazu-Jirapa/TB00037a/10m4d14h11m28s/log-archive/log_*.txt
 * <p/>
 * Stats data
 * 2013-03/Baazu-Jirapa/TB00037a/10m4d14h11m28s/statistics/stats/
 * File for every piece of content
 * <p/>
 * Also, flashdata.bin (???)
 */
public class DirectoryProcessor {

  static protected final Logger logger = LoggerFactory.getLogger(DirectoryProcessor.class);
  public static final Map<String, String> CATEGORY_MAP = ImmutableMap.<String, String>builder()
                                                                     .put("1", "AGRIC")
                                                                     .put("1-2", "LIVESTOCK")
                                                                     .put("2", "HEALTH")
                                                                     .put("9", "FEEDBACK")
                                                                     .put("0", "OTHER")
                                                                     .put("$0-1", "TB")
                                                                     .build();


  public static final Pattern UPDATE_PATTERN = Pattern.compile("(\\d+)-(\\d+)");

  //tbData-2013-10-3.txt
  public static final Pattern TBDATA_PATTERN = Pattern.compile("tbData-(\\d+)-(\\d+)-(\\d+).*");
  public static final Pattern ARCHIVED_LOG_PATTERN = Pattern.compile("log_(.*).txt");

  //Stats files don't have any "."s in them (because they have no file extensions)
  public static final Pattern STATS_FILE_PATTERN = Pattern.compile("([^.]+)");

  /**
   * Loads a FlashData file from a given sync directory.  This file was introduced
   * in a more recent update, so will not be around for all updates.
   *
   * @param syncDir
   * @return {@null} if the file is not there
   * @throws IOException
   */
  static public FlashData loadFlashDataFile(File syncDir) throws IOException {
    final File flashDataFile = new File(syncDir, FsUtils.FsAgnostify("statistics/stats/flashData.bin"));

    FlashData       retVal  = null;
    FileInputStream fis     = null;
    try {
      if (flashDataFile.exists()) {
        fis = new FileInputStream(flashDataFile);
        retVal = FlashData.parseFromStream(fis);

        LinkedList<String>  errors = new LinkedList<>();
        if (!retVal.isValid(errors)) {
          logger.error("Flashdata file look possibly corrupt.  Errors=" + StringUtils.join(errors, "; ") + "Path=" + flashDataFile.getCanonicalPath() + " FlashData=" + retVal.toString());
        }
      }
    } finally {
      IOUtils.closeQuietly(fis);
    }

    return retVal;
  }


  final List<TalkingBookDataProcessor>  dataProcessorEventListeners;
  final Map<String, String>   categoryMap;

  public DirectoryProcessor(TalkingBookDataProcessor dataProcessorEventListeners, Map<String, String> categoryMap) {
    this.dataProcessorEventListeners = Lists.newArrayList(dataProcessorEventListeners);
    this.categoryMap = categoryMap;
  }


  public DirectoryProcessor(List<TalkingBookDataProcessor>  dataProcessorEventListeners, Map<String, String> categoryMap) {
    this.dataProcessorEventListeners = dataProcessorEventListeners;
    this.categoryMap = categoryMap;
  }

  public void process(String syncDevice, File root) throws IOException {

    File[] updateFiles = root.listFiles((FilenameFilter) new RegexFileFilter(UPDATE_PATTERN));
    for (File f : updateFiles) {
      processUpdateDir(syncDevice, f);
    }

  }

  public void processUpdateDir(String syncDevice, File updateDir) throws IOException {
    final String contentUpdate = updateDir.getName();

    File[] updateFiles = updateDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
    for (File currFile : updateFiles) {
      processVillageDir(syncDevice, currFile, contentUpdate);
    }
  }

  public void processVillageDir(String syncDevice, File villageDir, String contentUpdate) throws IOException {

    final String villageName = villageDir.getName();

    File[] updateFiles = villageDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
    for (File currFile : updateFiles) {
      processTalkingBookDir(syncDevice, currFile, contentUpdate, villageName.trim());
    }
  }

  /**
   * This processes a talking book's syncOperations-up information.  This directory will contain at least one
   * directory with a name like {@code 10m4d15h19m23s}.  This represents the time the Talking Book was
   * synched and the stats were uploaded.
   * <p/>
   * For various reasons (mostly having to do with incomplete syncs), there may be multiple directories in this directory.
   * <p/>
   * In this case, the proper behaviour is to do a "union" of all the directories.  To do this, we will need to go through
   * all the directories in most-recent first order.  We will keep track of all the file names we process and if they show
   * up in a later directory we will not re-process.
   * <p/>
   * This way, we will give precidence to the most recent syncOperations (in case two log files disagree (which they shouldn't)) and
   * we shouldn't double process.
   *
   * @param talkingBookDir
   * @param contentUpdate
   * @param villageName
   * @author willpugh
   */
  public void processTalkingBookDir(String syncDevice, File talkingBookDir, String contentUpdate, String villageName) throws
                                                                                                                      IOException {
    final String talkingBookId = talkingBookDir.getName();
    final DeploymentId deploymentId = DeploymentId.parseContentUpdate(contentUpdate);

    final File[] files = talkingBookDir.listFiles(
        (FilenameFilter) new RegexFileFilter(SyncProcessingContext.SYNC_TIME_PATTERN));

    //Sort file to be most recent first.  Can't do alphabetical, because the name
    //doesn't always use 2 digits.  Eg.  11m  could show up before 4m.
    //So, instead, we extract the time from the Dir, and proceed.
    Arrays.sort(files, new Comparator<File>() {
      @Override
      public int compare(File o1, File o2) {
        final SyncDirId   dir1 = SyncDirId.parseSyncDir(deploymentId, o1.getName());
        final SyncDirId   dir2 = SyncDirId.parseSyncDir(deploymentId, o2.getName());

        return dir1.compareTo(dir2);
      }
    });

    if (files.length == 0) {
      logger.warn(
          "TalkingBook directory is empty!!!  Expected a sync dir: " + talkingBookDir.getAbsolutePath());
      return;
    }

    boolean flashDataFound = false;


    Set<String> processedFiles = new HashSet<>();
    for (File syncDir : files) {
      final FlashData             flashData = loadFlashDataFile(syncDir);
      final SyncProcessingContext syncProcessingContext = determineProcessingContext(syncDevice, syncDir, talkingBookId, contentUpdate, villageName, flashData);
      final boolean               isFirstFile = syncDir == files[0];
      final boolean               isLastFile = syncDir == files[files.length-1];

      if (isFirstFile) {
        for (TalkingBookDataProcessor processor : dataProcessorEventListeners) {
          processor.onTalkingBookStart(syncProcessingContext);
        }
      }

      //Only do callbacks for the flashdata file found in the latest sync dir
      //if ((syncDir == files[0])) {
      if (flashData != null && !flashDataFound) {
        processFlashData(syncProcessingContext, flashData);
        flashDataFound = true;
      }

      //}

      processSyncDir(syncDir, syncProcessingContext, processedFiles, true);

      if (isLastFile) {
        for (TalkingBookDataProcessor processor : dataProcessorEventListeners) {
          processor.onTalkingBookEnd(syncProcessingContext);
        }
      }
    }
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
   * Creates a processing context.  There are two ways to do this:
   *
   * <ol>
   *   <li>Use the flashData.bin file and get the info there there.  This is the MOST reliable way to do this, since this info comes from the NOR flash on the
   *   device, which tends to have corruption a lot less.  The only problem is that this is a newer mechanism and so there are a lot of times this file does not exist.
   *   </li>
   *   <li>
   *     Use the filepath conventions to figure this out.  This way always exists, but this information ultimately comes from memory on the device that we have seen
   *     corruption occur in.
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
                                         StringUtils.defaultIfEmpty(systemData.getSerialNumber(), talkingBookId),
                                         StringUtils.defaultIfEmpty(systemData.getLocation(), villageName),
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
