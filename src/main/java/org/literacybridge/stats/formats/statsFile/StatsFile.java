package org.literacybridge.stats.formats.statsFile;

import org.apache.commons.io.IOUtils;
import org.literacybridge.stats.formats.exceptions.CorruptFileException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.literacybridge.stats.formats.FirmwareConstants.*;

/**
 */
public class StatsFile {

  public static final int MsgIDLength = 20;
  public static final int SRNMaxLength = 12;
  public static final int NumberOfStatsPerMsg = 6;

  public static final int Version = 0;

  public static StatsFile read(InputStream is) throws IOException {

    try {
      final byte[] byteStream = IOUtils.toByteArray(is);
      final ByteBuffer byteBuffer = ByteBuffer.wrap(byteStream);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

      //Get the first short to check for corruption
      int startSequence = byteBuffer.getInt();
      if (startSequence != Version) {
        throw new CorruptFileException(
            "Version does not equal 0, so this file looks corrupt or is not compatible.");
      }

      final byte[] SRNArray = new byte[SRNMaxLength * SizeOfChar];
      byteBuffer.get(SRNArray);
      final String SRN = decodeString(SRNArray);

      final byte[] msgIdArray = new byte[MsgIDLength * SizeOfChar];
      byteBuffer.get(msgIdArray);
      final String msgId = decodeString(msgIdArray);

      int offsetToStats = byteStream.length - (NumberOfStatsPerMsg * SizeOfInt);
      byteBuffer.position(offsetToStats);

      StatsFile retVal = new StatsFile(SRN, 
    		  						   msgId,
                                       byteBuffer.getInt(),
                                       byteBuffer.getInt(),
                                       byteBuffer.getInt(),
                                       byteBuffer.getInt(),
                                       byteBuffer.getInt(),
                                       byteBuffer.getInt());

      if (retVal.openCount + retVal.surveyCount + retVal.appliedCount + retVal.uselessCount > 1000) {
        throw new CorruptFileException("Counts in the stats file seem too high to be realistic. . .");
      }

      return retVal;
    } catch (BufferUnderflowException e) {
      throw new CorruptFileException("Corrupt stats file, file not big enough.", e);
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  public static void write(StatsFile file, OutputStream outputStream) throws IOException {
    //Start Sequence + Message ID + the six stats counters.
	 
	final byte[] bytes = new byte[SizeOfInt + (SRNMaxLength * SizeOfChar) + (MsgIDLength * SizeOfChar) + (NumberOfStatsPerMsg * SizeOfInt)];
    final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

    byteBuffer.putInt(Version);
    byte[] SRNBytes = encodeString(file.SRN);
    byte[] messageBytes = encodeString(file.messageId);
    int bytesToWrite = Math.min(SRNBytes.length, SRNMaxLength * SizeOfChar);
    byteBuffer.put(SRNBytes, 0, bytesToWrite);

    bytesToWrite = Math.min(messageBytes.length, MsgIDLength * SizeOfChar);
    byteBuffer.put(messageBytes, 0, bytesToWrite);

    int offsetToStats = bytes.length - (NumberOfStatsPerMsg * SizeOfInt);
    byteBuffer.position(offsetToStats);

    byteBuffer.putInt(file.openCount);
    byteBuffer.putInt(file.completionCount);
    byteBuffer.putInt(file.copyCount);
    byteBuffer.putInt(file.surveyCount);
    byteBuffer.putInt(file.appliedCount);
    byteBuffer.putInt(file.uselessCount);

    outputStream.write(bytes);
  }

  public final String SRN;			  // SerialNumber that is written into each file.
  public final String messageId;      // Message ID that is written into each file.
  public final int openCount;         // 10 seconds or longer play
  public final int completionCount;   // Finished whole recording (-Ended in log file)
  public final int copyCount;         // Copy from device to device
  public final int surveyCount;       // How many times did the user bring up the servey
  public final int appliedCount;      // How many times did they say they would apply this content
  public final int uselessCount;      // How many times did they say this was not useful content

  public StatsFile(String SRN, String messageId, int openCount, int completionCount, int copyCount, int surveyCount,
                   int appliedCount, int uselessCount) {
	this.SRN = SRN;
	this.messageId = messageId;
    this.openCount = openCount;
    this.completionCount = completionCount;
    this.copyCount = copyCount;
    this.surveyCount = surveyCount;
    this.appliedCount = appliedCount;
    this.uselessCount = uselessCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof StatsFile)) return false;

    StatsFile statsFile = (StatsFile) o;

    if (appliedCount != statsFile.appliedCount) return false;
    if (completionCount != statsFile.completionCount) return false;
    if (copyCount != statsFile.copyCount) return false;
    if (openCount != statsFile.openCount) return false;
    if (surveyCount != statsFile.surveyCount) return false;
    if (uselessCount != statsFile.uselessCount) return false;
    if (messageId != null ? !messageId.equals(statsFile.messageId) : statsFile.messageId != null) return false;
    if (SRN != null ? !SRN.equals(statsFile.SRN) : statsFile.SRN != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = messageId != null ? messageId.hashCode() : 0;
    result = 31 * result + SRN != null ? SRN.hashCode() : 0;
    result = 31 * result + openCount;
    result = 31 * result + completionCount;
    result = 31 * result + copyCount;
    result = 31 * result + surveyCount;
    result = 31 * result + appliedCount;
    result = 31 * result + uselessCount;
    return result;
  }
}
