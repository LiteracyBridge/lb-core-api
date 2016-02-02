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

    /**
     * Like File, except that if the given child does not exist, will look for a file with a case-insensitive
     * variant of the child name.
     * @param parent The File, a directory, under which to look for the child.
     * @param child The name of the file, possibly with different casing.
     * @return A File with the given path. The returned File, if it exists, may have a casing different
     * than what was given. If the file does not exist, the name will be as given (because no substitution
     * was performed).
     */
    private static final File FileIgnoreCaseHelper(File parent, final String child) {
        File retval = new File(parent, child);
        // Check for name that matches, ignoring case.
        if (!retval.exists()) {
            File [] candidates = parent.listFiles(new FilenameFilter() {
                boolean found = false;
                @Override
                public boolean accept(File dir, String name) {
                    // Accept the first file that matches case insenstively.
                    if (!found && name.equalsIgnoreCase(child)) {
                        found = true;
                        return true;
                    }
                    return false;
                }
            });
            // If candidates contains a file, we know it exists, so use it.
            if (candidates.length == 1) {
                retval = candidates[0];
            }
        }
        return retval;
    }

    /**
     * Creates a File from an existing File and one or more path components, by appending the path components one
     * by one. At every step, if the given path component name does not exist, but there is a file or directory that
     * matches with a different casing, that matching file is substituted.
     * @param parent The File, a directory, under which to look for the child.
     * @param pathToChild A sequence of path components, like ["a", "b", "c"], to look for File/a/b/c.
     * @return A File with the given path.
     */
    public static final File FileIgnoreCase(File parent, String... pathToChild) {
        File file = parent;
        // Add the child parts of the path, one at a time.
        for (String child : pathToChild) {
             file = FileIgnoreCaseHelper(file, child);
        }
        return file;
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
