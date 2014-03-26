package org.literacybridge.stats.model.validation;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

/**
 */
public class InvalidSyncDirError extends ValidationError {

  public final File   currPath;
  public final File   expectedPath;


  public static String createErrorMessage(File currPath, File expectedPath, List<IncorrectPropertyValue> incorrectPropertyValues) {
    return String.format("Invalid sync directory.  Path=%s.  Should equal=%s.  Errors=%s",
                         currPath.getPath(),
                         expectedPath.getPath(),
                         StringUtils.join(incorrectPropertyValues, ","));
  }

  public InvalidSyncDirError(File currPath, File expectedPath, List<IncorrectPropertyValue> incorrectPropertyValues) {
    super(createErrorMessage(currPath, expectedPath, incorrectPropertyValues), INVALID_SYNC_DIR_PATH);
    this.currPath = currPath;
    this.expectedPath = expectedPath;
  }
}
