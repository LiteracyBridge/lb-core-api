package org.literacybridge.stats.model.validation;

/**
 */
public class ValidationError {

  public static final int INVALID_SYNC_DIR_PATH = 1;
  public static final int INVALID_SYNC_DIR_FORMAT = 2;
  public static final int NO_MATCHING_TBDATA_ENTRY = 3;
  public static final int MULTIPLE_MATCHING_TBDATA_ENTRY = 4;
  public static final int MANIFEST_DOES_NOT_CONTAIN_DEVICE = 5;
  public static final int DEVICE_DATE_OUT_OF_RANGE = 6;
  public static final int INVALID_DATA_IN_TBDATA = 7;
  public static final int UNMATCHED_TBDATA_ENTRIES = 8;

  public static final int INVALID_VILLAGE_NAME = 9;
  public static final int INVALID_TALKING_BOOK_NAME = 10;
  public static final int INVALID_DEPLOYMENT_ID = 11;
  public static final int EMPTY_SYNC_DIRECTORY = 12;


  public final String errorMessage;
  public final int errorId;

  public ValidationError(String errorMessage, int errorId) {
    this.errorMessage = errorMessage;
    this.errorId = errorId;
  }

  @Override
  public String toString() {
    return "ValidationError{" +
      "errorMessage='" + errorMessage + '\'' +
      ", errorId=" + errorId +
      '}';
  }
}
