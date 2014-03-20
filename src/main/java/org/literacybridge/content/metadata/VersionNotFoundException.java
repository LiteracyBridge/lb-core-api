package org.literacybridge.content.metadata;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Exception that is thrown in the case that a version is not found for a sync.  In this
 * case, there is not enough information to proceed with a partial sync, and the caller
 * should probably recover by doing a full sync, and replacing all their metadata.
 */
public class VersionNotFoundException extends Exception {
  @Nonnull
  public final Serializable syncVersion;

  public VersionNotFoundException(@Nonnull Serializable syncVersion) {
    this.syncVersion = syncVersion;
  }
}
