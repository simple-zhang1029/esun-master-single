package esun.token.service.impl;

import esun.token.service.DbHelperService;
import esun.token.service.TokenService;
import esun.token.utils.ResultUtil;
import esun.token.utils.TokenUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class TokenServiceImpl implements TokenService {
    @Value("${token.table}")
    String tokenTable;

    @Autowired
    DbHelperService dbHelperService;

    /**
     * 更新token
     * @param user 用户I
     * @return
     */
    @Override
    public ResultUtil updateToken(String user) {
        String token=TokenUtil.createToken(user,60*60*24*30);
        String sql="update "+tokenTable+" set user_token='"+token+"' where lower(user_userId)=lower('"+user+"')";
        String product="postgres_test";
        ResultUtil result=dbHelperService.update(sql,product);
        if(Integer.parseInt(result.get("code").toString())==200){
            return ResultUtil.ok("更新token成功").put("token",token);
        }
        return ResultUtil.error("更新token失败");
    }

    /**
     * 获取token
     * @param user
     * @return
     */
    @Override
    public ResultUtil getToken(String user) {
        String sql="select user_token from "+tokenTable+" where lower(user_userid)=lower('"+user+"')";
        String product="postgres_test";
        ResultUtil result=dbHelperService.select(sql,product);
        ArrayList resultList = (ArrayList) result.get("result");
        if(resultList==null){
            return ResultUtil.error(result.get("msg").toString());
        }
        HashMap hashMap= (HashMap) resultList.get(0);
        String userToken=hashMap.get("token").toString();
        if(StringUtils.isBlank(userToken)){
            return ResultUtil.error("获取token失败");
        }
        return ResultUtil.ok("获取token成功").put("token",userToken);
    }

    /**
     * 检测token
     * @param token
     * @return
     */
    @Override
    public ResultUtil checkToken( String token) {
        // 校验token是否过期
        Claims claims=TokenUtil.getClaimByToken(token);
        if(claims == null || TokenUtil.isTokenExpired(claims.getExpiration())){
            return ResultUtil.error("token已失效");
        }
        String user=claims.getSubject();
        String sql="select user_token from "+tokenTable+" where lower(user_userid)=lower('"+user+"')";
        String product="postgres_test";
        ResultUtil result=dbHelperService.select(sql,product);
        ArrayList resultList = (ArrayList) result.get("result");
        if (resultList.size()>0){
            HashMap hashMap= (HashMap) resultList.get(0);
            String userToken=hashMap.get("user_token").toString();
            if(token.equals(userToken)){
                return ResultUtil.ok("token校验成功");
            }
        }
        return ResultUtil.error("token检验失败");
    }
}
