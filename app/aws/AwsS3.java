package aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.net.URL;
import java.util.Date;

public class AwsS3 {
    private AmazonS3Client client = null;
    private static String accessKeyId = "AKIAJEERGXVG42FC6RBQ";
    private static String secretKey = "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";
    private static String endPoint = "https://s3.eu-central-1.amazonaws.com";
    public static final String imagesBucketName = "ru.marmans.images";

    public AwsS3() {
        client = new AmazonS3Client(new AWSCredentials() {
            public String getAWSAccessKeyId() {
                return accessKeyId;
            }
            public String getAWSSecretKey() {
                return secretKey;
            }
        });
        client.setEndpoint(endPoint);
    }

    public boolean putFile(String bucketName, String key, File srcFile) {
        boolean result = false;
        try {
            client.putObject(new PutObjectRequest(bucketName, key, srcFile).withCannedAcl(CannedAccessControlList.PublicRead));
            result = true;
        } finally {

        }
        return result;
    }

    public boolean getFile(String bucketName, String key, File dstFile) {
        boolean result = false;
        try {
            ObjectMetadata meta = client.getObject(new GetObjectRequest(bucketName, key), dstFile);
            result = true;
        } finally {

        }
        return result;
    }

    public String getUrl(String bucketName, String key) {
        return endPoint + "/" + bucketName + "/" + key;
    }

    public boolean deleteFile(String bucketName, String key) {
        boolean result = false;
        try {
            client.deleteObject(new DeleteObjectRequest(bucketName, key));
            result = true;
        } finally {

        }
        return result;
    }
}
