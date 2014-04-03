package org.literacybridge.stats.formats.formats.logFile;

/**
 * @author willpugh
 */
public interface LogFileEvents {

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

  void onFileStart(String fileName);

  void onFileEnd();
}
