package org.literacybridge.stats.formats.exceptions;

import java.io.IOException;

/**
 * This exception is thrown if a file is corrupt.
 *
 * @author willpugh
 */
public class CorruptFileException extends IOException {

  public CorruptFileException(String message, Throwable cause) {
    super(message, cause);
  }

  public CorruptFileException(String message) {
    super(message);
  }
}
