package cn.itcast.service.user;

import cn.itcast.domain.user.UserModel;

/**
 * Created by yqlbd on 2016/4/15.
 */
public interface IUserService {
    UserModel findByUserName(String username);

    UserModel findByPhone(String phone);

    boolean addUser(UserModel user);

    void updateSecure(int userId);

    UserModel findByUsernameAndPassword(String username, String pwd);
}
