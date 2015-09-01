package models.mappers;

import models.Session;
import org.apache.ibatis.annotations.Param;

public interface SessionMapper {
    public void createTable();
    public void dropTable();

    public void insertSession(@Param("session") String session, @Param("uid") Long uid,
                              @Param("expires") Long expires);
    public Session getSession(String session);
    public Session getSessionByUid(Long uid);
    public void deleteSession(String session);
    public void deleteExpired(Long currentTime);
}

