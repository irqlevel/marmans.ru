package models.mappers;

import models.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    public void createTable();
    public void dropTable();

    public void insertUser(@Param("uid") Long uid, @Param("name") String name,
                           @Param("email") String email);
    public User getUser(Long uid);
    public void deleteUser(Long uid);

    public List<User> selectAll();
}
