package org.literacybridge.content.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A list of preferences for an image.  This contains the allowed image
 * and the default profile for the image.  This is used by the Village map
 * to describe the allowed images for a village.
 *
 * @author willpugh
 */
public class ImagePreference {

  final public String imageName;
  final public String defaultProfile;

  @JsonCreator
  public ImagePreference(@JsonProperty(value = "imageName") String imageName,
                         @JsonProperty(value = "defaultProfile") String defaultProfile) {
    this.imageName = imageName;
    this.defaultProfile = defaultProfile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ImagePreference)) return false;

    ImagePreference that = (ImagePreference) o;

    if (defaultProfile != null ? !defaultProfile.equals(that.defaultProfile) : that.defaultProfile != null)
      return false;
    if (imageName != null ? !imageName.equals(that.imageName) : that.imageName != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = imageName != null ? imageName.hashCode() : 0;
    result = 31 * result + (defaultProfile != null ? defaultProfile.hashCode() : 0);
    return result;
  }
}
