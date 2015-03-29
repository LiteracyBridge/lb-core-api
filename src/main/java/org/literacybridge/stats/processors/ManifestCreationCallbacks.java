package org.literacybridge.stats.processors;

import org.joda.time.LocalDateTime;
import org.literacybridge.stats.model.DirectoryFormat;
import org.literacybridge.stats.model.StatsPackageManifest;
import org.literacybridge.stats.model.SyncDirId;
import org.literacybridge.stats.model.SyncRange;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 */
public class ManifestCreationCallbacks extends AbstractDirectoryProcessor {

  Map<String, LocalDateTime> latestSyncs = new HashMap<>();
  Map<String, LocalDateTime> earliestSyncs = new HashMap<>();

  @Override
  public boolean startDeviceOperationalData(String device) {
    return false;
  }

  @Override
  public void processSyncDir(SyncDirId syncDirId, File syncDir) throws Exception {
    if (syncDirId.dateTime != null) {

      LocalDateTime latestSync = latestSyncs.get(currDeploymentPerDevice.device);
      if (latestSync == null || syncDirId.dateTime.isAfter(latestSync)) {
        latestSyncs.put(currDeploymentPerDevice.device, syncDirId.dateTime);
      }

      LocalDateTime eariestSync = earliestSyncs.get(currDeploymentPerDevice.device);
      if (eariestSync == null || syncDirId.dateTime.isBefore(eariestSync)) {
        earliestSyncs.put(currDeploymentPerDevice.device, syncDirId.dateTime);
      }
    }
  }

  public StatsPackageManifest generateManifest(DirectoryFormat format) {
    Set<String> deviceNameSet = latestSyncs.keySet();
    Map<String, SyncRange> devices = new HashMap<>();

    for (String deviceName : deviceNameSet) {
      SyncRange syncRange = new SyncRange(earliestSyncs.get(deviceName).toDate(), latestSyncs.get(deviceName).toDate(), false);
      devices.put(deviceName, syncRange);
    }

    return new StatsPackageManifest(format.version, Collections.unmodifiableMap(devices));
  }
}
