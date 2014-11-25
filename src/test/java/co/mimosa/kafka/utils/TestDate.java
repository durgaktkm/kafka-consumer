package co.mimosa.kafka.utils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ramdurga on 11/24/14.
 */
public class TestDate {

  @Test
  public void testDate(){
    Calendar cal  = new GregorianCalendar();
    cal.setTime(new Date());
    System.out.println(cal.get(Calendar.YEAR));
    System.out.println(cal.get(Calendar.MONTH));
    System.out.println(cal.get(Calendar.DAY_OF_MONTH));
  }
}
