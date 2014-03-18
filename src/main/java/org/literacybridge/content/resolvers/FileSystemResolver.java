package org.literacybridge.content.resolvers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 */
public class FileSystemResolver implements ContentResolver {
  public final File fileRoot;

  public FileSystemResolver(File fileRoot) {
    this.fileRoot = fileRoot;
  }


  @Override
  public ContentInfo loadContent(String contentId) throws FileNotFoundException {
    File  contentFile = new File(fileRoot, contentId);
    if (!contentFile.canRead()) {
      throw new FileNotFoundException(contentId + " not found by the FileSystemResolver rooted at " + fileRoot.getPath());
    }

    return new ContentInfo(contentFile.lastModified(), contentFile.length(), new FileInputStream(contentFile));
  }
}
