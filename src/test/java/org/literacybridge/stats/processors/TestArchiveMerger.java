package org.literacybridge.stats.processors;

import org.literacybridge.stats.DirectoryIterator;
import org.literacybridge.stats.TestDirectoryIterator;
import org.literacybridge.stats.model.DirectoryFormat;
import org.literacybridge.utils.FsUtils;

import java.io.File;
import java.io.IOException;

/**
 */
public class TestArchiveMerger {

  File TEST_DATA  = new File(FsUtils.FsAgnostify("target/testData"));

  public void testMerge() throws Exception {
    File  destDir = File.createTempFile("TestArchiveMerger", "", TEST_DATA);
    destDir.delete();
    destDir.mkdirs();

    ArchiveMerger merger = new ArchiveMerger(destDir, DirectoryFormat.Archive);

    DirectoryIterator dirIter = new DirectoryIterator(TestDirectoryIterator.TEST1_ARCHIVE, DirectoryFormat.Archive, false);
    DirectoryIterator dirIter2 = new DirectoryIterator(TestDirectoryIterator.TEST1_SYNC,   DirectoryFormat.Sync, false);

    dirIter.process(merger);
    dirIter2.process(merger);


  }

}
