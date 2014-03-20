package org.literacybridge.content;

import com.google.common.collect.Sets;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.easymock.EasyMock;
import org.junit.Test;
import org.literacybridge.content.model.DeploymentDefinition;
import org.literacybridge.content.model.ImageDefinition;
import org.literacybridge.content.model.ImagePreference;
import org.literacybridge.content.resolvers.ContentResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 */
public class TestDeploymentObjectWriter {

  public static final File TEST_DIR = new File("target", "testData");
  ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testCreatingDeploymentObject() throws IOException {

    String  testVal = "HelloKitty";

    long  now = System.currentTimeMillis()-37;


    InputStream is = getClass().getResourceAsStream("/SimpleDeploymentDef.json");
    DeploymentDefinition deploymentDefinition = objectMapper.readValue(is, DeploymentDefinition.class);

    ContentResolver firmwareResolver = EasyMock.createMock(ContentResolver.class);
    ContentResolver contentResolver = EasyMock.createMock(ContentResolver.class);

    EasyMock.expect(firmwareResolver.loadContent("Firmware1")).andReturn(new ContentResolver.ContentInfo(now, testVal.length(),
                                                                                                         IOUtils.toInputStream(testVal)));

    EasyMock.expect(contentResolver.loadContent("Content1")).andReturn(new ContentResolver.ContentInfo(now, testVal.length(),
                                                                                                       IOUtils.toInputStream(testVal)));

    EasyMock.expect(contentResolver.loadContent("Content2")).andReturn(new ContentResolver.ContentInfo(now, testVal.length(),
                                                                                                       IOUtils.toInputStream(testVal)));

    EasyMock.checkOrder(contentResolver, false);
    EasyMock.replay(contentResolver, firmwareResolver);

    DeploymentObjectWriter  writer = new DeploymentObjectWriter(contentResolver, firmwareResolver);

    TEST_DIR.mkdirs();
    File  testFile = File.createTempFile("TestDeploymentObjectWriter-testCreatingDeploymentObject", ".zip", TEST_DIR);
    writer.write(deploymentDefinition, testFile, now);


    Set expectedEntries = Sets.newHashSet(DeploymentObjectWriter.VILLAGE_MAP_NAME,
                                          DeploymentObjectWriter.FIRMWARE_NAME,
                                          DeploymentObjectWriter.CONTENT_DIR + "/Content1",
                                          DeploymentObjectWriter.CONTENT_DIR + "/Content2",
                                          DeploymentObjectWriter.IMAGE_DEF_DIR + "/image1",
                                          DeploymentObjectWriter.IMAGE_DEF_DIR + "/image2");

    ZipInputStream  zis = new ZipInputStream(new FileInputStream(testFile));
    ZipEntry  currEntry;
    while ((currEntry = zis.getNextEntry()) != null) {
      TestCase.assertTrue(expectedEntries.contains(currEntry.getName()));

      // /Argh.  coversion between DOS time + Java time is lossy, so this is not exact equals.
      //TestCase.assertEquals(now , currEntry.getTime());

      byte[]  val = IOUtils.toByteArray(zis);
      TestCase.assertEquals(val.length, currEntry.getSize());

      if (currEntry.getName().startsWith(DeploymentObjectWriter.CONTENT_DIR)) {
        TestCase.assertTrue(Arrays.equals(testVal.getBytes(), val));
      } else if (currEntry.getName().startsWith(DeploymentObjectWriter.FIRMWARE_NAME)) {
        TestCase.assertTrue(Arrays.equals(testVal.getBytes(), val));
      } else if (currEntry.getName().equals(DeploymentObjectWriter.VILLAGE_MAP_NAME)) {
        Map<String, ImagePreference>  villageMap = objectMapper.readValue(val, 0, val.length, new TypeReference<
            HashMap<String,ImagePreference>
            >() {});
        TestCase.assertEquals(deploymentDefinition.getVillageMap(), villageMap);
      } else if (currEntry.getName().startsWith(DeploymentObjectWriter.IMAGE_DEF_DIR)) {

        ImageDefinition imageDefinition = objectMapper.readValue(val, 0, val.length, ImageDefinition.class);

        String  imageName = currEntry.getName().substring(DeploymentObjectWriter.IMAGE_DEF_DIR.length() + 1);
        TestCase.assertEquals(deploymentDefinition.getImageDefinitions().get(imageName), imageDefinition);
      } else {
        TestCase.fail("Illegal member in zip file");
      }

      //Remove entry when we saw it so we can make sure we saw every entry exactly once.
      expectedEntries.remove(currEntry.getName());
    }

    TestCase.assertEquals(0, expectedEntries.size());
  }
}
