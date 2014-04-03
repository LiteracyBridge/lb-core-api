package org.literacybridge.stats.formats.statsFile;

import org.literacybridge.stats.formats.SyncProcessingContext;

/**
 * @author willpugh
 */
public interface StatsFileEvents {

  void processStatsFile(SyncProcessingContext context, String contentId, StatsFile statsFile);

  void markStatsFileAsCorrupted(SyncProcessingContext context, String contentId, String errorMessage);

}
