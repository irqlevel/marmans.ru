package models.dba;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lib.Rng;
import models.Post;
import models.mappers.PostMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class Posts {

    public static Post create(long uid, String title, String content) {
        SqlSession session = null;

        Post newPost = new Post(), post = null;
        newPost.content = content;
        newPost.title = title;
        newPost.creationTime = System.currentTimeMillis();
        newPost.postId = 1000 + new Rng().randInt(1000000);
        newPost.uid = uid;
        newPost.active = 0;

        try {
            session = Db.getSession();
            PostMapper mapper = session.getMapper(PostMapper.class);
            mapper.insert(newPost.postId, newPost.uid, newPost.title, newPost.content, newPost.creationTime,
                          newPost.imageId, newPost.active);
            session.commit();
            post = newPost;
        } finally {
            if (session != null)
                session.close();
        }

        return post;
    }

    public static boolean setImage(long postId, long imageId) {
        SqlSession session = null;
        boolean result = false;
        try {
            session = Db.getSession();
            PostMapper mapper = session.getMapper(PostMapper.class);
            mapper.setImage(postId, imageId);
            session.commit();
            result = true;
        } finally {
            if (session != null)
                session.close();
        }
        return result;
    }

    public static boolean setActive(long postId, int active) {
        SqlSession session = null;
        boolean result = false;
        try {
            session = Db.getSession();
            PostMapper mapper = session.getMapper(PostMapper.class);
            mapper.setActive(postId, active);
            session.commit();
            result = true;
        } finally {
            if (session != null)
                session.close();
        }
        return result;
    }

    public static List<Post> getAll() {
        SqlSession session = null;
        List<Post> posts = null;
        try {
            session = Db.getSession();
            PostMapper mapper = session.getMapper(PostMapper.class);
            posts = mapper.getAll();
        } finally {
            if (session != null)
                session.close();
        }
        return posts;
    }

    public static List<Post> getLatest(long offset, long limit) {
        SqlSession session = null;
        List<Post> posts = null;
        try {
            session = Db.getSession();
            PostMapper mapper = session.getMapper(PostMapper.class);
            posts = mapper.getLatest(offset, limit);
        } finally {
            if (session != null)
                session.close();
        }
        return posts;
    }

    public static Post get(long postId) {
        SqlSession session = null;
        Post post = null;
        try {
            session = Db.getSession();
            PostMapper mapper = session.getMapper(PostMapper.class);
            post = mapper.get(postId);
        } finally {
            if (session != null)
                session.close();
        }
        return post;
    }
}
