package models.dba;

import aws.AwsS3;
import lib.Rng;
import models.Image;
import models.mappers.ImageMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.session.SqlSession;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URLEncoder;

public class Images {
    public static Image create(long uid, String title, String content, File file, String fileName, String fileType)
                                throws Throwable {
        SqlSession session = null;

        Image newImage = new Image(), image = null;
        newImage.uid = uid;
        newImage.title = title;
        newImage.content = content;
        newImage.s3bucket = AwsS3.imagesBucketName;
        newImage.fileName = fileName;
        newImage.fileBaseName = FilenameUtils.getBaseName(fileName);
        newImage.fileExtension = FilenameUtils.getExtension(fileName);
        newImage.s3key = new Rng().genBase64UrlString(16) +
                            URLEncoder.encode(newImage.fileBaseName + "." + newImage.fileExtension, "UTF-8");
        newImage.creationTime = System.currentTimeMillis();
        newImage.imageId = 1000 + new Rng().randInt(1000000);
        newImage.fileType = fileType;
        newImage.fileSize = file.length();

        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            newImage.width = bufferedImage.getWidth();
            newImage.height = bufferedImage.getHeight();

            AwsS3 awsS3 = new AwsS3();
            if (!awsS3.putFile(newImage.s3bucket, newImage.s3key, file))
                return null;
            newImage.url = awsS3.getUrl(newImage.s3bucket, newImage.s3key);
            if (newImage.url == null) {
                awsS3.deleteFile(newImage.s3bucket, newImage.s3key);
            }
            session = Db.getSession();
            ImageMapper mapper = session.getMapper(ImageMapper.class);
            mapper.insert(newImage.imageId, newImage.uid, newImage.title, newImage.content, newImage.creationTime,
                          newImage.url, newImage.s3bucket, newImage.s3key, newImage.width, newImage.height,
                          newImage.fileName, newImage.fileBaseName, newImage.fileExtension, newImage.fileSize, newImage.fileType);

            session.commit();
            image = newImage;
        } finally {
            if (session != null)
                session.close();
            if (image == null) {
                AwsS3 awsS3 = new AwsS3();
                awsS3.deleteFile(newImage.s3bucket, newImage.s3key);
            }
        }

        return image;
    }

    public static Image get(long imageId) {
        SqlSession session = null;
        Image image = null;
        try {
            session = Db.getSession();
            ImageMapper mapper = session.getMapper(ImageMapper.class);
            image = mapper.get(imageId);
        } finally {
            if (session != null)
                session.close();
        }
        return image;
    }

    public static boolean delete(long imageId) {
        boolean result = false;
        SqlSession session = null;
        try {
            Image image = get(imageId);
            if (image != null) {
                AwsS3 awsS3 = new AwsS3();
                awsS3.deleteFile(image.s3bucket, image.s3key);
            }
            session = Db.getSession();
            ImageMapper mapper = session.getMapper(ImageMapper.class);
            mapper.delete(imageId);
            session.commit();
            result = true;
        } finally {
            if (session != null)
                session.close();
        }
        return result;
    }
}
