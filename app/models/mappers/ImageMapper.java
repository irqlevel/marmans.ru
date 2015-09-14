package models.mappers;

import models.Comment;
import models.Image;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ImageMapper {
    public void insert(@Param("imageId") Long imageId, @Param("uid") Long uid, @Param("title") String title,
                       @Param("content") String content, @Param("creationTime") Long creationTime,
                       @Param("url") String url, @Param("s3bucket") String s3bucket, @Param("s3key") String s3key,
                       @Param("width") Long width, @Param("height") Long height, @Param("fileName") String fileName,
                       @Param("fileBaseName") String fileBaseName, @Param("fileExtension") String fileExtension,
                       @Param("fileSize") Long fileSize, @Param("fileType") String fileType);

    public Image get(Long imageId);
    public void delete(Long imageId);
}
