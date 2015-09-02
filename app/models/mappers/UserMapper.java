package models.mappers;

import models.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    public void createTable();
    public void dropTable();

    public void insertUser(@Param("uid") Long uid, @Param("name") String name,
                           @Param("email") String email, @Param("hashp") String hashp);
    public User getUser(Long uid);
    public User getUserByEmail(String email);
    public void deleteUser(Long uid);

    public List<User> selectAll();
}
