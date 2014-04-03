package org.literacybridge.stats.model.validation;

import java.io.File;

/**
 */
public class EmptySyncDirectory extends ValidationError {
  public final File syncDir;

  public EmptySyncDirectory(File syncDir) {
    super("Sync directory is completely empty : " + syncDir.getPath(), EMPTY_SYNC_DIRECTORY);
    this.syncDir = syncDir;
  }
}
