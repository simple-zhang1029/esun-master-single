package com.example.utils;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.util.DigestUtils;

/**
 * MD5工具类
 * @author xiaoliebin
 */
public class Md5Util {



    private  String salt;
    /**
     * 对密码进行加密
     * @param password
     * @return
     */
    public  String encodePassword(String password){
         salt= RandomStringUtils.randomAlphanumeric(20);
        return encodePassword(password,salt);

    }

    public  String encodePassword(String password,String salt){
        password= DigestUtils.md5DigestAsHex((password+salt).getBytes());
        return password;
    }

    /**
     * 对密码进行检验
     * @param password 输入的密码
     * @param salt 盐
     * @param oldPassword 原有用户的密码
     * @return
     */
    public  boolean checkPassword(String password,String salt,String oldPassword){
        return oldPassword.equals(encodePassword(password,salt));
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
