package org.literacybridge.stats.formats;

import java.nio.charset.Charset;

/**
 * This is the set of constants needed for basic translation from the Talking Book firmware to
 * Java code.  They should be as close in both names and values as possible.
 *
 * The firmware includes are in https://code.google.com/p/literacybridge/source/browse/device/software/device/trunk/firmware/Application/TalkingBook/Include/
 *
 * @author willpugh
 */
public class FirmwareConstants {

  //These are the sizes of the basic types on the Talking Book devices.
  public static final int SizeOfInt = 4;
  public static final int SizeOfChar = 2;


  //Constants for each structure's ID
  public static final short SYSTEM_DATA_ID                  = 254;
  public static final short NOR_STRUCT_ID_MSG_MAP           = 1;
  public static final short NOR_STRUCT_ID_MESSAGE_STATS     = 13;
  public static final short NOR_STRUCT_ID_ALL_MSGS			= 14;
  public static final short NOR_STRUCT_ID_NO_MESSAGE_STATS  = -2;

  public static final short NOR_STRUCT_ID_ROTATION          = 9;
  public static final short NOR_STRUCT_ID_COUNTS            = 10;


  //Constants used for fixed size portions of the data structures.
  //These are defined the same way as the ones in the C-structures, and
  //are thus in terms of chars, not bytes.
  public static final int FIXED_IMAGE_NAME_SIZE		= 20;
  public static final int FIXED_LOCATION_SIZE       = 40;
  public static final int FIXED_SERIAL_NUMBER_SIZE  = 12;
  public static final int FIXED_UPDATE_NUMBER_SIZE  = 20;
  public static final int MAX_MESSAGE_ID_LENGTH     = 20;
  public static final int MAX_TRACKED_MESSAGES      = 40;
  public static final int MAX_ROTATIONS             = 5;
  public static final int MAX_PROFILE_NAME_LENGTH	= 20;
  public static final int MAX_PROFILES	            = 2;

  /**
   * Decodes byte array that was written by the firmware into a Java String
   * @param bytes
   * @return
   */
  public static String decodeString(byte[] bytes) {
    final String    stringWithNulls = new String(bytes, Charset.forName("UTF-16LE"));
    final String[]  splitString = stringWithNulls.split("\u0000");
    return splitString.length > 0 ? splitString[0] : "";
  }

  /**
   * Encodes a string into a byte array that the firmware would understand.
   * @param str
   * @return
   */
  public static byte[] encodeString(String str) {
    return str.getBytes(Charset.forName("UTF-16LE"));
  }

  /**
   * Decodes a Java {@code short} (which was declared an {@code unsigned int} in the C-code) as an unsigned value.  Since,
   * there is no unsigned short in Java, this will elevate it to a short.
   *
   * @param val the short that needs to become unsigned + and int.
   * @return the unsigned value.
   */
  public static int decodeUnsignedInt(short val) {
    return ((int)val) & 0xFFFF;
  }
}
