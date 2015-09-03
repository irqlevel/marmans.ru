package models.mappers;

import models.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    public void createTable();
    public void dropTable();

    public void insert(@Param("uid") Long uid, @Param("name") String name,
                           @Param("email") String email, @Param("hashp") String hashp);

    public void updateName(@Param("uid") Long uid, @Param("name") String name);

    public User get(Long uid);
    public User getByEmail(String email);
    public void delete(Long uid);

    public List<User> getAll();
}
