package co.mimosa.kafka.utils;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by ramdurga on 11/23/14.
 */
public class TestKafkaUtils {

  @Test
  public void testCreateTmpDir(){
    File file = KafkaUtils.createTmpDir("kafka");
    assertThat(file).exists();
  }

  @Test
  public void testGetAvailablePort(){
    int availablePort = KafkaUtils.getAvailablePort();
    assertThat(availablePort).isGreaterThan(0);
  }

  @Test
  public void deleteFile() throws FileNotFoundException {
    File file = KafkaUtils.createTmpDir("kafka");
    boolean result = KafkaUtils.deleteFile(file);
    assertThat(result).isTrue();
  }


}
