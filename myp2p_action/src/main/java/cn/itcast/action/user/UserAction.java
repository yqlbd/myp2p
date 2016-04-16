package cn.itcast.action.user;

import cn.itcast.action.common.BaseAction;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.domain.user.UserModel;
import cn.itcast.service.user.IUserAccountService;
import cn.itcast.service.user.IUserService;
import cn.itcast.utils.*;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yqlbd on 2016/4/15.
 */

@Controller
@Scope("prototype")
@ParentPackage("json-default")
@Namespace("/user")
public class UserAction extends BaseAction implements ModelDriven<UserModel> {

    UserModel user = new UserModel();

    @Override//模型驱动，模型为UserModel
    public UserModel getModel() {
        return user;
    }

    @Resource(name = "redisCache")
    private BaseCacheService baseCacheService;

    @Autowired//自动注入
    private IUserService userService;
    @Autowired//自动注入
    private IUserAccountService userAccountService;


    @Action("uuid")//获取uuid方法
    public void uuid() {
        String uuid = UUID.randomUUID().toString();
        baseCacheService.set(uuid, uuid);
        baseCacheService.expire(uuid, 10 * 60);
//        System.out.println(uuid);
        try {
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setUuid(uuid).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Action("validateCode")//获取对应的随机验证码，并写回去
    public void validateCode() {
        //获得参数
        String tokenUuid = this.getRequest().getParameter("tokenUuid");
        //从redis中获取uuid的值
        String uuid = baseCacheService.get(tokenUuid);

        try {
            //不为空用工具类生成

            if (StringUtils.isNotEmpty(uuid)) {
                String code = ImageUtil.getRundomStr();

                String lowCode = code.toLowerCase();
//                System.out.println(lowCode);
                //删除以前的，存入真正的uuid对应的随机码
                baseCacheService.del(tokenUuid);
                baseCacheService.set(tokenUuid, lowCode);
                baseCacheService.expire(uuid, 10 * 60);
                ImageUtil.getImage(code, this.getResponse().getOutputStream());
            } else {
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.BREAK_DOWN).toJSON());
            }
        } catch (IOException e) {
            e.printStackTrace();

        }


    }

    @Action(value = "validateUserName")//服务端校验用户名是否存在
    public void validateUserName() {
        String username = this.getParameter("username");
//        System.out.println(username);
        UserModel user = userService.findByUserName(username);

        try {
            if (user == null) {
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
                return;
            }
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.ALREADY_EXIST_OF_USERNAME).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Action(value = "validatePhone")//服务端校验手机号码是否存在
    public void validatePhone() {
        String phone = this.getParameter("phone");
        UserModel user = userService.findByPhone(phone);
        try {
            if (user == null) {
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
                return;
            }
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.MOBILE_ALREADY_REGISTER).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Action(value = "codeValidate")//服务验证校验码是否正确
    public void codeValidate() {
        String signUuid = this.getParameter("signUuid");
        String signCode = this.getParameter("signCode").toLowerCase();
//        System.out.println("singCode="+signCode);

        try {
            if (StringUtils.isNotEmpty(signUuid) && StringUtils.isNotEmpty(signCode)) {
                String _singCode = baseCacheService.get(signUuid);
//                System.out.println("_singCode="+_singCode);
                if (StringUtils.isNotEmpty(_singCode)) {
                    if (signCode.equals(_singCode)) {
                        this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
                    } else {
                        this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
                    }
                } else {
                    this.getResponse().getWriter().write(Response.build().setStatus("156").toJSON());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Action(value = "signup")
    public void singup() {

        //获取请求参数，模型驱动能接受的参数不用封装
        String signUuid = this.getRequest().getParameter("signUuid"); // uuid
        String signCode = this.getRequest().getParameter("signCode"); // 图片验证码
        String phoneCode = this.getRequest().getParameter("phoneCode"); // 短信验证码

        //非空判断
        try {
            if (StringUtils.isEmpty(signUuid)) {
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_TOKEN).toJSON());
                return;
            }
            if (StringUtils.isEmpty(signCode)) {
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
                return;
            }
            if (StringUtils.isEmpty(phoneCode)) {
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
                return;
            }

            // 根据uuid我们可以在redis中获取图片验证码
            String _signCode = baseCacheService.get(signUuid);
            if (StringUtils.isEmpty(_signCode)) {
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NULL_OF_VALIDATE_CARD).toJSON());
                return;
            }

            if (!_signCode.equalsIgnoreCase(signCode)) {
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
                return;
            }
            // 判断短信验证码,根据用户的手机号
            String _phoneCode = baseCacheService.get(user.getPhone());
            if (!phoneCode.equalsIgnoreCase(_phoneCode)) {
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
                return;
            }

            //加密用户密码，原则：username+小写password进行md5加密
            String pwd = MD5Util.md5(user.getUsername().trim() + user.getPassword().trim().toLowerCase());
            user.setPassword(pwd);
            //调用service，向表中添加数据
            boolean flag = userService.addUser(user);
            //添加成功，就向reids中存入令牌数据
            if (flag) {
                String token = this.generateUserToken(user.getUsername());
                Map<String, Object> map = baseCacheService.getHmap(token);
                int userId = Integer.parseInt(map.get("id").toString());
                //向account表中插入一条数据
                userAccountService.addUserAccount(userId);
                //修改安全级别
                //domian中最好用基本类型，但要处理好空值问题
                userService.updateSecure(userId);
                Map<String, Object> returnMap = new HashMap<>();
                returnMap.put("id", userId);
                this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(returnMap).setToken(token).toJSON());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Action("userSecure")//验证级别
    public void userSecure() {
        String token = this.getRequest().getHeader("token");
        if (StringUtils.isNotEmpty(token)) {
            Map<String, Object> hmap = baseCacheService.getHmap(token);

            if (hmap != null) {
                try {
                    this.getResponse().getWriter().write(Response.build().setData(hmap).toJSON());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Action("login")//登陆
    public void login() {
        String username = this.getParameter("username");
        String password = this.getParameter("password");
        String signUuid = this.getParameter("signUuid");
        String signCode = this.getParameter("signCode");

        try {
            //获取uuid和code
            if (StringUtils.isNotEmpty(signUuid) && StringUtils.isNotEmpty(signCode)) {
                String _signCode = baseCacheService.get(signUuid);
                //先校验
                if (_signCode.equalsIgnoreCase(signCode)) {
                    //判断是否电话号码
                    if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
                        if (CommomUtil.isPhone(username)) {
                            UserModel _user = userService.findByPhone(username);
                            if (_user != null) {
                                username = _user.getUsername();
                            }
                        }

                        String pwd = MD5Util.md5(username.trim() + password.trim().toLowerCase());
                        UserModel user = userService.findByUsernameAndPassword(username, pwd);
                        if (user == null) {
                            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.ERROR_OF_USERNAME_PASSWORD).toJSON());
                        } else {
                            String token = this.generateUserToken(user.getUsername());
                            Map<String, Object> map = new HashMap<>();
                            map.put("userName", user.getUsername());
                            map.put("id", user.getId());
                            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).setData(map).setToken(token).toJSON());
                        }
                    }
                    return;
                }
            }
            this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.INPUT_ERROR_OF_VALIDATE_CARD).toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Action("logout")//退出
    public void logout() {
        String token = this.getRequest().getHeader("token");
        try {
            if (StringUtils.isNotEmpty(token)) {
                Map<String, Object> hmap = baseCacheService.getHmap(token);
                if (hmap != null) {
                    baseCacheService.del(token);
                    this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
                } else {
                    this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.NOT_LOGGED_IN).toJSON());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 将生成的tokey做为key，用户信息封装到Map中做为value,存储到redis中。
    public String generateUserToken(String userName) {
        try {
            // 生成令牌
            String token = TokenUtil.generateUserToken(userName);

            // 根据用户名获取用户
            UserModel user = userService.findByUserName(userName);
            // 将用户信息存储到map中。
            Map<String, Object> tokenMap = new HashMap<String, Object>();
            tokenMap.put("id", user.getId());
            tokenMap.put("userName", user.getUsername());
            tokenMap.put("phone", user.getPhone());
            tokenMap.put("userType", user.getUserType());
            tokenMap.put("payPwdStatus", user.getPayPwdStatus());
            tokenMap.put("emailStatus", user.getEmailStatus());
            tokenMap.put("realName", user.getRealName());
            tokenMap.put("identity", user.getIdentity());
            tokenMap.put("realNameStatus", user.getRealNameStatus());
            tokenMap.put("payPhoneStatus", user.getPhoneStatus());

            baseCacheService.del(token);
            baseCacheService.setHmap(token, tokenMap); // 将信息存储到redis中

            // 获取配置文件中用户的生命周期，如果没有，默认是30分钟
            String tokenValid = ConfigurableConstants.getProperty("token.validity", "30");
            tokenValid = tokenValid.trim();
            baseCacheService.expire(token, Long.valueOf(tokenValid) * 60);

            return token;
        } catch (Exception e) {
            e.printStackTrace();
            return Response.build().setStatus("-9999").toJSON();
        }
    }


}
