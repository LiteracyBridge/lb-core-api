package org.literacybridge.stats.model.validation;

/**
 */
public class ManfestDoesNotContainDevice extends ValidationError {

  public final String device;

  public ManfestDoesNotContainDevice(String device) {
    super("Manifest does not contain entries for " + device, MANIFEST_DOES_NOT_CONTAIN_DEVICE);
    this.device = device;
  }
}
