package org.literacybridge.stats.formats.logFile;

import org.joda.time.LocalTime;

/**
 * Thist structure represensts the data from the leading section of a TalkingBook log.  E.g.  something that
 * looks like:
 * {@code 2r0096c008p023d18h18m49s357/314/314V}
 * <p/>
 * For more info on what the peices mean, look at LogFileParser.LOG_LINE_START_PATTERN
 *
 * @author willpugh
 */
public class LogLineInfo {

  public final short householdRotation;
  public final short cycle;
  public final short period;
  public final short dayOfPeriod;

  public final LocalTime timeInPeriod;

  public final double maxVolts;
  public final double steadyStateVolts;
  public final double minVolts;

  public LogLineInfo(short householdRotation,
                     short cycle,
                     short period,
                     short dayOfPeriod,
                     LocalTime timeInPeriod,
                     double maxVolts,
                     double steadyStateVolts,
                     double minVolts) {
    this.householdRotation = householdRotation;
    this.cycle = cycle;
    this.period = period;
    this.dayOfPeriod = dayOfPeriod;
    this.timeInPeriod = timeInPeriod;
    this.maxVolts = maxVolts;
    this.steadyStateVolts = steadyStateVolts;
    this.minVolts = minVolts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LogLineInfo)) return false;

    LogLineInfo that = (LogLineInfo) o;

    if (cycle != that.cycle) return false;
    if (dayOfPeriod != that.dayOfPeriod) return false;
    if (householdRotation != that.householdRotation) return false;
    if (Double.compare(that.maxVolts, maxVolts) != 0) return false;
    if (Double.compare(that.minVolts, minVolts) != 0) return false;
    if (period != that.period) return false;
    if (Double.compare(that.steadyStateVolts, steadyStateVolts) != 0) return false;
    if (timeInPeriod != null ? !timeInPeriod.equals(that.timeInPeriod) : that.timeInPeriod != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = householdRotation;
    result = 31 * result + cycle;
    result = 31 * result + period;
    result = 31 * result + dayOfPeriod;
    result = 31 * result + (timeInPeriod != null ? timeInPeriod.hashCode() : 0);
    temp = maxVolts != +0.0d ? Double.doubleToLongBits(maxVolts) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = steadyStateVolts != +0.0d ? Double.doubleToLongBits(steadyStateVolts) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = minVolts != +0.0d ? Double.doubleToLongBits(minVolts) : 0L;
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }
}
