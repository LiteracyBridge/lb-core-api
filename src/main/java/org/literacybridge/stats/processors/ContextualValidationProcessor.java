package org.literacybridge.stats.processors;

import org.literacybridge.stats.model.DeploymentPerDevice;
import org.literacybridge.stats.model.validation.InvalidDeploymentId;
import org.literacybridge.stats.model.validation.InvalidTalkingBookId;
import org.literacybridge.stats.model.validation.InvalidVillageName;

import java.util.Set;

/**
 */
public class ContextualValidationProcessor extends ValidatingProcessor {

  public final Set<String> villageNames;
  public final Set<String> talkingBookNames;
  public final Set<String> deploymentIds;

  public ContextualValidationProcessor(Set<String> villageNames, Set<String> talkingBookNames, Set<String> deploymentIds) {
    this.villageNames = villageNames;
    this.talkingBookNames = talkingBookNames;
    this.deploymentIds = deploymentIds;
  }

  @Override
  public boolean startDeviceDeployment(DeploymentPerDevice deploymentPerDevice)
    throws Exception {
    if (!deploymentIds.contains(deploymentPerDevice.deployment)) {
      validationErrors.add(new InvalidDeploymentId(deploymentPerDevice.deployment));
    }

    return super.startDeviceDeployment(deploymentPerDevice);
  }

  @Override
  public boolean startVillage(String village) throws Exception {
    if (!villageNames.contains(village)) {
      validationErrors.add(new InvalidVillageName(village));
    }

    return super.startVillage(village);
  }

  @Override
  public boolean startTalkingBook(String talkingBook) throws Exception {
    if (!talkingBookNames.contains(talkingBook)) {
      validationErrors.add(new InvalidTalkingBookId(talkingBook));
    }

    return super.startTalkingBook(talkingBook);
  }
}
