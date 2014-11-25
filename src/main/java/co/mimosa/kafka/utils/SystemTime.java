package co.mimosa.kafka.utils;

import kafka.utils.Time;

/**
 * Created by ramdurga on 11/23/14.
 */
public class SystemTime implements Time {

  @Override public long milliseconds() {
    return System.currentTimeMillis();
  }

  @Override public long nanoseconds() {
    return System.nanoTime();
  }

  @Override public void sleep(long ms) {
    try{
      Thread.sleep(ms);
    }catch(InterruptedException ie){
      //Do nothing for now.
    }
  }
}
