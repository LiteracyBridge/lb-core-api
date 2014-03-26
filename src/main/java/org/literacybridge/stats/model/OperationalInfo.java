package org.literacybridge.stats.model;

import org.joda.time.LocalDateTime;

/**
 */
public class OperationalInfo {
  public final String        deviceName;
  public final String        syncDirName;
  public final LocalDateTime dateTimeOfSync;

  public final String inTalkingBook;
  public final String outTalkingBook;

  public final String inDeploymentId;
  public final String outDeploymentId;

  public final String inVillage;
  public final String outVillage;


  public OperationalInfo(String deviceName, String syncDirName, LocalDateTime dateTimeOfSync, String inTalkingBook,
                         String outTalkingBook,
                         String inDeploymentId, String outDeploymentId,
                         String inVillage, String outVillage) {
    this.inTalkingBook = inTalkingBook;
    this.outTalkingBook = outTalkingBook;

    this.deviceName = deviceName;
    this.syncDirName = syncDirName;
    this.dateTimeOfSync = dateTimeOfSync;
    this.inDeploymentId = inDeploymentId;
    this.outDeploymentId = outDeploymentId;
    this.inVillage = inVillage;
    this.outVillage = outVillage;
  }

  @Override
  public String toString() {
    return "OperationalInfo{" +
        "deviceName='" + deviceName + '\'' +
        ", syncDirName='" + syncDirName + '\'' +
        ", dateTimeOfSync=" + dateTimeOfSync +
        ", inTalkingBook='" + inTalkingBook + '\'' +
        ", outTalkingBook='" + outTalkingBook + '\'' +
        ", inDeploymentId='" + inDeploymentId + '\'' +
        ", outDeploymentId='" + outDeploymentId + '\'' +
        ", inVillage='" + inVillage + '\'' +
        ", outVillage='" + outVillage + '\'' +
        '}';
  }
}
