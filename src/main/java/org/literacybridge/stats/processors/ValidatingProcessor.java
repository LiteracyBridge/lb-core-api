package org.literacybridge.stats.processors;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDateTime;
import org.literacybridge.stats.formats.tbData.TbDataParser;
import org.literacybridge.stats.model.*;
import org.literacybridge.stats.model.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 */
public class ValidatingProcessor extends AbstractDirectoryProcessor {

  public static final String DEPLOY_ID_EXPECTED = "YYYY-XX formatted string with YYYYY being the year and XX being the current deployment in this year.";
  public static final String SYNC_DIR_EXPECTED = "Sync directory could not be parsed correct.  Look at https://docs.google.com/document/d/12Q0a7x15FqeZ4ys0gYy4O2MtWYrvGDUegOXwlsG9ZQY for a desciption of the appropriate formats.";
  public static final String CHECK_DISK_REFORMAT = "chkdsk-reformat.txt";
  protected static final Logger logger = LoggerFactory.getLogger(ValidatingProcessor.class);
  public final List<ValidationError> validationErrors = new ArrayList<>();

  final TreeMap<SyncDirId, OperationalInfo> tbDataInfo = new TreeMap<>(SyncDirId.TIME_COMPARATOR);
  final int maxTimeWindow = 10;
  final IdentityHashMap<SyncDirId, SyncDirId> foundSyncDirs = new IdentityHashMap<>();
  final Set<String> deviceIncorrectlyInManifest = new HashSet<>();
  final TbDataParser tbDataParser = new TbDataParser();

  private String currOperationalDevice = null;

  @Override
  public boolean startDeviceOperationalData(String device) {
    currOperationalDevice = device;
    return true;
  }

  @Override
  public void endDeviceOperationalData() {
    currOperationalDevice = null;
  }


  @Override
  public void processTbDataFile(File tbdataFile, boolean includesHeaders) throws IOException {

    int lineNumber = 1;
    List<IncorrectFilePropertyValue> incorrectFilePropertyValues = new ArrayList<>();
    final List<TbDataLine> tbDataLines = tbDataParser.parseTbDataFile(tbdataFile, includesHeaders);

    for (TbDataLine tbDataLine : tbDataLines) {
      processLine(tbDataLine, tbdataFile, lineNumber, incorrectFilePropertyValues);
    }

    if (!incorrectFilePropertyValues.isEmpty()) {
      validationErrors.add(new TbDataHasInvalidProperties(tbdataFile, incorrectFilePropertyValues));
    }
  }


  protected void processLine(TbDataLine line, File tbdataFile, int lineNumber,
                             List<IncorrectFilePropertyValue> incorrectFilePropertyValues) {


    String syncDirName = line.getUpdateDateTime() + "-" + currOperationalDevice;

    DeploymentId nextDeploymentId = DeploymentId.parseContentUpdate(line.getOutDeployment());
    if (nextDeploymentId.year == 0) {
      incorrectFilePropertyValues.add(
        new IncorrectFilePropertyValue("outDeploymentId", DEPLOY_ID_EXPECTED, line.getOutDeployment(), lineNumber));
    }

    DeploymentId currDeploymentId = DeploymentId.parseContentUpdate(line.getInDeployment());
    if (currDeploymentId.year == 0 && !"UNKNOWN".equalsIgnoreCase(currDeploymentId.id)) {
      incorrectFilePropertyValues.add(
        new IncorrectFilePropertyValue("inDeploymentId", DEPLOY_ID_EXPECTED, line.getInDeployment(), lineNumber));

      //Parse out the deployment and sync dir to make it a date.
      //Since, there is a decent amount of unknowns, make a guess in those cases. . .
      if (nextDeploymentId.year != 0) {
        currDeploymentId = nextDeploymentId.guessPrevious();
        logger.warn(
          "DeploymentID is incorrect for " + tbdataFile.getPath() + ":" + lineNumber + ".  Guessing that it should be : " + currDeploymentId.id + " based on the out version.");
      } else {
        logger.error("Unable to resolve a deployment ID for " + tbdataFile.getPath() + ":" + lineNumber);
      }
    }

    String inTalkingBook = line.getInSn();
    String outTalkingBook = line.getOutSn();
    if (!inTalkingBook.equalsIgnoreCase(outTalkingBook) && !"UNKNOWN".equalsIgnoreCase(inTalkingBook)) {
      incorrectFilePropertyValues.add(
        new IncorrectFilePropertyValue("outTalkingBook", inTalkingBook, outTalkingBook, lineNumber));
    }

    SyncDirId syncDirId = SyncDirId.parseSyncDir(currDeploymentId, syncDirName);
    LocalDateTime localDateTime = syncDirId.dateTime;
    if (localDateTime != null) {

      if ("update".equalsIgnoreCase(line.getAction())) {
        OperationalInfo operationalInfo = new OperationalInfo(currOperationalDevice, syncDirName, localDateTime,
          inTalkingBook, outTalkingBook,
          currDeploymentId.id, line.getOutDeployment(), line.getInCommunity(), line.getOutCommunity());

        //Theoretically, we can have dups, but it is unlikely.  In this case, log and add at a later millisecond.
        while ((operationalInfo = tbDataInfo.put(syncDirId, operationalInfo)) != null) {
          syncDirId = syncDirId.addMilli();
        }
      }
    } else {
      logger.error("Corrupt line " + tbdataFile.getPath() + ":" + lineNumber);
      incorrectFilePropertyValues.add(new IncorrectFilePropertyValue("syncDirName", SYNC_DIR_EXPECTED, syncDirName, lineNumber));
    }
  }

  @Override
  public void processSyncDir(SyncDirId syncDirId, File syncDir) throws Exception {


    //Find closest matching TbData entry
    SyncDirId tbDataEntry = findMatchingTbDataEntry(syncDirId);

    //Verify each sync directory matches to EXACTLY one entry.
    LocalDateTime maxAllowableTimeV1 = syncDirId.dateTime.plusMinutes(maxTimeWindow);

    //If this file is due to major corruption, just bail out.
    File chkdiskFile = new File(syncDir, CHECK_DISK_REFORMAT);
    if (chkdiskFile.exists()) {
      SyncDirId previousSyncDir = foundSyncDirs.put(tbDataEntry, tbDataEntry);
      if (previousSyncDir != null) {
        validationErrors.add(new MultipleTbDatasMatchError(tbDataEntry.dirName, tbDataInfo.get(tbDataEntry).deviceName));
      }

      return;
    }

    if (syncDir.list().length == 0) {
      validationErrors.add(new EmptySyncDirectory(syncDir));
      return;
    }


    if (tbDataEntry == null || (manifest.formatVersion == 1 && maxAllowableTimeV1.isBefore(tbDataEntry.dateTime))) {
      validationErrors.add(new NoMatchingTbDataError(syncDirId.dirName, syncDir, manifest.formatVersion == 1 ? SyncDirId.SYNC_VERSION_1 : SyncDirId.SYNC_VERSION_2));
    } else {
      SyncDirId previousSyncDir = foundSyncDirs.put(tbDataEntry, tbDataEntry);
      if (previousSyncDir != null) {
        validationErrors.add(new MultipleTbDatasMatchError(tbDataEntry.dirName, tbDataInfo.get(tbDataEntry).deviceName));
      }

      OperationalInfo operationalInfo = tbDataInfo.get(tbDataEntry);
      List<IncorrectPropertyValue> incorrectPropertyValues = new LinkedList<>();

      if (!currVillage.equalsIgnoreCase(operationalInfo.inVillage) && !"UNKNOWN".equalsIgnoreCase(operationalInfo.inVillage)) {
        incorrectPropertyValues.add(new IncorrectPropertyValue("Village", operationalInfo.inVillage, currVillage));
      }

      if (!currTalkingBook.equalsIgnoreCase(operationalInfo.inTalkingBook)) {
        incorrectPropertyValues.add(new IncorrectPropertyValue("Talking Book", operationalInfo.inTalkingBook, currTalkingBook));
      }

      if (!currDeploymentPerDevice.deployment.equalsIgnoreCase(operationalInfo.inDeploymentId)) {
        incorrectPropertyValues.add(new IncorrectPropertyValue("Deployment Id", operationalInfo.inDeploymentId, currDeploymentPerDevice.deployment));
      }

      if (!currDeploymentPerDevice.device.equalsIgnoreCase(operationalInfo.deviceName)) {
        incorrectPropertyValues.add(new IncorrectPropertyValue("Device", operationalInfo.deviceName, currDeploymentPerDevice.device));
      }


      if (!incorrectPropertyValues.isEmpty()) {
        File destFile = FileUtils.getFile(currDeploymentPerDevice.getRoot(currRoot, format), operationalInfo.inVillage,
          operationalInfo.inTalkingBook, syncDirId.dirName);

        validationErrors.add(new InvalidSyncDirError(syncDir, destFile, incorrectPropertyValues));
      }
    }

    //Validate Manifest File
    SyncRange range = manifest.devices.get(currDeploymentPerDevice.device);
    Date startTime = range != null ? range.getStartTime() : null;
    Date endTime = range != null ? range.getEndTime() : null;
    if (startTime == null || endTime == null) {

      if (!deviceIncorrectlyInManifest.contains(currDeploymentPerDevice.device)) {
        validationErrors.add(new ManfestDoesNotContainDevice(currDeploymentPerDevice.device));
        deviceIncorrectlyInManifest.add(currDeploymentPerDevice.device);
      }
    } else {
      Date syncDirDate = syncDirId.dateTime.toDate();
      int startCompare = startTime.compareTo(syncDirDate);
      int endCompare = endTime.compareTo(syncDirDate);

      if (startCompare > 0 || endCompare < 0) {
        if (!deviceIncorrectlyInManifest.contains(currDeploymentPerDevice.device)) {
          validationErrors.add(new ManifestHasWrongDeviceRanges(currDeploymentPerDevice.device, startTime, endTime, syncDirId.dateTime, syncDir));
          deviceIncorrectlyInManifest.add(currDeploymentPerDevice.device);
        }
      }
    }
  }


  private SyncDirId findMatchingTbDataEntry(SyncDirId syncDirId) {

    SyncDirId currSync = null;
    //If the manifest is from the older version, just need to verify there is an entry shortly after the syncDir time, and
    //that there are not duplicate directories going to the same one.  For the newer format, there needs to be an exact match.
    if (manifest.formatVersion == 1) {
      NavigableMap<SyncDirId, OperationalInfo> laterOperations = tbDataInfo.tailMap(syncDirId, true);
      Iterator<SyncDirId> iter = laterOperations.navigableKeySet().iterator();

      while (iter.hasNext()) {
        currSync = iter.next();
        if (currDeploymentPerDevice.device.equals(tbDataInfo.get(currSync).deviceName)) {
          break;
        } else {
          currSync = null;
        }
      }

    } else {
      if (syncDirId.version != SyncDirId.SYNC_VERSION_2) {
        validationErrors.add(new InvalidSyncDirFormat());
      }

      if (tbDataInfo.containsKey(syncDirId)) {
        //Make sure currSync is the same identity as in the map for the validation test below
        currSync = tbDataInfo.tailMap(syncDirId, true).firstKey();
      }
    }

    return currSync;
  }

  @Override
  public void endProcessing() throws Exception {
    super.endProcessing();

    //Check to see if anything was in the TBData files, but not on the file systems
    Set<SyncDirId> idsInTbDataNotUsed = Sets.difference(tbDataInfo.keySet(), foundSyncDirs.keySet());
    if (!idsInTbDataNotUsed.isEmpty()) {
      List<NonMatchingTbDataEntry> nonMatchingTbDataEntries = new ArrayList<>();
      for (SyncDirId id : idsInTbDataNotUsed) {
        nonMatchingTbDataEntries.add(new NonMatchingTbDataEntry(id, tbDataInfo.get(id)));
      }

      validationErrors.add(new UnmatchedTbDataEntries(nonMatchingTbDataEntries));
    }

  }
}
