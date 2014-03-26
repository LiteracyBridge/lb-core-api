package org.literacybridge.stats.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a deployment ID.  The pattern of these is
 *     YYYY-UU
 *
 * Where YYYY is the year and UU is the update this year.  UU should be 1 based.
 *
 * @author willpugh
 */
public class DeploymentId {

  public static final Pattern DEPLOYMENT_ID_PATTERN = Pattern.compile("(\\d+)-(\\d+)");

  public final short  year;
  public final short  update;
  public final String id;

  static public DeploymentId parseContentUpdate(String contentUpdate) {
    final Matcher matcher = DEPLOYMENT_ID_PATTERN.matcher(contentUpdate);
    if (!matcher.matches()) {
      return new DeploymentId((short) 0, (short) 0, contentUpdate);
    }

    return new DeploymentId(Short.parseShort(matcher.group(1)), Short.parseShort(matcher.group(2)), contentUpdate);
  }

  public DeploymentId(short year, short update, String id) {
    this.year = year;
    this.update = update;
    this.id = id;
  }

  public DeploymentId guessPrevious() {
    if (update > 1) {
      return new DeploymentId(year, (short)(update-1), String.format("%04d-%02d", year, (update-(short)1)));
    } else {
      return new DeploymentId((short)(year-1), (short)8, String.format("%04d-08", (year-1)));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DeploymentId)) return false;

    DeploymentId updateId = (DeploymentId) o;

    if (update != updateId.update) return false;
    if (year != updateId.year) return false;
    if (id != null ? !id.equals(updateId.id) : updateId.id != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) year;
    result = 31 * result + (int) update;
    result = 31 * result + (id != null ? id.hashCode() : 0);
    return result;
  }
}
