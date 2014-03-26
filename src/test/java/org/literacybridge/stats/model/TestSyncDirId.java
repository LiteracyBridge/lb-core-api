package org.literacybridge.stats.model;

import junit.framework.TestCase;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.literacybridge.stats.model.DeploymentId;
import org.literacybridge.stats.model.SyncDirId;

/**
 */
public class TestSyncDirId {

  @Test
  public void testParsingOldFormats() {

    DeploymentId  deploymentId = DeploymentId.parseContentUpdate("2013-1");
    SyncDirId id1 = SyncDirId.parseSyncDir(deploymentId, "8m30d17h18m4s");
    TestCase.assertEquals(id1.uniquifier, "");
    TestCase.assertEquals(id1.version, SyncDirId.SYNC_VERSION_1);
    TestCase.assertEquals(id1.dirName, "8m30d17h18m4s");
    TestCase.assertEquals(id1.dateTime, new LocalDateTime(2013, 8, 30, 17, 18, 4));

    SyncDirId id2 = SyncDirId.parseSyncDir(deploymentId, "12m30d17h18m4s");
    TestCase.assertEquals(id2.uniquifier, "");
    TestCase.assertEquals(id2.version, SyncDirId.SYNC_VERSION_1);
    TestCase.assertEquals(id2.dirName, "12m30d17h18m4s");
    TestCase.assertEquals(id2.dateTime, new LocalDateTime(2012, 12, 30, 17, 18, 4));

    SyncDirId id3 = SyncDirId.parseSyncDir(deploymentId, "1m3d17h18m4s");
    TestCase.assertEquals(id3.uniquifier, "");
    TestCase.assertEquals(id3.version, SyncDirId.SYNC_VERSION_1);
    TestCase.assertEquals(id3.dirName, "1m3d17h18m4s");
    TestCase.assertEquals(id3.dateTime, new LocalDateTime(2013, 1, 3, 17, 18, 4));


    DeploymentId  deploymentId2 = DeploymentId.parseContentUpdate("2013-6");
    SyncDirId id4 = SyncDirId.parseSyncDir(deploymentId2, "1m3d17h18m4s");
    TestCase.assertEquals(id4.uniquifier, "");
    TestCase.assertEquals(id4.version, SyncDirId.SYNC_VERSION_1);
    TestCase.assertEquals(id4.dirName, "1m3d17h18m4s");
    TestCase.assertEquals(id4.dateTime, new LocalDateTime(2014, 1, 3, 17, 18, 4));

  }

  @Test
  public void testParsingNewFormats() {

    DeploymentId  deploymentId = DeploymentId.parseContentUpdate("2013-1");
    SyncDirId id1 = SyncDirId.parseSyncDir(deploymentId, "2014y8m30d17h18m4s-XXX");
    TestCase.assertEquals(id1.uniquifier, "XXX");
    TestCase.assertEquals(id1.version, SyncDirId.SYNC_VERSION_2);
    TestCase.assertEquals(id1.dirName, "2014y8m30d17h18m4s-XXX");
    TestCase.assertEquals(id1.dateTime, new LocalDateTime(2014, 8, 30, 17, 18, 4));

    SyncDirId id2 = SyncDirId.parseSyncDir(deploymentId, "2014y12m30d17h18m4s-ZZZ");
    TestCase.assertEquals(id2.uniquifier, "ZZZ");
    TestCase.assertEquals(id2.version, SyncDirId.SYNC_VERSION_2);
    TestCase.assertEquals(id2.dirName, "2014y12m30d17h18m4s-ZZZ");
    TestCase.assertEquals(id2.dateTime, new LocalDateTime(2014, 12, 30, 17, 18, 4));

    SyncDirId id3 = SyncDirId.parseSyncDir(deploymentId, "2014y1m3d17h18m4s-YYY");
    TestCase.assertEquals(id3.uniquifier, "YYY");
    TestCase.assertEquals(id3.version, SyncDirId.SYNC_VERSION_2);
    TestCase.assertEquals(id3.dirName, "2014y1m3d17h18m4s-YYY");
    TestCase.assertEquals(id3.dateTime, new LocalDateTime(2014, 1, 3, 17, 18, 4));

  }


  @Test
  public void testComparingIds() {

    DeploymentId  deploymentId = DeploymentId.parseContentUpdate("2013-1");
    SyncDirId id0 = SyncDirId.parseSyncDir(deploymentId, "INVALID_DIR");
    SyncDirId id1 = SyncDirId.parseSyncDir(deploymentId, "12m30d17h18m4s");
    SyncDirId id2 = SyncDirId.parseSyncDir(deploymentId, "1m3d17h18m4s");
    SyncDirId id3 = SyncDirId.parseSyncDir(deploymentId, "2013y1m3d17h18m4s-YYY");
    SyncDirId id4 = SyncDirId.parseSyncDir(deploymentId, "2013y8m30d17h18m3s-XXX");
    SyncDirId id5 = SyncDirId.parseSyncDir(deploymentId, "8m30d17h18m4s");
    SyncDirId id6 = SyncDirId.parseSyncDir(deploymentId, "2013y12m30d17h18m4s-ZZZ");

    SyncDirId[] idsInOrder = new SyncDirId[] {
        id0, id1, id2, id3, id4, id5, id6
    };

    for (int i=0; i<idsInOrder.length; i++) {
      for (int j=0; j<idsInOrder.length; j++) {
        int compVal1 = idsInOrder[i].compareTo(idsInOrder[j]);
        int compVal2 = idsInOrder[j].compareTo(idsInOrder[i]);

        if (i<j) {
          TestCase.assertTrue(compVal1 < 0);
          TestCase.assertTrue(compVal2 > 0);
        } else if (j<i) {
          TestCase.assertTrue(compVal1 > 0);
          TestCase.assertTrue(compVal2 < 0);
        } else {
          TestCase.assertEquals(0, compVal1);
          TestCase.assertEquals(0, compVal2);
        }
      }

    }

  }


}
