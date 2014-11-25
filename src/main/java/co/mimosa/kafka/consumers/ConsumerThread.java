package co.mimosa.kafka.consumers;

import co.mimosa.kafka.s3.S3EventAnalyzer;
import com.amazonaws.services.s3.AmazonS3;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

final class ConsumerThread implements Runnable {

    private final KafkaStream stream;
    private final int threadNumber;
    private final AmazonS3 s3;
    private final S3EventAnalyzer s3EventAnalyzer;
    private final String s3Bucket;
    private final String s3Id;

    public ConsumerThread(KafkaStream stream, int threadNumber,AmazonS3 s3,S3EventAnalyzer s3EventAnalyzer,String s3Bucket,String s3Id) {
        this.threadNumber = threadNumber;
        this.stream = stream;
        this.s3 = s3;
        this.s3EventAnalyzer = s3EventAnalyzer;
        this.s3Bucket = s3Bucket;
        this.s3Id = s3Id;
    }

    public void run() {
        for (MessageAndMetadata<byte[], byte[]> aStream : (Iterable<MessageAndMetadata<byte[], byte[]>>) stream) {
            System.out.println("Message from thread " + threadNumber + ": ");
            String message = new String(aStream.message());
            //System.out.println("Message from thread " + threadNumber + ": " +message);
            s3EventAnalyzer.saveJsonStringIntoS3(message, s3, s3Bucket, s3Id);
        }
        //System.out.println("Shutting down thread: " + threadNumber);
    }

}
