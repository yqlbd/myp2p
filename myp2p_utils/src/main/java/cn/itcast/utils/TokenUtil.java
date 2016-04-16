package cn.itcast.utils;

import cn.itcast.apis.Constant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


/**
 * @author <a href="mailto:wpt1225@gmail.com">wpt</a>
 * @ClassName: TokenUtil
 * @Description: 用户令牌工具类
 * @date 2015年10月19日 上午10:54:51
 */
public class TokenUtil {

    /**
     * 生成用户令牌
     *
     * @param userName
     * @return
     */
    public static String generateUserToken(String userName) {
        String token = Jwts.builder().setSubject(userName + System.nanoTime()).signWith(SignatureAlgorithm.HS256, Constant.BASE_64KEY).compact();
        return token;
    }

    /**
     * 从令牌中取出用户名
     *
     * @param token
     * @return
     */
    public static String getTokenUserName(String token) {
        String userName = null;
        try {
            userName = Jwts.parser().setSigningKey(Constant.BASE_64KEY).parseClaimsJws(token).getBody().getSubject();
            return userName;
        } catch (Exception e) {
            return userName;
        }

    }


}
