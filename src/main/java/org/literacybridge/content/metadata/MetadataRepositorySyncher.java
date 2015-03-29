package org.literacybridge.content.metadata;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;

/**
 * Provides the APIs to actually sync to a Metadata Repository.
 */
public interface MetadataRepositorySyncher {

  /**
   * Returns the current version of the Metadata Repository.  This API should mainly be used for informational purposes,
   * since any sync wil provide a version more closely tied to the sync itself.
   *
   * @return object representing the current version.
   */
  @Nonnull
  Serializable currentVersion();

  /**
   * Finds all the changes since a previously sync-ed version.
   *
   * @param lastSyncVersion last version that was returned by a cal to changesSince or currentVersion.  If this value is
   *                        {@code null}, then the sync will be from the "dawn of time", which means every metadata record will be
   *                        listed as a change, and the list of deleted versions will be empty.
   * @return MetadataSync object with the changes as well as the version in it.
   * @throws VersionNotFoundException The lastSyncVersion field was either an invalid version, or one that the system
   *                                  has since forgotten about.  The best thing to do is to recover by calling changesSince with {@code null} lastSyncVersion
   *                                  and resetting to that.
   * @throws IOException
   */
  @Nonnull
  MetadataSync changesSince(@Nullable Serializable lastSyncVersion) throws VersionNotFoundException, IOException;

}
