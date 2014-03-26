package org.literacybridge.stats;

import org.literacybridge.stats.model.DirectoryFormat;
import org.literacybridge.stats.model.StatsPackageManifest;

import java.io.File;
import java.io.OutputStream;

/**
 */
public class StatsFileWriter {

  public void createStatsFileFromDir(StatsPackageManifest manifest, File root, OutputStream outputStream) {
    DirectoryFormat format = DirectoryFormat.fromVersion(manifest.formatVersion);

  }

  public void validateStatsFile() {

  }

  public void fullyValidateStatsFile() {

  }

  public File mergeStatsFiles() {
    return null;
  }
}
