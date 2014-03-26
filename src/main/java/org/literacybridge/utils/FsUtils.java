package org.literacybridge.utils;

import java.io.File;

/**
 */
public class FsUtils {
  public static final String FsAgnostify(String fsPath) {
    return fsPath.replace('/', File.separatorChar);
  }
}
