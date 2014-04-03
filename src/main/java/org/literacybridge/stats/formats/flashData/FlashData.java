package org.literacybridge.stats.formats.flashData;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;


import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.List;

/**
 * Parses a FlashData.bin file into Java structures.  The names and structures under this class are all meant to mirror the
 * Firmware's C-code, to make mapping between the two easier for humans.
 *
 * @author willpugh
 */
public class FlashData {

  public static final short   NO_SINGLE_ROTATION = (short) -1;

  static public FlashData parseFromStream(InputStream is) throws IOException {
    final byte[]      fullBuffer = IOUtils.toByteArray(is);
    final ByteBuffer  byteBuffer = ByteBuffer.wrap(fullBuffer);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

    return parseFromBuffer(byteBuffer, new FlashData());
  }

  static public FlashData parseFromBuffer(ByteBuffer byteBuffer, FlashData flashData) {
    flashData.systemData = SystemData.parseFromBuffer(byteBuffer);
    flashData.systemCounts = SystemCounts.parseFromBuffer(byteBuffer);
    flashData.msgMap = NORmsgMap.parseFromBuffer(byteBuffer);
    flashData.msgStats = NORallMsgStats.parseFromBuffer(flashData.getMsgMap().getMsgIdMap(), byteBuffer);

    return flashData;
  }

  static public boolean doValidate(boolean condition, Collection<String> errors, String errorMsg) {
    if (!condition && errors != null) {
      errors.add(errorMsg);
    }
    return condition;
  }

  private SystemData      systemData;
  private SystemCounts    systemCounts;
  private NORmsgMap       msgMap;
  private NORallMsgStats  msgStats;

  public boolean isValid(Collection<String> errors) {
    boolean retVal =
        doValidate(systemData != null,    errors, "systemData is null.") &&
        doValidate(systemCounts != null,  errors, "systemCounts is null.") &&
        doValidate(msgMap != null,        errors, "msgMap is null.") &&
        doValidate(msgStats != null,      errors, "msgStats is null.");

    retVal = retVal & systemData.isValid(errors);
    retVal = retVal & msgStats.isValid(errors);
    return retVal;
  }

  public SystemData getSystemData() {
    return systemData;
  }

  public void setSystemData(SystemData systemData) {
    this.systemData = systemData;
  }

  public SystemCounts getSystemCounts() {
    return systemCounts;
  }

  public void setSystemCounts(SystemCounts systemCounts) {
    this.systemCounts = systemCounts;
  }

  public NORmsgMap getMsgMap() {
    return msgMap;
  }

  public void setMsgMap(NORmsgMap msgMap) {
    this.msgMap = msgMap;
  }

  public NORallMsgStats getMsgStats() {
    return msgStats;
  }

  public void setMsgStats(NORallMsgStats msgStats) {
    this.msgStats = msgStats;
  }

  /**
   * Creates a list of one stat per content ID.  It will aggregate all the stats across all the rotations logged.
   *
   * @return
   */
  public List<NORmsgStats>  allStats() {
    final ImmutableList.Builder<NORmsgStats> retValBuilder = new ImmutableList.Builder<>();

    for (NORmsgStats[] rotationStats : msgStats.getStats()) {

      if (rotationStats.length > 0) {
        final NORmsgStats msgStats = new NORmsgStats();

        msgStats.setEmpty(rotationStats[0].isEmpty());

        for (NORmsgStats singleRotationStats : rotationStats) {

          if (!singleRotationStats.isEmpty()) {
            msgStats.setIndexMsg(singleRotationStats.getIndexMsg());
            msgStats.setContentId(singleRotationStats.getContentId());
            msgStats.setEmpty(singleRotationStats.isEmpty());

            msgStats.addToCountStarted(singleRotationStats.getCountStarted());
            msgStats.addToCountQuarter(singleRotationStats.getCountQuarter());
            msgStats.addToCountHalf(singleRotationStats.getCountHalf());
            msgStats.addToCountThreequarters(singleRotationStats.getCountThreequarters());
            msgStats.addToCountCompleted(singleRotationStats.getCountCompleted());
            msgStats.addToCountApplied(singleRotationStats.getCountApplied());
            msgStats.addToCountUseless(singleRotationStats.getCountUseless());
            msgStats.addToTotalSecondsPlayed(singleRotationStats.getTotalSecondsPlayed());
          }
        }

        msgStats.setNumberRotation(NO_SINGLE_ROTATION);

        if (!msgStats.isEmpty()) {
          retValBuilder.add(msgStats);
        }
      }

    }

    return retValBuilder.build();
  }


  /**
   * Returns a list of all the content message stats.  This call does no aggregation, so there will be a
   * stat per each content message and each rotation tracked.
   *
   * @return
   */
  public List<NORmsgStats>  allStatsPerRotation() {
    final ImmutableList.Builder<NORmsgStats> retValBuilder = new ImmutableList.Builder<>();

    for (NORmsgStats[] rotationStats : msgStats.getStats()) {
      for (NORmsgStats singleRotationStats : rotationStats) {
        retValBuilder.add(singleRotationStats);
      }
    }

    return retValBuilder.build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FlashData)) return false;

    FlashData flashData = (FlashData) o;

    if (msgMap != null ? !msgMap.equals(flashData.msgMap) : flashData.msgMap != null) return false;
    if (msgStats != null ? !msgStats.equals(flashData.msgStats) : flashData.msgStats != null) return false;
    if (systemCounts != null ? !systemCounts.equals(flashData.systemCounts) : flashData.systemCounts != null)
      return false;
    if (systemData != null ? !systemData.equals(flashData.systemData) : flashData.systemData != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = systemData != null ? systemData.hashCode() : 0;
    result = 31 * result + (systemCounts != null ? systemCounts.hashCode() : 0);
    result = 31 * result + (msgMap != null ? msgMap.hashCode() : 0);
    result = 31 * result + (msgStats != null ? msgStats.hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    return new ToStringBuilder(this)
        .append("systemData", systemData)
        .append("systemCounts", systemCounts)
        .append("msgMap", msgMap)
        .append("msgStats", msgStats)
        .toString();
  }
}
