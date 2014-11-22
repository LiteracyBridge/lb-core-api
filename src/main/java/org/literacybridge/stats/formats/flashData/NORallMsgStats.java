package org.literacybridge.stats.formats.flashData;

import org.literacybridge.stats.formats.FirmwareConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Contains a map of all the NOR stats messages per-content ID and per-rotation.  This mirrors the data structure of the
 * same name in https://code.google.com/p/literacybridge/source/browse/device/software/device/trunk/firmware/Application/TalkingBook/Include/filestats.h
 *
 * To get the actual content IDs of the stats indexed here, you need to look them up in the NORmsgMap.
 *
 * @author willpugh
 */
public class NORallMsgStats {

	static protected final Logger logger = LoggerFactory.getLogger(NORallMsgStats.class);
    static public NORallMsgStats parseFromBuffer(List<String> conentIdList, ByteBuffer byteBuffer) { return parseFromBuffer(conentIdList, byteBuffer, new NORallMsgStats()); }

    static public NORallMsgStats parseFromBuffer(List<String> conentIdList, ByteBuffer byteBuffer, NORallMsgStats allMsgStats) {

    short structId = byteBuffer.getShort();
    if (structId != FirmwareConstants.NOR_STRUCT_ID_ALL_MSGS) {
      logger.error("Invalid struct ID.  Should be " + FirmwareConstants.NOR_STRUCT_ID_ALL_MSGS + " but is actually " + structId);
    }

    allMsgStats.profileOrder = byteBuffer.getShort();

    byte[]  profileNameBuffer = new byte[FirmwareConstants.MAX_PROFILE_NAME_LENGTH * FirmwareConstants.SizeOfChar];
    byteBuffer.get(profileNameBuffer);
    allMsgStats.profileName = FirmwareConstants.decodeString(profileNameBuffer);     
    
    allMsgStats.totalMessages = byteBuffer.getShort();
    allMsgStats.totalRotations = byteBuffer.getShort();
    allMsgStats.stats = new ArrayList<>(allMsgStats.totalMessages);

    //Run through and decode messages.  Remember, this is a fixed size
    //structure, so we need to run through the full structure, even if not every
    //element has something filled in.
    for (int i=0; i< FirmwareConstants.MAX_TRACKED_MESSAGES; i++) {
        if (allMsgStats.totalRotations <= 0) {
        	allMsgStats.totalRotations = 0;
        	return allMsgStats;
        }
      NORmsgStats[]   rotationStats = new NORmsgStats[allMsgStats.totalRotations];

      for (int j=0; j< FirmwareConstants.MAX_ROTATIONS; j++) {
        final String contentId = (i < conentIdList.size()) ? conentIdList.get(i) : null;
        NORmsgStats stats = NORmsgStats.parseFromBuffer(contentId, byteBuffer);
        if (j < rotationStats.length) {
          rotationStats[j] = stats;
        }
      }

      if (i < allMsgStats.totalMessages) {
        allMsgStats.stats.add(rotationStats);
      }
    }

    return allMsgStats;
  }

  short               profileOrder;
  String              profileName;
  short               totalMessages;
  short               totalRotations;
  List<NORmsgStats[]> stats;

  public boolean isValid(Collection<String> errors) {
	List<NORmsgStats[]> stats = getStats();
	if (stats == null) {
		return false;
	}
    for (NORmsgStats[] rotationStats : stats) {
      for (NORmsgStats singleRotationStats : rotationStats) {
        if (singleRotationStats == null || !singleRotationStats.isValid(errors)) {
          return false;
        }
      }
    }
    return true;
  }

  public short getProfileOrder() {
	  return profileOrder;
  }

  public void setProfileOrder(short profileOrder) {
	  this.profileOrder = profileOrder;
  }
  
  public String getProfileName() {
	  return profileName;
  }

  public void setProfileName(String profileName) {
	  this.profileName = profileName;
  }

  public short getTotalMessages() {
    return totalMessages;
  }

  public void setTotalMessages(short totalMessages) {
    this.totalMessages = totalMessages;
  }

  public short getTotalRotations() {
    return totalRotations;
  }

  public void setTotalRotations(short totalRotations) {
    this.totalRotations = totalRotations;
  }

  public List<NORmsgStats[]> getStats() {
    return stats;
  }

  public void setStats(List<NORmsgStats[]> stats) {
    this.stats = stats;
  }
}
