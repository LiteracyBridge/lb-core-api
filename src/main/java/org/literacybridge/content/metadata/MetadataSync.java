package org.literacybridge.content.metadata;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a sync of metadata from a repository.  Contains both a unique version for
 * when the sync took place and the list of metadata objects that had updates occur in this sync.
 */
@Immutable
public class MetadataSync {

  /**
   * The version this sync occured from.  This should be used in later calls to MetadataRepositorySyncher.changesSince
   * in order to find out if any changes occured since this sync.
   */
  @Nonnull
  public final Serializable syncVersion;


  /**
   * All content metadata that got added or modified since the last sync.  Each Metadata object will represent the
   * full state of the object (not deltas), so that providing idempotent operations is easier.
   */
  @Nonnull
  public final List<Metadata> changedMetadata;

  /**
   * All content metadata that was deleted since the last sync.
   */
  @Nonnull
  public final List<String> deletedMetadata;


  public MetadataSync(@Nonnull Serializable syncVersion,
                      @Nonnull List<Metadata> changedMetadata,
                      @Nonnull List<String> deletedMetadata) {
    this.syncVersion = syncVersion;
    this.changedMetadata = changedMetadata;
    this.deletedMetadata = deletedMetadata;
  }
}
