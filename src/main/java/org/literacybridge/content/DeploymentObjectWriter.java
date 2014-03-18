package org.literacybridge.content;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.literacybridge.content.model.*;
import org.literacybridge.content.resolvers.ContentResolver;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Creates a Deployment Object based on a Deployment Definition.
 *
 * The structure will look like:
 *
 *   /villageMap.json
 *   /firmware.bin
 *   /imageDefinitions/[ImageName].json
 *   /content/[ContentID]
 *
 * https://docs.google.com/document/d/1xy9cHB43qcPv3zdo1ZmbZkmLAlbFgmsip8yaOIPtbHc/edit#
 */
public class DeploymentObjectWriter {

  public static final String VILLAGE_MAP_NAME = "villageMap.json";
  public static final String FIRMWARE_NAME = "firmware.bin";
  public static final String IMAGE_DEF_DIR = "imageDefinitions";
  public static final String CONTENT_DIR = "content";


  final ContentResolver      contentResolver;
  final ContentResolver      firemwareResolver;
  final ObjectMapper         mapper = new ObjectMapper();


  public DeploymentObjectWriter(ContentResolver contentResolver,
                                ContentResolver firemwareResolver) {
    this.contentResolver = contentResolver;
    this.firemwareResolver = firemwareResolver;
  }

  public void write(DeploymentDefinition deploymentDefinition,
                    File file,
                    long modificationTime) throws IOException {
    FileOutputStream fos = new FileOutputStream(file);
    try {
      write(deploymentDefinition, fos, modificationTime);
    } finally {
      IOUtils.closeQuietly(fos);
    }
  }

  public void write(DeploymentDefinition deploymentDefinition,
                    OutputStream os,
                    long modificationTime) throws IOException {
    ZipOutputStream zos = new ZipOutputStream(os);
    try {

      writeFirmware(deploymentDefinition.getFirmwareVersion(), zos);
      writeVillageMap(deploymentDefinition.getVillageMap(), modificationTime, zos);

      Set<String> contentIdsReferenced = writeImageDefinitions(deploymentDefinition.getImageDefinitions(), modificationTime, zos);
      writeContentLibrary(contentIdsReferenced, zos);

    } finally {
      zos.close();
    }
  }

  protected void writeVillageMap(Map<String, ImagePreference> villageMap, long modificationTime, ZipOutputStream zos)
      throws IOException {

    ZipEntry  zipEntry = new ZipEntry(VILLAGE_MAP_NAME);
    zipEntry.setTime(modificationTime);

    byte[] contentBytes = mapper.writeValueAsBytes(villageMap);
    zipEntry.setSize(contentBytes.length);

    zos.putNextEntry(zipEntry);
    try {
      zos.write(contentBytes);
    } finally {
      zos.closeEntry();
    }
  }

  protected void writeFirmware(String firmwareId, ZipOutputStream zos) throws IOException {
    ZipEntry  zipEntry = new ZipEntry(FIRMWARE_NAME);
    ContentResolver.ContentInfo contentInfo = firemwareResolver.loadContent(firmwareId);
    zipEntry.setTime(contentInfo.lastModified);
    zipEntry.setSize(contentInfo.size);

    zos.putNextEntry(zipEntry);
    try {
      IOUtils.copy(contentInfo.inputStream, zos);
    } finally {
      zos.closeEntry();
    }
  }

  protected Set<String> writeImageDefinitions(Map<String, ImageDefinition> imageDefinitions, long modificationTime, ZipOutputStream zos)
      throws IOException {

    Set<String>   contentIdsReferenced = new HashSet<String>();

    for (String  imageDefinitionName : imageDefinitions.keySet()) {
      ZipEntry  zipEntry = new ZipEntry(IMAGE_DEF_DIR + "/" + imageDefinitionName);
      zipEntry.setTime(modificationTime);

      ImageDefinition imageDefinition = imageDefinitions.get(imageDefinitionName);

      //Get all the contentIds listed
      collectContentIds(imageDefinition.getProfiles(), contentIdsReferenced);

      //Write the object definition
      byte[]  image = mapper.writeValueAsBytes(imageDefinition);
      zipEntry.setSize(image.length);

      zos.putNextEntry(zipEntry);
      try {
        zos.write(image);
      } finally {
        zos.closeEntry();
      }
    }

    return contentIdsReferenced;
  }

  protected void collectContentIds(Map<String, Profile> profiles, Set<String> contentIdsReferenced) {
    if (profiles != null) {
      for (Profile profile : profiles.values()) {
        for (PlayList playlist : profile.getContentPackage().getPlaylists()) {
          for (String contentId : playlist.getContentIds()) {
            contentIdsReferenced.add(contentId);
          }
        }
      }
    }
  }

  protected void writeContentLibrary(Set<String> contentIds, ZipOutputStream zos) throws IOException {
    for (String contentId : contentIds) {
      ZipEntry  zipEntry = new ZipEntry(CONTENT_DIR + "/" + contentId);

      ContentResolver.ContentInfo contentInfo = contentResolver.loadContent(contentId);
      zipEntry.setTime(contentInfo.lastModified);
      zipEntry.setSize(contentInfo.size);

      zos.putNextEntry(zipEntry);
      try {
        IOUtils.copy(contentInfo.inputStream, zos);
      } finally {
        zos.closeEntry();
      }
    }
  }

}
