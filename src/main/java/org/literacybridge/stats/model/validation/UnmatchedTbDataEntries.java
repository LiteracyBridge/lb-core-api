package org.literacybridge.stats.model.validation;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

/**
 */
public class UnmatchedTbDataEntries extends ValidationError {

  public final List<NonMatchingTbDataEntry> nonMatchingTbDataEntries;

  public static String createErrorMessage(List<NonMatchingTbDataEntry> nonMatchingTbDataEntries) {
    return String.format("There are values in the TBData that do not correspond to any directories in the file system.  These entries are: \n  %s",
                         StringUtils.join(nonMatchingTbDataEntries, "\n  "));
  }

  public UnmatchedTbDataEntries(List<NonMatchingTbDataEntry> nonMatchingTbDataEntries) {
    super(createErrorMessage(nonMatchingTbDataEntries), UNMATCHED_TBDATA_ENTRIES);
    this.nonMatchingTbDataEntries = nonMatchingTbDataEntries;
  }
}
