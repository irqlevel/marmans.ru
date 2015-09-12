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
    private static String accessKeyId = "AKIAIO244G3G3GH7GL5Q";
    private static String secretKey = "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";
    public static final String imagesBucketName = "marmans.ru.images";

    public AwsS3() {
        client = new AmazonS3Client(new AWSCredentials() {
            public String getAWSAccessKeyId() {
                return accessKeyId;
            }
            public String getAWSSecretKey() {
                return secretKey;
            }
        });
    }

    public boolean putFile(String bucketName, String key, File srcFile) {
        boolean result = false;
        try {
            client.putObject(new PutObjectRequest(bucketName, key, srcFile));
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

    public String generateUrl(String bucketName, String key) {
        URL url = null;
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.GET);
            request.setExpiration(new Date(Long.MAX_VALUE));
            url = client.generatePresignedUrl(request);
        } finally {

        }
        return (url != null) ? url.toString() : null;
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
