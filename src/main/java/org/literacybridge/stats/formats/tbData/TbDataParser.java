package org.literacybridge.stats.formats.tbData;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.WordUtils;
import org.literacybridge.stats.model.TbDataLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Parses a tbData file.  Does some work to try to work for older formats.
 */
public class TbDataParser {


  protected static final Logger logger = LoggerFactory.getLogger(TbDataParser.class);

  protected static final Map<String, Integer> V3_TB_MAP = ImmutableMap.<String, Integer>builder()
          .put("PROJECT", 0)
          .put("UPDATE_DATE_TIME", 1)
          .put("OUT_SYNCH_DIR", 2)
          .put("LOCATION", 3)
          .put("ACTION", 4)
          .put("DURATION_SEC", 5)
          .put("OUT-SN", 6)
          .put("OUT-DEPLOYMENT", 7)
          .put("OUT-IMAGE", 8)
          .put("OUT-FW-REV", 9)
          .put("OUT-COMMUNITY", 10)
          .put("OUT-ROTATION-DATE", 11)
          .put("IN-SN", 12)
          .put("IN-DEPLOYMENT", 13)
          .put("IN-IMAGE", 14)
          .put("IN-FW-REV", 15)
          .put("IN-COMMUNITY", 16)
          .put("IN-LAST-UPDATED", 17)
          .put("IN-SYNCH-DIR", 18)
          .put("IN-DISK-LABEL", 19)
          .put("CHKDSK CORRUPTION?", 20)
          .put("FLASH-SN", 21)
          .put("FLASH-REFLASHES", 22)
          .put("FLASH-DEPLOYMENT", 23)
          .put("FLASH-IMAGE", 24)
          .put("FLASH-COMMUNITY", 25)
          .put("FLASH-LAST-UPDATED", 26)
          .put("FLASH-CUM-DAYS", 27)
          .put("FLASH-CORRUPTION-DAY", 28)
          .put("FLASH-VOLT", 29)
          .put("FLASH-POWERUPS", 30)
          .put("FLASH-PERIODS", 31)
          .put("FLASH-ROTATIONS", 32)
          .put("FLASH-MSGS", 33)
          .put("FLASH-MINUTES", 34)
          .put("FLASH-STARTS", 35)
          .put("FLASH-PARTIAL", 36)
          .put("FLASH-HALF", 37)
          .put("FLASH-MOST", 38)
          .put("FLASH-ALL", 39)
          .put("FLASH-APPLIED", 40)
          .put("FLASH-USELESS", 41)
          .put("FLASH-MINUTES-R0", 41)
          .put("FLASH-PERIOD-R0", 42)
          .put("FLASH-HRS-POST-UPDATE-R0", 43)
          .put("FLASH-VOLT-R0", 44)
          .put("FLASH-ROTATION", 45)
          .put("FLASH-MINUTES-R1", 46)
          .put("FLASH-PERIOD-R1", 47)
          .put("FLASH-HRS-POST-UPDATE-R1", 48)
          .put("FLASH-VOLT-R1", 49)
          .put("FLASH-MINUTES-R2", 50)
          .put("FLASH-PERIOD-R2", 51)
          .put("FLASH-HRS-POST-UPDATE-R2", 52)
          .put("FLASH-VOLT-R2", 53)
          .put("FLASH-MINUTES-R3", 54)
          .put("FLASH-PERIOD-R3", 55)
          .put("FLASH-HRS-POST-UPDATE-R3", 56)
          .put("FLASH-VOLT-R3", 57)
          .put("FLASH-MINUTES-R4", 58)
          .put("FLASH-PERIOD-R4", 59)
          .put("FLASH-HRS-POST-UPDATE-R4", 60)
          .put("FLASH-VOLT-R4", 61)
          .build();

  protected static final Map<String, Integer> V1_TB_MAP = ImmutableMap.<String, Integer>builder()
    .put("UPDATE_DATE_TIME", 0)
    .put("IN-SYNCH-DIR", 0)
    .put("IN-SN", 2)
    .put("OUT-SN", 8)
    .put("IN-DEPLOYMENT", 4)
    .put("OUT-DEPLOYMENT", 9)
    .put("IN-COMMUNITY", 5)
    .put("OUT-COMMUNITY", 10)
    .put("ACTION", 3)
    .build();

  protected static final Map<String, Integer> V0_TB_MAP = ImmutableMap.<String, Integer>builder()
    .put("UPDATE_DATE_TIME", 0)
    .put("IN-SYNCH-DIR", 0)
    .put("IN-SN", 9)
    .put("OUT-SN", 3)
    .put("IN-DEPLOYMENT", 10)
    .put("OUT-DEPLOYMENT", 4)
    .put("IN-COMMUNITY", 13)
    .put("OUT-COMMUNITY", 7)
    .put("ACTION", 2)
    .build();


  protected static final String[] V3_FIELD_NAMES = new String[]{
    "PROJECT", "UPDATE_DATE_TIME", "OUT_SYNCH_DIR", "LOCATION", "ACTION", "DURATION_SEC", "OUT-SN", "OUT-DEPLOYMENT",
    "OUT-IMAGE", "OUT-FW-REV", "OUT-COMMUNITY", "OUT-ROTATION-DATE", "IN-SN", "IN-DEPLOYMENT", "IN-IMAGE", "IN-FW-REV",
    "IN-COMMUNITY", "IN-LAST-UPDATED", "IN-SYNCH-DIR", "IN-DISK-LABEL", "CHKDSK CORRUPTION?", "FLASH-SN", "FLASH-REFLASHES",
    "FLASH-DEPLOYMENT", "FLASH-IMAGE", "FLASH-COMMUNITY", "FLASH-LAST-UPDATED", "FLASH-CUM-DAYS", "FLASH-CORRUPTION-DAY",
    "FLASH-VOLT", "FLASH-POWERUPS", "FLASH-PERIODS", "FLASH-ROTATIONS", "FLASH-MSGS", "FLASH-MINUTES", "FLASH-STARTS",
    "FLASH-PARTIAL", "FLASH-HALF", "FLASH-MOST", "FLASH-ALL", "FLASH-APPLIED", "FLASH-USELESS",
    "FLASH-MINUTES-R0", "FLASH-PERIOD-R0", "FLASH-HRS-POST-UPDATE-R0", "FLASH-VOLT-R0", "FLASH-ROTATION", "FLASH-MINUTES-R1",
    "FLASH-PERIOD-R1", "FLASH-HRS-POST-UPDATE-R1", "FLASH-VOLT-R1", "FLASH-MINUTES-R2", "FLASH-PERIOD-R2",
    "FLASH-HRS-POST-UPDATE-R2", "FLASH-VOLT-R2", "FLASH-MINUTES-R3", "FLASH-PERIOD-R3", "FLASH-HRS-POST-UPDATE-R3",
    "FLASH-VOLT-R3", "FLASH-MINUTES-R4", "FLASH-PERIOD-R4", "FLASH-HRS-POST-UPDATE-R4", "FLASH-VOLT-R"
  };

  protected static final char[] FIELD_DELIMITERS = new char[]{'-'};

  private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
    for (Map.Entry<T, E> entry : map.entrySet()) {
      if (Objects.equals(value, entry.getValue())) {
        return entry.getKey();
      }
    }
    return null;
  }

  public List<TbDataLine> parseTbDataFile(File tbdataFile, boolean includesHeaders) throws IOException {

    System.out.println(" reading " + tbdataFile.getName());
    List<TbDataLine> retVal = new ArrayList<>();
    FileReader fileReader = new FileReader(tbdataFile);
    CSVReader csvReader = new CSVReader(fileReader);

    int lineNumber = 1;
    List<String[]> lines = csvReader.readAll();

    Map<String, Integer> headerMap = V3_TB_MAP;
    if (getTBdataVersion(tbdataFile) == 1) {
      headerMap = V1_TB_MAP;
    } else if (getTBdataVersion(tbdataFile) == 0) {
      headerMap = V0_TB_MAP;
    }

    for (String[] line : lines) {

      if (lineNumber == 1 && includesHeaders) {
        String firstHeaderDefined = getKeyByValue(headerMap,0);
        String firstHeaderActual = line[0];
        if (firstHeaderActual.equalsIgnoreCase(firstHeaderDefined)) {
          headerMap = processHeader(line);
        } else {
          includesHeaders = false;
        }
      }
      if (!(lineNumber == 1 && includesHeaders)) {
        try {
          retVal.add(processLine(line, headerMap));
          processLine(line, headerMap);
        } catch (NoSuchMethodException e) {
          throw new IOException(e);
        } catch (IllegalAccessException e) {
          throw new IOException(e);
        } catch (InvocationTargetException e) {
          throw new IOException(e);
        }

      }

      lineNumber++;
    }
    return retVal;
  }

  public TbDataLine processLine(String[] line, Map<String, Integer> headerToIndex) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    TbDataLine retVal = new TbDataLine();
    Map<String, String> lineValues = buildLineMap(line, headerToIndex);

    for (String fieldName : V3_FIELD_NAMES) {
      setProperty(fieldName, lineValues, retVal);
    }
    return retVal;
  }

  protected void setProperty(String propertyName, Map<String, String> lineValues, TbDataLine line) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    if (lineValues.containsKey(propertyName)) {
      String[] propertyNameParts = propertyName.toLowerCase().split("[? _-]");

      StringBuilder javaSetterNameBuilder = new StringBuilder("set");
      for (String propertyNamePart : propertyNameParts) {
        javaSetterNameBuilder.append(WordUtils.capitalize(propertyNamePart, FIELD_DELIMITERS));
      }

      Method setter = null;
      Method[] methods = line.getClass().getMethods();
      String javaSetterName = javaSetterNameBuilder.toString();
      for (Method method : methods) {
        if (method.getName().equals(javaSetterName)) {
          setter = method;
          break;
        }
      }

      if (setter == null) {
        logger.warn("No setter for " + javaSetterName);
        return;
      }

      Class type = setter.getParameterTypes()[0];
      if (type.equals(Date.class)) {
        try {
          setter.invoke(line, new Date(lineValues.get(propertyName)));
        } catch (IllegalArgumentException e) {
          logger.error("Invalid date value " + lineValues.get(propertyName) + ".  Ignoring field " + propertyName + ".");
        }
      } else if (type.equals(int.class) || type.equals(Integer.TYPE)) {
        try {
          setter.invoke(line, new Integer(lineValues.get(propertyName)));
        } catch (NumberFormatException e) {
          logger.error("Invalid integer value " + lineValues.get(propertyName) + ".  Ignoring field " + propertyName + ".");
        }
      } else {
        setter.invoke(line, lineValues.get(propertyName));
      }
    }
  }

  public Map<String, String> buildLineMap(String[] line, Map<String, Integer> headerToIndex) {
    Map<String, String> lineValueMap = new HashMap<>();
    for (String fieldName : V3_FIELD_NAMES) {
      if (headerToIndex.containsKey(fieldName)) {
        int headerIndex = headerToIndex.get(fieldName);
        if (line.length > headerIndex) {
          lineValueMap.put(fieldName, line[headerIndex]);
        }
      }
    }
    return lineValueMap;
  }

  private int getTBdataVersion(File f) {
    int version;
    String stringVersion = f.getName().substring(8, 10);
    version = Integer.parseInt(stringVersion);
    return version;
  }

  protected Map<String, Integer> processHeader(String[] line) {

    Map<String, Integer> headerMap = new HashMap<>();
    for (int i = 0; i < line.length; i++) {
      headerMap.put(line[i], i);
    }
    return ImmutableMap.copyOf(headerMap);
  }


}
