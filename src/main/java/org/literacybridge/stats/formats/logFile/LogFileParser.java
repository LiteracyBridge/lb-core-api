package org.literacybridge.stats.formats.logFile;

import com.google.common.collect.Lists;
import org.joda.time.LocalTime;
import org.literacybridge.stats.api.TalkingBookDataProcessor;
import org.literacybridge.stats.model.SyncProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author willpugh
 */
public class LogFileParser {

  /**
   * Matches a line that we are able to process.  Will try to be fairly forgiving in terms of what it accepts.
   * For example, the line:
   * 2r0096c008p023d18h18m53s401/314/314V:PLAY TB000248_372AB558 @VOL=03 @Volt=314
   * <p/>
   * Will be captured in three parts (brackets used to denote captures):
   * <p/>
   * [2r0096c008p023d18h18m53s401/314/314V]:[PLAY] [TB000248_372AB558 @VOL=03 @Volt=314]
   */
  public static final Pattern LOG_LINE_PATTERN = Pattern.compile("([^:]*):(\\w+):*\\s*(.*)");
  /**
   * This matches the beginning of log lines that look like :
   * 2r0096c008p023d18h18m49s357/314/314V
   * <p/>
   * and capture each piece of data.  For example the brackets will denote the captured text. . .
   * <p/>
   * [2]r[0096]c[008]p[023]d[18]h[18]m[49]s[357]/[314]/[314]V
   * <p/>
   * Capture groups are:
   * <ol>
   * <li>Household rotation</li>
   * <li>What power cycle the device is in (how many times its been turned on and off)</li>
   * <li>What period the device is in (count of continuous time blocks, which break when power is lost, usually during battery change)</li>
   * <li>Day in period</li>
   * <li>Hours of Day in period</li>
   * <li>Minutes of Hour in period</li>
   * <li>Seconds of Minute in period</li>
   * <li>Highest Transient Voltage Detected</li>
   * <li>Steady State Voltage</li>
   * <li>Lowest Transient Voltage Detected</li>
   * </ol>
   */
  public static final Pattern LOG_LINE_START_PATTERN = Pattern.compile(
    "(0|(\\d+)r)(\\d+)c(\\d+)p\\D*(\\d+)d(\\d+)h(\\d+)m(\\d+)s(\\d+)/(\\d+)/(\\d+)V");
  /**
   * This matches the newer log line format that begins with a 0p
   */
  public static final Pattern NEW_LINE_PATTERN = Pattern.compile("\\d+p(.*)");
  /**
   * Matches the rest of the PLAY event, after the parts are pulled out from LOG_LINE_PATTERN +  LOG_LINE_START_PATTERN
   * <p/>
   * This will turn something that looks like:
   * 00046a_9_4E7A864E @VOL=03 @Volt=250
   * <p/>
   * And capture each important piece (brackets used to denote captures):
   * [00046a_9_4E7A864E] @VOL=[03] @Volt=[250]
   */
  public static final Pattern REST_OF_PLAY = Pattern.compile("(\\S+)\\s+@VOL=(\\d+)\\s+@Volt=(\\S+)\\s*");
  /**
   * Matches the rest of the PLAYED event, after the parts are pulled out from LOG_LINE_PATTERN +  LOG_LINE_START_PATTERN
   * <p/>
   * This will turn something that looks like:
   * 00046a_9_F70FDD0B 0002/0001sec @VOL=01 @Volt=202-Ended
   * <p/>
   * And capture each important piece (brackets used to denote captures):
   * [00046a_9_F70FDD0B] [0002]/[0001]sec @VOL=[01] @Volt=[202-Ended]
   */
  public static final Pattern REST_OF_PLAYED = Pattern.compile(
    "(\\S+)\\s+(\\d+)/(\\d+)sec\\s+@VOL=(\\d+)\\s+@Volt=(\\S+)\\s*");
  /**
   * Matches the rest of the RECORD event, after the parts are pulled out from LOG_LINE_PATTERN +  LOG_LINE_START_PATTERN
   * <p/>
   * This will turn something that looks like:
   * 00046a_9_7F67F127 -> 9
   * <p/>
   * And capture each important piece (brackets used to denote captures):
   * [00046a_9_7F67F127] -> [9]
   */
  public static final Pattern REST_OF_RECORD = Pattern.compile("(\\S+)\\s+->\\s+(\\d+)\\s*");
  /**
   * Matches the rest of the TIME RECORD event, after the parts are pulled out from LOG_LINE_PATTERN +  LOG_LINE_START_PATTERN
   * <p/>
   * This will turn something that looks like:
   * RECORDED (secs): 0004
   * <p/>
   * And capture each important piece (brackets used to denote captures):
   * RECORDED (secs): [0004]
   */
  public static final Pattern REST_OF_RECORDED = Pattern.compile("RECORDED\\s+\\(secs\\):\\s*(\\d+)\\s*");
  /**
   * Matches the pattern for a VOLTAGE DROP.  This can happen during any of the other patterns
   * <p/>
   * This will turn something that looks like:
   * VOLTAGE DROP: 0.02v in 0003 sec
   * <p/>
   * And capture each important piece (brackets used to denote captures):
   * VOLTAGE DROP: [0.02]v in [0003] sec
   */
  public static final Pattern VOLTAGE_DROP = Pattern.compile("VOLTAGE DROP:\\s*([0-9.]+)v\\s*in\\s*(\\d+)\\s+sec");
  static protected final Logger logger = LoggerFactory.getLogger(LogFileParser.class);
  private final Collection<TalkingBookDataProcessor> eventCallbacks;
  private final SyncProcessingContext context;
  private final Map<String, String> categoryMap;

  //Last piece of content played
  private String contentLastPlayed = "";

  public LogFileParser(TalkingBookDataProcessor eventCallbacks, SyncProcessingContext context,
                       Map<String, String> categoryMap) {
    this.eventCallbacks = Lists.newArrayList(eventCallbacks);
    this.context = context;
    this.categoryMap = categoryMap;
  }

  public LogFileParser(Collection<TalkingBookDataProcessor> eventCallbacks, SyncProcessingContext context,
                       Map<String, String> categoryMap) {
    this.eventCallbacks = eventCallbacks;
    this.context = context;
    this.categoryMap = categoryMap;
  }

  static boolean checkForMatch(String action, String args, LogFilePosition filePosition, Matcher matcher) {
    if (!matcher.matches()) {

      //If this is not a feedback message, mark as being an error
      if (!"Feedback".equalsIgnoreCase(args)) {
        final String errorString = String.format("%s : %d - Cannot match arguments in %s action. Args=%s",
          filePosition.fileName, filePosition.lineNumber, action, args);
        logger.error(errorString);
      }

      return false;
    }
    return true;
  }

  protected void clearParseState() {
    contentLastPlayed = "";
  }

  public String getContentLastPlayed() {
    return contentLastPlayed;
  }

  public void parse(final String fileName, final InputStream is) throws IOException {

    int lineNumber = 1;
    final BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

    clearParseState();
    for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
      eventCallback.onLogFileStart(fileName);
    }

    try {
      String strLine;
      while ((strLine = br.readLine()) != null) {

        final Matcher fullLineMatcher = LOG_LINE_PATTERN.matcher(strLine);
        if (fullLineMatcher.matches()) {

          final String preludeString = fullLineMatcher.group(1);
          final String action = fullLineMatcher.group(2);
          final String actionParams = fullLineMatcher.group(3);

          parseAction(fileName, lineNumber, preludeString, action, actionParams);
        }

        lineNumber++;
      }
    } finally {
      for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
        eventCallback.onLogFileEnd();
      }
    }
  }

  public LogLineContext parseLogLineContext(String fileName, int lineNumber, String line) {

    final LogFilePosition logFilePosition = new LogFilePosition(fileName, lineNumber);

    LogLineInfo logLineInfo = null;
    try {
      logLineInfo = parseLogLineInfo(line);
    } catch (NumberFormatException e) {
      final String errorString = String.format("%s : %d - Invalid number in log info. Line=%s, Error=%s", fileName,
        lineNumber, line, e.getMessage());
      logger.error(errorString);
    }

    return new LogLineContext(logLineInfo, logFilePosition, context);
  }

  public LogLineInfo parseLogLineInfo(String line) throws NumberFormatException {

    //The line pattern seems to have changed to include a 0p at the beginning.
    //This catches that.
    Matcher checkOldLine = NEW_LINE_PATTERN.matcher(line);
    if (checkOldLine.matches()) {
      line = checkOldLine.group(1);
    }

    Matcher matcher = LOG_LINE_START_PATTERN.matcher(line);
    if (!matcher.matches()) {
      return null;
    }

    short rotation = 0;
    if (!"0".matches(matcher.group(1))) {
      rotation = Short.parseShort(matcher.group(2));
    }

    final short cycle = Short.parseShort(matcher.group(3));
    final short period = Short.parseShort(matcher.group(4));

    short dayOfPeriod = Short.parseShort(matcher.group(5));
    int hourOfPeriod = Integer.parseInt(matcher.group(6));
    int minuteOfPeriod = Integer.parseInt(matcher.group(7));
    int secondOfPeriod = Integer.parseInt(matcher.group(8));
    final double highestVoltage = Double.parseDouble(matcher.group(9)) / 100;
    final double steadyStateVoltage = Double.parseDouble(matcher.group(10)) / 100;
    final double lowestVoltage = Double.parseDouble(matcher.group(11)) / 100;

    //Somehow, there appear to be log messages with hour of day > 24 and minutes > 60

    minuteOfPeriod += secondOfPeriod / 60;
    secondOfPeriod = secondOfPeriod % 60;
    hourOfPeriod += minuteOfPeriod / 60;
    minuteOfPeriod = minuteOfPeriod % 60;
    dayOfPeriod += hourOfPeriod / 24;
    hourOfPeriod = hourOfPeriod % 24;

    final LocalTime periodTime = new LocalTime(hourOfPeriod, minuteOfPeriod, secondOfPeriod);

    return new LogLineInfo(rotation, cycle, period, dayOfPeriod, periodTime, highestVoltage, steadyStateVoltage,
      lowestVoltage);
  }

  public void parseAction(final String fileName, final int lineNumber, final String preludeString, final String action,
                          final String actionParams) {

    final LogLineContext logLineContext = parseLogLineContext(fileName, lineNumber, preludeString);
    final Matcher voltageMatcher = VOLTAGE_DROP.matcher(actionParams);
    final boolean isVoltageDrop = voltageMatcher.matches();
    final LogAction logAction = LogAction.lookup(action);

    if (logAction == null) {
      logger.debug("Invalid action " + action);
      return;
    }

    if (!isVoltageDrop) {

      switch (logAction) {

        case play:
          processPlay(logLineContext, actionParams);
          break;

        case played:
          processPlayed(logLineContext, actionParams);
          break;

        case category:
          processCategory(logLineContext, actionParams);
          break;

        case paused:
          for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
            eventCallback.onPause(logLineContext, contentLastPlayed);
          }
          break;

        case unpaused:
          for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
            eventCallback.onUnPause(logLineContext, contentLastPlayed);
          }
          break;

        case record:
          processRecord(logLineContext, actionParams);
          break;

        case time_recorded:
          processRecorded(logLineContext, actionParams);
          break;

        case survey:
          processSurvey(logLineContext, actionParams);
          break;

        case shuttingDown:
          for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
            eventCallback.onShuttingDown(logLineContext);
          }
          break;

        default:
          logger.error("Illegal action found " + logAction.actionName);
          break;
      }

    } else {
      final double voltsDropped = Double.parseDouble(voltageMatcher.group(1));
      final int time = Integer.parseInt(voltageMatcher.group(2));
      for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
        eventCallback.onVoltageDrop(logLineContext, logAction, voltsDropped, time);
      }
    }

  }

  protected void processPlay(LogLineContext logLineContext, String args) {
    final Matcher matcher = REST_OF_PLAY.matcher(args);
    if (!checkForMatch("Play", args, logLineContext.logFilePosition, matcher)) {
      return;
    }


    final String contentId = matcher.group(1);
    contentLastPlayed = contentId;

    try {
      final int volume = Integer.parseInt(matcher.group(2));
      final double voltage = Double.parseDouble(matcher.group(3)) / 100;
      for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
        eventCallback.onPlay(logLineContext, contentId, volume, voltage);
      }
    } catch (NumberFormatException e) {
      final String errorString = String.format("%s : %d - Invalid number in Play action. Args=%s, Error=%s",
        logLineContext.logFilePosition.fileName,
        logLineContext.logFilePosition.lineNumber, args, e.getMessage());
      logger.error(errorString);
    }

  }

  protected void processPlayed(LogLineContext logLineContext, String args) {
    final Matcher matcher = REST_OF_PLAYED.matcher(args);
    if (!checkForMatch("Played", args, logLineContext.logFilePosition, matcher)) {
      return;
    }


    final String contentId = matcher.group(1);
    contentLastPlayed = contentId;

    try {
      final short timePlayed = Short.parseShort(matcher.group(2));
      final short timeSomething = Short.parseShort(matcher.group(3));
      final int volume = Integer.parseInt(matcher.group(4));
      final String voltageString = matcher.group(5);

      final String[] voltageParts = voltageString.split("-");
      final double voltage = Double.parseDouble(voltageParts[0]) / 100;
      final boolean isEnded = (voltageParts.length == 2) && (voltageParts[1].equalsIgnoreCase("ended"));

      for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
        eventCallback.onPlayed(logLineContext, contentId, timePlayed, timeSomething, volume, voltage, isEnded);
      }
    } catch (NumberFormatException e) {
      final String errorString = String.format("%s : %d - Invalid number in Played action. Args=%s, Error=%s",
        logLineContext.logFilePosition.fileName,
        logLineContext.logFilePosition.lineNumber, args, e.getMessage());
      logger.error(errorString);
    }

  }

  protected void processCategory(final LogLineContext logLineContext, final String categoryId) {
    final String category = categoryMap.get(categoryId.trim());
    for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
      eventCallback.onCategory(logLineContext, (category != null ? category : categoryId));
    }
  }

  protected void processRecord(LogLineContext logLineContext, String args) {

    //There are several "record" messages that don't have args.  Not much we
    //can do with them.
    if (args.isEmpty()) {
      return;
    }

    final Matcher matcher = REST_OF_RECORD.matcher(args);
    if (!checkForMatch("Record", args, logLineContext.logFilePosition, matcher)) {
      return;
    }


    final String contentId = matcher.group(1);

    try {
      final int unknownId = Integer.parseInt(matcher.group(2));
      for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
        eventCallback.onRecord(logLineContext, contentId, unknownId);
      }
    } catch (NumberFormatException e) {
      final String errorString = String.format("%s : %d - Invalid number in Record action. Args=%s, Error=%s",
        logLineContext.logFilePosition.fileName,
        logLineContext.logFilePosition.lineNumber, args, e.getMessage());
      logger.error(errorString);
    }

  }

  protected void processRecorded(LogLineContext logLineContext, String args) {
    final Matcher matcher = REST_OF_RECORDED.matcher(args);
    if (!checkForMatch("Recorded", args, logLineContext.logFilePosition, matcher)) {
      return;
    }

    try {
      final int time = Integer.parseInt(matcher.group(1));
      for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
        eventCallback.onRecorded(logLineContext, time);
      }
    } catch (NumberFormatException e) {
      final String errorString = String.format("%s : %d - Invalid number in Record action. Args=%s, Error=%s",
        logLineContext.logFilePosition.fileName,
        logLineContext.logFilePosition.lineNumber, args, e.getMessage());
      logger.error(errorString);
    }
  }

  protected void processSurvey(LogLineContext logLineContext, String args) {
    if (args == null) {
      final String errorString = String.format("%s : %d - No argument for Survey action.",
        logLineContext.logFilePosition.fileName,
        logLineContext.logFilePosition.lineNumber);
      logger.error(errorString);
    }

    if ("taken".equalsIgnoreCase(args)) {
      for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
        eventCallback.onSurvey(logLineContext, getContentLastPlayed());
      }
    } else if ("apply".equalsIgnoreCase(args)) {
      for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
        eventCallback.onSurveyCompleted(logLineContext, getContentLastPlayed(), true);
      }
    } else if ("useless".equalsIgnoreCase(args)) {
      for (TalkingBookDataProcessor eventCallback : eventCallbacks) {
        eventCallback.onSurveyCompleted(logLineContext, getContentLastPlayed(), false);
      }
    } else {
      final String errorString = String.format("%s : %d - Invalid argument for Surveyaction. Args=%s",
        logLineContext.logFilePosition.fileName,
        logLineContext.logFilePosition.lineNumber, args);
      logger.error(errorString);
    }
  }
}
