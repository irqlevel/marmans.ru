package models;

import models.mappers.SessionMapper;
import models.mappers.UserMapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import play.Logger;

import java.io.IOException;
import java.util.Properties;


public class Mybatis {
    private static volatile SqlSessionFactory factory = null;

    private static SqlSessionFactory buildFactory() {
        SqlSessionFactory factory = null;
        Properties properties = null;
        try {
            properties = new Properties();
            properties.setProperty("driver", "org.postgresql.Driver");
            properties.setProperty("url", "jdbc:postgresql://127.0.0.1:32771/test_database");
            properties.setProperty("username", "test_user");
            properties.setProperty("password", "1q2w3e");
            factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("MybatisConfig.xml"),
                    properties);
        } catch (IOException e) {
            Logger.error("cant build factory", e);
        }

        return factory;
    }

    public static SqlSession getSession() {
        return getFactory().openSession(false);
    }

    public static boolean dbSync() {
        SqlSession session = null;
        try {
            session = getFactory().openSession(false);

            UserMapper userMapper = session.getMapper(UserMapper.class);
            SessionMapper sessionMapper = session.getMapper(SessionMapper.class);

            userMapper.createTable();
            sessionMapper.createTable();

            session.commit();
            Logger.info("db synced");
            return true;
        } catch (Exception e) {
            Logger.error("cant sync db", e);
        } finally {
            if (session != null)
                session.close();
        }
        return false;
    }

    public static boolean dbDrop() {
        SqlSession session = null;
        try {
            session = getFactory().openSession(false);

            UserMapper userMapper = session.getMapper(UserMapper.class);
            SessionMapper sessionMapper = session.getMapper(SessionMapper.class);

            userMapper.dropTable();
            sessionMapper.dropTable();

            session.commit();
            Logger.info("db dropped");
            return true;
        } catch (Exception e) {
            Logger.error("cant drop db", e);
        } finally {
            if (session != null)
                session.close();
        }
        return false;
    }

    public static SqlSessionFactory getFactory() {
        if (factory != null)
            return factory;
        synchronized (Mybatis.class) {
            if (factory == null) {
                factory = buildFactory();
            }
        }
        return factory;
    }
}
