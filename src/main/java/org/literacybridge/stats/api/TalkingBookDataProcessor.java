package org.literacybridge.stats.api;

import org.literacybridge.stats.formats.ProcessingContext;
import org.literacybridge.stats.formats.SyncProcessingContext;
import org.literacybridge.stats.formats.flashData.FlashData;
import org.literacybridge.stats.formats.formats.logFile.LogAction;
import org.literacybridge.stats.formats.formats.logFile.LogLineContext;
import org.literacybridge.stats.formats.statsFile.StatsFile;

import java.io.IOException;

/**
 * @author willpugh
 */
public interface TalkingBookDataProcessor {

  //+++++++++++++++Directory Processing ++++++++++++++++++//
  void onTalkingBookStart(ProcessingContext context);
  void onTalkingBookEnd(ProcessingContext context);

  //+++++++++++++++Processing Flash Data ++++++++++++++++++//

  void processFlashData(SyncProcessingContext context, FlashData flashData) throws IOException;
  void processCorruptFlashData(SyncProcessingContext context, String flashDataPath, String errorMessage);


  //+++++++++++++++Processing Log Files ++++++++++++++++++//
  void onPlay(LogLineContext context, String contentId, int volume, double voltage);

  void onPlayed(LogLineContext context, String contentId, short secondsPlayed, short secondsSomething, int volume,
                double voltage, boolean ended);

  void onCategory(LogLineContext context, String categoryId);

  void onRecord(LogLineContext context, String contentId, int unknownNumber);

  void onRecorded(LogLineContext context, int secondsRecorded);

  void onPause(LogLineContext context, String contentId);

  void onUnPause(LogLineContext context, String contentId);

  void onSurvey(LogLineContext context, String contentId);

  void onSurveyCompleted(LogLineContext context, String contentId, boolean useful);

  void onShuttingDown(LogLineContext context);

  void onVoltageDrop(LogLineContext context, LogAction action, double voltageDropped, int time);

  void onLogFileStart(String fileName);

  void onLogFileEnd();

  //+++++++++++++++Processing Stats Files ++++++++++++++++++//
  void processStatsFile(SyncProcessingContext context, String contentId, StatsFile statsFile);
  void markStatsFileAsCorrupted(SyncProcessingContext context, String contentId, String errorMessage);


}
