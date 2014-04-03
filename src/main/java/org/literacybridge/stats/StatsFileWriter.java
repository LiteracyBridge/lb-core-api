package org.literacybridge.stats;

import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.archivers.zip.ZipUtil;
import org.literacybridge.stats.model.DirectoryFormat;
import org.literacybridge.stats.model.StatsPackageManifest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 */
public class StatsFileWriter {

  public File unpackStatsFile(File fileToUnpack, File destDir) throws IOException {

    FileSystem fs = FileSystems.newFileSystem(Paths.get(fileToUnpack.toURI()), null);
    return null;
  }

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
