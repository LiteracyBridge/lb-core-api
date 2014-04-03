package org.literacybridge.stats.formats.formats.flashData;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import junit.framework.TestCase;
import org.junit.Test;
import org.literacybridge.stats.formats.FirmwareConstants;
import org.literacybridge.stats.formats.flashData.FlashData;
import org.literacybridge.stats.formats.flashData.NORmsgStats;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author willpugh
 */
public class TestFlashData {

  @Test
  public void testLoadingFlashData() throws IOException {

    final InputStream is = getClass().getResourceAsStream("/flashDatas/flashData.bin");
    FlashData flashData = FlashData.parseFromStream(is);
    TestCase.assertNotNull(flashData);

    TestCase.assertEquals(FirmwareConstants.MAX_TRACKED_MESSAGES, flashData.getMsgStats().getStats().size());

    TestCase.assertEquals(1, flashData.allStats().size());

    Collection<NORmsgStats> nonEmptyStats = Collections2.filter(flashData.allStats(), new Predicate<NORmsgStats>() {
      @Override public boolean apply(NORmsgStats input) {
        return !input.isEmpty();
      }
    });
    TestCase.assertEquals(1, nonEmptyStats.size());

  }

  /*
  @Test
  public void testLoading2() throws IOException {

    final InputStream is = new FileInputStream("/Users/willpugh/projects/Dropbox_folder/Fidelis/collected-data/2013-03/Nyeni-Jirapa/TB000473/8m20d20h14m18s/statistics/stats/flashData.bin");
    FlashData flashData = FlashData.parseFromStream(is);
    TestCase.assertNotNull(flashData);

    for (NORmsgStats stats : flashData.allStats()) {
      TestCase.assertFalse(stats.isEmpty());
      //TestCase.assertNotNull(stats.getContentId());
    }

  }
  */
  /*
  @Test
  public void testLoading3() throws IOException {
    final String flashDataPath = "/Users/willpugh/projects/Dropbox_folder/Wa-01/collected-data/2013-5/Baazu-Jirapa/TB0003cb/10m12d9h40m5s/statistics/stats/flashData.bin";
    //final String flashDataPath = "/Users/willpugh/projects/Dropbox_folder/Fidelis/collected-data/2013-03/Jeffiri-Jirapa/TB000487/8m21d12h14m12s/statistics/stats/flashData.bin";
    final InputStream is = new FileInputStream(flashDataPath);
    FlashData flashData = FlashData.parseFromStream(is);
    for (NORmsgStats  stats : flashData.allStats()) {
      System.out.println(stats.toString());
    }

    //LinkedList<String>  errors = new LinkedList<>();
    //boolean isValid = flashData.isValid(errors);
    //TestCase.assertTrue("Invalid flashdata: " + StringUtils.join(errors, "; "), isValid);
    //System.out.println("++++++++++++++++++++++++++++++++++++++++");

    OldTbInfo oldTbInfo = new OldTbInfo(flashDataPath);
    System.out.println(oldTbInfo.toString());
  }
  */
}
