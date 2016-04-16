package cn.itcast.service.user.impl;

import cn.itcast.dao.user.IUserDao;
import cn.itcast.domain.user.UserModel;
import cn.itcast.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yqlbd on 2016/4/15.
 */
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserDao userDao;


    @Override
    public UserModel findByUserName(String username) {
        UserModel userModel = userDao.findByUsername(username);
//        System.out.println(userModel);
        return userModel;
    }

    @Override
    public UserModel findByPhone(String phone) {
        UserModel userModel = userDao.findByPhone(phone);
        return userModel;
    }

    @Override
    @Transactional
    public boolean addUser(UserModel user) {
        UserModel userModel = userDao.save(user);
        return userModel!=null;
    }

    @Override
    @Transactional
    public void updateSecure(int userId) {
        userDao.updateSecure(userId);
    }

    @Override
    public UserModel findByUsernameAndPassword(String username, String pwd) {
        return userDao.findByUsernameAndPassword(username,pwd);
    }
}
