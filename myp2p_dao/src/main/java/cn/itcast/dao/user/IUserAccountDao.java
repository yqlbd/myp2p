package cn.itcast.dao.user;

import cn.itcast.domain.userAccount.UserAccountModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by yqlbd on 2016/4/16.
 */
public interface IUserAccountDao extends JpaRepository<UserAccountModel,Integer> {
}
