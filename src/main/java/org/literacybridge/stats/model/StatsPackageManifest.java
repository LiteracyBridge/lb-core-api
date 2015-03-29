package org.literacybridge.stats.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collections;
import java.util.Map;

/**
 * This manifest provides a description of what a stats file contains in terms
 * of the format version and the devices/time ranges contained.
 */
public class StatsPackageManifest {

  public final int formatVersion;
  public final Map<String, SyncRange> devices;

  @JsonCreator
  public StatsPackageManifest(@JsonProperty(value = "formatVersion") int formatVersion,
                              @JsonProperty(value = "devices") Map<String, SyncRange> devices) {
    this.formatVersion = formatVersion;
    this.devices = devices != null ? Collections.unmodifiableMap(devices) : Collections.EMPTY_MAP;
  }
}
