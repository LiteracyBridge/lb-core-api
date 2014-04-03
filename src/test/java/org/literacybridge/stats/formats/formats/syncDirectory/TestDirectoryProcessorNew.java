package org.literacybridge.stats.formats.formats.syncDirectory;

import org.easymock.EasyMock;
import org.junit.Test;
import org.literacybridge.stats.api.TalkingBookDataProcessor;
import org.literacybridge.stats.formats.ProcessingContext;
import org.literacybridge.stats.formats.SyncProcessingContext;
import org.literacybridge.stats.formats.formats.logFile.LogAction;
import org.literacybridge.stats.formats.formats.logFile.LogLineContext;
import org.literacybridge.stats.formats.statsFile.StatsFile;
import org.literacybridge.stats.DirectoryIterator;
import org.literacybridge.stats.formats.syncDirectory.DirectoryProcessor;
import org.literacybridge.stats.formats.syncDirectory.DirectoryProcessorNew;
import org.literacybridge.stats.model.DirectoryFormat;
import org.literacybridge.utils.FsUtils;

import java.io.File;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;

/**
 */
public class TestDirectoryProcessorNew {

  @Test
  public void testEndToEnd() throws Exception {

    final TalkingBookDataProcessor eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
    final File testRoot = new File(FsUtils.FsAgnostify("src/test/resources/testSyncDir"));
    final File deviceRoot = new File(testRoot, FsUtils.FsAgnostify("testDevice/collected-data"));
    final File talkingBookDir = new File(deviceRoot, FsUtils.FsAgnostify("2013-03/Baazu-Jirapa/TB0002FE"));

    final File logTxtFile2 = new File(talkingBookDir,FsUtils.FsAgnostify("11m14d18h45m55s/log/log.txt"));
    final File logTxtFile = new File(talkingBookDir, FsUtils.FsAgnostify("8m30d17h9m8s/log/log.txt"));

    final File logArchiveFile = new File(talkingBookDir, FsUtils.FsAgnostify("8m30d17h9m8s/log-archive"));
    final File TB0002FE_0003_File = new File(logArchiveFile, "log_TB0002FE_0003_0000.txt");
    final File TB0002FE_0004_File = new File(logArchiveFile, "log_TB0002FE_0004_0000.txt");


    eventInterface.onTalkingBookStart(anyObject(ProcessingContext.class));
    //Events from /logs/log.txt
    eventInterface.onLogFileStart(logTxtFile.getAbsolutePath());
    eventInterface.onVoltageDrop(anyObject(LogLineContext.class), eq(LogAction.playing), eq(.12), eq(3));
    eventInterface.onVoltageDrop(anyObject(LogLineContext.class), eq(LogAction.playing), eq(.26), eq(0));
    eventInterface.onShuttingDown(anyObject(LogLineContext.class));

    eventInterface.onLogFileEnd();

    //Events from /log-archive/log_TB0002FE_0003_0000.txt
    eventInterface.onLogFileStart(TB0002FE_0003_File.getAbsolutePath());

    eventInterface.onCategory(anyObject(LogLineContext.class), eq(DirectoryProcessor.CATEGORY_MAP.get("1")));
    eventInterface.onPlay(anyObject(LogLineContext.class), eq("TB0003e8_6F58EE29"), eq(3), eq(3.49));
    eventInterface.onVoltageDrop(anyObject(LogLineContext.class), eq(LogAction.paused), eq(0.04), eq(0));

    eventInterface.onCategory(anyObject(LogLineContext.class), eq(DirectoryProcessor.CATEGORY_MAP.get("1")));
    eventInterface.onCategory(anyObject(LogLineContext.class), eq(DirectoryProcessor.CATEGORY_MAP.get("1-2")));
    eventInterface.onCategory(anyObject(LogLineContext.class), eq(DirectoryProcessor.CATEGORY_MAP.get("2")));
    eventInterface.onPlay(anyObject(LogLineContext.class), eq("LB-2_fz02ma8nab_d"), eq(3), eq(3.16));
    eventInterface.onPlayed(anyObject(LogLineContext.class), eq("LB-2_fz02ma8nab_d"), eq((short) 13), eq((short) 422),
                            eq(3), eq(3.16), eq(false));
    eventInterface.onPlay(anyObject(LogLineContext.class), eq("LB-2_x6g9tv0p3e_m"), eq(3), eq(3.16));
    eventInterface.onPlayed(anyObject(LogLineContext.class), eq("LB-2_x6g9tv0p3e_m"), eq((short) 205), eq((short) 204),
                            eq(3), eq(3.16), eq(true));
    eventInterface.onVoltageDrop(anyObject(LogLineContext.class), eq(LogAction.playing), eq(.06), eq(2));
    eventInterface.onLogFileEnd();

    //Events from /log-archive/log_TB0002FE_0004_0000.txt
    eventInterface.onLogFileStart(TB0002FE_0004_File.getAbsolutePath());
    eventInterface.onCategory(EasyMock.anyObject(LogLineContext.class), eq(DirectoryProcessor.CATEGORY_MAP.get("2")));
    eventInterface.onPlay(EasyMock.anyObject(LogLineContext.class), eq("LB-2_fz02ma8nab_d"), eq(3), eq(2.89));
    eventInterface.onPlayed(EasyMock.anyObject(LogLineContext.class), eq("LB-2_fz02ma8nab_d"), eq((short) 12),
                            eq((short) 422), eq(3), eq(2.89), eq(false));

    eventInterface.onPlay(EasyMock.anyObject(LogLineContext.class), eq("LB-2_x6g9tv0p3e_m"), eq(3), eq(2.89));
    eventInterface.onPlayed(EasyMock.anyObject(LogLineContext.class), eq("LB-2_x6g9tv0p3e_m"), eq((short) 205),
                            eq((short) 204), eq(3), eq(2.89), eq(true));
    eventInterface.onLogFileEnd();

    eventInterface.onLogFileStart(logTxtFile2.getAbsolutePath());
    eventInterface.onPause(anyObject(LogLineContext.class), eq(""));
    eventInterface.onVoltageDrop(anyObject(LogLineContext.class), eq(LogAction.paused), eq(.04), eq(136));
    eventInterface.onShuttingDown(anyObject(LogLineContext.class));

    eventInterface.onCategory(anyObject(LogLineContext.class), eq(DirectoryProcessor.CATEGORY_MAP.get("1")));
    eventInterface.onPlay(anyObject(LogLineContext.class), eq("H_0200_6A0564FF"), eq(04), eq(2.90));
    eventInterface.onPlayed(anyObject(LogLineContext.class), eq("H_0200_6A0564FF"), eq((short) 1), eq((short) 818),
                            eq(04), eq(2.90), eq(false));
    eventInterface.onShuttingDown(anyObject(LogLineContext.class));
    eventInterface.onLogFileEnd();
    eventInterface.onTalkingBookEnd(anyObject(ProcessingContext.class));


    EasyMock.checkOrder(eventInterface, false);
    eventInterface.processStatsFile(EasyMock.anyObject(SyncProcessingContext.class), eq("H_0200_6A0564FF"),
                                    eq(new StatsFile("H_0200_6A0564FF", 0, 0, 0, 0, 0, 0)));
    eventInterface.processStatsFile(EasyMock.anyObject(SyncProcessingContext.class), eq("LB-2_fz02ma8nab_d"),
                                    eq(new StatsFile("LB-2_fz02ma8nab_d", 2, 0, 0, 0, 0, 0)));
    eventInterface.processStatsFile(EasyMock.anyObject(SyncProcessingContext.class), eq("LB-2_x6g9tv0p3e_m"),
                                    eq(new StatsFile("LB-2_x6g9tv0p3e_m", 2, 2, 0, 0, 0, 0)));
    eventInterface.markStatsFileAsCorrupted(EasyMock.anyObject(SyncProcessingContext.class), eq("Invalid-Name"),
                                            EasyMock.anyString());

    EasyMock.replay(eventInterface);

    DirectoryProcessorNew processor = new DirectoryProcessorNew(eventInterface, DirectoryProcessor.CATEGORY_MAP);
    DirectoryIterator iterator = new DirectoryIterator(testRoot, DirectoryFormat.Sync, true);
    iterator.process(processor);

    EasyMock.verify(eventInterface);
  }

}
