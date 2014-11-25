package co.mimosa.kafka.consumers;

import co.mimosa.kafka.s3.S3EventAnalyzer;
import com.amazonaws.services.s3.AmazonS3;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;


final class ConsumerThread implements Runnable {

    private KafkaStream stream;
    private int threadNumber;
    private AmazonS3 s3;
    private final String s3Bucket;
    private final String s3Id;
    private final S3EventAnalyzer s3EventAnalyzer = new S3EventAnalyzer();
    public ConsumerThread(KafkaStream stream, int threadNumber,AmazonS3 s3,String s3Bucket,String s3Id) {
        this.threadNumber = threadNumber;
        this.stream = stream;
        this.s3 = s3;
        this.s3Bucket = s3Bucket;
        this.s3Id = s3Id;
    }

    public void run() {
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while (it.hasNext()) {
            System.out.println("Message from thread " + threadNumber + ": " );
            String message = new String(it.next().message());
            //System.out.println("Message from thread " + threadNumber + ": " +message);
            s3EventAnalyzer.saveJsonStringIntoS3(message,s3,s3Bucket,s3Id);
        }
        //System.out.println("Shutting down thread: " + threadNumber);
    }

}
