package org.literacybridge.stats.model;

import org.literacybridge.stats.DirectoryIterator;
import org.literacybridge.utils.FsUtils;

import java.io.File;
import java.util.Comparator;

/**
 * Represents a tuple of a Device (such as a laptop or android device, NOT a talking book), and a
 * deployment.
 *
 * The main reason for this abstraction is that between the archive format and the syncing format, almost
 * all of the directory structure is the same, except the device/deployment hierarchy.
 */
public class DeploymentPerDevice {
  public final String deployment;
  public final String device;


  public static final Comparator<DeploymentPerDevice> ORDER_BY_DEVICE = new Comparator<DeploymentPerDevice>() {
    @Override
    public int compare(DeploymentPerDevice o1, DeploymentPerDevice o2) {
      if (o1 == o2) return 0;
      if (o1 == null) return -1;
      if (o2 == null) return 1;
      int deviceCompare = o1.device.compareToIgnoreCase(o2.device);
      if (deviceCompare != 0) {
        return deviceCompare;
      }
      return o1.deployment.compareToIgnoreCase(o2.deployment);
    }
  };

  public static final Comparator<DeploymentPerDevice> ORDER_BY_DEPLOY = new Comparator<DeploymentPerDevice>() {
    @Override
    public int compare(DeploymentPerDevice o1, DeploymentPerDevice o2) {
      if (o1 == o2) return 0;
      if (o1 == null) return -1;
      if (o2 == null) return 1;
      int deviceCompare = o1.deployment.compareToIgnoreCase(o2.deployment);
      if (deviceCompare != 0) {
        return deviceCompare;
      }
      return o1.device.compareToIgnoreCase(o2.device);
    }
  };

  public DeploymentPerDevice(String deployment, String device) {
    this.deployment = deployment;
    this.device = device;
  }

  public File getRoot(File basePath, DirectoryFormat format) {
    return format == DirectoryFormat.Archive ? getArchiveRoot(basePath) : getSyncRoot(basePath);
  }

  public File getSyncRoot(File basePath) {
    return new File(basePath, FsUtils.FsAgnostify(
        device + "/" + DirectoryIterator.UPDATE_ROOT_V1 + "/" + deployment));
  }

  public File getArchiveRoot(File basePath) {
    return new File(basePath, FsUtils.FsAgnostify(deployment + "/" + device));
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DeploymentPerDevice)) return false;

    DeploymentPerDevice that = (DeploymentPerDevice) o;

    if (deployment != null ? !deployment.equals(that.deployment) : that.deployment != null) return false;
    if (device != null ? !device.equals(that.device) : that.device != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = deployment != null ? deployment.hashCode() : 0;
    result = 31 * result + (device != null ? device.hashCode() : 0);
    return result;
  }
}
