package org.literacybridge.stats.formats.flashData;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.literacybridge.stats.formats.FirmwareConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collection;


/**
 * Data structure that describes the basic system data from the Talking Book.  This data comes off the NOR Flash, so should be extremely
 * reliable in the face of power failures and other potentially corrupting efforts.
 *
 * Corresponds to the C-structure in
 *   https://code.google.com/p/literacybridge/source/browse/device/software/device/trunk/firmware/Application/TalkingBook/Include/sys_counters.h
 *
 * The C-code us using 2-byte characters.
 *
 * @Author willpugh
 */
public class SystemData {
  static protected final Logger logger = LoggerFactory.getLogger(SystemData.class);

    static public SystemData parseFromBuffer(ByteBuffer byteBuffer) { return parseFromBuffer(byteBuffer, new SystemData()); }

    static public SystemData parseFromBuffer(ByteBuffer byteBuffer, SystemData systemData) {
    short structId = byteBuffer.getShort();
    if (structId != FirmwareConstants.SYSTEM_DATA_ID) {
      logger.error("Invalid struct ID.  Should be " + FirmwareConstants.SYSTEM_DATA_ID + " but is actually " + structId);
    }

    systemData.countReflashes = byteBuffer.getShort();

    byte[] serialNumberBytes          = new byte[FirmwareConstants.FIXED_SERIAL_NUMBER_SIZE * FirmwareConstants.SizeOfChar];
    byte[] updateNumberBytes          = new byte[FirmwareConstants.FIXED_UPDATE_NUMBER_SIZE * FirmwareConstants.SizeOfChar];
    byte[] locationNumberBytes        = new byte[FirmwareConstants.FIXED_LOCATION_SIZE * FirmwareConstants.SizeOfChar];
    byte[] contentPackageNumberBytes  = new byte[FirmwareConstants.FIXED_CONTENT_PACKAGE_SIZE * FirmwareConstants.SizeOfChar];

    byteBuffer.get(serialNumberBytes);
    byteBuffer.get(updateNumberBytes);
    byteBuffer.get(locationNumberBytes);
    byteBuffer.get(contentPackageNumberBytes);

    systemData.serialNumber   = FirmwareConstants.decodeString(serialNumberBytes).trim();
    systemData.updateNumber   = FirmwareConstants.decodeString(updateNumberBytes).trim();
    systemData.location       = FirmwareConstants.decodeString(locationNumberBytes).trim();
    systemData.contentPackage = FirmwareConstants.decodeString(contentPackageNumberBytes).trim();

    systemData.dayLastUpdated = byteBuffer.getShort();
    systemData.monthLastUpdated = byteBuffer.getShort();
    systemData.yearLastUpdated = byteBuffer.getShort();

    return systemData;
  }

  short  countReflashes;
  String serialNumber;
  String updateNumber;
  String location;
  String contentPackage;
  short  dayLastUpdated;
  short  monthLastUpdated;
  short  yearLastUpdated;


  public boolean isValid(Collection<String> errors) {

    return  FlashData.doValidate(StringUtils.isNotEmpty(serialNumber), errors, "serialNumber is empty.") &&
            FlashData.doValidate(StringUtils.isEmpty(updateNumber), errors, "updateNumber is empty.") &&
            FlashData.doValidate(StringUtils.isEmpty(location), errors, "location is empty.") &&
            FlashData.doValidate(StringUtils.isEmpty(contentPackage), errors, "contentPackage is empty.");
  }

  /**
   * The number of times the NOR Flash has been re-flashed for this data structure.
   *
   * This is important, because the cost of the NORFlash being so reliable is that it can only
   * be re-flashed a limited number of times.
   *
   * @return
   */
  public short getCountReflashes() {
    return countReflashes;
  }

  public void setCountReflashes(short countReflashes) {
    this.countReflashes = countReflashes;
  }

  /**
   * Serial number for the device.  This is the same as the Talking Book ID.
   * @return
   */
  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  /**
   * The Content Update, this is a string of the format YEAR-UPDATE, e.g.
   *     2013-6
   * for the sixth update in 2014
   * @return
   */
  public String getUpdateNumber() {
    return updateNumber;
  }

  public void setUpdateNumber(String updateNumber) {
    this.updateNumber = updateNumber;
  }

  /**
   * The village this talking book was deployed to.
   * @return
   */
  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * The content package on this talking book.  This is almost 100% the same as the
   * update number, but there are some cases where talking books have additional content
   * and in the future, we would like to be able to have a difference between an update that is
   * uniform for all talking books in a region and the content that may vary based on village.
   *
   * @return
   */
  public String getContentPackage() {
    return contentPackage;
  }

  public void setContentPackage(String contentPackage) {
    this.contentPackage = contentPackage;
  }

  public short getDayLastUpdated() {
    return dayLastUpdated;
  }

  public void setDayLastUpdated(short dayLastUpdated) {
    this.dayLastUpdated = dayLastUpdated;
  }

  public short getMonthLastUpdated() {
    return monthLastUpdated;
  }

  public void setMonthLastUpdated(short monthLastUpdated) {
    this.monthLastUpdated = monthLastUpdated;
  }

  public short getYearLastUpdated() {
    return yearLastUpdated;
  }

  public void setYearLastUpdated(short yearLastUpdated) {
    this.yearLastUpdated = yearLastUpdated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SystemData)) return false;

    SystemData that = (SystemData) o;

    if (countReflashes != that.countReflashes) return false;
    if (dayLastUpdated != that.dayLastUpdated) return false;
    if (monthLastUpdated != that.monthLastUpdated) return false;
    if (yearLastUpdated != that.yearLastUpdated) return false;
    if (contentPackage != null ? !contentPackage.equals(that.contentPackage) : that.contentPackage != null)
      return false;
    if (location != null ? !location.equals(that.location) : that.location != null) return false;
    if (serialNumber != null ? !serialNumber.equals(that.serialNumber) : that.serialNumber != null) return false;
    if (updateNumber != null ? !updateNumber.equals(that.updateNumber) : that.updateNumber != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) countReflashes;
    result = 31 * result + (serialNumber != null ? serialNumber.hashCode() : 0);
    result = 31 * result + (updateNumber != null ? updateNumber.hashCode() : 0);
    result = 31 * result + (location != null ? location.hashCode() : 0);
    result = 31 * result + (contentPackage != null ? contentPackage.hashCode() : 0);
    result = 31 * result + (int) dayLastUpdated;
    result = 31 * result + (int) monthLastUpdated;
    result = 31 * result + (int) yearLastUpdated;
    return result;
  }

  @Override public String toString() {
    return new ToStringBuilder(this)
        .append("countReflashes", countReflashes)
        .append("serialNumber", serialNumber)
        .append("updateNumber", updateNumber)
        .append("location", location)
        .append("contentPackage", contentPackage)
        .append("dayLastUpdated", dayLastUpdated)
        .append("monthLastUpdated", monthLastUpdated)
        .append("yearLastUpdated", yearLastUpdated)
        .toString();
  }
}
