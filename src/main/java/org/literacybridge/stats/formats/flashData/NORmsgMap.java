package org.literacybridge.stats.formats.flashData;

import org.literacybridge.stats.formats.FirmwareConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This structure lists out the audio content IDs in the same order that they will be listed in the NORallMsgStats structure.
 * This data structure decodes the C structure with the same name that's in https://code.google.com/p/literacybridge/source/browse/device/software/device/trunk/firmware/Application/TalkingBook/Include/filestats.h
 *
 * @author willpugh
 */
public class NORmsgMap {

  static protected final Logger logger = LoggerFactory.getLogger(NORmsgMap.class);


  private short totalMessages;
  private List<String> msgIdMap;

  static public NORmsgMap parseFromBuffer(ByteBuffer byteBuffer) {
    return parseFromBuffer(byteBuffer, new NORmsgMap());
  }

  static public NORmsgMap parseFromBuffer(ByteBuffer byteBuffer, NORmsgMap msgMap) {

    short structId = byteBuffer.getShort();
    if (structId != FirmwareConstants.NOR_STRUCT_ID_MSG_MAP) {
      logger.error("Invalid struct ID.  Should be " + FirmwareConstants.NOR_STRUCT_ID_MSG_MAP + " but is actually " + structId);
    }

    msgMap.totalMessages = byteBuffer.getShort();
    msgMap.msgIdMap = new ArrayList<>(msgMap.totalMessages);

    byte[]  audioContentBuffer = new byte[FirmwareConstants.MAX_MESSAGE_ID_LENGTH * FirmwareConstants.SizeOfChar];
    for (int i=0; i< FirmwareConstants.MAX_TRACKED_MESSAGES; i++) {
      byteBuffer.get(audioContentBuffer);
      if (i<msgMap.totalMessages) {
        msgMap.msgIdMap.add(FirmwareConstants.decodeString(audioContentBuffer));
      }
    }

    return msgMap;
  }


  public short getTotalMessages() {
    return totalMessages;
  }

  public void setTotalMessages(short totalMessages) {
    this.totalMessages = totalMessages;
  }

  public List<String> getMsgIdMap() {
    return msgIdMap;
  }

  public void setMsgIdMap(List<String> msgIdMap) {
    this.msgIdMap = msgIdMap;
  }
}
