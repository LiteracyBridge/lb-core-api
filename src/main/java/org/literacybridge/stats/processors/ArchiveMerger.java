package org.literacybridge.stats.processors;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.literacybridge.stats.DirectoryIterator;
import org.literacybridge.stats.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 */
public class ArchiveMerger extends AbstractDirectoryProcessor{

  public final File             dest;
  public final DirectoryFormat  format;

  public String operationalDevice;

  public ArchiveMerger(File dest, DirectoryFormat format) {
    this.dest = dest;
    this.format = format;
  }



  public static StatsPackageManifest  mergeManifests(StatsPackageManifest manifest1, StatsPackageManifest manifest2) {
    if (manifest1.formatVersion != manifest2.formatVersion) {
      throw new IllegalArgumentException("Format Versions need to be the same to merge them.");
    }

    Set<String> devices =  Sets.union(manifest1.devices.keySet(), manifest2.devices.keySet());
    Map<String, SyncRange> deviceRanges = new HashMap<>();
    for (String device : devices) {
      deviceRanges.put(device, mergeSyncRanges(manifest1.devices.get(device), manifest1.devices.get(device)));
    }

    return new StatsPackageManifest(manifest1.formatVersion, deviceRanges);
  }

  public static SyncRange mergeSyncRanges(SyncRange range1, SyncRange range2) {
    if (range1 == null) {
      return range2;
    }

    if (range2 == null) {
      return range1;
    }

    return new SyncRange(range1.getStartTime().compareTo(range2.getStartTime()) < 0 ?  range1.getStartTime() : range2.getStartTime(),
                         range1.getEndTime().compareTo(range2.getEndTime()) > 0 ?  range1.getEndTime() : range2.getEndTime(),
                         range1.isIncomplete() || range2.isIncomplete());
  }

  @Override
  public boolean startProcessing(File root, StatsPackageManifest manifest, DirectoryFormat format) throws Exception {
    File destManifestFile = DirectoryIterator.getManifestFile(dest);
    StatsPackageManifest destManifest = DirectoryIterator.readInManifest(destManifestFile, DirectoryFormat.Archive,
                                                                         false);
    StatsPackageManifest mergedManifest = mergeManifests(manifest, destManifest);
    super.startProcessing(root, mergedManifest, format);

    DirectoryIterator.mapper.writeValue(destManifestFile, mergedManifest);
    return true;

  }

  @Override
  public boolean startDeviceDeployment(DeploymentPerDevice deploymentPerDevice)
      throws Exception {
    super.startDeviceDeployment(deploymentPerDevice);

    File srcDir = deploymentPerDevice.getRoot(currRoot, format);
    File destDir = deploymentPerDevice.getRoot(dest, format);

    FileUtils.copyDirectory(srcDir, destDir, true);
    return false;
  }

  @Override
  public boolean startDeviceOperationalData(String device) {
    this.operationalDevice = device;
    return true;
  }

  @Override
  public void processTbDataFile(File tbdataFile) throws IOException {
    FileInputStream fis = new FileInputStream(tbdataFile);

    File outputFile = DirectoryIterator.getTbDataDir(dest, operationalDevice, format);
    FileOutputStream fos = new FileOutputStream(outputFile, true);

    IOUtils.copy(fis, fos);
  }

  @Override
  public void processTbLoaderLogFile(File logFile) throws IOException {
    File destDir = DirectoryIterator.getTbLoaderLogFileDir(dest, operationalDevice, format);
    File destFile = new File(destDir, logFile.getName());

    FileInputStream fis = new FileInputStream(logFile);
    FileOutputStream fos = new FileOutputStream(destFile);

    IOUtils.copy(fis, fos);
  }
}
