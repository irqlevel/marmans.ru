package models.dba;

import lib.Rng;
import models.Comment;
import models.mappers.CommentMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class Comments {

    public static Comment get(long commentId) {
        SqlSession session = null;
        Comment comment = null;
        try {
            session = Db.getSession();
            CommentMapper mapper = session.getMapper(CommentMapper.class);
            comment = mapper.get(commentId);
        } finally {
            if (session != null)
                session.close();
        }
        return comment;
    }

    public static List<Comment> getCommentsByPostId(long postId) {
        SqlSession session = null;
        List<Comment> comments = null;
        try {
            session = Db.getSession();
            CommentMapper mapper = session.getMapper(CommentMapper.class);
            comments = mapper.getByPostId(postId);
        } finally {
            if (session != null)
                session.close();
        }
        return comments;
    }

    public static Comment create(long uid, long postId, long parentId, String content) {
        SqlSession session = null;

        Comment newComment = new Comment(), comment = null;
        newComment.content = content;
        newComment.postId = postId;
        newComment.creationTime = System.currentTimeMillis();
        newComment.commentId = 1000 + new Rng().randInt(1000000);
        newComment.uid = uid;
        newComment.parentId = parentId;

        try {
            session = Db.getSession();
            CommentMapper mapper = session.getMapper(CommentMapper.class);
            mapper.insert(newComment.postId, newComment.commentId, newComment.content,
                          newComment.creationTime, newComment.uid, newComment.parentId, newComment.imageId);
            session.commit();
            comment = newComment;
        } finally {
            if (session != null)
                session.close();
        }

        return comment;
    }

    public static long countComments(long postId) {
        SqlSession session = null;
        long nrComments = 0;
        try {
            session = Db.getSession();
            CommentMapper mapper = session.getMapper(CommentMapper.class);
            nrComments = mapper.countComments(postId);
        } finally {
            if (session != null)
                session.close();
        }
        return nrComments;
    }
}
