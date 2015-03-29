package org.literacybridge.stats.model.validation;

/**
 */
public class IncorrectFilePropertyValue extends IncorrectPropertyValue {

  final public int lineNumber;

  public IncorrectFilePropertyValue(String propertyName, String expectedValue, String actualValue, int lineNumber) {
    super(propertyName, expectedValue, actualValue);
    this.lineNumber = lineNumber;
  }

  @Override
  public String toString() {
    return "IncorrectFilePropertyValue{" +
      "lineNumber=" + lineNumber +
      ", propertyName='" + propertyName + '\'' +
      ", expectedValue='" + expectedValue + '\'' +
      ", actualValue='" + actualValue + '\'' +
      '}';
  }
}
