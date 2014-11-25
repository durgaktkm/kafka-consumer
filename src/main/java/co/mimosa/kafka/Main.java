package co.mimosa.kafka;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by ramdurga on 11/23/14.
 */
public class Main {
  public static void main(String[] args) {
    ApplicationContext context =
        new ClassPathXmlApplicationContext("kafkaconsumer.xml");
  }
}
