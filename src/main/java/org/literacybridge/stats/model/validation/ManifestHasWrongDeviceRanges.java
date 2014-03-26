package org.literacybridge.stats.model.validation;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;

import java.io.File;
import java.util.Date;
import java.util.List;

/**

 */
public class ManifestHasWrongDeviceRanges extends ValidationError{
  public final String        device;
  public final Date          startRange;
  public final Date          endRange;
  public final LocalDateTime syncDate;
  public final File          syncDir;

  public static String createErrorMessage(String device, Date startRange, Date endRange, LocalDateTime syncDate, File syncDir) {
    return String.format("%s has an invalid date range in the manifest.  Range is [%s, %s], however, sync directory is %s.",
                         device,
                         startRange.toString(),
                         endRange.toString(),
                         syncDate.toString(),
                         syncDir.getPath());
  }

  public ManifestHasWrongDeviceRanges(String device, Date startRange, Date endRange, LocalDateTime syncDate, File syncDir) {
    super(createErrorMessage(device, startRange, endRange, syncDate, syncDir), DEVICE_DATE_OUT_OF_RANGE);
    this.device = device;
    this.startRange = startRange;
    this.endRange = endRange;
    this.syncDate = syncDate;
    this.syncDir = syncDir;
  }
}
