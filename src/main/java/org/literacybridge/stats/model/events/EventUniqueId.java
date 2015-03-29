package org.literacybridge.stats.model.events;

import org.literacybridge.stats.formats.logFile.LogLineContext;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Time;

/**
 * All the fields that go into uniquifying an event.  Since, we want events population to be
 * idempotent, it is important to use unique identifiers that are reproducible.  Since, all
 * these fields come from the logs, they are reproducible.
 */
@Embeddable
public class EventUniqueId implements Serializable {

  @Column
  String talkingBookId;
  @Column
  short year;
  @Column
  short updateInYear;
  @Column
  short householdRotation;
  @Column
  short cycle;
  @Column
  short period;
  @Column
  short dayInPeriod;
  @Column
  Time timeInDay;

  static public EventUniqueId CreateFromLogLineContext(final LogLineContext context) {
    final EventUniqueId id = new EventUniqueId();

    id.talkingBookId = context.context.talkingBookId;
    id.year = context.context.deploymentId.year;
    id.updateInYear = context.context.deploymentId.update;

    if (context.logLineInfo == null) {
      return null;

    }

    id.setHouseholdRotation(context.logLineInfo.householdRotation);
    id.setCycle(context.logLineInfo.cycle);
    id.setPeriod(context.logLineInfo.period);
    id.setDayInPeriod(context.logLineInfo.dayOfPeriod);
    id.setTimeInDay(new Time(context.logLineInfo.timeInPeriod.getMillisOfDay()));
    return id;
  }


  public String getTalkingBookId() {
    return talkingBookId;
  }

  public void setTalkingBookId(String talkingBookId) {
    this.talkingBookId = talkingBookId;
  }

  public short getYear() {
    return year;
  }

  public void setYear(short year) {
    this.year = year;
  }

  public short getUpdateInYear() {
    return updateInYear;
  }

  public void setUpdateInYear(short updateInYear) {
    this.updateInYear = updateInYear;
  }

  public short getHouseholdRotation() {
    return householdRotation;
  }

  public void setHouseholdRotation(short householdRotation) {
    this.householdRotation = householdRotation;
  }

  public short getCycle() {
    return cycle;
  }

  public void setCycle(short cycle) {
    this.cycle = cycle;
  }

  public short getPeriod() {
    return period;
  }

  public void setPeriod(short period) {
    this.period = period;
  }

  public short getDayInPeriod() {
    return dayInPeriod;
  }

  public void setDayInPeriod(short dayInPeriod) {
    this.dayInPeriod = dayInPeriod;
  }

  public Time getTimeInDay() {
    return timeInDay;
  }

  public void setTimeInDay(Time timeInDay) {
    this.timeInDay = timeInDay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EventUniqueId)) return false;

    EventUniqueId that = (EventUniqueId) o;

    if (cycle != that.cycle) return false;
    if (dayInPeriod != that.dayInPeriod) return false;
    if (householdRotation != that.householdRotation) return false;
    if (period != that.period) return false;
    if (updateInYear != that.updateInYear) return false;
    if (year != that.year) return false;
    if (talkingBookId != null ? !talkingBookId.equals(that.talkingBookId) : that.talkingBookId != null)
      return false;
    if (timeInDay != null ? !timeInDay.equals(that.timeInDay) : that.timeInDay != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = talkingBookId != null ? talkingBookId.hashCode() : 0;
    result = 31 * result + (int) year;
    result = 31 * result + (int) updateInYear;
    result = 31 * result + (int) householdRotation;
    result = 31 * result + (int) cycle;
    result = 31 * result + (int) period;
    result = 31 * result + (int) dayInPeriod;
    result = 31 * result + (timeInDay != null ? timeInDay.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "EventUniqueId{" +
      "talkingBookId='" + talkingBookId + '\'' +
      ", year=" + year +
      ", updateInYear=" + updateInYear +
      ", householdRotation=" + householdRotation +
      ", cycle=" + cycle +
      ", period=" + period +
      ", dayInPeriod=" + dayInPeriod +
      ", timeInDay=" + timeInDay +
      '}';
  }
}
