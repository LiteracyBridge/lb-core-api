package org.literacybridge.stats.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

  public static final Pattern DEPLOYMENT_ID_PATTERN = Pattern.compile("(\\d+)-(\\d+)(\\D*)");

  public final short  year;
  public final short  update;

  @Nonnull
  public final String flavor;

  @Nonnull
  public final String id;

  static public DeploymentId parseContentUpdate(String contentUpdate) {
    final Matcher matcher = DEPLOYMENT_ID_PATTERN.matcher(contentUpdate);
    if (!matcher.matches()) {
      return new DeploymentId((short) 0, (short) 0, null, contentUpdate);
    }

    return new DeploymentId(Short.parseShort(matcher.group(1)), Short.parseShort(matcher.group(2)), matcher.group(3), contentUpdate);
  }

  public DeploymentId(short year, short update, @Nonnull String id) {
    this(year, update, null, id);
  }

  public DeploymentId(short year, short update, @Nullable String flavor, @Nonnull String id) {
    this.year = year;
    this.update = update;
    this.flavor = (flavor == null) ? "" : flavor;
    this.id = id;
  }

  public DeploymentId guessPrevious() {
    if (update > 1) {
      return new DeploymentId(year, (short)(update-1), flavor, String.format("%04d-%02d%s", year, (update-(short)1), flavor ));
    } else {
      return new DeploymentId((short)(year-1), (short)8, flavor, String.format("%04d-08%s", (year-1), flavor));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DeploymentId)) return false;

    DeploymentId that = (DeploymentId) o;

    if (update != that.update) return false;
    if (year != that.year) return false;
    if (!flavor.equals(that.flavor)) return false;
    if (!id.equals(that.id)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) year;
    result = 31 * result + (int) update;
    result = 31 * result + flavor.hashCode();
    result = 31 * result + id.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return id;
  }
}
