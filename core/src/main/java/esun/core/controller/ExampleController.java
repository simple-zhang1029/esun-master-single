package esun.core.controller;


import com.google.inject.internal.cglib.core.$KeyFactory;
import com.sun.istack.Nullable;
import esun.core.annotation.LoginRequire;
import esun.core.annotation.Router;
import esun.core.constant.Message;
import esun.core.exception.ClientException;
import esun.core.utils.FTPUtils;
import esun.core.utils.MessageUtil;
import esun.core.utils.ResultUtil;
import esun.core.service.ExampleService;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.net.ftp.FtpClient;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//示例控制器,Controller层只做基本的数据处理和校验，不允许在Controller层中写业务逻辑


//声明为RestfulController，自动进行注入,如不声明则接口无法生效
@RestController
//声明接口,格式为"/版本号/接口"
@RequestMapping("/v1/example/")
public class ExampleController {

    //调用注入的Service
    @Autowired
    ExampleService exampleService;

    //创建日志对象
    private static Logger logger= LoggerFactory.getLogger(ExampleController.class);

    //使用Restful命名风格的标签，只允许指定类型的请求调用接口
    @PostMapping("login")
    //使用Router标签指定用户访问权限,拥有该权限才可请求此接口,否者返回错误,router权限在存储在数据库中
//    @Router(name = "user:login")
    //所有接口的返回类型固定为ResultUtil封装类
    //请求参数使用@RequestParam标签获取。
    public ResultUtil login(@RequestParam("name")String name,
                            @RequestParam("password")String password){

        //检查密码长度
        if(password.length()<6 || password.length()>20){
            String message=MessageUtil.getMessage(Message.PASSWORD_NOT_STANDARD.getCode());
            return ResultUtil.error(message);
        }
        return exampleService.login(name,password);
    }

    /**
     * 注册接口
     * @param name
     * @param password
     * @param email
     * @param telephone
     * @return
     * @author john.xiao
     */
    @PostMapping("register")
    public ResultUtil register(@RequestParam("name")String name,
                               @RequestParam("password")String password,
                               @RequestParam("email")String email,
                               //通过required来设置该参数是否为必须，通过设置@Nullable设置是否允许该参数为null值
                               @RequestParam(value = "telephone",required = false) @Nullable String telephone ){
        //检查密码长度
        if(password.length()<6 || password.length()>20){
            String message=MessageUtil.getMessage(Message.PASSWORD_NOT_STANDARD.getCode());
            return ResultUtil.error(message);
        }
        //验证邮箱是否合法
        String reg= "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern=Pattern.compile(reg);
        Matcher matcher=pattern.matcher(email);
        if (!matcher.matches()){
            String message= MessageUtil.getMessage(Message.EMAIL_NOT_STANDARD.getCode());
            return ResultUtil.error(message);
        }
        return exampleService.register(name,password,email,telephone);
    }
    /**
     * 上传文件
     * @param file 传入的文件
     * @return
     * @author john.xiao
     */
    @LoginRequire
    @PostMapping("upload")
//    @Router(name = "file:upload")
    //上传文件格式使用MultipartFile
    public ResultUtil uploadImg(@RequestParam("file") MultipartFile file){
        if (file.getSize() / 1024 / 1024 > 1) {
            return ResultUtil.error("上传文件过大");
        }
        return exampleService.upload(file);
    }

    /**
     * 登录用户名单
     * @return
     * @author john.xiao
     */
    //使用loginRequire标签设置该接口是否要求登入后才能请求
//    @LoginRequire
//    @GetMapping("loggedList")
//    public ResultUtil loggedList(){
//        return exampleService.loggedList();
//    }


    /**
     * @author john.xiao
     * @param username 查询用户名
     * @return
     */
    @LoginRequire
    @GetMapping("userInfo")
//    @Router(name = "user:info:get")
    public ResultUtil getUserInfo(@RequestParam("username") String username){
        return exampleService.getUserInfo(username);
    }

    /**
     * 删除用户
     * @param username
     * @return
     * @author john.xiao
     */
    @LoginRequire
    @DeleteMapping("userInfo")
//    @Router(name = "user:info:delete")
    public ResultUtil deleteUserInfo(@RequestParam("username") String username){
        return exampleService.deleteUserInfo(username);
    }

    /**
     * 更新用户信息
     * @param userId
     * @param username
     * @param language
     * @param email
     * @param type
     * @param phone
     * @param country
     * @param isActive
     * @param depart
     * @param post
     * @param qqNum
     * @return
     * @author john.xiao
     */
    @LoginRequire
    @PostMapping("userInfo")
//    @Router(name = "user:info:update")
    public ResultUtil updateUserInfo(@RequestParam("userId") String userId,
                                     @RequestParam("username") String username,
                                     @RequestParam(value = "password",required = false,defaultValue = "") String password,
                                     @RequestParam(value = "language") String language,
                                     @RequestParam(value = "email")  String email,
                                     @RequestParam(value = "type") String type,
                                     @RequestParam(value = "phone")  String phone,
                                     @RequestParam(value = "country") String country,
                                     @RequestParam(value = "isActive" )boolean isActive,
                                     @RequestParam(value = "depart")String depart,
                                     @RequestParam(value = "post")String post,
                                     @RequestParam(value = "qqNum")String qqNum){
        if(!StringUtils.isBlank(password)){
            return exampleService.updateUserInfo(userId,username,password,language,email,type,phone,country,isActive,depart,post,qqNum);
        }
        return exampleService.updateUserInfo(userId,username,language,email,type,phone,country,isActive,depart,post,qqNum);
    }

    /**
     * 插入用户信息
     * @param email
     * @return
     * @author john.xiao
     */
    @LoginRequire
    @PutMapping("userInfo")
//    @Router(name = "user:info:insert")
    public ResultUtil insertUserInfo(@RequestParam("userId") String userId,
                                     @RequestParam("username") String username,
                                     @RequestParam("password") String password,
                                     @RequestParam(value = "language",required = false,defaultValue = "CH") String language,
                                     @RequestParam(value = "email")  String email,
                                     @RequestParam(value = "type",required = false,defaultValue = "外部用户") String type,
                                     @RequestParam(value = "phone")  String phone,
                                     @RequestParam(value = "country",required = false,defaultValue = "CH") String country,
                                     @RequestParam(value = "isActive",required = false,defaultValue ="false" )boolean isActive,
                                     @RequestParam(value = "depart",required = false,defaultValue = "DEFAULT")String depart,
                                     @RequestParam(value = "post",required = false,defaultValue = "DEFAULT")String post,
                                     @RequestParam(value = "qqNum",required = false,defaultValue = " ")String qqNum){
        return exampleService.insertUserInfo(userId,username,password,language,email,type,phone,country,isActive,depart,post,qqNum);
    }

    /**
     * 批量注册
     * @param file
     * @return
     * @author john.xiao
     */
    @LoginRequire
    @PostMapping("batchRegister")
    public ResultUtil batchRegister(@RequestParam("file")MultipartFile file){
        Workbook workbook = null;
        try {
            InputStream inputStream=file.getInputStream();
            //使用Workbook类处理Excel表格
            workbook= WorkbookFactory.create(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return exampleService.batchRegister(workbook);
    }

    /**
     * 分页获取用户信息列表
     * @param pageIndex 页数。默认值为1
     * @param pageSize  每页大小。默认值为10
     * @param criteria 排序条件
     * @param sort 排序顺序 0:正序 1:倒序
     * @param username 查询用户名
     * @return
     * @author john.xiao
     * @date 2020-09-21 14:41
     */
    @LoginRequire
    @GetMapping("userInfoList")
    public ResultUtil getUserInfoList(@RequestParam(value = "pageIndex",required = false,defaultValue = "1")int pageIndex,
                                      @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
                                      @RequestParam(value = "username",required = false,defaultValue = "")String username,
                                      @RequestParam(value = "criteria",required = false,defaultValue = "userId")String criteria,
                                      @RequestParam(value = "sort",required = false,defaultValue = "0")int sort){
        String tableParam;
        switch (criteria){
            case "userId":
                tableParam="user_userid";
                break;
            case "username":
                tableParam="user_name";
                break;
            case "language":
                tableParam="user_lang";
                break;
            case "email":
                tableParam="user_mail_address";
                break;
            case "phone":
                tableParam="user_phone";
                break;
            case "qqNum":
                tableParam="user_qqnum";
                break;
            default:
                tableParam="user_userid";
        }
        return  exampleService.getUserInfoList(pageIndex,pageSize,username,tableParam,sort);
    }

    /**
     * 修改密码
     * @param name
     * @param password
     * @param newPassword
     * @return
     * @author john.xiao
     */
    @LoginRequire
    @PostMapping("password")
    public ResultUtil updatePassword(@RequestParam("username")String name,
                                     @RequestParam("password")String password,
                                     @RequestParam("newPassword")String newPassword){
        //检测密码是否相同
        if(password.equals(newPassword)){
          String message=MessageUtil.getMessage(Message.PASSWORD_IS_SAME.getCode());
          logger.error(message);
          return ResultUtil.error(message);
        }
        return exampleService.updatePassword(name,password,newPassword);
    }

    /**
     * 通过Excel文件批量插入或更新数据
     * @param file
     * @return
     * @author john.xiao
     */
    @LoginRequire
    @PostMapping("batchUserInfo")
    public ResultUtil batchUserInfoInsertOrUpdate(@RequestParam("file") MultipartFile file){
        //初步处理Excel文件
        Workbook workbook=null;
        try {
            InputStream inputStream=file.getInputStream();
            workbook=WorkbookFactory.create(inputStream);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return exampleService.batchUserInfoInsertOrUpdate(workbook);
    }

    /**
     * Excel批量删除用户信息
     * @param file
     * @return
     * @author john.xiao
     */
    @LoginRequire
    @DeleteMapping("batchUserInfoWithExcel")
    public ResultUtil batchUserInfoDeleteWithExcel(@RequestParam("file")MultipartFile file){

        //初步处理Excel文件
        Workbook workbook=null;
        try {
            InputStream inputStream=file.getInputStream();
            workbook=WorkbookFactory.create(inputStream);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return exampleService.batchUserInfoDeleteWithExcel(workbook);
    }

    /**
     * 批量删除用户信息
     * @param list 传入的用户信息列表
     * @return
     * @author john.xiao
     */
    @LoginRequire
    @DeleteMapping("batchUserInfo")
    public ResultUtil batchUserInfo(@RequestParam("list")String list ){
      //使用Json解析转换列表
      List<Map<String,Object>> jsonArray=JSONArray.fromObject(list);
      return exampleService.batchUserInfoDelete(jsonArray);
    }

    /**
     * 通过username导出用户信息
     * @param username
     * @return
     * @author john.xiao
     */
    @LoginRequire
    @GetMapping("batchUserInfo")
    public ResultUtil exportUserInfo(@RequestParam(value = "username",required = false,defaultValue ="" )String username){
        return exampleService.exportUserInfo(username);
    }
}

