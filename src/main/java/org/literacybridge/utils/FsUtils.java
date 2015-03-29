package org.literacybridge.utils;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.CountingInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 */
public class FsUtils {
  public static final String FsAgnostify(String fsPath) {
    return fsPath.replace('/', File.separatorChar);
  }

  public static HashingInputStream createSHAStream(InputStream is) {
    return new HashingInputStream(Hashing.sha256(), is);
  }

  public static CountingInputStream createCountingStream(InputStream is) {
    return new CountingInputStream(is);
  }


  public static void unzip(File zipFile, File rootDir) throws IOException {
    ZipFile zip = new ZipFile(zipFile);
    Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      java.io.File f = new java.io.File(rootDir, entry.getName());
      if (entry.isDirectory()) {
        continue;
      }

      File parentFile = f.getParentFile();
      if (!parentFile.exists()) {
        parentFile.mkdirs();
      }

      BufferedInputStream bis = new BufferedInputStream(zip.getInputStream(entry)); // get the input stream
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
      IOUtils.copy(bis, bos);

      f.setLastModified(entry.getTime());
      bos.close();
      bis.close();
    }
  }
}
