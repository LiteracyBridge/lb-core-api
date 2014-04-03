package org.literacybridge.stats.formats.formats.logFile;

import junit.framework.TestCase;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.easymock.EasyMock;
import org.literacybridge.stats.api.TalkingBookDataProcessor;
import org.literacybridge.stats.formats.SyncProcessingContext;
import org.literacybridge.stats.formats.syncDirectory.DirectoryProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestLogFileParser {


    public static final SyncProcessingContext TEST_FILE_CONTEXT = new SyncProcessingContext("SyncDevice1", "10m1d16h37m35s", "TB10", "MyVillage", "ContentPackage", "ContentUpdate");

    public static final String TEST_FILE_NAME = "TestFileName";
    public static final LogFilePosition TEST_FILE_POSITION = new LogFilePosition(TEST_FILE_NAME, 1);
    public static final LogFilePosition TEST_FILE_POSITION_2 = new LogFilePosition(TEST_FILE_NAME, 2);
    public static final LogFilePosition TEST_FILE_POSITION_3 = new LogFilePosition(TEST_FILE_NAME, 3);


    @Test
    public void testParseLogLineInfo() {

      TalkingBookDataProcessor eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        LogFileParser logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);


        LogLineInfo logLineInfo = logFileParser.parseLogLineInfo("2r0096c008p023d18h18m53s401/314/314V");
        TestCase.assertEquals(2, logLineInfo.householdRotation);
        TestCase.assertEquals(96, logLineInfo.cycle);
        TestCase.assertEquals(8, logLineInfo.period);
        TestCase.assertEquals(23, logLineInfo.dayOfPeriod);
        TestCase.assertEquals(new LocalTime(18, 18, 53), logLineInfo.timeInPeriod);
        TestCase.assertEquals(4.01, logLineInfo.maxVolts);
        TestCase.assertEquals(3.14, logLineInfo.steadyStateVolts);
        TestCase.assertEquals(3.14, logLineInfo.minVolts);
    }

    @Test
    public void testParseLogLineInfoNoRotation() {

        TalkingBookDataProcessor eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        LogFileParser logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);


        LogLineInfo logLineInfo = logFileParser.parseLogLineInfo("0096c008p023d18h18m53s401/314/314V");
        TestCase.assertEquals(0, logLineInfo.householdRotation);
        TestCase.assertEquals(96, logLineInfo.cycle);
        TestCase.assertEquals(8, logLineInfo.period);
        TestCase.assertEquals(23, logLineInfo.dayOfPeriod);
        TestCase.assertEquals(new LocalTime(18, 18, 53), logLineInfo.timeInPeriod);
        TestCase.assertEquals(4.01, logLineInfo.maxVolts);
        TestCase.assertEquals(3.14, logLineInfo.steadyStateVolts);
        TestCase.assertEquals(3.14, logLineInfo.minVolts);
    }

    @Test
    public void testParseLogLineInfo2() {

        TalkingBookDataProcessor eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        LogFileParser logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);


        LogLineInfo logLineInfo = logFileParser.parseLogLineInfo("0r0015c003p004d14h09m42s339/289/289V");
        TestCase.assertEquals(0, logLineInfo.householdRotation);
        TestCase.assertEquals(15, logLineInfo.cycle);
        TestCase.assertEquals(3, logLineInfo.period);
        TestCase.assertEquals(4, logLineInfo.dayOfPeriod);
        TestCase.assertEquals(new LocalTime(14, 9, 42), logLineInfo.timeInPeriod);
        TestCase.assertEquals(3.39, logLineInfo.maxVolts);
        TestCase.assertEquals(2.89, logLineInfo.steadyStateVolts);
        TestCase.assertEquals(2.89, logLineInfo.minVolts);
    }



    @Test
    public void testParseLogLineInfoWithFunnyS() {

        //UNDONE(willpugh) -- I don't know why there is an S sometimes.  Ask Cliff. . .
        TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);


        LogLineInfo logLineInfo = logFileParser.parseLogLineInfo("0r0035c012pS18d14h22m59s306/217/216V");
        TestCase.assertEquals(0, logLineInfo.householdRotation);
        TestCase.assertEquals(35, logLineInfo.cycle);
        TestCase.assertEquals(12, logLineInfo.period);
        TestCase.assertEquals(18, logLineInfo.dayOfPeriod);
        TestCase.assertEquals(new LocalTime(14, 22, 59), logLineInfo.timeInPeriod);
        TestCase.assertEquals(3.06, logLineInfo.maxVolts);
        TestCase.assertEquals(2.17, logLineInfo.steadyStateVolts);
        TestCase.assertEquals(2.16, logLineInfo.minVolts);
    }

    @Test
    public void testParseInvalidLogLineInfo() {

        TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);

        LogLineInfo logLineInfo = logFileParser.parseLogLineInfo("ar0035c012pS18d14h22m59s306/217/216V");
        TestCase.assertEquals(null, logLineInfo);
    }

    @Test
    public void testPlay() throws IOException {

        final String testline = "0r0032c012pS15d02h44m41s297/221/209V:PLAY TB0003a2_2156B516 @VOL=03 @Volt=221";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((byte) 0, (byte) 32, (short) 12, (short) 15, new LocalTime(2, 44, 41), 2.97, 2.21, 2.09);
        final LogLineContext context = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onPlay(context, "TB0003a2_2156B516", 3, 2.21);
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        TestCase.assertEquals("", logFileParser.getContentLastPlayed());

        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);

        TestCase.assertEquals("TB0003a2_2156B516", logFileParser.getContentLastPlayed());
    }


    @Test
    public void testPlayedWOEnded() throws IOException {

        final String testline = "0r0032c012pS15d02h45m47s295/214/214V:PLAYED H_0172_FC0929CA 0002/0753sec @VOL=02 @Volt=214";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((byte) 0, (byte) 32, (short) 12, (short) 15, new LocalTime(2, 45, 47), 2.95, 2.14, 2.14);
        final LogLineContext    context = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onPlayed(context, "H_0172_FC0929CA", (short) 2, (short) 753, 2, 2.14, false);
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        TestCase.assertEquals("", logFileParser.getContentLastPlayed());

        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);

        TestCase.assertEquals("H_0172_FC0929CA", logFileParser.getContentLastPlayed());
    }

    @Test
    public void testPlayedWEnded() throws IOException {

        final String testline = "0r0032c012pS15d02h44m27s302/228/222V:PLAYED TB000248_AA94FE16 0732/0732sec @VOL=03 @Volt=228-Ended";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((byte) 0, (byte) 32, (short) 12, (short) 15, new LocalTime(2, 44, 27), 3.02, 2.28, 2.22);
        final LogLineContext    context = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onPlayed(context, "TB000248_AA94FE16", (short) 732, (short) 732, 3, 2.28, true);
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        TestCase.assertEquals("", logFileParser.getContentLastPlayed());

        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);

        TestCase.assertEquals("TB000248_AA94FE16", logFileParser.getContentLastPlayed());
    }


    @Test
    public void testCategory() throws IOException {

        final String testline = "0r0033c012pS18d03h09m25s315/229/229V:Category: $0-1";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((byte) 0, (byte) 33, (short) 12, (short) 18, new LocalTime(3, 9, 25), 3.15, 2.29, 2.29);
        final LogLineContext    context = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onCategory(context, "TB");
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);
    }

    @Test
    public void testPaused() throws IOException {

        final String testline = "0r0039c012pS18d14h27m59s305/221/221V:PAUSED\n" +
                                "0r0039c012pS18d14h27m59s305/221/221V:PLAY TB0003a2_2156B516 @VOL=03 @Volt=221\n" +
                                "0r0039c012pS18d14h27m59s305/221/221V:PAUSED";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((byte) 0, (byte) 39, (short) 12, (short) 18, new LocalTime(14, 27, 59), 3.05, 2.21, 2.21);
        final LogLineContext    contextFirstPause = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION, TEST_FILE_CONTEXT);
        final LogLineContext    contextPlayed = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION_2, TEST_FILE_CONTEXT);
        final LogLineContext    contextPaused = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION_3, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onPause(contextFirstPause, "");
        eventInterface.onPlay (contextPlayed, "TB0003a2_2156B516", 3, 2.21);
        eventInterface.onPause(contextPaused, "TB0003a2_2156B516");
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);
    }

    @Test
    public void testUnpaused() throws IOException {

        final String testline = "0r0039c012pS18d14h28m01s303/221/221V:UNPAUSED\n" +
                                "0r0039c012pS18d14h28m01s303/221/221V:PLAY TB0003a2_2156B516 @VOL=03 @Volt=221\n" +
                                "0r0039c012pS18d14h28m01s303/221/221V:UNPAUSED";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((byte) 0, (byte) 39, (short) 12, (short) 18, new LocalTime(14, 28, 01), 3.03, 2.21, 2.21);
        final LogLineContext    contextFirstUnPause = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION, TEST_FILE_CONTEXT);
        final LogLineContext    contextPlayed = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION_2, TEST_FILE_CONTEXT);
        final LogLineContext    contextUnPaused = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION_3, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onUnPause(contextFirstUnPause, "");
        eventInterface.onPlay   (contextPlayed, "TB0003a2_2156B516", 3, 2.21);
        eventInterface.onUnPause(contextUnPaused, "TB0003a2_2156B516");
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);
    }

    @Test
    public void testInvalidAction() throws IOException {

        final String testline = "0r0039c012pS18d14h27m59s305/221/221V:INVALID";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);
    }

    @Test
    public void testRecord() throws IOException {

        final String testline = "0r0035c012pS18d14h17m12s311/217/217V:RECORD 00037a_9_3DCBA2D0 -> 9";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((byte) 0, (byte) 35, (short) 12, (short) 18, new LocalTime(14, 17, 12), 3.11, 2.17, 2.17);
        final LogLineContext    context = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onRecord(context, "00037a_9_3DCBA2D0", 9);
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);
    }

    @Test
    public void testRecorded() throws IOException {

        final String testline = "0r0035c012pS18d14h17m32s311/217/217V:TIME RECORDED (secs): 0005";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((byte) 0, (byte) 35, (short) 12, (short) 18, new LocalTime(14, 17, 32), 3.11, 2.17, 2.17);
        final LogLineContext    context = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onRecorded(context, 5);
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);
    }

    @Test
    public void testShuttingDown() throws IOException {

        final String testline = "0r0027c011pS85d00h00m01s329/279/279V:SHUTTING DOWN";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((byte) 0, (byte) 27, (short) 11, (short) 85, new LocalTime(00, 00, 1), 3.29, 2.79, 2.79);
        final LogLineContext    context = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onShuttingDown(context);
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);
    }

    @Test
    public void testStartingSurvey() throws IOException {

        final String testline = "0r0145c079p009d18h33m00s340/291/291V:SURVEY:taken\n" +
                                "0r0145c079p009d18h33m00s340/291/291V:PLAYED H_0172_FC0929CA 0002/0753sec @VOL=02 @Volt=214\n" +
                                "0r0145c079p009d18h33m00s340/291/291V:SURVEY:taken\n";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((short) 0, (short) 145, (short) 79, (short) 9, new LocalTime(18, 33, 0), 3.40, 2.91, 2.91);

        final LogLineContext    surveyContext       = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION,   TEST_FILE_CONTEXT);
        final LogLineContext    playedContext       = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION_2, TEST_FILE_CONTEXT);
        final LogLineContext    secondSurveycontext = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION_3, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onSurvey(surveyContext, "");
        eventInterface.onPlayed(playedContext, "H_0172_FC0929CA", (short)2, (short) 753, 2, 2.14, false);
        eventInterface.onSurvey(secondSurveycontext, "H_0172_FC0929CA");
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);
    }

    @Test
    public void testSurveyApplied() throws IOException {

        final String testline = "0r0145c079p009d18h33m00s340/291/291V:SURVEY:apply\n" +
                                "0r0145c079p009d18h33m00s340/291/291V:PLAYED H_0172_FC0929CA 0002/0753sec @VOL=02 @Volt=214\n" +
                                "0r0145c079p009d18h33m00s340/291/291V:SURVEY:apply";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((short) 0, (short) 145, (short) 79, (short) 9, new LocalTime(18, 33, 00), 3.40, 2.91, 2.91);

        final LogLineContext    surveyContext       = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION, TEST_FILE_CONTEXT);
        final LogLineContext    playedContext       = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION_2, TEST_FILE_CONTEXT);
        final LogLineContext    secondSurveycontext = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION_3, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);

        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onSurveyCompleted(surveyContext, "", true);
        eventInterface.onPlayed(playedContext, "H_0172_FC0929CA", (short) 2, (short) 753, 2, 2.14, false);
        eventInterface.onSurveyCompleted(secondSurveycontext, "H_0172_FC0929CA", true);
        eventInterface.onLogFileEnd();

        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);
    }

    @Test
    public void testSurveyNotApplied() throws IOException {

        final String testline = "0r0145c079p009d18h33m00s340/291/291V:SURVEY:useless\n" +
                                "0r0145c079p009d18h33m00s340/291/291V:PLAYED H_0172_FC0929CA 0002/0753sec @VOL=02 @Volt=214\n" +
                                "0r0145c079p009d18h33m00s340/291/291V:SURVEY:useless";
        final InputStream is = new ByteArrayInputStream(testline.getBytes());

        final LogLineInfo expectedLogLineInfo = new LogLineInfo((short) 0, (short) 145, (short) 79, (short) 9, new LocalTime(18, 33, 00), 3.40, 2.91, 2.91);

        final LogLineContext    surveyContext       = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION, TEST_FILE_CONTEXT);
        final LogLineContext    playedContext       = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION_2, TEST_FILE_CONTEXT);
        final LogLineContext    secondSurveycontext = new LogLineContext(expectedLogLineInfo, TEST_FILE_POSITION_3, TEST_FILE_CONTEXT);

        final TalkingBookDataProcessor   eventInterface = EasyMock.createMock(TalkingBookDataProcessor.class);
        eventInterface.onLogFileStart(TEST_FILE_NAME);
        eventInterface.onSurveyCompleted(surveyContext, "", false);
        eventInterface.onPlayed(playedContext, "H_0172_FC0929CA", (short) 2, (short) 753, 2, 2.14, false);
        eventInterface.onSurveyCompleted(secondSurveycontext, "H_0172_FC0929CA", false);
        eventInterface.onLogFileEnd();
        EasyMock.replay(eventInterface);

        final LogFileParser   logFileParser = new LogFileParser(eventInterface, TEST_FILE_CONTEXT, DirectoryProcessor.CATEGORY_MAP);
        logFileParser.parse(TEST_FILE_NAME, is);
        EasyMock.verify(eventInterface);
    }

}
