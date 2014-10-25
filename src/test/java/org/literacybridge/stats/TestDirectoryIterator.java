package org.literacybridge.stats;

import org.easymock.EasyMock;
import org.junit.Test;
import org.literacybridge.stats.api.DirectoryCallbacks;
import org.literacybridge.stats.model.*;
import org.literacybridge.utils.FsUtils;

import java.io.File;
import java.util.HashMap;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.eq;

/**
 */
public class TestDirectoryIterator {

  public static final File TEST1_SYNC = new File(FsUtils.FsAgnostify("src/test/resources/testPackages/test1-sync"));
  public static final File TEST1_ARCHIVE = new File(FsUtils.FsAgnostify("src/test/resources/testPackages/test1-archive"));

  protected DirectoryCallbacks setupMock(File root, DirectoryFormat format, StatsPackageManifest manifest) throws Exception {

    File  device1OpsDir = (format==DirectoryFormat.Sync) ?
                          new File(root, FsUtils.FsAgnostify("device1/collected-data")) :
                          new File(root, FsUtils.FsAgnostify("OperationalData/device1/tbdata"));
    File  device2OpsDir = (format==DirectoryFormat.Sync) ?
                          new File(root, FsUtils.FsAgnostify("device2/collected-data")) :
                          new File(root, FsUtils.FsAgnostify("OperationalData/device2/tbdata"));

    DirectoryCallbacks callbacks = createMock(DirectoryCallbacks.class);
    expect(callbacks.startProcessing(eq(root), anyObject(StatsPackageManifest.class), eq(format))).andReturn(true);
    expect(callbacks.startDeviceOperationalData(eq("device1"))).andReturn(true);

    if (format == DirectoryFormat.Archive) {
      callbacks.processTbDataFile(eq(new File(device1OpsDir, "tbData-v00-2013y07m15d-device1.csv")), eq(true));
      callbacks.processTbDataFile(eq(new File(device1OpsDir, "tbData-v00-2013y08m15d-device1.csv")), eq(true));
      callbacks.processTbDataFile(eq(new File(device1OpsDir, "tbData-v00-2013y08m16d-device1.csv")), eq(true));
    } else {
      callbacks.processTbDataFile(eq(new File(device1OpsDir, "tbData-v00-2013y07m15d-device1.csv")), eq(false));
      callbacks.processTbDataFile(eq(new File(device1OpsDir, "tbData-v00-2013y08m15d-device1.csv")), eq(false));
      callbacks.processTbDataFile(eq(new File(device1OpsDir, "tbData-v00-2013y08m16d-device1.csv")), eq(false));
    }
    callbacks.endDeviceOperationalData();

    expect(callbacks.startDeviceOperationalData(eq("device2"))).andReturn(true);
    if (format == DirectoryFormat.Archive) {
      callbacks.processTbDataFile(eq(new File(device2OpsDir, "tbData-v00-2013y07m15d-device2.csv")), eq(true));
      callbacks.processTbDataFile(eq(new File(device2OpsDir, "tbData-v00-2013y08m15d-device2.csv")), eq(true));
    } else {
      callbacks.processTbDataFile(eq(new File(device2OpsDir, "tbData-v00-2013y07m15d-device2.csv")), eq(false));
      callbacks.processTbDataFile(eq(new File(device2OpsDir, "tbData-v00-2013y08m15d-device2.csv")), eq(false));
    }
    callbacks.endDeviceOperationalData();

    DeploymentPerDevice deploymentPerDevice = new DeploymentPerDevice("2013-03", "device1");
    DeploymentId deploymentId = new DeploymentId((short)2013, (short)3, "2013-03");

    expect(callbacks.startDeviceDeployment(eq(deploymentPerDevice))).andReturn(true);
    expect(callbacks.startVillage("village1")).andReturn(true);
    expect(callbacks.startTalkingBook("TB1")).andReturn(true);

    String dirName = (format == DirectoryFormat.Archive) ? "2013y07m15d17h01m50s-device1" : "7m15d17h1m50s";
    callbacks.processSyncDir(eq(SyncDirId.parseSyncDir(deploymentId, dirName)),
                                eq(new File(deploymentPerDevice.getRoot(root, format),
                                            FsUtils.FsAgnostify("village1/TB1/" + dirName))));
    callbacks.endTalkingBook();

    expect(callbacks.startTalkingBook("TB2")).andReturn(true);
    dirName = (format == DirectoryFormat.Archive) ? "2013y07m15d18h01m50s-device1" : "7m15d18h1m50s";
    callbacks.processSyncDir(eq(SyncDirId.parseSyncDir(deploymentId, dirName)), eq(new File(deploymentPerDevice.getRoot(root, format), FsUtils.FsAgnostify("village1/TB2/" + dirName))));
    callbacks.endTalkingBook();
    callbacks.endVillage();

    expect(callbacks.startVillage("village2")).andReturn(true);
    expect(callbacks.startTalkingBook("TB3")).andReturn(true);
    dirName = (format == DirectoryFormat.Archive) ? "2013y07m15d19h01m50s-device1" : "7m15d19h1m50s";
    callbacks.processSyncDir(eq(SyncDirId.parseSyncDir(deploymentId, dirName)), eq(new File(deploymentPerDevice.getRoot(root, format), FsUtils.FsAgnostify("village2/TB3/" + dirName))));
    callbacks.endTalkingBook();
    callbacks.endVillage();
    callbacks.endDeviceDeployment();

    deploymentPerDevice = new DeploymentPerDevice("2013-04", "device1");
    deploymentId = new DeploymentId((short)2013, (short)4, "2013-04");

    expect(callbacks.startDeviceDeployment(eq(deploymentPerDevice))).andReturn(true);
    expect(callbacks.startVillage("village1")).andReturn(true);
    expect(callbacks.startTalkingBook("TB1")).andReturn(true);
    dirName = (format == DirectoryFormat.Archive) ? "2013y08m15d17h01m50s-device1" : "8m15d17h1m50s";
    callbacks.processSyncDir(eq(SyncDirId.parseSyncDir(deploymentId, dirName)),
                             eq(new File(deploymentPerDevice.getRoot(root, format), FsUtils.FsAgnostify("village1/TB1/" + dirName))));
    callbacks.endTalkingBook();
    callbacks.endVillage();

    expect(callbacks.startVillage("village2")).andReturn(true);
    expect(callbacks.startTalkingBook("TB2")).andReturn(true);
    dirName = (format == DirectoryFormat.Archive) ? "2013y08m15d18h01m50s-device1" : "8m15d18h1m50s";
    callbacks.processSyncDir(eq(SyncDirId.parseSyncDir(deploymentId, dirName)), eq(new File(deploymentPerDevice.getRoot(root, format), FsUtils.FsAgnostify("village2/TB2/" + dirName))));

    dirName = (format == DirectoryFormat.Archive) ? "2013y08m16d18h01m50s-device1" : "8m16d18h1m50s";
    callbacks.processSyncDir(eq(SyncDirId.parseSyncDir(deploymentId, dirName)), eq(new File(deploymentPerDevice.getRoot(root, format), FsUtils.FsAgnostify("village2/TB2/" + dirName))));
    callbacks.endTalkingBook();

    expect(callbacks.startTalkingBook("TB3")).andReturn(true);
    dirName = (format == DirectoryFormat.Archive) ? "2013y08m15d19h01m50s-device1" : "8m15d19h1m50s";
    callbacks.processSyncDir(eq(SyncDirId.parseSyncDir(deploymentId, dirName)), eq(new File(deploymentPerDevice.getRoot(root, format), FsUtils.FsAgnostify("village2/TB3/" + dirName))));
    callbacks.endTalkingBook();

    expect(callbacks.startTalkingBook("TB4")).andReturn(true);
    dirName = (format == DirectoryFormat.Archive) ? "2013y08m15d20h01m50s-device1" : "8m15d20h1m50s";
    callbacks.processSyncDir(eq(SyncDirId.parseSyncDir(deploymentId, dirName)), eq(new File(deploymentPerDevice.getRoot(root, format), FsUtils.FsAgnostify("village2/TB4/" + dirName))));
    callbacks.endTalkingBook();
    callbacks.endVillage();
    callbacks.endDeviceDeployment();

    deploymentPerDevice = new DeploymentPerDevice("2013-03", "device2");
    deploymentId = new DeploymentId((short)2013, (short)3, "2013-03");
    expect(callbacks.startDeviceDeployment(eq(deploymentPerDevice))).andReturn(true);
    expect(callbacks.startVillage("village1")).andReturn(true);
    expect(callbacks.startTalkingBook("TB5")).andReturn(true);
    dirName = (format == DirectoryFormat.Archive) ? "2013y07m15d17h10m50s-device2" : "7m15d17h10m50s";
    callbacks.processSyncDir(eq(SyncDirId.parseSyncDir(deploymentId, dirName)), eq(new File(deploymentPerDevice.getRoot(root, format), FsUtils.FsAgnostify("village1/TB5/" + dirName))));
    callbacks.endTalkingBook();
    callbacks.endVillage();

    expect(callbacks.startVillage("village3")).andReturn(true);
    callbacks.endVillage();

    callbacks.endDeviceDeployment();

    deploymentPerDevice = new DeploymentPerDevice("2013-04", "device2");
    deploymentId = new DeploymentId((short)2013, (short)4, "2013-04");

    expect(callbacks.startDeviceDeployment(eq(deploymentPerDevice))).andReturn(true);
    expect(callbacks.startVillage("village1")).andReturn(true);
    expect(callbacks.startTalkingBook("TB5")).andReturn(true);
    callbacks.endTalkingBook();
    callbacks.endVillage();

    expect(callbacks.startVillage("village4")).andReturn(true);
    expect(callbacks.startTalkingBook("TB6")).andReturn(true);
    dirName = (format == DirectoryFormat.Archive) ? "2013y08m15d18h11m50s-device2" : "8m15d18h11m50s";
    callbacks.processSyncDir(eq(SyncDirId.parseSyncDir(deploymentId, dirName) ), eq(new File(deploymentPerDevice.getRoot(root, format), FsUtils.FsAgnostify("village4/TB6/" + dirName))));
    callbacks.endTalkingBook();
    callbacks.endVillage();
    callbacks.endDeviceDeployment();

    callbacks.endProcessing();
    EasyMock.checkOrder(callbacks, false);
    EasyMock.replay(callbacks);
    return callbacks;
  }

  @Test
  public void testIteratorSync() throws Exception {

    StatsPackageManifest  manifest = new StatsPackageManifest(1, new HashMap<String, SyncRange>());
    DirectoryCallbacks  callbacks = setupMock(TEST1_SYNC, DirectoryFormat.Sync, manifest);
    DirectoryIterator iterator = new DirectoryIterator(TEST1_SYNC, DirectoryFormat.Sync, false);
    iterator.process(callbacks);
//    verify(callbacks);

  }


  @Test
  public void testIteratorArchive() throws Exception {

    StatsPackageManifest  manifest = new StatsPackageManifest(2, new HashMap<String, SyncRange>());
    DirectoryCallbacks  callbacks = setupMock(TEST1_ARCHIVE, DirectoryFormat.Archive, manifest);
    DirectoryIterator iterator = new DirectoryIterator(TEST1_ARCHIVE, DirectoryFormat.Archive, false);
    iterator.process(callbacks);
    verify(callbacks);

  }


}
