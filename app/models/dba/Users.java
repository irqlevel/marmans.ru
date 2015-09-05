package models.dba;

import lib.BCrypt;
import lib.Rng;
import models.User;
import models.mappers.UserMapper;
import org.apache.ibatis.session.SqlSession;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

public class Users {

    private static String genRndString() {
        SecureRandom rng = new SecureRandom();
        byte[] rndBytes = new byte[16];
        rng.nextBytes(rndBytes);
        return Base64.getEncoder().encodeToString(rndBytes);
    }

    public static User join(String email, String password) {
        SqlSession session = null;

        User newUser = new User(), user = null;
        newUser.setEmail(email);
        newUser.setUid(1000 + new Rng().randInt(1000000));
        newUser.setName(email);
        newUser.setHashp(BCrypt.hashpw(password, BCrypt.gensalt()));

        try {
            session = Db.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.insert(newUser.getUid(), newUser.getName(), newUser.getEmail(), newUser.getHashp());
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

    public static User create() {
        SqlSession session = null;

        User user = null;
        User newUser = new User();
        newUser.setEmail(genRndString());
        newUser.setUid(new Rng().nextLong());
        newUser.setName(genRndString());

        try {
            session = Db.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.insert(newUser.getUid(), newUser.getName(), newUser.getEmail(), genRndString());
            session.commit();
            user = newUser;
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
