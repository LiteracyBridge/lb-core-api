package org.literacybridge.stats.formats.syncDirectory;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author willpugh
 */
public class SyncResults {


  Set<File> filesProcessed = new HashSet<>();


  long numberOfLogFilesProcesed;
  long numberOfStatsFilesProcessed;

  Map<String, String> filesWithErrors = new HashMap<>();

  public long getNumberOfLogFilesProcesed() {
    return numberOfLogFilesProcesed;
  }

  public void setNumberOfLogFilesProcesed(long numberOfLogFilesProcesed) {
    this.numberOfLogFilesProcesed = numberOfLogFilesProcesed;
  }

  public long addToNumberOfLogFilesProcesed(long numToAdd) {
    return this.numberOfLogFilesProcesed += numToAdd;
  }

  public long getNumberOfStatsFilesProcessed() {
    return numberOfStatsFilesProcessed;
  }

  public void setNumberOfStatsFilesProcessed(long numberOfStatsFilesProcessed) {
    this.numberOfStatsFilesProcessed = numberOfStatsFilesProcessed;
  }

  public long addToNumberOfStatsFilesProcessed(long numToAdd) {
    return this.numberOfStatsFilesProcessed += numToAdd;
  }

  public Map<String, String> getFilesWithErrors() {
    return filesWithErrors;
  }

  public void setFilesWithErrors(Map<String, String> filesWithErrors) {
    this.filesWithErrors = filesWithErrors;
  }

  public Set<File> getFilesProcessed() {
    return filesProcessed;
  }
}
