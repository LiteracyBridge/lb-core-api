package org.literacybridge.stats.model.validation;

/**
 */
public class IncorrectPropertyValue {
  public final String propertyName;
  public final String expectedValue;
  public final String actualValue;

  public IncorrectPropertyValue(String propertyName, String expectedValue, String actualValue) {
    this.propertyName = propertyName;
    this.expectedValue = expectedValue;
    this.actualValue = actualValue;
  }

  @Override
  public String toString() {
    return "IncorrectPropertyValue{" +
      "propertyName='" + propertyName + '\'' +
      ", expectedValue='" + expectedValue + '\'' +
      ", actualValue='" + actualValue + '\'' +
      '}';
  }
}
