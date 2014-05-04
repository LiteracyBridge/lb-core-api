package org.literacybridge.stats.processors;

import org.literacybridge.stats.DirectoryIterator;
import org.literacybridge.stats.model.DirectoryFormat;
import org.literacybridge.stats.model.validation.EmptySyncDirectory;
import org.literacybridge.stats.model.validation.InvalidSyncDirError;
import org.literacybridge.stats.model.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes a list of validation errors and fixes up what errors in can.
 *
 */
public class DirectoryCorruptionFixer {
  protected static final Logger logger = LoggerFactory.getLogger(DirectoryCorruptionFixer.class);

  public final File            root;
  public final DirectoryFormat format;
  public final boolean         strict;

  public DirectoryCorruptionFixer(File root, DirectoryFormat format, boolean strict) {
    this.root = root;
    this.format = format;
    this.strict = strict;
  }

  public List<ValidationError> fixUp() throws Exception {

    ValidatingProcessor validatingProcessor = new ValidatingProcessor();
    DirectoryIterator directoryIterator = new DirectoryIterator(root, format, strict);

    directoryIterator.process(validatingProcessor);

    return fixupValidationError(validatingProcessor.validationErrors);
  }

  public List<ValidationError> fixupValidationError(List<ValidationError> errors) {

    List<ValidationError> unprocessedErrors = new ArrayList<>();
    //Do deletes first
    for (ValidationError  error : errors) {
      if (error.errorId == ValidationError.EMPTY_SYNC_DIRECTORY) {
        EmptySyncDirectory  emptySyncDirectory = (EmptySyncDirectory) error;
        boolean retVal = emptySyncDirectory.syncDir.delete();
        if (!retVal) {
          logger.error("Unable to delete empty directory : " + emptySyncDirectory.syncDir);
          unprocessedErrors.add(emptySyncDirectory);
        }
      }
    }

    for (ValidationError  error : errors) {
      switch (error.errorId) {
        case ValidationError.INVALID_SYNC_DIR_PATH:
          if (!fixInvalidPathError((InvalidSyncDirError) error)) {
            unprocessedErrors.add(error);
          }
          break;

        case ValidationError.EMPTY_SYNC_DIRECTORY:
          break;

        default:
          unprocessedErrors.add(error);
      }
    }
    return unprocessedErrors;
  }

  public boolean fixInvalidPathError(InvalidSyncDirError  error) {

    error.expectedPath.getParentFile().mkdirs();
    boolean retVal = error.currPath.renameTo(error.expectedPath);

    if (!retVal) {
      if (error.expectedPath.exists()) {
        File checkdiskFile = new File(error.expectedPath, "chkdsk-reformat.txt");
        if (!checkdiskFile.exists()) {
          logger.error(error.expectedPath + " already exists, connot move " + error.currPath + " to it.");
        }
      } else {
        logger.error("Unable to move from " + error.currPath + " to " + error.expectedPath);
      }
    } else {
      logger.info("Fixed-up directory by moving from "+ error.currPath + " to " + error.expectedPath);
    }
    return retVal;
  }

}
