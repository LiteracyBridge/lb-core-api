package org.literacybridge.content.resolvers;

import java.io.IOException;
import java.io.InputStream;

/**
 */
public interface ContentResolver {

  ContentInfo loadContent(String contentId) throws IOException;

  static public class ContentInfo {
    public final long lastModified;
    public final long size;
    public final InputStream inputStream;

    public ContentInfo(long lastModified, long size, InputStream inputStream) {
      this.lastModified = lastModified;
      this.size = size;
      this.inputStream = inputStream;
    }


  }

}
