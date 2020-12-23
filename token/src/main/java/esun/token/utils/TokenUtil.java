package esun.token.utils;

import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenUtil {
    private static Logger logger= LoggerFactory.getLogger(TokenUtil.class);
    /**
     *  token加密秘钥
     */
    private static  final  String secret="SecretKey";
    /**
     * token有效时长
     */
    private static final long expire=60*60*24*30;


    /**
     * 生成token
     * @param username
     * @return
     */
    public static String createToken(String username){
        return  createToken(username,expire);
    }

    public static String createToken(String username,long expire){
        Date nowDate=new Date();
        //过期时间
        Date expireDate=new Date(nowDate.getTime()+expire*1000);
        return  createToken(username,nowDate,expireDate);
    }

    private static String createToken(String username, Date startDate, Date expireDate) {
       return  createToken(username,startDate,expireDate,secret);
    }

    private static String createToken(String username,Date startDate,Date expireDate,String secret){
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setIssuedAt(startDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }


    /**
     * token是否过期
     * @return  true：过期
     */
    public static boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }

    /**
     * 获取jwt的Claims对象
     * @param token
     * @return
     */
    public static Claims getClaimByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.debug("validate is token error ", e);
            return null;
        }
    }
}
