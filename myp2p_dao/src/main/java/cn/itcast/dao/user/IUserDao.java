package cn.itcast.dao.user;

import cn.itcast.domain.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by yqlbd on 2016/4/15.
 */

public interface IUserDao extends JpaRepository<UserModel, Integer> {

    UserModel findByUsername(String username);

    UserModel findByPhone(String phone);

    @Modifying
    @Query("update UserModel u set u.userSecure=u.userSecure+1 where u.id=?1")
    void updateSecure(int userId);

    @Query("select u from UserModel u where u.username=?1 and u.password=?2")
    UserModel findByUsernameAndPassword(String username, String pwd);
}
