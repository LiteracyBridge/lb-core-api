package org.literacybridge.stats.model;

import junit.framework.TestCase;
import org.junit.Test;

/**
 */
public class TestDeploymentId {


  @Test
  public void testIds() {
    DeploymentId  deploymentId = DeploymentId.parseContentUpdate("2013-13");
    TestCase.assertEquals(2013, deploymentId.year);
    TestCase.assertEquals(13, deploymentId.update);
    TestCase.assertEquals("2013-13", deploymentId.id);

    deploymentId = DeploymentId.parseContentUpdate("2013-2");
    TestCase.assertEquals(2013, deploymentId.year);
    TestCase.assertEquals(2, deploymentId.update);
    TestCase.assertEquals("2013-2", deploymentId.id);

    deploymentId = deploymentId.guessPrevious();
    TestCase.assertEquals(2013, deploymentId.year);
    TestCase.assertEquals(1, deploymentId.update);
    TestCase.assertEquals("2013-01", deploymentId.id);

    deploymentId = deploymentId.guessPrevious();
    TestCase.assertEquals(2012, deploymentId.year);
    TestCase.assertEquals(8, deploymentId.update);
    TestCase.assertEquals("2012-08", deploymentId.id);

    deploymentId = DeploymentId.parseContentUpdate("Unknown");
    TestCase.assertEquals(0, deploymentId.year);
    TestCase.assertEquals(0, deploymentId.update);
    TestCase.assertEquals("Unknown", deploymentId.id);

  }
}
