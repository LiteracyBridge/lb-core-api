package org.literacybridge.stats.processors;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import junit.framework.TestCase;
import org.junit.Test;
import org.literacybridge.stats.DirectoryIterator;
import org.literacybridge.stats.TestDirectoryIterator;
import org.literacybridge.stats.model.DirectoryFormat;
import org.literacybridge.stats.model.validation.TbDataHasInvalidProperties;
import org.literacybridge.stats.model.validation.ValidationError;
import org.literacybridge.utils.FsUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;

/**
 */
public class TestValidatingProcessor {

  public static final File ERROR_TEST1_ARCHIVE = new File(FsUtils.FsAgnostify("src/test/resources/testPackages/errorTest1-archive"));
  public static final File ERROR_TEST2_SYNC = new File(FsUtils.FsAgnostify("src/test/resources/testPackages/errorTest2-sync"));


  @Test
  public void testValidV2NoManifest() throws Exception {
    ValidatingProcessor validatingProcessor = new ValidatingProcessor();
    DirectoryIterator directoryIterator = new DirectoryIterator(TestDirectoryIterator.TEST1_ARCHIVE,
                                                                DirectoryFormat.Archive, false);

    directoryIterator.process(validatingProcessor);
    TestCase.assertEquals(10, validatingProcessor.validationErrors.size());

    Collection<ValidationError> nonFormatErrors = Collections2.filter(validatingProcessor.validationErrors,
                                                                      new Predicate<ValidationError>() {
                                                                        @Override
                                                                        public boolean apply(
                                                                            @Nullable ValidationError input) {
                                                                          return input.errorId != ValidationError.INVALID_SYNC_DIR_FORMAT;
                                                                        }
                                                                      });

    TestCase.assertEquals(0, nonFormatErrors.size());
  }

  @Test
  public void testValidV1NoManifest() throws Exception {
    ValidatingProcessor validatingProcessor = new ValidatingProcessor();
    DirectoryIterator   directoryIterator = new DirectoryIterator(TestDirectoryIterator.TEST1_SYNC, DirectoryFormat.Sync, false);

    directoryIterator.process(validatingProcessor);
    TestCase.assertEquals(0, validatingProcessor.validationErrors.size());
  }

  @Test
  public void testV2WithManyErrors() throws Exception {
    ValidatingProcessor validatingProcessor = new ValidatingProcessor();
    DirectoryIterator   directoryIterator = new DirectoryIterator(ERROR_TEST1_ARCHIVE, null, true);

    directoryIterator.process(validatingProcessor);
    TestCase.assertEquals(6, validatingProcessor.validationErrors.size());

    ValidationError currError = validatingProcessor.validationErrors.get(0);
    TestCase.assertEquals(ValidationError.INVALID_DATA_IN_TBDATA, currError.errorId);

    TbDataHasInvalidProperties  invalidProperties = (TbDataHasInvalidProperties) currError;
    TestCase.assertEquals(2, invalidProperties.incorrectFilePropertyValues.size());

    int invalidDir = 0;
    int noDeviceInManifest = 0;
    int noTbEntryForDir = 0;

    for (int i=1; i<validatingProcessor.validationErrors.size()-1; i++) {
      currError = validatingProcessor.validationErrors.get(i);
      if (currError.errorId == ValidationError.INVALID_SYNC_DIR_PATH) {
        invalidDir++;
      } else if (currError.errorId == ValidationError.MANIFEST_DOES_NOT_CONTAIN_DEVICE) {
        noDeviceInManifest++;
      } else if (currError.errorId == ValidationError.NO_MATCHING_TBDATA_ENTRY) {
        noTbEntryForDir++;
      } else {
        TestCase.fail("Unexpected error message");
      }
    }

    TestCase.assertEquals(2, invalidDir);
    TestCase.assertEquals(1, noDeviceInManifest);
    TestCase.assertEquals(1, noTbEntryForDir);


    currError = validatingProcessor.validationErrors.get(validatingProcessor.validationErrors.size()-1);
    TestCase.assertEquals(ValidationError.UNMATCHED_TBDATA_ENTRIES, currError.errorId);
  }

  @Test
  public void testV1WithManyErrors() throws Exception {
    ValidatingProcessor validatingProcessor = new ValidatingProcessor();
    DirectoryIterator   directoryIterator = new DirectoryIterator(ERROR_TEST2_SYNC, null, true);

    directoryIterator.process(validatingProcessor);
    TestCase.assertEquals(3, validatingProcessor.validationErrors.size());

    ValidationError currError = validatingProcessor.validationErrors.get(0);
    TestCase.assertEquals(ValidationError.INVALID_DATA_IN_TBDATA, currError.errorId);
    TestCase.assertEquals(2, ((TbDataHasInvalidProperties)currError).incorrectFilePropertyValues.size());

    currError = validatingProcessor.validationErrors.get(1);
    TestCase.assertEquals(ValidationError.DEVICE_DATE_OUT_OF_RANGE, currError.errorId);

    currError = validatingProcessor.validationErrors.get(2);
    TestCase.assertEquals(ValidationError.MULTIPLE_MATCHING_TBDATA_ENTRY, currError.errorId);

  }

}
