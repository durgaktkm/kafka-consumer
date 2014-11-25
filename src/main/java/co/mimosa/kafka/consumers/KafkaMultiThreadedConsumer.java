package co.mimosa.kafka.consumers;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ramdurga on 11/23/14.
 */
//@ImportResource("classpath*:kafka.properties")
public class KafkaMultiThreadedConsumer implements SmartLifecycle {
  //private static final Logger logger = LoggerFactory.getLogger(KafkaMultiThreadedConsumer.class);
  private ConsumerConnector consumer;

  private String topic;
  private ThreadPoolTaskExecutor executorService;
  private boolean started = false;
  private int phase = 1;
  private String numThreads;
  private String zookeeperConnection;
  private String groupId;
  private String zookeeperSessionTimeOutMs;
  private String zookeeperSyncTimeMs;
  private String autoCommitIntervalMs;
  private AmazonS3Client s3;
  private ProfileCredentialsProvider awsCredentials;
  private String s3_bucket;
  private String s3_id;

  @Override
  public void start() {
    s3 = new AmazonS3Client(awsCredentials);
    consumer = Consumer.createJavaConsumerConnector(createConsumerConfig());
    Map<String, Integer> topicCount = new HashMap<>();
    topicCount.put(topic, Integer.parseInt(numThreads));

    Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(topicCount);
    List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topic);

    int threadNumber = 0;
    for (final KafkaStream stream : streams) {
      executorService.submit(new ConsumerThread(stream, threadNumber,s3,s3_bucket,s3_id)); //Make it as provider pattern
      threadNumber++;
    }
    started = true;
  }

  @Override
  public boolean isRunning() {
    return started;
  }

  @Override
  public void stop() {
    if (started) {
      consumer.shutdown();
      executorService.shutdown();
    }

  }

  @Override
  public int getPhase() {
    return phase;
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void stop(Runnable runnable) {
    stop();
    runnable.run();
  }

  private ConsumerConfig createConsumerConfig() {
    Properties props = new Properties();
    props.put("zookeeper.connect", zookeeperConnection);
    props.put("group.id", groupId);
    props.put("zookeeper.session.timeout.ms", zookeeperSessionTimeOutMs);
    props.put("zookeeper.sync.time.ms", zookeeperSyncTimeMs);
    props.put("auto.commit.interval.ms", autoCommitIntervalMs);
    return new ConsumerConfig(props);
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }



  public void setPhase(int phase) {
    this.phase = phase;
  }

  public void setNumThreads(String numThreads) {
    this.numThreads = numThreads;
  }

  public String getNumThreads() {
    return numThreads;
  }

  public void setZookeeperConnection(String zookeeperConnection) {
    this.zookeeperConnection = zookeeperConnection;
  }

  public String getZookeeperConnection() {
    return zookeeperConnection;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setZookeeperSessionTimeOutMs(String zookeeperSessionTimeOutMs) {
    this.zookeeperSessionTimeOutMs = zookeeperSessionTimeOutMs;
  }

  public String getZookeeperSessionTimeOutMs() {
    return zookeeperSessionTimeOutMs;
  }

  public void setZookeeperSyncTimeMs(String zookeeperSyncTimeMs) {
    this.zookeeperSyncTimeMs = zookeeperSyncTimeMs;
  }

  public String getZookeeperSyncTimeMs() {
    return zookeeperSyncTimeMs;
  }

  public void setAutoCommitIntervalMs(String autoCommitIntervalMs) {
    this.autoCommitIntervalMs = autoCommitIntervalMs;
  }

  public String getAutoCommitIntervalMs() {
    return autoCommitIntervalMs;
  }

  public void setS3(AmazonS3Client s3) {
    this.s3 = s3;
  }

  public AmazonS3Client getS3() {
    return s3;
  }

  public void setAwsCredentials(ProfileCredentialsProvider awsCredentials) {
    this.awsCredentials = awsCredentials;
  }

  public ProfileCredentialsProvider getAwsCredentials() {
    return awsCredentials;
  }

  public void setExecutorService(ThreadPoolTaskExecutor executorService) {
    this.executorService = executorService;
  }

  public ThreadPoolTaskExecutor getExecutorService() {
    return executorService;
  }

  public void setS3_bucket(String s3_bucket) {
    this.s3_bucket = s3_bucket;
  }

  public String getS3_bucket() {
    return s3_bucket;
  }

  public void setS3_id(String s3_id) {
    this.s3_id = s3_id;
  }

  public String getS3_id() {
    return s3_id;
  }
}
