package org.literacybridge.stats.model.validation;

/**
 */
public class InvalidVillageName extends ValidationError {
  public final String villageName;

  public InvalidVillageName(String villageName) {
    super("Invalid village name: " + villageName, INVALID_VILLAGE_NAME);
    this.villageName = villageName;
  }
}
