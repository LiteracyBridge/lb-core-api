package org.literacybridge.content.model;

import java.util.Map;

/**
 * Defines a deployment.  This structure will include all the structures needed to build a deployment object
 * that can be partied on by the TB Loader to put on talking books.
 *
 * @author willpugh
 */
public class DeploymentDefinition {

  String firmwareVersion;
  Map<String, ImagePreference> villageMap;
  Map<String, ImageDefinition> imageDefinitions;


  public String getFirmwareVersion() {
    return firmwareVersion;
  }

  public void setFirmwareVersion(String firmwareVersion) {
    this.firmwareVersion = firmwareVersion;
  }

  public Map<String, ImagePreference> getVillageMap() {
    return villageMap;
  }

  public void setVillageMap(Map<String, ImagePreference> villageMap) {
    this.villageMap = villageMap;
  }

  public Map<String, ImageDefinition> getImageDefinitions() {
    return imageDefinitions;
  }

  public void setImageDefinitions(Map<String, ImageDefinition> imageDefinitions) {
    this.imageDefinitions = imageDefinitions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DeploymentDefinition)) return false;

    DeploymentDefinition that = (DeploymentDefinition) o;

    if (firmwareVersion != null ? !firmwareVersion.equals(that.firmwareVersion) : that.firmwareVersion != null)
      return false;
    if (imageDefinitions != null ? !imageDefinitions.equals(that.imageDefinitions) : that.imageDefinitions != null)
      return false;
    if (villageMap != null ? !villageMap.equals(that.villageMap) : that.villageMap != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = firmwareVersion != null ? firmwareVersion.hashCode() : 0;
    result = 31 * result + (villageMap != null ? villageMap.hashCode() : 0);
    result = 31 * result + (imageDefinitions != null ? imageDefinitions.hashCode() : 0);
    return result;
  }
}
