package org.literacybridge.stats.model.validation;

/**
 */
public class InvalidDeploymentId extends ValidationError {

  public final String deploymentId;

  public InvalidDeploymentId(String deploymentId) {
    super("Invalid deploymentId name: " + deploymentId, INVALID_DEPLOYMENT_ID);
    this.deploymentId = deploymentId;
  }
}
