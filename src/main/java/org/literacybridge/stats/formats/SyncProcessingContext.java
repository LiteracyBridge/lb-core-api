package org.literacybridge.stats.formats;

import org.joda.time.LocalDateTime;
import org.literacybridge.stats.formats.ProcessingContext;
import org.literacybridge.stats.model.SyncDirId;

/**
 * THis is a general model class that is useful for processing any format of file that has
 * been synced by a talking book.  It contains the context about the content the talking book
 * had on it as well as the time it was synced.
 *
 * @author willpugh
 */
public class SyncProcessingContext extends ProcessingContext {
  public final LocalDateTime syncTime;

  public SyncProcessingContext(String syncString, String talkingBookId, String village, String contentPackage,
                               String contentUpdate, String deviceSyncedFrom) {
    super(talkingBookId, village, contentPackage, contentUpdate, deviceSyncedFrom);
    SyncDirId syncDirId = SyncDirId.parseSyncDir(deploymentId, syncString);
    this.syncTime = syncDirId.dateTime;

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SyncProcessingContext)) return false;
    if (!super.equals(o)) return false;

    SyncProcessingContext that = (SyncProcessingContext) o;

    if (syncTime != null ? !syncTime.equals(that.syncTime) : that.syncTime != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (syncTime != null ? syncTime.hashCode() : 0);
    return result;
  }
}
