package org.literacybridge.stats.model.events;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Defines an event by someone filling out a survey.
 */
@Entity
@Table(name = "surveyevents")
public class SurveyEvent extends Event {
  @Column
  private String contentId;
  @Column
  private Boolean isUseful;

  public String getContentId() {
    return contentId;
  }

  public void setContentId(String contentId) {
    this.contentId = contentId;
  }

  public Boolean getIsUseful() {
    return isUseful;
  }

  public void setIsUseful(Boolean isUseful) {
    this.isUseful = isUseful;
  }
}
