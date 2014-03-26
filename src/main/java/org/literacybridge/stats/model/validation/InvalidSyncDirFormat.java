package org.literacybridge.stats.model.validation;

/**
 */
public class InvalidSyncDirFormat extends ValidationError {

  public InvalidSyncDirFormat() {
    super("The format for sync directories is an older format than this directory should use based on its manifest.", INVALID_SYNC_DIR_FORMAT);
  }
}
