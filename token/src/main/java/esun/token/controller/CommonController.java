package esun.token.controller;

import esun.token.exception.CustomHttpException;
import esun.token.service.DbHelperService;
import esun.token.service.TokenService;
import esun.token.utils.ResultUtil;
import esun.token.utils.TokenUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/esun")
public class CommonController {
    private static Logger logger= LoggerFactory.getLogger(CommonController.class);

    @Autowired
    TokenService tokenService;


    /**
     * 更新token
     *
     * @return
     */
    @PostMapping("/token")
    public ResultUtil updateToken(@RequestParam("user") String user) {
        return tokenService.updateToken(user);
    }

    /**
     * 获取token
     */
    @GetMapping("/token")
    public ResultUtil getToken(@RequestParam("user") String user) {
        return tokenService.getToken(user);
    }

    /**
     * token校验
     */
    @RequestMapping("/checkToken")
    public ResultUtil checkToken(@RequestParam("token")String token){

        return tokenService.checkToken(token);

    }
}


