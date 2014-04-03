package org.literacybridge.stats.formats.formats.logFile;

import org.literacybridge.stats.formats.SyncProcessingContext;

/**
 * This is the context for a single line being parsed.  In include location info, to all errors to better
 * represent where the occured as well as the general log information that is consistent for all events.
 * <p/>
 * logLineInfo can be null if there was some error when parsing the file.
 *
 * @author willpugh
 */
public class LogLineContext {

  public final LogLineInfo logLineInfo;
  public final LogFilePosition logFilePosition;
  public final SyncProcessingContext context;

  public LogLineContext(LogLineInfo logLineInfo, LogFilePosition logFilePosition, SyncProcessingContext context) {
    this.logLineInfo = logLineInfo;
    this.logFilePosition = logFilePosition;
    this.context = context;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LogLineContext)) return false;

    LogLineContext that = (LogLineContext) o;

    if (context != null ? !context.equals(that.context) : that.context != null) return false;
    if (logFilePosition != null ? !logFilePosition.equals(that.logFilePosition) : that.logFilePosition != null)
      return false;
    if (logLineInfo != null ? !logLineInfo.equals(that.logLineInfo) : that.logLineInfo != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = logLineInfo != null ? logLineInfo.hashCode() : 0;
    result = 31 * result + (logFilePosition != null ? logFilePosition.hashCode() : 0);
    result = 31 * result + (context != null ? context.hashCode() : 0);
    return result;
  }
}
