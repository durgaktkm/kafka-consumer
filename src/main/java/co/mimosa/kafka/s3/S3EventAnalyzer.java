package co.mimosa.kafka.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ramdurga on 11/24/14.
 */
public class S3EventAnalyzer {
  private static final Logger logger = LoggerFactory.getLogger(S3EventAnalyzer.class);
  //TODO move out to amazons3.property file.
  private static final String SERIALNUMBER_REGEX = "(?<=\"serialNumber\":\")\\d{10}";
  private static final Pattern SERIALNUMBER_PATTERN = Pattern.compile(SERIALNUMBER_REGEX);
  private static final Pattern EVENT_TYPE_PATTERN = Pattern.compile("(?<=\"eventType\":\")\\w+");
  private static final String NEWLINE_REPLACEMENT = "#";

  private static final String DIR_SEPARATOR="/";

  public void saveJsonStringIntoS3(String eventJsonString,AmazonS3 s3,String bucketName,String s3Id){
    String eventType;
    eventJsonString = eventJsonString.replaceAll("\n", NEWLINE_REPLACEMENT);
    int eventDataEllipsisSize = (eventJsonString.length() > 65 ? 65 : eventJsonString.length());
    logger.debug("Hash replaced content={}", eventJsonString.substring(0, eventDataEllipsisSize).trim() + "...");
    Calendar cal  = new GregorianCalendar();
    cal.setTime(new Date());

    try{
      String serialNumber = getMatchingString(SERIALNUMBER_PATTERN, eventJsonString);
      eventType = getMatchingString(EVENT_TYPE_PATTERN, eventJsonString);
      S3File file = new S3File();
      file.setRaw_json(eventJsonString);
      file.setSerialNumber(serialNumber);
      file.setTime_stamp(cal.getTimeInMillis());
      //TODO remove after talking to venkatesh. Dont need this.
      if(!s3.doesBucketExist(bucketName)) {
        s3.createBucket(bucketName);
      }
      String key = s3Id+DIR_SEPARATOR+cal.get(Calendar.YEAR)+DIR_SEPARATOR+(cal.get(Calendar.MONTH)+1)+DIR_SEPARATOR+cal.get(Calendar.DAY_OF_MONTH)+DIR_SEPARATOR+serialNumber+DIR_SEPARATOR+eventType+DIR_SEPARATOR+eventType+"_"+cal.getTimeInMillis();
      uploadData(s3, key, bucketName,file);
    }catch(Exception e){
      logger.error("Error while uploading JSON string to S3"+e.getMessage());
      //TODO
    }
  }

  void uploadData(AmazonS3 s3Client,String keyName,String bucketName, S3File deviceResponseDetail) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      String data ;
      try {
        data = mapper.writeValueAsString(deviceResponseDetail);
      } catch (Exception e) {
        logger.error("Error while creating JSON string for S3");
        return;
      }

      logger.debug("Uploading a new object to S3 from a file\n");
      ObjectMetadata objectMetadata = new ObjectMetadata();
      objectMetadata.setContentLength(data.length());


      PutObjectResult result = s3Client.putObject(new PutObjectRequest(bucketName, keyName, new ByteArrayInputStream(data.getBytes()),
          objectMetadata));
      logger.debug("S3 Response + " + result);
    } catch (AmazonServiceException ase) {
      logger.warn("Error while uploading device action response on S3");
      logger.debug("Caught an AmazonServiceException, which " + "means your request made it "
          + "to Amazon S3, but was rejected with an error response" + " for some reason.");
      logger.debug("Error Message:    " + ase.getMessage());
      logger.debug("HTTP Status Code: " + ase.getStatusCode());
      logger.debug("AWS Error Code:   " + ase.getErrorCode());
      logger.debug("Error Type:       " + ase.getErrorType());
      logger.debug("Request ID:       " + ase.getRequestId());
    } catch (AmazonClientException ace) {
      logger.warn("Error while uploading device action response on S3");
      logger.debug("Caught an AmazonClientException, which " + "means the client encountered "
          + "an internal error while trying to " + "communicate with S3, "
          + "such as not being able to access the network.");
      logger.debug("Error Message: " + ace.getMessage());
    }
  }
  private String getMatchingString(Pattern pattern, String jsonString) {
    Matcher matcher = pattern.matcher(jsonString);
    if(matcher.find())
      return matcher.group();
    return null;
  }
}
