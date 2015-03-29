package org.literacybridge.stats.formats.flashData;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.literacybridge.stats.formats.FirmwareConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * Contains all the per-message stats that are tracked for a piece of content in the NOR flash.  If you need other stats,
 * you probably need to extract the from the logs by creating a new AbstractLogProcessor
 * <p/>
 * This class mirrors the C-structure with the same name in https://code.google.com/p/literacybridge/source/browse/device/software/device/trunk/firmware/Application/TalkingBook/Include/filestats.h
 *
 * @author willpugh
 */
public class NORmsgStats {

  static protected final Logger logger = LoggerFactory.getLogger(NORmsgStats.class);
  private boolean isEmpty;
  private String contentId;
  private short indexMsg;
  private short numberProfile;
  private short numberRotation;
  private short countStarted;
  private short countQuarter;
  private short countHalf;
  private short countThreequarters;
  private short countCompleted;
  private short countApplied;
  private short countUseless;
  private int totalSecondsPlayed;

  public static NORmsgStats parseFromBuffer(String contentId, ByteBuffer byteBuffer) {
    return parseFromBuffer(contentId, byteBuffer, new NORmsgStats());
  }

  public static NORmsgStats parseFromBuffer(String contentId, ByteBuffer byteBuffer, NORmsgStats normsgStats) {

    short structId = byteBuffer.getShort();
    if (structId != FirmwareConstants.NOR_STRUCT_ID_MESSAGE_STATS && structId != FirmwareConstants.NOR_STRUCT_ID_NO_MESSAGE_STATS) {
      logger.error("Invalid struct ID.  Should be " + FirmwareConstants.NOR_STRUCT_ID_MESSAGE_STATS + " but is actually " + structId);
    }

    //If we got a NOR_STRUCT_ID_NO_MESSAGE_STATS instead of a NOR_STRUCT_ID_MESSAGE_STATS, it means this is an empty record.
    //It should still be property initialized to 0.
    normsgStats.isEmpty = (structId != FirmwareConstants.NOR_STRUCT_ID_MESSAGE_STATS);
    normsgStats.contentId = contentId;
    normsgStats.indexMsg = byteBuffer.getShort();
    normsgStats.numberProfile = byteBuffer.getShort();
    normsgStats.numberRotation = byteBuffer.getShort();
    normsgStats.countStarted = byteBuffer.getShort();
    normsgStats.countQuarter = byteBuffer.getShort();
    normsgStats.countHalf = byteBuffer.getShort();
    normsgStats.countThreequarters = byteBuffer.getShort();
    normsgStats.countCompleted = byteBuffer.getShort();
    normsgStats.countApplied = byteBuffer.getShort();
    normsgStats.countUseless = byteBuffer.getShort();
    normsgStats.totalSecondsPlayed = FirmwareConstants.decodeUnsignedInt(byteBuffer.getShort());

    return normsgStats;
  }

  public boolean isValid(Collection<String> errors) {
    if (isEmpty) return true;

    return FlashData.doValidate(StringUtils.isNotEmpty(contentId), errors, "ContentID is empty in the NORmsgStats");
  }

  public boolean isEmpty() {
    return isEmpty;
  }

  public void setEmpty(boolean isEmpty) {
    this.isEmpty = isEmpty;
  }

  public String getContentId() {
    return contentId;
  }

  public void setContentId(String contentId) {
    this.contentId = contentId;
  }

  public short getIndexMsg() {
    return indexMsg;
  }

  public void setIndexMsg(short indexMsg) {
    this.indexMsg = indexMsg;
  }

  public short getNumberProfile() {
    return numberProfile;
  }

  public void setNumberProfile(short numberProfile) {
    this.numberProfile = numberProfile;
  }

  public short getNumberRotation() {
    return numberRotation;
  }

  public void setNumberRotation(short numberRotation) {
    this.numberRotation = numberRotation;
  }

  public short getCountStarted() {
    return countStarted;
  }

  public void setCountStarted(short countStarted) {
    this.countStarted = countStarted;
  }

  public void addToCountStarted(short countStarted) {
    this.countStarted += countStarted;
  }


  public short getCountQuarter() {
    return countQuarter;
  }

  public void setCountQuarter(short countQuarter) {
    this.countQuarter = countQuarter;
  }

  public void addToCountQuarter(short countQuarter) {
    this.countQuarter += countQuarter;
  }


  public short getCountHalf() {
    return countHalf;
  }

  public void setCountHalf(short countHalf) {
    this.countHalf = countHalf;
  }

  public void addToCountHalf(short countHalf) {
    this.countHalf += countHalf;
  }

  public short getCountThreequarters() {
    return countThreequarters;
  }

  public void setCountThreequarters(short countThreequarters) {
    this.countThreequarters = countThreequarters;
  }

  public void addToCountThreequarters(short countThreequarters) {
    this.countThreequarters += countThreequarters;
  }

  public short getCountCompleted() {
    return countCompleted;
  }

  public void setCountCompleted(short countCompleted) {
    this.countCompleted = countCompleted;
  }

  public void addToCountCompleted(short countCompleted) {
    this.countCompleted += countCompleted;
  }

  public short getCountApplied() {
    return countApplied;
  }

  public void setCountApplied(short countApplied) {
    this.countApplied = countApplied;
  }

  public void addToCountApplied(short countApplied) {
    this.countApplied += countApplied;
  }

  public short getCountUseless() {
    return countUseless;
  }

  public void setCountUseless(short countUseless) {
    this.countUseless = countUseless;
  }

  public void addToCountUseless(short countUseless) {
    this.countUseless = countUseless;
  }

  public int getTotalSecondsPlayed() {
    return totalSecondsPlayed;
  }

  public void setTotalSecondsPlayed(int totalSecondsPlayed) {
    this.totalSecondsPlayed = totalSecondsPlayed;
  }

  public void addToTotalSecondsPlayed(int totalSecondsPlayed) {
    this.totalSecondsPlayed = totalSecondsPlayed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof NORmsgStats)) return false;

    NORmsgStats that = (NORmsgStats) o;

    if (countApplied != that.countApplied) return false;
    if (countCompleted != that.countCompleted) return false;
    if (countHalf != that.countHalf) return false;
    if (countQuarter != that.countQuarter) return false;
    if (countStarted != that.countStarted) return false;
    if (countThreequarters != that.countThreequarters) return false;
    if (countUseless != that.countUseless) return false;
    if (indexMsg != that.indexMsg) return false;
    if (isEmpty != that.isEmpty) return false;
    if (numberProfile != that.numberProfile) return false;
    if (numberRotation != that.numberRotation) return false;
    if (totalSecondsPlayed != that.totalSecondsPlayed) return false;
    if (contentId != null ? !contentId.equals(that.contentId) : that.contentId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (isEmpty ? 1 : 0);
    result = 31 * result + (contentId != null ? contentId.hashCode() : 0);
    result = 31 * result + (int) indexMsg;
    result = 31 * result + (int) numberProfile;
    result = 31 * result + (int) numberRotation;
    result = 31 * result + (int) countStarted;
    result = 31 * result + (int) countQuarter;
    result = 31 * result + (int) countHalf;
    result = 31 * result + (int) countThreequarters;
    result = 31 * result + (int) countCompleted;
    result = 31 * result + (int) countApplied;
    result = 31 * result + (int) countUseless;
    result = 31 * result + totalSecondsPlayed;
    return result;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("isEmpty", isEmpty)
      .append("contentId", contentId)
      .append("indexMsg", indexMsg)
      .append("numberProfile", numberProfile)
      .append("numberRotation", numberRotation)
      .append("countStarted", countStarted)
      .append("countQuarter", countQuarter)
      .append("countHalf", countHalf)
      .append("countThreequarters", countThreequarters)
      .append("countCompleted", countCompleted)
      .append("countApplied", countApplied)
      .append("countUseless", countUseless)
      .append("totalSecondsPlayed", totalSecondsPlayed)
      .toString();
  }
}
