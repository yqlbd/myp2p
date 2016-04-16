package cn.itcast.action.user;

import cn.itcast.action.common.BaseAction;
import cn.itcast.cache.BaseCacheService;
import cn.itcast.utils.FrontStatusConstants;
import cn.itcast.utils.Response;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * VerificationAction类
 * Created by yqlbd on 2016/4/15.
 */

@Controller
@Scope("prototype")
@ParentPackage("json-default")
@Namespace("/verification")
public class VerificationAction extends BaseAction {

    @Resource(name = "redisCache")
    private BaseCacheService baseCacheService;

    @Action("sendMessage")//发送短信操作
    public void sendMessage() {
        String phone = this.getParameter("phone");
        String code = RandomStringUtils.randomNumeric(6);
        if (StringUtils.isNotEmpty(phone)) {
            baseCacheService.del(phone);
            baseCacheService.set(phone, code);
            baseCacheService.expire(phone, 60 * 10);
        }

        //TODO
        //省略了短信发送步骤

        System.out.println("P2P注册验证码:" + code);

        /*
        * 如下代码用于记录短信发送条数，用了redis中存取字符串的方式
        * 当超过5条时，返回超过限定次数的状态码
        */
        String phoneCountKey = phone + "key";

        String phoneCountValue = baseCacheService.get(phoneCountKey);

        try {
            if (StringUtils.isEmpty(phoneCountValue)) {
                phoneCountValue = "0";
            } else {
                int time = Integer.parseInt(phoneCountValue);
                if (time >= 5) {
                    this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.OVER_MESSAGE_TIMES).toJSON());
                    return;
                } else {
                    phoneCountValue = (time + 1) + "";
                }
            }
            baseCacheService.set(phoneCountKey, phoneCountValue);
            baseCacheService.expire(phoneCountKey, 24 * 60 * 60);
            this.getResponse().getWriter().write(Response.build().setStatus("1").toJSON());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Action("validateSMS")//服务端验证手机校验码是否正确，正确则放行
    public void validateSMS() {
        try {
            String phone = this.getRequest().getParameter("phone");
            String code = this.getRequest().getParameter("code");
            if (StringUtils.isNotEmpty(phone)) {
                String _code = baseCacheService.get(phone);
                if (StringUtils.isNotEmpty(_code)) {
                    if (_code.equals(code)) {
                        this.getResponse().getWriter().write(Response.build().setStatus(FrontStatusConstants.SUCCESS).toJSON());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
