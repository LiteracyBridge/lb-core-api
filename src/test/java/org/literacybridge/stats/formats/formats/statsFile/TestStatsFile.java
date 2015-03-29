package org.literacybridge.stats.formats.formats.statsFile;

import junit.framework.TestCase;
import org.junit.Test;
import org.literacybridge.stats.formats.statsFile.StatsFile;

import java.io.*;

/**
 */
public class TestStatsFile {

  @Test
  public void testStatsFile() throws IOException {

    FileInputStream is = new FileInputStream("src/test/resources/statsFiles/2014-2^b-30346464_0E246981.stat".replace('/', File.separatorChar));
    StatsFile statsFile = StatsFile.read(is);
    TestCase.assertEquals(54, statsFile.openCount);
    TestCase.assertEquals(48, statsFile.completionCount);
    TestCase.assertEquals(0, statsFile.copyCount);
    TestCase.assertEquals(0, statsFile.surveyCount);
    TestCase.assertEquals(0, statsFile.appliedCount);
    TestCase.assertEquals(0, statsFile.uselessCount);
  }

  @Test
  public void testRoundTrip() throws IOException {

    StatsFile writtenFile = new StatsFile("srn", "Hello", 1, 2, 3, 4, 5, 6);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    StatsFile.write(writtenFile, baos);
    baos.flush();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    StatsFile readFile = StatsFile.read(bais);
//        TestCase.assertEquals(writtenFile, readFile);
  }

  @Test
  public void testRoundTripLongMessageId() throws IOException {

    StatsFile fileToWrite = new StatsFile("srn", "Hello12345678901234567890", 1, 2, 3, 4, 5, 6);
    StatsFile fileActuallyWritten = new StatsFile("srn", "Hello12345678901234567890", 1, 2, 3, 4, 5, 6);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    StatsFile.write(fileToWrite, baos);
    baos.flush();

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    StatsFile readFile = StatsFile.read(bais);
//        TestCase.assertEquals(fileActuallyWritten, readFile);
  }

  @Test
  public void writeFilesForTests() throws Exception {

    StatsFile[] files = new StatsFile[]{
      new StatsFile("srn", "H_0200_6A0564FF", 0, 0, 0, 0, 0, 0),
      new StatsFile("srn", "LB-2_fz02ma8nab_d", 2, 0, 0, 0, 0, 0),
      new StatsFile("srn", "LB-2_x6g9tv0p3e_m", 2, 2, 0, 0, 0, 0)
    };

    for (StatsFile file : files) {
      String fileName = "src/test/resources/testSyncDir/testDevice/collected-data/2013-03/Baazu-Jirapa/TB0002FE/8m30d17h9m8s/statistics/stats/" + file.messageId;
      FileOutputStream fos = new FileOutputStream(fileName.replace('/', File.separatorChar));
      StatsFile.write(file, fos);
      fos.close();
    }
  }


}
