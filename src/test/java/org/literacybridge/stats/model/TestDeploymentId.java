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
    TestCase.assertEquals("", deploymentId.flavor);

    deploymentId = DeploymentId.parseContentUpdate("2013-2");
    TestCase.assertEquals(2013, deploymentId.year);
    TestCase.assertEquals(2, deploymentId.update);
    TestCase.assertEquals("2013-2", deploymentId.id);
    TestCase.assertEquals("", deploymentId.flavor);

    deploymentId = deploymentId.guessPrevious();
    TestCase.assertEquals(2013, deploymentId.year);
    TestCase.assertEquals(1, deploymentId.update);
    TestCase.assertEquals("2013-01", deploymentId.id);
    TestCase.assertEquals("", deploymentId.flavor);

    deploymentId = deploymentId.guessPrevious();
    TestCase.assertEquals(2012, deploymentId.year);
    TestCase.assertEquals(8, deploymentId.update);
    TestCase.assertEquals("2012-08", deploymentId.id);
    TestCase.assertEquals("", deploymentId.flavor);

    deploymentId = DeploymentId.parseContentUpdate("Unknown");
    TestCase.assertEquals(0, deploymentId.year);
    TestCase.assertEquals(0, deploymentId.update);
    TestCase.assertEquals("Unknown", deploymentId.id);
    TestCase.assertEquals("", deploymentId.flavor);
  }

  @Test
  public void testIdsWithFlavor() {
    DeploymentId  deploymentId = DeploymentId.parseContentUpdate("2013-13b");
    TestCase.assertEquals(2013, deploymentId.year);
    TestCase.assertEquals(13, deploymentId.update);
    TestCase.assertEquals("2013-13b", deploymentId.id);
    TestCase.assertEquals("b", deploymentId.flavor);

    deploymentId = DeploymentId.parseContentUpdate("2008-1WillyWonka");
    TestCase.assertEquals(2008, deploymentId.year);
    TestCase.assertEquals(1, deploymentId.update);
    TestCase.assertEquals("2008-1WillyWonka", deploymentId.id);
    TestCase.assertEquals("WillyWonka", deploymentId.flavor);
  }

  @Test
  public void testEqualsAndHash() {
    DeploymentId  id_Simple = DeploymentId.parseContentUpdate("2013-13");
    DeploymentId  id_Simple_Copy = DeploymentId.parseContentUpdate("2013-13");
    DeploymentId  id_Simple_Diff_Year = DeploymentId.parseContentUpdate("2012-13");
    DeploymentId  id_Simple_Diff_Update = DeploymentId.parseContentUpdate("2013-12");
    DeploymentId  id_Simple_Diff_Flavor = DeploymentId.parseContentUpdate("2013-13b");
    DeploymentId  id_Simple_Diff_Flavor_copy = DeploymentId.parseContentUpdate("2013-13b");

    TestCase.assertEquals(id_Simple, id_Simple_Copy);
    TestCase.assertEquals(id_Simple.hashCode(), id_Simple_Copy.hashCode());

    TestCase.assertFalse(id_Simple.equals(id_Simple_Diff_Year));
    TestCase.assertFalse(id_Simple.hashCode() == id_Simple_Diff_Year.hashCode());

    TestCase.assertFalse(id_Simple.equals(id_Simple_Diff_Update));
    TestCase.assertFalse(id_Simple.hashCode() == id_Simple_Diff_Update.hashCode());

    TestCase.assertFalse(id_Simple.equals(id_Simple_Diff_Flavor));
    TestCase.assertFalse(id_Simple.hashCode() == id_Simple_Diff_Flavor.hashCode());

    TestCase.assertEquals(id_Simple_Diff_Flavor, id_Simple_Diff_Flavor_copy);
    TestCase.assertEquals(id_Simple_Diff_Flavor.hashCode(), id_Simple_Diff_Flavor_copy.hashCode());

  }

  @Test
  public void testGuessPrevious() {
    DeploymentId  id_Simple = DeploymentId.parseContentUpdate("2013-07");
    DeploymentId  id_Simple_prev = DeploymentId.parseContentUpdate("2013-06");
    TestCase.assertEquals(id_Simple_prev, id_Simple.guessPrevious());

    DeploymentId  id_Simple_flavor = DeploymentId.parseContentUpdate("2013-07b");
    DeploymentId  id_Simple_flavor_prev = DeploymentId.parseContentUpdate("2013-06b");
    TestCase.assertEquals(id_Simple_flavor_prev, id_Simple_flavor.guessPrevious());

    DeploymentId  id_FirstUpdate = DeploymentId.parseContentUpdate("2013-01");
    DeploymentId  id_FirstUpdate_prev = DeploymentId.parseContentUpdate("2012-08");
    TestCase.assertEquals(id_FirstUpdate_prev, id_FirstUpdate.guessPrevious());

    DeploymentId  id_FirstUpdate_flavor = DeploymentId.parseContentUpdate("2013-01b");
    DeploymentId  id_FirstUpdate_flavor_prev = DeploymentId.parseContentUpdate("2012-08b");
    TestCase.assertEquals(id_FirstUpdate_flavor_prev, id_FirstUpdate_flavor.guessPrevious());

  }
}
