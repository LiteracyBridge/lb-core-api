package org.literacybridge.stats.formats.flashData;


import org.literacybridge.stats.formats.SyncProcessingContext;

/**
 * @author willpugh
 */
public interface FlashDataEvents {

  void processFlashData(SyncProcessingContext context, FlashData flashData);

  void markStatsFileAsCorrupted(SyncProcessingContext context, String flashDataPath, String errorMessage);

}
