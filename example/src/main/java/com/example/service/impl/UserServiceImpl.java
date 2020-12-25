package com.example.service.impl;

import com.example.constant.Message;
import com.example.service.DbHelperService;
import com.example.service.TokenService;
import com.example.service.UserService;
import com.example.utils.*;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.joda.time.DateTime;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service类，进行业务逻辑的具体实现
 * 所有Service的方法通过相应的接口类获取,不允许在实现类中写单独的方法
 * 声明该类为Service，进行自动注入
 * 用户信息业务
 * @author john.xiao
 * @date 2020-12-24 16:04
 */
@Service
public class UserServiceImpl implements UserService {

    public static final String CODE = "code";

    @Autowired
    @Lazy
    DbHelperService dbHelperService;

    @Autowired
    @Lazy
    TokenService tokenService;

    private final static String LOGIN_SET="loginSet";

    private final static String DEFAULT_PRODUCT="default";

    private final static String DATASOURCE_POSTGRES="postgres";

    @Value("${file.diskPath}")
    String diskPath;


    @Value("${ftp.url}")
    String ftpUrl;

    @Value("${ftp.port}")
    int ftpPort;

    @Value(("${ftp.username}"))
    String ftpUsername;

    @Value(("${ftp.password}"))
    String ftpPassword;


    private static Logger logger= LoggerFactory.getLogger(ExampleServiceImpl.class);



    /**
     * 根据用户ID删除用户，该方法为精确查询
     * @param userId 用户Id
     * @return 结果封装类
     */
    @Override
    public ResultUtil deleteUserInfo(String userId) {
        String message;
        String sql="delete from user_mstr where lower(user_userid)=lower('"+userId+"')";
        ResultUtil result=dbHelperService.delete(sql, DATASOURCE_POSTGRES);
        if(HttpStatus.OK.value()!= (int)result.get(CODE)){
            message=MessageUtil.getMessage(Message.USER_INFO_DELETE_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.USER_INFO_DELETE_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message);
    }

    /**
     * 根据用户Id更新用户信息
     * @param userId 用户Id
     * @param username 用户名
     * @param language 语言
     * @param email 电子邮箱
     * @param type 用户类型
     * @param phone 电话号码
     * @param country 国家
     * @param isActive 是否可用
     * @param depart 部门
     * @param post 岗位人
     * @param qqNum QQ号码
     * @return 结果封装类
     */
    @Override
    public ResultUtil updateUserInfo(String userId, String username, String language, String email, String type, String phone, String country, boolean isActive, String depart, String post, String qqNum) {
        DateTime dateTime=new DateTime();
        String changeTime=dateTime.toString("yyyyMMdd");
        String message;
        String sql="update user_mstr set user_name='"+username+"',user_lang='"+language+"', user_mail_address='"+email+"',user_last_chg_date='"+changeTime+"'," +
                "user_country='"+country+"',user_actived="+isActive+",user_depart='"+depart+"',user_post='"+post+"',user_type='"+type+"', "+
                "user_phone='"+phone+"',user_qqnum='"+qqNum+"' "+
                "where lower(user_userid)=lower('"+userId+"') ;";
        ResultUtil result=dbHelperService.update(sql,DATASOURCE_POSTGRES);
        if(HttpStatus.OK.value() != (int)result.get(CODE)){
            message=MessageUtil.getMessage(Message.USER_INFO_UPDATE_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.USER_INFO_UPDATE_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message);
    }

    /**
     * 根据用户Id更新用户信息（修改密码）
     * @param userId 用户Id
     * @param username 用户名
     * @param language 语言
     * @param email 电子邮箱
     * @param type 用户类型
     * @param phone 电话号码
     * @param country 国家
     * @param isActive 是否可用
     * @param depart 部门
     * @param post 岗位人
     * @param qqNum QQ号码
     * @param password 用户密码
     * @return 结果封装类
     */
    @Override
    public ResultUtil updateUserInfo(String userId, String username, String password, String language, String email, String type, String phone, String country, boolean isActive, String depart, String post, String qqNum) {
        DateTime dateTime=new DateTime();
        String changeTime=dateTime.toString("yyyy-MM-dd");
        String message;
        Md5Util md5Util=new Md5Util();
        String encodePassword=md5Util.encodePassword(password);
        String salt=md5Util.getSalt();
        String sql="update user_mstr set user_name='"+username+"',user_lang='"+language+"', user_mail_address='"+email+"',user_last_chg_date='"+changeTime+"'," +
                "user_country='"+country+"',user_actived="+isActive+",user_depart='"+depart+"',user_post='"+post+"',user_type='"+type+"', "+
                "user_phone='"+phone+"',user_qqnum='"+qqNum+"',user_password = '"+encodePassword+"',user_salt = '"+salt+"' "+
                "where lower(user_userid)=lower('"+userId+"') ;";
        ResultUtil result=dbHelperService.update(sql,DATASOURCE_POSTGRES);
        if(HttpStatus.OK.value() != (int)result.get(CODE)){
            message=MessageUtil.getMessage(Message.USER_INFO_UPDATE_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.USER_INFO_UPDATE_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message);
    }

    /**
     * 根据用户Id插入用户信息
     * @param userId 用户Id
     * @param username 用户名
     * @param language 语言
     * @param email 电子邮箱
     * @param type 用户类型
     * @param phone 电话号码
     * @param country 国家
     * @param isActive 是否可用
     * @param depart 部门
     * @param post 岗位人
     * @param qqNum QQ号码
     * @param password 用户密码
     * @return 结果封装类
     */
    @Override
    public ResultUtil insertUserInfo(String userId, String username, String password, String language, String email, String type, String phone, String country, boolean isActive, String depart, String post, String qqNum) {
        String message;
        DateTime date=new DateTime();
        String changeTime=date.toString("yyyyMMdd");
        Md5Util md5Util=new Md5Util();
        String encodePassword=md5Util.encodePassword(password);
        String salt=md5Util.getSalt();
        String sql="insert into user_mstr "+
                "(user_userid,user_name,user_lang,user_password,user_last_chg_date,user_mail_address,user_type," +
                "user_country,user_actived,user_depart,user_post,user_phone,user_qqnum,user_salt) " +
                "values('"+userId+"','"+username+"','"+language+"','"+encodePassword+"','"+changeTime+"','"+email+"','"+type+"'," +
                "'"+country+"',"+isActive+",'"+depart+"','"+post+"','"+phone+"','"+qqNum+"','"+salt+"');";
        ResultUtil result=dbHelperService.insert(sql,DATASOURCE_POSTGRES);
        if(HttpStatus.OK.value()!= (int)result.get(CODE)){
            message=MessageUtil.getMessage(Message.USER_INFO_INSERT_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.USER_INFO_INSERT_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message);
    }

    /**
     * 分页模糊查询用户，
     * @param pageIndex 页码数
     * @param pageSize 分页大小
     * @param userId 用户Id
     * @param criteriaList  排序列表
     * @return 结果封装类
     */
    @Override
    public ResultUtil getUserInfoList(int pageIndex, int pageSize, String userId, List<Map<String, Object>> criteriaList) {
        String sortString=getSortString(criteriaList);
        String sql="select user_name as \"username\" ,user_userid as \"userId\" ,user_phone as \"phone\" ," +
                    "user_mail_address as \"email\" ,user_lang as \"language\", user_type as \"type\", " +
                    "user_country as \"country\", user_actived as \"isActive\" ,user_depart as \"depart\"," +
                    "user_post as \"post\" , user_qqnum as \"qqNum\" " +
                    " from user_mstr where user_userid ilike '%"+userId+"%' order by "+sortString+";";
        String message;
        ResultUtil result=dbHelperService.selectPage(sql,DATASOURCE_POSTGRES,pageIndex,pageSize);
        if(HttpStatus.OK.value()!= (int)result.get(CODE)){
            message=MessageUtil.getMessage(Message.USER_INFO_GET_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        ArrayList list= (ArrayList) result.get("result");
        //获取总页数
        int pageCount= (int) result.get("pageCount");
        //获取总条数
        int count= (int) result.get("count");
        message=MessageUtil.getMessage(Message.USER_INFO_GET_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message).put("result",list).put("pageCount",pageCount).put("count",count);
    }


    /**
     * 获取排序条件字符串
     * @param criteriaList 排序列表
     * @return 排序后的字符
     * @author john.xiao
     * @date 2020-12-17 11-27
     */
    private String getSortString(List<?> criteriaList){
        StringBuilder criteriaBuilder=new StringBuilder();
        if(criteriaList.size()>0){
            for (int i = 0; i < criteriaList.size(); i++) {
                Map<String,Object> listMap= (Map<String, Object>) criteriaList.get(i);
                Optional<Object> sort=Optional.ofNullable(listMap.get("sort"));
                Optional<Object> criteria=Optional.ofNullable(listMap.get("criteria"));
                criteriaBuilder.append(criteria.orElse("order_id"));
                if (!"0".equals(sort.orElse("0"))){
                    criteriaBuilder.append(" desc");
                }
                criteriaBuilder.append(" ,");
            }
        }
        else {
            //设置默认排序项
            criteriaBuilder.append("user_userid");
        }
        return  criteriaBuilder.toString();
    }

    /**
     * 修改密码
     * @param userId 用户Id
     * @param password 用户密码
     * @param newPassword 新密码
     * @return 结果封装类
     */
    @Override
    public ResultUtil updatePassword(String userId, String password, String newPassword) {
        Md5Util md5Util=new Md5Util();
        String message;
        //获取盐
        String saltSql="select user_salt,user_password from user_mstr where lower(user_userid)= lower('"+userId+"');";
        ResultUtil saltResult=dbHelperService.select(saltSql,DATASOURCE_POSTGRES);
        if(HttpStatus.OK.value()!= (int)saltResult.get(CODE)){
            message=MessageUtil.getMessage(Message.GET_SALT_FAIL.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.GET_SALT_SUCCESS.getCode());
        logger.info(message);
        ArrayList<Map<String,Object>> list= (ArrayList) saltResult.get("result");
        String userSalt=list.get(0).get("user_salt").toString();
        String userPassword=list.get(0).get("user_password").toString();
        //校验密码是否相同
        if(!md5Util.checkPassword(password,userSalt,userPassword)){
            message=MessageUtil.getMessage(Message.PASSWORD_ERROR.getCode());
            logger.error(userId+":"+message);
            return ResultUtil.error(message);
        }
        //使用MD5进行加密
        String encodePassword= md5Util.encodePassword(newPassword);
        String salt=md5Util.getSalt();
        String sql="update user_mstr set user_password= '"+encodePassword+"',user_salt= '"+salt+"'"+
                "where lower(user_userid) = lower('"+userId+"') ;";
        ResultUtil result=dbHelperService.update(sql,DATASOURCE_POSTGRES);
        if(HttpStatus.OK.value()!= (int)result.get(CODE)){
            message=MessageUtil.getMessage(Message.UPDATE_PASSWORD_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.UPDATE_PASSWORD_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message);
    }

    /**
     * 批量删除用户信息
     * @param list 用户信息列表
     * @return 结果封装类
     */
    @Override
    public ResultUtil batchUserInfoDelete(List<Map<String, Object>> list) {
        String message;
        List<Map<String,Object>> resultList=new ArrayList<>();
        Map<String,Object> resultMap=new HashMap<>();
        for (int i = 0; i <list.size() ; i++) {
            String userId=list.get(i).get("userId").toString();
            resultMap.put("username",userId);
            //检查用户是否存在
            if (!checkUserExist(userId)){
                message=MessageUtil.getMessage(Message.USER_NOT_EXIST.getCode());
                resultMap.put("code",HttpStatus.BAD_REQUEST.value());
                resultMap.put("msg",message);
            }
            else {
                ResultUtil deleteResult=deleteUserInfo(userId);
                resultMap.put("code",deleteResult.get("code"));
                resultMap.put("msg",deleteResult.get("msg"));
            }
            resultList.add(resultMap);
        }
        return ResultUtil.ok().put("msg","执行完毕").put("result",resultList);
    }

    /**
     * 导入Excel更新或添加用户信息
     * @param workbook Excel文件
     * @return 结果封装类
     */
    @Override
    public ResultUtil batchUserInfoInsertOrUpdate(Workbook workbook) {
        String defaultPassword= "123456";
        //获取Excel文档第一个表格
        Sheet sheet=workbook.getSheetAt(0);
        //获取表格标题列表
        List titleList= PoiUtils.getTitleList(PoiUtils.getRow(sheet,0));
        //请求结果列表
        List<Map<String,Object>> resultList=new ArrayList<>();
        Map<String,Object> userInfo;
        Optional<Object> username;
        Optional<Object> userId;
        Optional<Object> language;
        Optional<Object> email;
        Optional<Object> type;
        Optional<Object> phone;
        Optional<Object> country;
        Optional<Object> isActive;
        Optional<Object> depart;
        Optional<Object> post;
        Optional<Object> qqNum;


        //循环遍历用户信息写入列表
        for (int i = 1; i <=sheet.getLastRowNum() ; i++) {
            //获取相应行的数据，转换为list
            userInfo=PoiUtils.getRowData(PoiUtils.getRow(sheet,i),titleList);
            username=Optional.ofNullable(userInfo.get("username"));
            userId=Optional.ofNullable(userInfo.get("userId"));
            language=Optional.ofNullable(userInfo.get("language"));
            email=Optional.ofNullable(userInfo.get("email"));
            type=Optional.ofNullable(userInfo.get("type"));
            phone=Optional.ofNullable(userInfo.get("phone"));
            country=Optional.ofNullable(userInfo.get("country"));
             isActive= Optional.ofNullable(userInfo.get("isActive"));
            depart=Optional.ofNullable(userInfo.get("depart"));
            post=Optional.ofNullable(userInfo.get("post"));
            qqNum=Optional.ofNullable(userInfo.get("qqNum"));
            //用户运行结果Map
            Map<String,Object> resultMap=new HashMap<>();
            resultMap.put("userId",userId);
            //查看该用户是否存在
            if (!checkUserExist(userId.orElse("").toString())){
                ResultUtil insertResult= insertUserInfo(userId.orElse("").toString(),username.orElse("").toString(),defaultPassword,language.orElse("").toString(),email.orElse("").toString(),
                        type.orElse("").toString(),phone.orElse("").toString(),country.orElse("").toString(),Boolean.parseBoolean(isActive.orElse("false").toString()),depart.orElse("").toString(),
                        post.orElse("").toString(),qqNum.orElse("").toString());
                resultMap.put("code",insertResult.get("code"));
                resultMap.put("msg",insertResult.get("msg"));
            }
            else {
                ResultUtil updateResult=updateUserInfo(userId.orElse("").toString(),username.orElse("").toString(),language.orElse("").toString(),email.orElse("").toString(),
                        type.orElse("").toString(),phone.orElse("").toString(),country.orElse("").toString(),Boolean.parseBoolean(isActive.orElse("false").toString()),
                        depart.orElse("").toString(),post.orElse("").toString(),qqNum.orElse("").toString());
                resultMap.put("code",updateResult.get("code"));
                resultMap.put("msg",updateResult.get("msg"));
            }
            resultList.add(resultMap);
        }
        return ResultUtil.ok().put("result",resultList);
    }

    /**
     * 模糊查询后导出用户信息
     * @param userId 用户Id
     * @return 结果封装类
     */
    @Override
    public ResultUtil exportUserInfo(String userId) {
        String sql="select user_name as \"username\" ,user_userid as \"userId\" ,user_phone as \"phone\" ," +
                "user_mail_address as \"email\" ,user_lang as \"language\", user_type as \"type\", " +
                "user_country as \"country\", user_actived as \"isActive\" ,user_depart as \"depart\"," +
                "user_post as \"post\" , user_qqnum as \"qqNum\" " +
                " from user_mstr where user_userid ilike '%"+userId+"%';";
        String message;
        ResultUtil result=dbHelperService.select(sql,DATASOURCE_POSTGRES);
        if(HttpStatus.OK.value()!= (int)result.get(CODE)){
            message=MessageUtil.getMessage(Message.USER_INFO_GET_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        ArrayList list= (ArrayList) result.get("result");
        //判断用户是否存在
        if (list.size()==0){
            message=MessageUtil.getMessage(Message.USER_NOT_EXIST.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        List<String> titleList=new ArrayList<>();
        titleList.add("userId");
        titleList.add("username");
        titleList.add("phone");
        titleList.add("email");
        titleList.add("language");
        titleList.add("type");
        titleList.add("country");
        titleList.add("isActive");
        titleList.add("depart");
        titleList.add("post");
        titleList.add("qqNum");

        String file=ExcelUtils.createMapListExcel(list,diskPath,titleList);
        String ftpFile= RandomStringUtils.randomAlphanumeric(32)+".xls";
        String ftpPath="/userInfoExport/";
        //FTP上传文件
        FTPUtils ftpUtils=new FTPUtils();
        ftpUtils.setHostname(ftpUrl);
        ftpUtils.setPort(ftpPort);
        ftpUtils.setUsername(ftpUsername);
        ftpUtils.setPassword(ftpPassword);
        if(!ftpUtils.uploadFile(ftpPath,ftpFile,file)){
            message=MessageUtil.getMessage(Message.UPLOAD_FTP_FAIL.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.UPLOAD_FTP_SUCCESS.getCode());
        logger.info(message);
        String path="ftp://"+ftpUrl+ftpPath+ftpFile;
        return ResultUtil.ok().put("msg",message).put("result",path);
    }

    /**
     * 检查用户是否存在
     * @param userId 用户Id
     * @return 用户是否存在
     */
    private boolean checkUserExist(String userId){
        String sql ="select 1 from  user_mstr  where lower(user_userid)=lower('"+userId+"')";
        ResultUtil result=dbHelperService.select(sql,DATASOURCE_POSTGRES);
        String meesage;
        if(HttpStatus.OK.value() != (int)result.get(CODE)){
            meesage=MessageUtil.getMessage(Message.USER_INFO_GET_ERROR.getCode());
            logger.error(meesage);
        }
        ArrayList list= (ArrayList) result.get("result");
        return list.size() > 0;
    }
}
