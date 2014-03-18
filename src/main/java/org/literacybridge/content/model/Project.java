package org.literacybridge.content.model;

import java.util.Map;

/**
 * Defines a project in
 *
 *
 */
public class Project {
  String    firmwareLibrary;
  String    contentLibrary;

  Map<String, DeploymentDefinition>   deployments;

  public String getFirmwareLibrary() {
    return firmwareLibrary;
  }

  public void setFirmwareLibrary(String firmwareLibrary) {
    this.firmwareLibrary = firmwareLibrary;
  }

  public String getContentLibrary() {
    return contentLibrary;
  }

  public void setContentLibrary(String contentLibrary) {
    this.contentLibrary = contentLibrary;
  }

  public Map<String, DeploymentDefinition> getDeployments() {
    return deployments;
  }

  public void setDeployments(Map<String, DeploymentDefinition> deployments) {
    this.deployments = deployments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Project)) return false;

    Project project = (Project) o;

    if (contentLibrary != null ? !contentLibrary.equals(project.contentLibrary) : project.contentLibrary != null)
      return false;
    if (deployments != null ? !deployments.equals(project.deployments) : project.deployments != null) return false;
    if (firmwareLibrary != null ? !firmwareLibrary.equals(project.firmwareLibrary) : project.firmwareLibrary != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = firmwareLibrary != null ? firmwareLibrary.hashCode() : 0;
    result = 31 * result + (contentLibrary != null ? contentLibrary.hashCode() : 0);
    result = 31 * result + (deployments != null ? deployments.hashCode() : 0);
    return result;
  }
}
