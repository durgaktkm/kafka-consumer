package co.mimosa.kafka.utils;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created by ramdurga on 11/23/14.
 */
public class TestSystemTime {
  SystemTime systemTime ;
  @Before
  public  void setup(){
    systemTime = new SystemTime();
  }

  @Test
  public void testMilliseconds(){
    long milliseconds = systemTime.milliseconds();
    assertThat(milliseconds).isLessThanOrEqualTo(System.currentTimeMillis());
  }
  @Test
  public void testNanoseconds(){
    long milliseconds = systemTime.nanoseconds();
    assertThat(milliseconds).isLessThanOrEqualTo(System.nanoTime());
  }
}
