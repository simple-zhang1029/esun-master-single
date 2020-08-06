package esun.token.service;

import esun.token.utils.ResultUtil;

public interface TokenService {
    /**
     * 更新token
     * @param user
     * @return
     */
    ResultUtil updateToken(String user);

    /**
     * 获取token
     * @param user
     * @return
     */
    ResultUtil getToken(String user);

    /**
     * 检验token
     * @param token
     * @return
     */
    ResultUtil checkToken(String token);
}
