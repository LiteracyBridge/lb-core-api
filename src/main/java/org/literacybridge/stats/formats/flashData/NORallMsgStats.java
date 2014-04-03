package org.literacybridge.stats.formats.flashData;

import org.literacybridge.stats.formats.FirmwareConstants;

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

    static public NORallMsgStats parseFromBuffer(List<String> conentIdList, ByteBuffer byteBuffer) { return parseFromBuffer(conentIdList, byteBuffer, new NORallMsgStats()); }

    static public NORallMsgStats parseFromBuffer(List<String> conentIdList, ByteBuffer byteBuffer, NORallMsgStats allMsgStats) {

    allMsgStats.totalMessages = byteBuffer.getShort();
    allMsgStats.totalRotations = byteBuffer.getShort();

    allMsgStats.stats = new ArrayList<>(allMsgStats.totalMessages);

    //Run through and decode messages.  Remember, this is a fixed size
    //structure, so we need to run through the full structure, even if not every
    //element has something filled in.
    for (int i=0; i< FirmwareConstants.MAX_TRACKED_MESSAGES; i++) {
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

  short               totalMessages;
  short               totalRotations;
  List<NORmsgStats[]> stats;

  public boolean isValid(Collection<String> errors) {
    for (NORmsgStats[] rotationStats : getStats()) {
      for (NORmsgStats singleRotationStats : rotationStats) {
        if (!singleRotationStats.isValid(errors)) {
          return false;
        }
      }
    }
    return true;
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
