package org.literacybridge.stats.formats.formats.logFile;

/**
 * @author willpugh
 */
public class LogFilePosition {

  final public String fileName;
  final public int lineNumber;

  public LogFilePosition(String fileName, int lineNumber) {
    this.fileName = fileName;
    this.lineNumber = lineNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LogFilePosition)) return false;

    LogFilePosition that = (LogFilePosition) o;

    if (lineNumber != that.lineNumber) return false;
    if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = fileName != null ? fileName.hashCode() : 0;
    result = 31 * result + lineNumber;
    return result;
  }
}
