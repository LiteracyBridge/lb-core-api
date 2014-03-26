package org.literacybridge.stats.model.validation;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

/**
 */
public class TbDataHasInvalidProperties extends ValidationError {

  public final File tbDataFile;
  public final List<IncorrectFilePropertyValue> incorrectFilePropertyValues;

  public static String createErrorMessage(File tbDataPath, List<IncorrectFilePropertyValue> incorrectPropertyValues) {
    return String.format("Invalid values in the TBData files: Path=%s. Errors=%s",
                         tbDataPath.getPath(),
                         StringUtils.join(incorrectPropertyValues, ","));
  }


  public TbDataHasInvalidProperties(File tbDataPath, List<IncorrectFilePropertyValue> incorrectPropertyValues) {
    super(createErrorMessage(tbDataPath, incorrectPropertyValues), INVALID_DATA_IN_TBDATA);
    this.tbDataFile = tbDataPath;
    this.incorrectFilePropertyValues = incorrectPropertyValues;
  }
}
