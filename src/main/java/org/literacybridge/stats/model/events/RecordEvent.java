package org.literacybridge.stats.model.events;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: willpugh
 * Date: 2/4/14
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "recordevents")
public class RecordEvent extends Event {

  @Column private String contentId;
  @Column private int secondsRecorded;

  public String getContentId() {
    return contentId;
  }

  public void setContentId(String contentId) {
    this.contentId = contentId;
  }

  public int getSecondsRecorded() {
    return secondsRecorded;
  }

  public void setSecondsRecorded(int secondsRecorded) {
    this.secondsRecorded = secondsRecorded;
  }
}
