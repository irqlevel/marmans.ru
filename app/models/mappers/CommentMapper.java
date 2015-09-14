package models.mappers;

import models.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    public void insert(@Param("postId") Long postId, @Param("commentId") Long commentId,
                       @Param("content") String content, @Param("creationTime") Long creationTime,
                       @Param("uid") Long uid, @Param("parentId") Long parentId, @Param("imageId") Long imageId);
    public Comment get(Long commentId);
    public List<Comment> getByPostId(Long postId);
    public void delete(Long commentId);
}
