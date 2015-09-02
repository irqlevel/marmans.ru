package models;

import lib.BCrypt;
import lib.Rng;
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

    public static User joinUser(String email, String password) {
        SqlSession session = null;

        User newUser = new User(), user = null;
        newUser.setEmail(email);
        newUser.setUid(1000 + new Rng().randInt(1000000));
        newUser.setName(email);
        newUser.setHashp(BCrypt.hashpw(password, BCrypt.gensalt()));

        try {
            session = Mybatis.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.insertUser(newUser.getUid(), newUser.getName(), newUser.getEmail(), newUser.getHashp());
            session.commit();
            user = newUser;
        } finally {
            if (session != null)
                session.close();
        }

        return user;

    }

    public static User getUserByEmail(String email) {
        SqlSession session = null;
        User user = null;
        try {
            session = Mybatis.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            user = mapper.getUserByEmail(email);
        } finally {
            if (session != null)
                session.close();
        }

        return user;
    }

    public static User getUser(long uid) {
        SqlSession session = null;
        User user = null;
        try {
            session = Mybatis.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            user = mapper.getUser(uid);
        } finally {
            if (session != null)
                session.close();
        }

        return user;
    }

    public static User createUser() {
        SqlSession session = null;

        User user = null;
        User newUser = new User();
        newUser.setEmail(genRndString());
        newUser.setUid(new Rng().nextLong());
        newUser.setName(genRndString());

        try {
            session = Mybatis.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.insertUser(newUser.getUid(), newUser.getName(), newUser.getEmail(), genRndString());
            session.commit();
            user = newUser;
        } finally {
            if (session != null)
                session.close();
        }

        return user;
    }

    public static List<User> getUsers() {
        SqlSession session = null;
        List<User> users = null;

        try {
            session = Mybatis.getSession();
            UserMapper mapper = session.getMapper(UserMapper.class);
            users = mapper.selectAll();
        } finally {
            if (session != null)
                session.close();
        }

        return users;
    }
}
