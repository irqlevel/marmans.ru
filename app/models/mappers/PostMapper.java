package models.mappers;

import models.Post;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostMapper {
    public void insert(@Param("postId") Long postId, @Param("uid") Long uid, @Param("title") String title,
                       @Param("content") String content, @Param("creationTime") Long creationTime,
                       @Param("imageId") Long imageId, @Param("active") Integer active, @Param("youtubeLinkId") String youtubeLinkId);

    public void setImage(@Param("postId") Long postId, @Param("imageId") Long imageId);
    public void setActive(@Param("postId") Long postId, @Param("active") Integer active);

    public Post get(Long postId);
    public List<Post> getLatest(@Param("offset") Long offset, @Param("limit") Long limit);
    public void delete(Long postId);

    public List<Post> getUserAll(Long uid);
    public List<Post> getAll();
}

