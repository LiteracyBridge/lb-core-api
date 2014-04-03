package org.literacybridge.stats.formats.flashData;

import org.literacybridge.stats.formats.FirmwareConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Data structure that keeps track about the time periods and initial voltages for a rotations this
 * talking book went through.  This matches up with the {@code NORrotation} datastructure on the Talking
 * book defined in https://code.google.com/p/literacybridge/source/browse/device/software/device/trunk/firmware/Application/TalkingBook/Include/filestats.h
 *
 * @author willpugh
 */
public class NORrotation {
  static protected final Logger logger = LoggerFactory.getLogger(NORrotation.class);


  static public NORrotation parseFromBuffer(ByteBuffer byteBuffer) {
    return parseFromBuffer(byteBuffer, new NORrotation());
  }

  /**
   * Fills in the noRotation structure with the contents from the bytebuffer.
   *
   * If the structId is incorrect, the structure will be filled in, but null will be returned.
   *
   * SIDEEFFECTS:  As a side effect, the bytebuffer will ALWAYS have its position incremented by the
   * size of the NORotation structure.
   *
   * @param byteBuffer
   * @param noRrotation
   * @return the passed in struct if the structId is correct, otherwise null.
   */
  static public NORrotation parseFromBuffer(ByteBuffer byteBuffer, NORrotation noRrotation) {

    NORrotation retVal = noRrotation;
    short structId = byteBuffer.getShort();
    if (structId != FirmwareConstants.NOR_STRUCT_ID_ROTATION) {
      //The way the firware works, is it always allocates space for the max number of rotations, but
      //will only write the number it has.  So there is a case where we are getting bunk here instead of the
      //structID.  In this case, burn the right number of bytes and return null.
      retVal = null;
    }

    noRrotation.rotationNumber        = byteBuffer.getShort();
    noRrotation.periodNumber          = byteBuffer.getShort();
    noRrotation.hoursAfterLastUpdate  = byteBuffer.getShort();
    noRrotation.initVoltage           = byteBuffer.getShort();

    return retVal;
  }

  short rotationNumber;
  short periodNumber;
  short hoursAfterLastUpdate;
  short initVoltage;

  public short getRotationNumber() {
    return rotationNumber;
  }

  public void setRotationNumber(short rotationNumber) {
    this.rotationNumber = rotationNumber;
  }

  public short getPeriodNumber() {
    return periodNumber;
  }

  public void setPeriodNumber(short periodNumber) {
    this.periodNumber = periodNumber;
  }

  public short getHoursAfterLastUpdate() {
    return hoursAfterLastUpdate;
  }

  public void setHoursAfterLastUpdate(short hoursAfterLastUpdate) {
    this.hoursAfterLastUpdate = hoursAfterLastUpdate;
  }

  public short getInitVoltage() {
    return initVoltage;
  }

  public void setInitVoltage(short initVoltage) {
    this.initVoltage = initVoltage;
  }
}
