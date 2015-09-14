package models.dba;

import lib.BCrypt;
import lib.Rng;
import models.User;
import models.mappers.UserMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class Users {

    public static User join(String email, String password) {
        SqlSession session = null;

        User newUser = new User(), user = null;
        newUser.email = email;
        newUser.uid = 1000 + new Rng().randInt(1000000);
        newUser.name = email;
        newUser.hashp = BCrypt.hashpw(password, BCrypt.gensalt());

        try {
            session = Db.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.insert(newUser.uid, newUser.name, newUser.email, newUser.hashp, newUser.avatarId, newUser.thumbnailId);
            session.commit();
            user = newUser;
        } finally {
            if (session != null)
                session.close();
        }

        return user;

    }

    public static boolean updateName(long uid, String name) {
        SqlSession session = null;
        boolean result = false;
        try {
            session = Db.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.updateName(uid, name);
            session.commit();
            result = true;
        } finally {
            if (session != null)
                session.close();
        }
        return result;
    }

    public static boolean updateAvatar(long uid, long avatarId) {
        SqlSession session = null;
        boolean result = false;
        try {
            session = Db.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.updateAvatar(uid, avatarId);
            session.commit();
            result = true;
        } finally {
            if (session != null)
                session.close();
        }
        return result;
    }

    public static boolean updateThumbnail(long uid, long thumbnailId) {
        SqlSession session = null;
        boolean result = false;
        try {
            session = Db.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.updateThumbnail(uid, thumbnailId);
            session.commit();
            result = true;
        } finally {
            if (session != null)
                session.close();
        }
        return result;
    }

    public static User getByEmail(String email) {
        SqlSession session = null;
        User user = null;
        try {
            session = Db.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            user = mapper.getByEmail(email);
        } finally {
            if (session != null)
                session.close();
        }

        return user;
    }

    public static User get(long uid) {
        SqlSession session = null;
        User user = null;
        try {
            session = Db.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            user = mapper.get(uid);
        } finally {
            if (session != null)
                session.close();
        }

        return user;
    }

    public static List<User> getAll() {
        SqlSession session = null;
        List<User> users = null;

        try {
            session = Db.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            users = mapper.getAll();
        } finally {
            if (session != null)
                session.close();
        }

        return users;
    }
}
