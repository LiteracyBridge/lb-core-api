package org.literacybridge.stats.model.events;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Describes an event caused by someone playing a peice of content.
 */

@Entity
@Table(name = "playedevents")
public class PlayedEvent extends Event {
  @Column private String contentId;
  @Column private int volume;
  @Column private short timePlayed;
  @Column private short totalTime;
  @Column private double percentDone;
  @Column private boolean isFinished;


    public PlayedEvent() {}

    public PlayedEvent(String contentId, int volume, short timePlayed, short totalTime, double percentDone, boolean finished) {
        this.contentId = contentId;
        this.volume = volume;
        this.timePlayed = timePlayed;
        this.totalTime = totalTime;
        this.percentDone = percentDone;
        isFinished = finished;
    }

    public String getContentId() {
    return contentId;
  }

  public void setContentId(String contentId) {
    this.contentId = contentId;
  }

  public int getVolume() {
    return volume;
  }

  public void setVolume(int volume) {
    this.volume = volume;
  }

  public short getTimePlayed() {
    return timePlayed;
  }

  public void setTimePlayed(short timePlayed) {
    this.timePlayed = timePlayed;
  }

  public short getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(short totalTime) {
    this.totalTime = totalTime;
  }

  public double getPercentDone() {
    return percentDone;
  }

  public void setPercentDone(double percentDone) {
    this.percentDone = percentDone;
  }

  public boolean isFinished() {
    return isFinished;
  }

  public void setFinished(boolean finished) {
    isFinished = finished;
  }
}
