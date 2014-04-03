package org.literacybridge.stats;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.codehaus.jackson.map.ObjectMapper;
import org.literacybridge.stats.api.DirectoryCallbacks;
import org.literacybridge.stats.model.*;
import org.literacybridge.stats.processors.ManifestCreationCallbacks;
import org.literacybridge.utils.FsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This class is responsible for navigating the directory structure in a Stats Update Package.
 *
 */
public class DirectoryIterator {
  protected static final Logger logger = LoggerFactory.getLogger(DirectoryIterator.class);


  public static final Pattern UPDATE_PATTERN = Pattern.compile("(\\d+)-(\\d+)");

  public static final Pattern TBDATA_PATTERN       = Pattern.compile("tbData-(\\d+)-(\\d+)-(\\d+).*");
  public static final Pattern SYNC_TIME_PATTERN_V1 = Pattern.compile("(\\d+)m(\\d+)d(\\d+)h(\\d+)m(\\d+)s");
  public static final Pattern SYNC_TIME_PATTERN_V2 = Pattern.compile("(\\d+)y(\\d+)m(\\d+)d(\\d+)h(\\d+)m(\\d+)s-(.*)");

  public static final Pattern TBDATA_PATTERN_V2 = Pattern.compile("tbData-(\\d+)-(\\d+)-(\\d+).csv");


  public static final String MANIFEST_FILE_NAME = "StatsPackageManifest.json";

  public static final String TBLOADER_LOG_DIR                 = "logs";
  public static final String TBDATA_DIR_V2                    = "tbdata";
  public static final String UPDATE_ROOT_V1                   = "collected-data";
  public static final String DEVICE_OPERATIONS_DIR_ARCHIVE_V2 = "operations";

  public static final ObjectMapper mapper = new ObjectMapper();

  public static File getManifestFile(File root) {
    return new File(root, MANIFEST_FILE_NAME);
  }

  public static File getTbDataDir(File root, String device, DirectoryFormat format) {
    File retVal;

    if (format == DirectoryFormat.Sync) {
      retVal = new File(root, FsUtils.FsAgnostify(device + "/" + UPDATE_ROOT_V1));
    } else {
      retVal = new File(root, FsUtils.FsAgnostify(DEVICE_OPERATIONS_DIR_ARCHIVE_V2 + "/" + TBDATA_DIR_V2 + "/" + device));
    }

    return retVal;
  }

  public static File getTbLoaderLogFileDir(File root, String device, DirectoryFormat format) {
    File retVal;

    if (format == DirectoryFormat.Sync) {
      retVal = new File(root, FsUtils.FsAgnostify(device + "/" + UPDATE_ROOT_V1 + "/" + TBLOADER_LOG_DIR));
    } else {
      retVal = new File(root, FsUtils.FsAgnostify(DEVICE_OPERATIONS_DIR_ARCHIVE_V2 + "/" + TBLOADER_LOG_DIR + "/" + device));
    }

    return retVal;
  }

  public final boolean         strict;
  public final File            root;
  public       DirectoryFormat format;

  public DirectoryIterator(File root, DirectoryFormat format, boolean strict) {
    this.root = root;
    this.strict = strict;
    this.format = format;
  }

  public void process(DirectoryCallbacks callbacks) throws Exception {


    StatsPackageManifest manifest = null;
    File manifestFile = getManifestFile(root);
    if (manifestFile.exists()) {
      manifest = readInManifest(manifestFile, format, strict);
      format = DirectoryFormat.fromVersion(manifest.formatVersion);
    } else {
      if (format == null) {
        if (strict) {
          throw new IllegalArgumentException("No Manifest is set, and no directory format is set.");
        }

        format = DirectoryFormat.Sync;
      }

      manifest = generateManifest(format);
    }

    process(manifest, callbacks);
  }

  public static StatsPackageManifest readInManifest(File manifestFile, DirectoryFormat format, boolean strict) throws IOException {

    StatsPackageManifest manifest = mapper.readValue(manifestFile, StatsPackageManifest.class);
    DirectoryFormat manifestFormat = DirectoryFormat.fromVersion(manifest.formatVersion);
    if (format != null && format != manifestFormat) {
      String errorMessage = "Format is set as " + manifestFormat +
          " in the manifest, but the Directory iterator is created with format=" + format +
          ".  If the directory will always have a manifest, you can simply create the DirectoryIterator with a null format.";
      if (strict) {
        throw new IllegalArgumentException(errorMessage);
      }
      logger.error(errorMessage);
    }
    format = manifestFormat;

    return manifest;
  }

  public StatsPackageManifest generateManifest(DirectoryFormat format) throws Exception {
    this.format = format;

    ManifestCreationCallbacks manifestCreationCallbacks = new ManifestCreationCallbacks();
    process(null, manifestCreationCallbacks);
    return manifestCreationCallbacks.generateManifest(format);
  }

  public void process(@Nullable StatsPackageManifest manifest, @Nonnull DirectoryCallbacks callbacks) throws Exception {

    if (!root.exists()) {
      throw new IllegalArgumentException("Root directory does not exist: " + root.getCanonicalPath());
    }

    if (callbacks.startProcessing(root, manifest, format)) {

      TreeSet<DeploymentPerDevice> deploymentPerDevices = loadDeviceDeployments();

      if (!deploymentPerDevices.isEmpty()) {
        String currDevice = null;
        boolean deviceAlreadyProcessed = false;
        boolean processDevice = false;

        for (DeploymentPerDevice  deploymentPerDevice : deploymentPerDevices) {
          if (!deploymentPerDevice.device.equalsIgnoreCase(currDevice)) {

            if (processDevice) {
              callbacks.endDeviceOperationalData();
            }

            currDevice = deploymentPerDevice.device;
            deviceAlreadyProcessed = false;
            processDevice = callbacks.startDeviceOperationalData(currDevice);
          }

          if (processDevice) {
            if (!deviceAlreadyProcessed) {
              File  tbdataDir = getTbDataDir(root, currDevice, format);
              if (!tbdataDir.exists()) {
                throw new IllegalArgumentException("Malformed directory structure.  The operations portion is not properly setup: " + tbdataDir.getPath() + " does not exist.");
              }

              if (format == DirectoryFormat.Sync) {
                for (File potential : tbdataDir.listFiles((FilenameFilter) new RegexFileFilter(TBDATA_PATTERN))) {
                  callbacks.processTbDataFile(potential);
                }
              } else {
                for (File potential : tbdataDir.listFiles((FilenameFilter) new RegexFileFilter(TBDATA_PATTERN_V2))) {
                  callbacks.processTbDataFile(potential);
                }
              }
              deviceAlreadyProcessed = true;
            }
          }
        }

        if (processDevice) {
          callbacks.endDeviceOperationalData();
        }
      } else {
        throw new IllegalArgumentException("No records found in this directory.  Make sure the format in the manifest is correct?  Make sure the directory structure is correct?");
      }

      for (DeploymentPerDevice deploymentPerDevice : deploymentPerDevices) {
        DeploymentId  deploymentId = DeploymentId.parseContentUpdate(deploymentPerDevice.deployment);
        if (deploymentId.year==0 && strict) {
          throw new IllegalArgumentException("Illegal deployment: " + deploymentId);
        }

        if (callbacks.startDeviceDeployment(deploymentPerDevice, deploymentId)) {
          processDeviceDeployment(deploymentId, deploymentPerDevice.getRoot(root, format), callbacks);
          callbacks.endDeviceDeployment();
        }
      }
      callbacks.endProcessing();
    }
  }

  public void processDeviceDeployment(DeploymentId deploymentId, File deviceDeploymentDir, DirectoryCallbacks callbacks) throws Exception {
    for (File village : deviceDeploymentDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)) {
      if (callbacks.startVillage(village.getName().trim())) {
        processVillage(deploymentId, village, callbacks);
        callbacks.endVillage();
      }
    }
  }

  public void processVillage(DeploymentId deploymentId, File villageDir, DirectoryCallbacks callbacks) throws Exception {

    for (File talkingBook : villageDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)) {
      if (callbacks.startTalkingBook(talkingBook.getName().trim())) {
          processTalkingBook(deploymentId, talkingBook, callbacks);
          callbacks.endTalkingBook();
      }
    }
  }

  public void processTalkingBook(DeploymentId deploymentId, File talkingBookDir, DirectoryCallbacks callbacks) throws Exception {
    for (File syncDir : talkingBookDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)) {

      SyncDirId syncDirId = SyncDirId.parseSyncDir(deploymentId, syncDir.getName().trim());
      if (syncDirId.dateTime != null) {
        if (format == DirectoryFormat.Archive && syncDirId.version==1 && strict) {
          throw new IllegalArgumentException("Directory structure is the newer 'Archive' structure, but the sync directory is using the old format : " + syncDir.getName());
        }

        callbacks.processSyncDir(syncDirId, syncDir);
      }
    }
  }

  public TreeSet<DeploymentPerDevice> loadDeviceDeployments() {

    TreeSet<DeploymentPerDevice> retVal =  new TreeSet<DeploymentPerDevice>(DeploymentPerDevice.ORDER_BY_DEVICE);

    if (format == DirectoryFormat.Sync) {
      for (File candidateDevice : root.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)) {
        File  collectedData = new File(candidateDevice, UPDATE_ROOT_V1);
        if (collectedData.exists() && collectedData.isDirectory()) {
          for (File deploymentDir : collectedData.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)) {
            if (UPDATE_PATTERN.matcher(deploymentDir.getName()).matches()) {
              retVal.add(new DeploymentPerDevice(deploymentDir.getName(), candidateDevice.getName()));
            }
          }
        }
      }
    } else {
      for (File deploymentDir : root.listFiles((FilenameFilter) new RegexFileFilter(UPDATE_PATTERN))) {
        for (File device : deploymentDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)) {
          retVal.add(new DeploymentPerDevice(deploymentDir.getName(), device.getName()));
        }
      }
    }

    return retVal;
  }

  public DirectoryFormat getFormat() {
    return format;
  }

  public void setFormat(DirectoryFormat format) {
    this.format = format;
  }
}
