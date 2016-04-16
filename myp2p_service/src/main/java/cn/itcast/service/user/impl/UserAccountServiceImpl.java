package cn.itcast.service.user.impl;

import cn.itcast.dao.user.IUserAccountDao;
import cn.itcast.domain.userAccount.UserAccountModel;
import cn.itcast.service.user.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yqlbd on 2016/4/16.
 */
@Service
public class UserAccountServiceImpl implements IUserAccountService {

    @Autowired
    private IUserAccountDao userAccountDao;

    @Override
    @Transactional
    public void addUserAccount(int userId) {
        UserAccountModel userAccount = new UserAccountModel();
        userAccount.setUserId(userId);
        userAccountDao.save(userAccount);
    }
}
