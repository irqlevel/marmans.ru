package models;

import lib.BCrypt;
import lib.Rng;
import models.mappers.UserMapper;
import models.mappers.UserSessionMapper;
import org.apache.ibatis.session.SqlSession;

public class UserSessions {

    public static boolean insert(UserSession session) {
        SqlSession sql = null;
        boolean result = false;
        try {
            sql = Mybatis.getSession();
            UserSessionMapper mapper = sql.getMapper(UserSessionMapper.class);
            mapper.insert(session.getValue(), session.getCsrfToken(), session.getUid(), session.getExpires());
            sql.commit();
            result = true;
        } finally {
            if (sql != null)
                sql.close();
        }

        return result;
    }

    public static boolean deleteByUid(long uid) {
        SqlSession sql = null;
        boolean result = false;
        try {
            sql = Mybatis.getSession();
            UserSessionMapper mapper = sql.getMapper(UserSessionMapper.class);
            mapper.deleteByUid(uid);
            sql.commit();
            result = true;
        } finally {
            if (sql != null)
                sql.close();
        }
        return result;
    }

    public static boolean delete(String value) {
        SqlSession sql = null;
        boolean result = false;
        try {
            sql = Mybatis.getSession();
            UserSessionMapper mapper = sql.getMapper(UserSessionMapper.class);
            mapper.delete(value);
            sql.commit();
            result = true;
        } finally {
            if (sql != null)
                sql.close();
        }
        return result;
    }

    public static boolean deleteExpired() {
        long now = System.currentTimeMillis();
        SqlSession sql = null;
        boolean result = false;
        try {
            sql = Mybatis.getSession();
            UserSessionMapper mapper = sql.getMapper(UserSessionMapper.class);
            mapper.deleteExpired(now);
            sql.commit();
            result = true;
        } finally {
            if (sql != null)
                sql.close();
        }
        return result;
    }

    public static UserSession get(String value) {
        SqlSession sql = null;
        UserSession session = null;
        try {
            sql = Mybatis.getSession();
            UserSessionMapper mapper = sql.getMapper(UserSessionMapper.class);
            session = mapper.get(value);
        } finally {
            if (sql != null)
                sql.close();
        }
        return session;
    }
}
