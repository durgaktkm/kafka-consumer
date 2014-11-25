package co.mimosa.kafka.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

/**
 * Created by ramdurga on 11/23/14.
 */
public class KafkaUtils {
  private static final Random RANDOM = new Random();
  public static File createTmpDir(String directoryPrefix) {
    File file = new File(System.getProperty("java.io.tmpdir"),directoryPrefix+RANDOM.nextInt(10000000));
    if(!file.mkdir()){
      throw new RuntimeException("Creation of tmp directory failed "+file.getAbsolutePath());
    }
    file.deleteOnExit();
    return file;
  }

  public static int getAvailablePort() {
    try {
      ServerSocket socket = new ServerSocket(0);
      try {
        return socket.getLocalPort();
      } finally {
        socket.close();
      }
    } catch (IOException e) {
      throw new IllegalStateException("Cannot find available port: " + e.getMessage(), e);
    }
  }

  public static boolean deleteFile(File file) throws FileNotFoundException {
    if (!file.exists()) {
      throw new FileNotFoundException(file.getAbsolutePath());
    }
    boolean ret = true;
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        ret = ret && deleteFile(f);
      }
    }
    return ret && file.delete();
  }
}
