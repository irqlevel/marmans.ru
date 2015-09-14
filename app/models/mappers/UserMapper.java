package models.mappers;

import models.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    public void insert(@Param("uid") Long uid, @Param("name") String name,
                       @Param("email") String email, @Param("hashp") String hashp, @Param("avatarId") Long avatarId,
                       @Param("thumbnailId") Long thumbnailId);

    public void updateName(@Param("uid") Long uid, @Param("name") String name);
    public void updateAvatar(@Param("uid") Long uid, @Param("avatarId") Long avatarId);
    public void updateThumbnail(@Param("uid") Long uid, @Param("thumbnailId") Long thumbnailId);
    public User get(Long uid);
    public User getByEmail(String email);
    public void delete(Long uid);

    public List<User> getAll();
}
