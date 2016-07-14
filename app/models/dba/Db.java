package models.dba;

import models.mappers.UserMapper;
import models.mappers.UserSessionMapper;

import models.mappers.PostMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import play.Logger;

import java.io.IOException;
import java.util.Properties;


public class Db {
    private static volatile SqlSessionFactory factory = null;
    private static final String dbHost = "db";
    private static final int dbPort = 5432;
    private static final String dbName = "dbname";
    private static final String dbUser = "super";
    private static final String dbUserPassword = "1q2w3e";

    private static SqlSessionFactory buildFactory(String dbHost, int dbPort, String dbName,
                                                  String dbUser, String dbUserPassword) {
        SqlSessionFactory factory = null;
        Properties properties = null;
        try {
            properties = new Properties();
            properties.setProperty("driver", "org.postgresql.Driver");
            properties.setProperty("url", "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName);
            properties.setProperty("username", dbUser);
            properties.setProperty("password", dbUserPassword);
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

    public static SqlSessionFactory getFactory() {
        if (factory != null)
            return factory;
        synchronized (Db.class) {
            if (factory == null) {
                factory = buildFactory(dbHost, dbPort, dbName, dbUser, dbUserPassword);
            }
        }
        return factory;
    }
}
