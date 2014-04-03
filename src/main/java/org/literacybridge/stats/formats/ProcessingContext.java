package org.literacybridge.stats.formats;

import org.literacybridge.stats.formats.SyncProcessingContext;
import org.literacybridge.stats.model.DeploymentId;

import java.util.regex.Pattern;

/**
 * A General procssing context that contains all the update/content/device information,
 * but no more specific information about a particular "sync" that occurred (e.g. specific time, etc)
 *
 * @author willpugh
 */
public class ProcessingContext {
  public static final Pattern SYNC_TIME_PATTERN = Pattern.compile("(\\d+)m(\\d+)d(\\d+)h(\\d+)m(\\d+)s");
  public final DeploymentId deploymentId;
  public final String       contentPackage;
  public final String       village;
  public final String       talkingBookId;
  public final String       deviceSyncedFrom;


  public ProcessingContext(String talkingBookId, String village, String contentPackage,
                           String contentUpdate, String deviceSyncedFrom) {
    this.talkingBookId = talkingBookId;
    this.village = village;
    this.contentPackage = contentPackage;
    this.deploymentId = DeploymentId.parseContentUpdate(contentUpdate);
    this.deviceSyncedFrom = deviceSyncedFrom;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SyncProcessingContext)) return false;

    SyncProcessingContext that = (SyncProcessingContext) o;

    if (contentPackage != null ? !contentPackage.equals(that.contentPackage) : that.contentPackage != null)
      return false;
    if (deploymentId != null ? !deploymentId.equals(that.deploymentId) : that.deploymentId != null)
      return false;
    if (deviceSyncedFrom != null ? !deviceSyncedFrom.equals(that.deviceSyncedFrom) : that.deviceSyncedFrom != null)
      return false;
    if (talkingBookId != null ? !talkingBookId.equals(that.talkingBookId) : that.talkingBookId != null)
      return false;
    if (village != null ? !village.equals(that.village) : that.village != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = deploymentId != null ? deploymentId.hashCode() : 0;
    result = 31 * result + (contentPackage != null ? contentPackage.hashCode() : 0);
    result = 31 * result + (village != null ? village.hashCode() : 0);
    result = 31 * result + (talkingBookId != null ? talkingBookId.hashCode() : 0);
    result = 31 * result + (deviceSyncedFrom != null ? deviceSyncedFrom.hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    return new org.apache.commons.lang.builder.ToStringBuilder(this)
        .append("contentUpdateId", deploymentId)
        .append("contentPackage", contentPackage)
        .append("village", village)
        .append("talkingBookId", talkingBookId)
        .append("deviceSyncedFrom", deviceSyncedFrom)
        .toString();
  }
}
