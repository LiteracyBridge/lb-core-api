package org.literacybridge.stats;

import org.apache.commons.io.FileUtils;
import org.literacybridge.stats.model.DeploymentPerDevice;
import org.literacybridge.stats.model.DirectoryFormat;
import org.literacybridge.stats.model.validation.ValidationError;
import org.literacybridge.stats.processors.DirectoryCorruptionFixer;
import org.literacybridge.utils.FsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Copies a directory structure from the sync format to be in the archive format.  The only
 * real difference here is that the sync format is rooted at the devices doing the sync, e.g.
 *
 * /Fidelis/collected-data/2013-03
 *
 * and the archive is rooted at the Deployment Id
 *
 * /2013-03/Fidelis
 *
 * In addition, this code can do some fixup if it finds any obvious corruption.
 *
 *
 * /deployment/device/
 * /operations/device/[tbdata files]
 *
 */
public class DataArchiver {

  protected static final Logger logger = LoggerFactory.getLogger(DataArchiver.class);

  public static final String DEVICE_OPERATIONS_DIR_ARCHIVE = "operations";

  public final File            toDir;
  public final DirectoryFormat toDirFormat;

  public final File            fromDir;
  public final DirectoryFormat fromDirFormat;

  public final boolean doFixup;

  public DataArchiver(File toDir, DirectoryFormat toDirFormat, File fromDir,
                      DirectoryFormat fromDirFormat, boolean doFixup) {
    this.toDir = toDir;
    this.toDirFormat = toDirFormat;
    this.fromDir = fromDir;
    this.fromDirFormat = fromDirFormat;
    this.doFixup = doFixup;
  }

  public void archive() throws Exception {

    DirectoryIterator srcDirectory = new DirectoryIterator(fromDir, fromDirFormat, false);
    TreeSet<DeploymentPerDevice> deployments = srcDirectory.loadDeviceDeployments(fromDir);
    List<TbDataFile> tbDataFiles = new ArrayList<>();
    Set<String> devices = new HashSet<>();


    //Copy all the files from one to the other.
    if (toDirFormat == fromDirFormat) {
      FileUtils.copyDirectory(fromDir, toDir);

      for (DeploymentPerDevice deployment : deployments) {
        devices.add(deployment.device);
      }

      for (String deviceName : devices) {
        List<TbDataFile> tbDataDeviceFiles = getTbDataFiles(fromDir, deviceName, fromDirFormat);
        tbDataFiles.addAll(tbDataDeviceFiles);
      }

    } else {

      //Copy all the data from the talking books.
      for (DeploymentPerDevice deployment : deployments) {
        devices.add(deployment.device);

        if (fromDirFormat == DirectoryFormat.Sync) {
          FileUtils.copyDirectory(deployment.getSyncRoot(fromDir), deployment.getArchiveRoot(toDir));
        } else {
          FileUtils.copyDirectory(deployment.getArchiveRoot(fromDir), deployment.getSyncRoot(toDir));
        }
      }

      for (String deviceName : devices) {

        File destOperationFile = DeviceOperationsDirectory(toDir, deviceName, toDirFormat);

        //Copy all the TBDatas
        List<TbDataFile>  tbDataDeviceFiles = getTbDataFiles(fromDir, deviceName, fromDirFormat);
        for (TbDataFile dataFile : tbDataDeviceFiles) {
          tbDataFiles.add(dataFile);
          FileUtils.copyFileToDirectory(dataFile.file, destOperationFile);
        }
      }
    }

    if (doFixup) {
      fixupOnly(toDir, toDirFormat);
    }

  }

  static public void fixupOnly(File dir, DirectoryFormat format) throws Exception {

    DirectoryCorruptionFixer fixer = new DirectoryCorruptionFixer(dir, format, false);
    List<ValidationError> errors = fixer.fixUp();

    for (ValidationError error : errors) {
      logger.error(error.errorMessage);
    }

  }


  static List<TbDataFile> getTbDataFiles(File rootDir, String deviceName, DirectoryFormat fmt) {
    List<TbDataFile>  retVal = new ArrayList<>();

    File operationsDir = DeviceOperationsDirectory(rootDir, deviceName, fmt);
    for (File tbDataFile : operationsDir.listFiles()) {
      if (DirectoryIterator.TBDATA_PATTERN.matcher(tbDataFile.getName()).matches()) {
        retVal.add(new TbDataFile(deviceName, tbDataFile));
      }
    }
    return retVal;
  }

  static private class TbDataFile {
    final String  device;
    final File    file;

    private TbDataFile(String device, File file) {
      this.device = device;
      this.file = file;
    }
  }

  static File DeviceOperationsDirectory(File rootDir, String deviceName, DirectoryFormat fmt) {
    if (fmt == DirectoryFormat.Sync) {
      return new File(rootDir, FsUtils.FsAgnostify(deviceName + "/" + DirectoryIterator.UPDATE_ROOT_V1));
    } else {
      return new File(rootDir, FsUtils.FsAgnostify(DEVICE_OPERATIONS_DIR_ARCHIVE + "/" + deviceName));
    }
  }

}
