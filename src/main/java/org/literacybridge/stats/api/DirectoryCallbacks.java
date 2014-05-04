package org.literacybridge.stats.api;

import org.literacybridge.stats.model.*;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * Interface defining the callbacks that a DirectoryIterator will use as it iterates through
 * a stats update file.
 */
public interface DirectoryCallbacks {

  /**
   * Start processing a stats update directory.
   *
   * @param root directory that is the root of the file structure that has the stats in it.
   * @param manifest the manifest for the directory structure.  This should never be null for
   *                 normal uses.  If the directory structure does not have a StatsPackageManifest, one
   *                 will be generated and passed in.
   * @param format the directory format of the stats update directory being processed
   * @return true if processessing should continue
   *         false if the caller should stop after this call
   * @throws Exception
   */
  boolean startProcessing(@Nonnull File root, StatsPackageManifest manifest, @Nonnull DirectoryFormat format) throws Exception;
  void endProcessing() throws Exception;

  /**
   * Called when the iterator is about to run over operational data, such as TBData files.
   *
   * @param device the device that operational data will be processed for
   * @return  true if all the operational data for this device should be processed
   *         false if the operational data for this device should be skipped
   */
  boolean startDeviceOperationalData(@Nonnull String device);
  void endDeviceOperationalData();

  /**
   * Called when a TBData file is visited, this will have already been preceeded by a startDeviceOperationalData,
   * where the device would have been identified.
   *
   * @param tbdataFile the file being visited
   * @throws IOException
   */
  void processTbDataFile(@Nonnull File tbdataFile) throws IOException;

  /**
   * Called when operational logs are being visited, this will have already been preceeded by a startDeviceOperationalData,
   * where the device would have been identified.
   *
   * @param logFile  the log file related to TBLoader activity that is being visited.  This is NOT a
   *                 log file for Talking Book activity.
   * @throws IOException
   */
  void processTbLoaderLogFile(@Nonnull File logFile) throws IOException;


  /**
   * Called for each Device/Deployment that is being visited.  So for example, this could be
   * Device1 for 2014-1.
   *
   *
   * @param deploymentPerDevice structure containing the device name and deployment id
   * @return true if the iterator should continue processing files related to this (Device,Deployment) pair.
   *         false if the iterator should skip files related to this (Device,Deployment) pair.
   * @throws Exception
   */
  boolean startDeviceDeployment(@Nonnull DeploymentPerDevice deploymentPerDevice) throws Exception;
  void endDeviceDeployment() throws Exception;

  /**
   * Called for each village (within a deploymentPerDevice) that is being visited.
   * @param village name of the village
   * @return  true if the iterator should continue processing this village
   *          false if the iterator should skip this village (within this deploymentPerDevice)
   * @throws Exception
   */
  boolean startVillage(@Nonnull String village) throws Exception;
  void endVillage() throws Exception;

  /**
   * Called for a talking book that is being visited  (within a deploymentPerDevice + Village).
   *
   * @param talkingBook the talking book name
   * @return  true if the iterator should continue processing this talking book
   *          false if the iterator should skip this talking book (within this deploymentPerDevice + Village)
   * @throws Exception
   */
  boolean startTalkingBook(String talkingBook) throws Exception;
  void endTalkingBook();

  /**
   * Called to process a sync directory within a talking book directory.
   *
   * @param syncDirId parsed sync directory ID
   * @param syncDir directory relating to this sync directory
   * @throws Exception
   */
  void processSyncDir(SyncDirId syncDirId, File syncDir) throws Exception;



}
