package models.mappers;

import models.UserSession;
import org.apache.ibatis.annotations.Param;

public interface UserSessionMapper {
    public void createTable();
    public void dropTable();

    public void insert(@Param("value") String value, @Param("csrfToken") String csrfToken,
                       @Param("uid") Long uid, @Param("expires") Long expires);
    public UserSession get(String value);
    public UserSession getByUid(Long uid);
    public void delete(String value);
    public void deleteByUid(Long uid);
    public void deleteExpired(Long currentTime);
}

