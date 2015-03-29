package org.literacybridge.stats.formats.flashData;

import org.literacybridge.stats.formats.FirmwareConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * A data structure that keeps track of various system information about power on/off cycles.  This comes from the NOR flash of the
 * talking books, so the data in here should be pretty reliable in the face of corruption.
 * <p/>
 * This corresponds to the SystemCounts2 structure in https://code.google.com/p/literacybridge/source/browse/device/software/device/trunk/firmware/Application/TalkingBook/Include/sys_counters.h
 *
 * @author willpugh
 */
public class SystemCounts {

  static protected final Logger logger = LoggerFactory.getLogger(SystemCounts.class);
  short period;
  short cumulativeDays;
  short corruptionDay;
  short powerups;
  short lastInitVoltage;
  NORrotation[] noRrotations;

  static public SystemCounts parseFromBuffer(ByteBuffer byteBuffer) {
    return parseFromBuffer(byteBuffer, new SystemCounts());
  }

  static public SystemCounts parseFromBuffer(ByteBuffer byteBuffer, SystemCounts systemCounts) {

    short structId = byteBuffer.getShort();
    if (structId != FirmwareConstants.NOR_STRUCT_ID_COUNTS) {
      logger.error("Invalid struct ID.  Should be " + FirmwareConstants.NOR_STRUCT_ID_COUNTS + " but is actually " + structId);
    }

    systemCounts.period = byteBuffer.getShort();
    systemCounts.cumulativeDays = byteBuffer.getShort();
    systemCounts.corruptionDay = byteBuffer.getShort();
    systemCounts.powerups = byteBuffer.getShort();
    systemCounts.lastInitVoltage = byteBuffer.getShort();
    systemCounts.noRrotations = new NORrotation[FirmwareConstants.MAX_ROTATIONS];

    for (int i = 0; i < FirmwareConstants.MAX_ROTATIONS; i++) {
      systemCounts.noRrotations[i] = NORrotation.parseFromBuffer(byteBuffer);
    }

    return systemCounts;
  }

  public short getPeriod() {
    return period;
  }

  public void setPeriod(short period) {
    this.period = period;
  }

  public short getCumulativeDays() {
    return cumulativeDays;
  }

  public void setCumulativeDays(short cumulativeDays) {
    this.cumulativeDays = cumulativeDays;
  }

  public short getCorruptionDay() {
    return corruptionDay;
  }

  public void setCorruptionDay(short corruptionDay) {
    this.corruptionDay = corruptionDay;
  }

  public short getPowerups() {
    return powerups;
  }

  public void setPowerups(short powerups) {
    this.powerups = powerups;
  }

  public short getLastInitVoltage() {
    return lastInitVoltage;
  }

  public void setLastInitVoltage(short lastInitVoltage) {
    this.lastInitVoltage = lastInitVoltage;
  }

  public NORrotation[] getNoRrotations() {
    return noRrotations;
  }

  public void setNoRrotations(NORrotation[] noRrotations) {
    this.noRrotations = noRrotations;
  }
}
