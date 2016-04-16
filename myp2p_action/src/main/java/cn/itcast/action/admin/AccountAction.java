package cn.itcast.action.admin;

import cn.itcast.action.common.BaseAction;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("prototype")
@ParentPackage("json-default")
@Namespace("/account")
public class AccountAction extends BaseAction {

    private static Logger logger = Logger.getLogger(AccountAction.class);

    @Action(value = "login", results = {@Result(name = "success", type = "json")})
    public void login() {
        // 获取用户名与密码
        String username = this.getRequest().getParameter("username");
        String password = this.getRequest().getParameter("password");
//		System.out.println(username + "@@@@@@@" + password);

        //获取该用户所属的角色
        List<JSONObject> list = new ArrayList<JSONObject>();
        //暂时写成静态数据
        for (int i = 1; i < 30; i++) {
            JSONObject json = new JSONObject();
            json.put("id", i);
            list.add(json);
        }
        try {
            if ("admin".equals(username) && "admin".equals(password)) {
                //登录成功
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(list).toJSON());
            } else {
                //登录失败
                getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.ERROR_OF_USERNAME_PASSWORD).toJSON());

            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

    }

}
