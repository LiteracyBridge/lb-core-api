package org.literacybridge.stats.model;

import java.io.Serializable;

/**
 * Created by wpugh on 3/29/15.
 */
public class TbDataLIneId implements Serializable {
  public String project = "";
  public String updateDateTime;
  public String outSn;

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public String getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(String updateDateTime) {
    this.updateDateTime = updateDateTime;
  }

  public String getOutSn() {
    return outSn;
  }

  public void setOutSn(String outSn) {
    this.outSn = outSn;
  }
}
