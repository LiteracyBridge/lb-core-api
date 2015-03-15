package org.literacybridge.stats.model.events;

import org.literacybridge.stats.formats.logFile.LogLineContext;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Base class for events that are pulled out of the Talking Book logs
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class Event implements Serializable {

  @EmbeddedId private EventUniqueId idFields;

  @Column private double maxVolts;
  @Column private double steadyStateVolts;
  @Column private double minVolts;
  @Column private String packageId;
  @Column private String village;

  public static void populateEvent(final LogLineContext context, final Event event) {
    event.setIdFields(EventUniqueId.CreateFromLogLineContext(context));
    event.setVillage(context.context.village);
    event.setPackageId(context.context.contentPackage);

    if (context.logLineInfo != null) {
      event.setMaxVolts(context.logLineInfo.maxVolts);
      event.setMinVolts(context.logLineInfo.minVolts);
      event.setSteadyStateVolts(context.logLineInfo.steadyStateVolts);
    }

  }


  public EventUniqueId getIdFields() {
    return idFields;
  }

  public void setIdFields(EventUniqueId idFields) {
    this.idFields = idFields;
  }

  public double getMaxVolts() {
    return maxVolts;
  }

  public void setMaxVolts(double maxVolts) {
    this.maxVolts = maxVolts;
  }

  public double getSteadyStateVolts() {
    return steadyStateVolts;
  }

  public void setSteadyStateVolts(double steadyStateVolts) {
    this.steadyStateVolts = steadyStateVolts;
  }

  public double getMinVolts() {
    return minVolts;
  }

  public void setMinVolts(double minVolts) {
    this.minVolts = minVolts;
  }

  public String getPackageId() {
    return packageId;
  }

  public void setPackageId(String packageId) {
    this.packageId = packageId;
  }

  public String getVillage() {
    return village;
  }

  public void setVillage(String village) {
    this.village = village.toUpperCase();
  }
}
