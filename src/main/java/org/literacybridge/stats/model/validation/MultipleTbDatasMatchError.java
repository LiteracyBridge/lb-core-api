package org.literacybridge.stats.model.validation;

/**
 */
public class MultipleTbDatasMatchError extends ValidationError {

  public MultipleTbDatasMatchError(String tbDataSyncDir, String device) {
    super("Multiple Sync directories match to the same TBData value : " + tbDataSyncDir + " on device :" + device, MULTIPLE_MATCHING_TBDATA_ENTRY);
  }

}
