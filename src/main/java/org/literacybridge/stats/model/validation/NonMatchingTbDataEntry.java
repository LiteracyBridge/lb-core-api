package org.literacybridge.stats.model.validation;

import org.literacybridge.stats.model.OperationalInfo;
import org.literacybridge.stats.model.SyncDirId;

/**
 */
public class NonMatchingTbDataEntry {

  public final SyncDirId        syncDirId;
  public final OperationalInfo  operationalInfo;

  public NonMatchingTbDataEntry(SyncDirId syncDirId, OperationalInfo operationalInfo) {
    this.syncDirId = syncDirId;
    this.operationalInfo = operationalInfo;
  }

  @Override
  public String toString() {
    return "NonMatchingTbDataEntry{" +
        "syncDirId=" + syncDirId +
        ",\noperationalInfo=" + operationalInfo +
        '}';
  }
}
