package org.literacybridge.stats.formats.formats;

import junit.framework.TestCase;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.literacybridge.stats.formats.SyncProcessingContext;

public class TestSyncProcessingContext {

  @Test
  public void testSyncDirectoryNameParsing() {

    SyncProcessingContext context = new SyncProcessingContext("7m15d18h33m52s", "TB1", "Village1", "ContentPackage", "2013-03", "Device1");

    LocalDateTime timestamp = context.syncTime;
    TestCase.assertEquals(2013, timestamp.getYear());
    TestCase.assertEquals(7, timestamp.getMonthOfYear());
    TestCase.assertEquals(15, timestamp.getDayOfMonth());
    TestCase.assertEquals(18, timestamp.getHourOfDay());
    TestCase.assertEquals(33, timestamp.getMinuteOfHour());
    TestCase.assertEquals(52, timestamp.getSecondOfMinute());

  }
}
