package org.literacybridge.stats.model.validation;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

/**
 */
public class NoMatchingTbDataError extends ValidationError {

  public static String createErrorMessage(String syncDirName, File filePath, int version) {
    if (version == 1) {
      return String.format("No matching TBData entry for %s.  Tried to find matches with same device that are slightly newer.  Full path is: %s",
                           syncDirName,
                           filePath.getPath());
    } else {
      return String.format("No matching TBData entry for %s.  For v2 directories, this MUST be an exact match of the syncDirName.  Full path is: %s",
                           syncDirName,
                           filePath.getPath());
    }
  }


  public NoMatchingTbDataError(String syncDirName, File filePath, int version) {
    super(createErrorMessage(syncDirName, filePath, version), NO_MATCHING_TBDATA_ENTRY);
  }
}
