package org.literacybridge.stats.model;

import org.joda.time.LocalDateTime;
import org.literacybridge.stats.DirectoryIterator;

import java.util.regex.Matcher;

/**
 */
public class SyncDirId implements Comparable<SyncDirId>{

  public static final int SYNC_VERSION_1 = 1;
  public static final int SYNC_VERSION_2 = 2;

  public final LocalDateTime  dateTime;
  public final String         dirName;
  public final String         uniquifier;
  public final int            version;


  public static SyncDirId parseSyncDir(DeploymentId deploymentId, String syncDirName) {

    SyncDirId retVal = null;
    Matcher matchv2 = DirectoryIterator.SYNC_TIME_PATTERN_V2.matcher(syncDirName);
    if (matchv2.matches()) {

      LocalDateTime dateTime = new LocalDateTime(Integer.parseInt(matchv2.group(1)),
                                                 Integer.parseInt(matchv2.group(2)),
                                                 Integer.parseInt(matchv2.group(3)),
                                                 Integer.parseInt(matchv2.group(4)),
                                                 Integer.parseInt(matchv2.group(5)),
                                                 Integer.parseInt(matchv2.group(6)));
      retVal = new SyncDirId(dateTime, syncDirName, matchv2.group(7), SYNC_VERSION_2);

    } else {
      LocalDateTime dateTime = parseV1SyncTime(syncDirName, deploymentId.year);

      //Check to see if we are in a weird "wrap around" case where the deployment update was in December (so name
      // has the previous year), but the sync happened in the new year.  In this case, fix up the year
      if (dateTime != null) {

        if ((dateTime.getMonthOfYear() == 1 || dateTime.getMonthOfYear() == 2) &&
            (deploymentId.update != 1 && deploymentId.update != 2)) {
          dateTime = dateTime.plusYears(1);
        } else if ((dateTime.getMonthOfYear() == 11 || dateTime.getMonthOfYear() == 12) &&
            (deploymentId.update == 1)) {
          dateTime = dateTime.minusYears(1);
        }
      }

      retVal = new SyncDirId(dateTime, syncDirName, "", SYNC_VERSION_1);
    }

    return retVal;
  }

  /**
   * Parses the old sync directory version, this had the format
   *
   * @param syncTime
   * @param baseYear
   * @return
   */
  static public LocalDateTime parseV1SyncTime(String syncTime, int baseYear) {
    Matcher match = DirectoryIterator.SYNC_TIME_PATTERN_V1.matcher(syncTime);
    if (!match.matches()) {
      return null;
    }

    return new LocalDateTime(baseYear,
                             Integer.parseInt(match.group(1)),
                             Integer.parseInt(match.group(2)),
                             Integer.parseInt(match.group(3)),
                             Integer.parseInt(match.group(4)),
                             Integer.parseInt(match.group(5)));

  }


  protected SyncDirId(LocalDateTime dateTime, String dirName, String uniquifier, int version) {
    this.dateTime = dateTime;
    this.dirName = dirName;
    this.uniquifier = uniquifier;
    this.version = version;
  }

  /**
   * Adds a millisecond to the localdatetime.  This is to uniquify in some collections.
   * @return
   */
  public SyncDirId addMilli() {
    return new SyncDirId(dateTime.plusMillis(1), dirName, uniquifier, version);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SyncDirId)) return false;

    SyncDirId syncDirId = (SyncDirId) o;

    if (dirName != null ? !dirName.equals(syncDirId.dirName) : syncDirId.dirName != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return dirName != null ? dirName.hashCode() : 0;
  }

  @Override
  public int compareTo(SyncDirId o) {
    if (o == this) { return 0; }
    if (this.dateTime==null && o.dateTime!=null) {
      return -1;
    }

    if (o.dateTime == null && this.dateTime != null) {
      return 1;
    }

    int retVal = this.dateTime.compareTo(o.dateTime);
    if (retVal == 0) {
      retVal = this.uniquifier.compareTo(o.uniquifier);
    }
    return retVal;
  }

  @Override
  public String toString() {
    return "SyncDirId{" +
        "dirName='" + dirName + '\'' +
        '}';
  }
}
