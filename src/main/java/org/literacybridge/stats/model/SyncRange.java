package org.literacybridge.stats.model;

import java.util.Date;

/**
 *  Represents the time range this represents for when the device was synced.  The
 *  startTime is considered inclusive and the end time is considered exclusive.
 *
 *  So if a Device were to sync for the first time, the range would be something like
 *  [0, 2013-08-19T08:33:32)
 *
 *  then the next sync would start at the end time, till current time, e.g.
 *  [2013-08-19T08:33:32, 2013-10-05T13:04:12)
 *
 *  Times SHOULD be standardized into UTC, however, since we are mainly using this information to
 *  look for gaps in the records, then as long as the time is consistent, we should be O.K.
 *
 */
public class SyncRange {

  /**
   * The start time for the human time period this sync is responsible for.
   */
  private Date startTime;

  /**
   * The end time for the human time period this sync is responsible for.
   */
  private Date endTime;

  /**
   * True if the data in the StatsPackage does not necessarily represent then entire range.  For
   * data being sent up from the devices in the field to the Dashboard, this should always be false, since
   * the TBLoader should be keeping track of all operations it did over a time period.  There are cases,
   * however, where this format may be used for exporting stats from the dashboard where a period may not
   * be fully represented.
   */
  private boolean incomplete;

  public SyncRange() {
  }

  public SyncRange(Date startTime, Date endTime, boolean incomplete) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.incomplete = incomplete;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public boolean isIncomplete() {
    return incomplete;
  }

  public void setIncomplete(boolean incomplete) {
    this.incomplete = incomplete;
  }
}
