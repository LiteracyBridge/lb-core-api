package org.literacybridge.content.metadata;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Map;

/**
 * The core metadata class.  This treats metadata as a simple multi-value map.  Any enforcement of required
 * properties happens outside this class.
 */
@Immutable
public class Metadata {

  public static final String CONTENT_LENGTH = "ContentLength";


  @Nonnull
  public final String contentId;

  @Nonnull
  public final Map<String, List<String>> properties;

  public Metadata(@Nonnull String contentId, @Nonnull Map<String, List<String>> properties) {
    this.contentId = contentId;
    this.properties = properties;
  }
}
