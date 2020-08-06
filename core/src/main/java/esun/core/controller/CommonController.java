package esun.core.controller;

import com.sun.istack.Nullable;
import esun.core.annotation.LoginRequire;
import esun.core.service.CommonService;
import esun.core.utils.FileServerConfigUtil;
import esun.core.utils.ResultUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("common")
public class CommonController {



    @Autowired
    CommonService commonService;


    /**
     * 上传文件
     * @param file
     * @return
     */
    @RequestMapping("upload")
    public ResultUtil uploadImg(@RequestParam("file") MultipartFile file){
        if (file.getSize() / 1024 / 1024 > 1) {
            return ResultUtil.error("上传文件过大");
        }
        return commonService.upload(file);

    }

    /**
     * 获取在线人数
     * @return
     */
    @RequestMapping("loginNum")
    public ResultUtil getOnlineUserNum(){
        return commonService.loginNum();
    }
    /**
     * 用户登入
     * @param name
     * @param password
     * @return
     */
//    @Async
    @RequestMapping("login")
    public ResultUtil login(@RequestParam("name")String name, @RequestParam("password")String password){
        //检查密码长度
        if(password.length()<6 || password.length()>20){
            return ResultUtil.error("密码长度必须为6到20之间");
        }
        return commonService.login(name,password);
    }

    /**
     * 登出
     * @param
     * @return
     */
    @LoginRequire
    @RequestMapping("logout")
    public ResultUtil logout(@RequestParam("logoutName") String name){
       return commonService.logout(name);
    }

    /**
     * 获取在线人数名单
     * @return
     */
    @RequestMapping("queryLoginUser")
    public ResultUtil queryLoginUser(){
       return commonService.queryLoginUser();
    }

    /**
     * 用户注册
     * @param name
     * @param password
     * @return
     */
    @RequestMapping("register")
    public ResultUtil register(@RequestParam("name")String name,
                           @RequestParam("password")String password,
                           @RequestParam("email") String email,
                           @RequestParam("telephone") @Nullable  String telephone){
        //检查密码长度
        if(password.length()<6 || password.length()>20){
            return ResultUtil.error("密码长度必须为6到20之间");
        }
        //验证邮箱是否合法
        String reg= "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern=Pattern.compile(reg);
        Matcher matcher=pattern.matcher(email);
        if (!matcher.matches()){
            return ResultUtil.error("该邮箱不合法");
        }

        return commonService.register(name,password,email,telephone);
    }

    public ResultUtil batchRegister(@RequestParam("file")MultipartFile file){
        try {
            InputStream inputStream=file.getInputStream();
            Workbook workbook= WorkbookFactory.create(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultUtil.ok();
    }
}
