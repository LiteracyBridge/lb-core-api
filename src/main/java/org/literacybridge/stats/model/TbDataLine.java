package org.literacybridge.stats.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.util.Date;

/**
 * Represents a line in the tbdata
 */
@Entity(name = "TbDataOperations")
@IdClass(TbDataLIneId.class)
public class TbDataLine implements Serializable {


  @Id
  private String project = "";
  @Id
  private String updateDateTime;
  private String outSyncDir;
  private String location;
  private String action;
  private int durationSec;  // In seconds
  @Id
  private String outSn;
  private String outDeployment;
  private String outImage;
  private String outFwRev;
  private String outCommunity;
  private Date outRotationDate;
  private String inSn;
  private String inDeployment;
  private String inImage;
  private String inFwRev;
  private String inCommunity;
  private Date inLastUpdated;
  private String inSyncDir;
  private String inDiskLabel;
  private String chkdskCorruption;
  private String flashSn;
  private int flashReflashes;
  private String flashDeployment;
  private String flashImage;
  private String flashCommunity;
  private Date flashLastUpdated;
  private int flashCumDays;
  private int flashCorruptionDay;
  private int flashVolt;
  private int flashPowerups;
  private int flashPeriods;
  private int flashMsgs;
  private int flashMinutes;
  private int flashStarts;
  private int flashPartial;
  private int flashHalf;
  private int flashMost;
  private int flashAll;
  private int flashApplied;
  private int flashUseless;
  private int flashMinutesR0;
  private int flashPeriodR0;
  private int flashHrsPostUpdateR0;
  private int flashVoltR0;
  private int flashMinutesR1;
  private int flashPeriodR1;
  private int flashHrsPostUpdateR1;
  private int flashVoltR1;
  private int flashMinutesR2;
  private int flashPeriodR2;
  private int flashHrsPostUpdateR2;
  private int flashVoltR2;
  private int flashMinutesR3;
  private int flashPeriodR3;
  private int flashHrsPostUpdateR3;
  private int flashVoltR3;
  private int flashMinutesR4;
  private int flashPeriodR4;
  private int flashHrsPostUpdateR4;
  private int flashVoltR4;

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

  public String getOutSyncDir() {
    return outSyncDir;
  }

  public void setOutSyncDir(String outSyncDir) {
    this.outSyncDir = outSyncDir;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public int getDurationSec() {
    return durationSec;
  }

  public void setDurationSec(int durationSec) {
    this.durationSec = durationSec;
  }

  public String getOutSn() {
    return outSn;
  }

  public void setOutSn(String outSn) {
    this.outSn = outSn;
  }

  public String getOutDeployment() {
    return outDeployment;
  }

  public void setOutDeployment(String outDeployment) {
    this.outDeployment = outDeployment;
  }

  public String getOutImage() {
    return outImage;
  }

  public void setOutImage(String outImage) {
    this.outImage = outImage;
  }

  public String getOutFwRev() {
    return outFwRev;
  }

  public void setOutFwRev(String outFwdRev) {
    this.outFwRev = outFwdRev;
  }

  public String getOutCommunity() {
    return outCommunity;
  }

  public void setOutCommunity(String outCommunity) {
    this.outCommunity = outCommunity;
  }

  public Date getOutRotationDate() {
    return outRotationDate;
  }

  public void setOutRotationDate(Date outRotationDate) {
    this.outRotationDate = outRotationDate;
  }

  public String getInSn() {
    return inSn;
  }

  public void setInSn(String inSn) {
    this.inSn = inSn;
  }

  public String getInDeployment() {
    return inDeployment;
  }

  public void setInDeployment(String inDeployment) {
    this.inDeployment = inDeployment;
  }

  public String getInImage() {
    return inImage;
  }

  public void setInImage(String inImage) {
    this.inImage = inImage;
  }

  public String getInFwRev() {
    return inFwRev;
  }

  public void setInFwRev(String inFwdRev) {
    this.inFwRev = inFwdRev;
  }

  public String getInCommunity() {
    return inCommunity;
  }

  public void setInCommunity(String inCommunity) {
    this.inCommunity = inCommunity;
  }

  public Date getInLastUpdated() {
    return inLastUpdated;
  }

  public void setInLastUpdated(Date inLastUpdated) {
    this.inLastUpdated = inLastUpdated;
  }

  public String getInSyncDir() {
    return inSyncDir;
  }

  public void setInSyncDir(String inSyncDir) {
    this.inSyncDir = inSyncDir;
  }

  public String getInDiskLabel() {
    return inDiskLabel;
  }

  public void setInDiskLabel(String inDiskLabel) {
    this.inDiskLabel = inDiskLabel;
  }

  public String getChkdskCorruption() {
    return chkdskCorruption;
  }

  public void setChkdskCorruption(String chkdskCorruption) {
    this.chkdskCorruption = chkdskCorruption;
  }

  public String getFlashSn() {
    return flashSn;
  }

  public void setFlashSn(String flashSn) {
    this.flashSn = flashSn;
  }

  public int getFlashReflashes() {
    return flashReflashes;
  }

  public void setFlashReflashes(int flashReflashes) {
    this.flashReflashes = flashReflashes;
  }

  public String getFlashDeployment() {
    return flashDeployment;
  }

  public void setFlashDeployment(String flashDeployment) {
    this.flashDeployment = flashDeployment;
  }

  public String getFlashImage() {
    return flashImage;
  }

  public void setFlashImage(String flashImage) {
    this.flashImage = flashImage;
  }

  public String getFlashCommunity() {
    return flashCommunity;
  }

  public void setFlashCommunity(String flashCommunity) {
    this.flashCommunity = flashCommunity;
  }

  public Date getFlashLastUpdated() {
    return flashLastUpdated;
  }

  public void setFlashLastUpdated(Date flashLastUpdated) {
    this.flashLastUpdated = flashLastUpdated;
  }

  public int getFlashCumDays() {
    return flashCumDays;
  }

  public void setFlashCumDays(int flashCumDays) {
    this.flashCumDays = flashCumDays;
  }

  public int getFlashCorruptionDay() {
    return flashCorruptionDay;
  }

  public void setFlashCorruptionDay(int flashCorruptionDay) {
    this.flashCorruptionDay = flashCorruptionDay;
  }

  public int getFlashVolt() {
    return flashVolt;
  }

  public void setFlashVolt(int flashVolt) {
    this.flashVolt = flashVolt;
  }

  public int getFlashPowerups() {
    return flashPowerups;
  }

  public void setFlashPowerups(int flashPowerups) {
    this.flashPowerups = flashPowerups;
  }

  public int getFlashPeriods() {
    return flashPeriods;
  }

  public void setFlashPeriods(int flashPeriods) {
    this.flashPeriods = flashPeriods;
  }

  public int getFlashMsgs() {
    return flashMsgs;
  }

  public void setFlashMsgs(int flashMsgs) {
    this.flashMsgs = flashMsgs;
  }

  public int getFlashMinutes() {
    return flashMinutes;
  }

  public void setFlashMinutes(int flashMinutes) {
    this.flashMinutes = flashMinutes;
  }

  public int getFlashStarts() {
    return flashStarts;
  }

  public void setFlashStarts(int flashStarts) {
    this.flashStarts = flashStarts;
  }

  public int getFlashPartial() {
    return flashPartial;
  }

  public void setFlashPartial(int flashPartial) {
    this.flashPartial = flashPartial;
  }

  public int getFlashHalf() {
    return flashHalf;
  }

  public void setFlashHalf(int flashHalf) {
    this.flashHalf = flashHalf;
  }

  public int getFlashMost() {
    return flashMost;
  }

  public void setFlashMost(int flashMost) {
    this.flashMost = flashMost;
  }

  public int getFlashAll() {
    return flashAll;
  }

  public void setFlashAll(int flashAll) {
    this.flashAll = flashAll;
  }

  public int getFlashApplied() {
    return flashApplied;
  }

  public void setFlashApplied(int flashApplied) {
    this.flashApplied = flashApplied;
  }

  public int getFlashUseless() {
    return flashUseless;
  }

  public void setFlashUseless(int flashUseless) {
    this.flashUseless = flashUseless;
  }

  public int getFlashMinutesR0() {
    return flashMinutesR0;
  }

  public void setFlashMinutesR0(int flashMinutesR0) {
    this.flashMinutesR0 = flashMinutesR0;
  }

  public int getFlashPeriodR0() {
    return flashPeriodR0;
  }

  public void setFlashPeriodR0(int flashPeriodR0) {
    this.flashPeriodR0 = flashPeriodR0;
  }

  public int getFlashHrsPostUpdateR0() {
    return flashHrsPostUpdateR0;
  }

  public void setFlashHrsPostUpdateR0(int flashHrsPostUpdateR0) {
    this.flashHrsPostUpdateR0 = flashHrsPostUpdateR0;
  }

  public int getFlashVoltR0() {
    return flashVoltR0;
  }

  public void setFlashVoltR0(int flashVoltR0) {
    this.flashVoltR0 = flashVoltR0;
  }

  public int getFlashMinutesR1() {
    return flashMinutesR1;
  }

  public void setFlashMinutesR1(int flashMinutesR1) {
    this.flashMinutesR1 = flashMinutesR1;
  }

  public int getFlashPeriodR1() {
    return flashPeriodR1;
  }

  public void setFlashPeriodR1(int flashPeriodR1) {
    this.flashPeriodR1 = flashPeriodR1;
  }

  public int getFlashHrsPostUpdateR1() {
    return flashHrsPostUpdateR1;
  }

  public void setFlashHrsPostUpdateR1(int flashHrsPostUpdateR1) {
    this.flashHrsPostUpdateR1 = flashHrsPostUpdateR1;
  }

  public int getFlashVoltR1() {
    return flashVoltR1;
  }

  public void setFlashVoltR1(int flashVoltR1) {
    this.flashVoltR1 = flashVoltR1;
  }

  public int getFlashMinutesR2() {
    return flashMinutesR2;
  }

  public void setFlashMinutesR2(int flashMinutesR2) {
    this.flashMinutesR2 = flashMinutesR2;
  }

  public int getFlashPeriodR2() {
    return flashPeriodR2;
  }

  public void setFlashPeriodR2(int flashPeriodR2) {
    this.flashPeriodR2 = flashPeriodR2;
  }

  public int getFlashHrsPostUpdateR2() {
    return flashHrsPostUpdateR2;
  }

  public void setFlashHrsPostUpdateR2(int flashHrsPostUpdateR2) {
    this.flashHrsPostUpdateR2 = flashHrsPostUpdateR2;
  }

  public int getFlashVoltR2() {
    return flashVoltR2;
  }

  public void setFlashVoltR2(int flashVoltR2) {
    this.flashVoltR2 = flashVoltR2;
  }

  public int getFlashMinutesR3() {
    return flashMinutesR3;
  }

  public void setFlashMinutesR3(int flashMinutesR3) {
    this.flashMinutesR3 = flashMinutesR3;
  }

  public int getFlashPeriodR3() {
    return flashPeriodR3;
  }

  public void setFlashPeriodR3(int flashPeriodR3) {
    this.flashPeriodR3 = flashPeriodR3;
  }

  public int getFlashHrsPostUpdateR3() {
    return flashHrsPostUpdateR3;
  }

  public void setFlashHrsPostUpdateR3(int flashHrsPostUpdateR3) {
    this.flashHrsPostUpdateR3 = flashHrsPostUpdateR3;
  }

  public int getFlashVoltR3() {
    return flashVoltR3;
  }

  public void setFlashVoltR3(int flashVoltR3) {
    this.flashVoltR3 = flashVoltR3;
  }

  public int getFlashMinutesR4() {
    return flashMinutesR4;
  }

  public void setFlashMinutesR4(int flashMinutesR4) {
    this.flashMinutesR4 = flashMinutesR4;
  }

  public int getFlashPeriodR4() {
    return flashPeriodR4;
  }

  public void setFlashPeriodR4(int flashPeriodR4) {
    this.flashPeriodR4 = flashPeriodR4;
  }

  public int getFlashHrsPostUpdateR4() {
    return flashHrsPostUpdateR4;
  }

  public void setFlashHrsPostUpdateR4(int flashHrsPostUpdateR4) {
    this.flashHrsPostUpdateR4 = flashHrsPostUpdateR4;
  }

  public int getFlashVoltR4() {
    return flashVoltR4;
  }

  public void setFlashVoltR4(int flashVoltR4) {
    this.flashVoltR4 = flashVoltR4;
  }
}
