package org.literacybridge.stats.model;

import org.literacybridge.stats.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 */
public interface DirectoryCallbacks {

  boolean startProcessing(File root, StatsPackageManifest manifest, DirectoryFormat format) throws Exception;
  void endProcessing() throws Exception;

  boolean startDeviceOperationalData(String device);
  void endDeviceOperationalData();

  void processTbDataFile(File tbdataFile) throws IOException;
  void processTbLoaderLogFile(File tbdataFile);

  boolean startDeviceDeployment(DeploymentPerDevice deploymentPerDevice, DeploymentId deploymentId) throws Exception;
  void endDeviceDeployment() throws Exception;

  boolean startVillage(String village) throws Exception;
  void endVillage() throws Exception;

  boolean startTalkingBook(String talkingBook) throws Exception;
  void endTalkingBook();

  void processSyncDir(SyncDirId syncDirId, File syncDir) throws Exception;



}
