package org.literacybridge.content.model;

import java.util.Map;

/**
 * Uniquely defines an image in a Deployment Definition.  This does not contain the
 * unique reference to firmware as that is held at the deployment definition level.
 */
public class ImageDefinition {
  Map<String, Profile> profiles;

  public Map<String, Profile> getProfiles() {
    return profiles;
  }

  public void setProfiles(Map<String, Profile> profiles) {
    this.profiles = profiles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ImageDefinition)) return false;

    ImageDefinition that = (ImageDefinition) o;

    if (profiles != null ? !profiles.equals(that.profiles) : that.profiles != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return profiles != null ? profiles.hashCode() : 0;
  }
}
