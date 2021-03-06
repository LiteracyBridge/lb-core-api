package org.literacybridge.stats.processors;

import org.literacybridge.stats.api.DirectoryCallbacks;
import org.literacybridge.stats.model.*;

import java.io.File;
import java.io.IOException;

/**
 */
abstract public class AbstractDirectoryProcessor implements DirectoryCallbacks {

  protected File currRoot;
  protected DirectoryFormat format;
  protected StatsPackageManifest manifest;
  protected DeploymentPerDevice currDeploymentPerDevice;
  protected DeploymentId deploymentId;
  protected String currVillage;
  protected String currTalkingBook;

  @Override
  public boolean startProcessing(File root, StatsPackageManifest manifest, DirectoryFormat format) throws Exception {
    this.currRoot = root;
    this.format = format;
    this.manifest = manifest;
    return true;
  }

  @Override
  public void endProcessing() throws Exception {
    currRoot = null;
  }


  @Override
  public boolean startDeviceOperationalData(String device) {
    return false;
  }

  @Override
  public void endDeviceOperationalData() {

  }

  @Override
  public void processTbDataFile(File tbdataFile, boolean includesHeaders) throws IOException {

  }

  @Override
  public void processTbLoaderLogFile(File logFile) throws IOException {

  }

  @Override
  public boolean startDeviceDeployment(DeploymentPerDevice deploymentPerDevice) throws Exception {
    currDeploymentPerDevice = deploymentPerDevice;
    this.deploymentId = deploymentId;
    return true;
  }

  @Override
  public void endDeviceDeployment() throws Exception {
    currDeploymentPerDevice = null;
    deploymentId = null;
  }

  @Override
  public boolean startVillage(String village) throws Exception {
    currVillage = village;
    return true;
  }

  @Override
  public void endVillage() throws Exception {
    currVillage = null;
  }

  @Override
  public boolean startTalkingBook(String talkingBook) throws Exception {
    currTalkingBook = talkingBook;
    return true;
  }

  @Override
  public void endTalkingBook() {
    currTalkingBook = null;
  }

  @Override
  public void processSyncDir(SyncDirId syncDirId, File syncDir) throws Exception {
  }
}
