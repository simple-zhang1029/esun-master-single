package esun.core.service.impl;

import com.google.common.io.Files;
import com.sun.istack.Nullable;
import esun.core.constant.Message;
import esun.core.exception.CustomHttpException;
import esun.core.service.DbHelperService;
import esun.core.service.ExampleService;
import esun.core.service.TokenService;
import esun.core.utils.*;
import jdk.nashorn.tools.Shell;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

//Service类，进行业务逻辑的具体实现
//所有Service的方法通过相应的接口类获取,不允许在实现类中写单独的方法
//声明该类为Service，进行自动注入
@Service
public class ExampleServiceImpl implements ExampleService {
    @Autowired
    @Lazy
    DbHelperService dbHelperService;

    @Autowired
    @Lazy
    TokenService tokenService;
    @Autowired
    //进行懒加载
    @Lazy
    private RedisTemplate redisTemplate;

    //声明静态常量，所有常量都需实现声明使用，不允许出现魔法值
    private final static String LOGIN_SET="loginSet";

    //默认产品名
    private final static String DEFAULT_PRODUCT="default";

    //存储路径，使用Value标签通过配置文件获取
    @Value("${file.diskPath}")
    String diskPath;
    //用户表,从配置文件中获取
    @Value("${user.table}")
    String userTable;

    @Value("${message.table}")
    String message_table;

    //路由表
    @Value("${router.table}")
    String router_table;

    //用户组路由表
    @Value("${router.group.table}")
    String router_group_table;

    @Value("${postgres_user_table}")
    String postgres_user_table;

    @Value("${ftp.url}")
    String ftpUrl;

    @Value("${ftp.port}")
    int ftpPort;

    @Value(("${ftp.username}"))
    String ftpUsername;

    @Value(("${ftp.password}"))
    String ftpPassword;



    //创建日志对象
    private static Logger logger= LoggerFactory.getLogger(ExampleServiceImpl.class);

    /**
     * 登入
     * @param name
     * @param password
     * @return
     */
    @Override
    public ResultUtil login(String name, String password) {
        // 获取用户信息
        //SQL语句
        String sql = "select user_password,user_salt from "+postgres_user_table+" where user_name= '"+name+"' ";
        //结果信息
        String message;
        //调用DbHelper中间件服务，所有对数据库的请求均使用该中间件调用
        ResultUtil result=dbHelperService.select(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            //使用MessageUtil.getMessage方法从数据库中获取信息，不允许自己写返回信息
            //返回信息的code在Message枚举类中创建，在数据库中插入相应语言版本的返回信息
            message=MessageUtil.getMessage(Message.USER_INFO_GET_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        ArrayList list= (ArrayList) result.get("result");
        HashMap resultmap= (HashMap) list.get(0);
        if(resultmap.size()==0){
            message=MessageUtil.getMessage(Message.USER_NOT_EXIST.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        String salt=resultmap.get("user_salt").toString();
        Md5Util md5Util=new Md5Util();
        if(!md5Util.checkPassword(password,salt,resultmap.get("user_password").toString())){
            message=MessageUtil.getMessage(Message.PASSWORD_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }

        // 检验是否登入
        //使用redisTemplate.opsForSet().add()方法向redis插入数据
//        if(redisTemplate.opsForSet().add(LOGIN_SET,name)!=1){
//            message=MessageUtil.getMessage(Message.USER_LOGGED.getCode());
//            logger.error(message);
//            return ResultUtil.error(message);
//        }
        //更新token
        ResultUtil tokenResult=tokenService.updateToken(name);
        if(HttpStatus.OK.value()!= (int)tokenResult.get("code")){
            message=MessageUtil.getMessage(Message.TOKEN_UPDATE_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        //获取token
        String token=tokenResult.get("token").toString();
        //ResultUtil.put()传输返回结果
        message=MessageUtil.getMessage(Message.LOGIN_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok(message).put("token",token);
    }

    /**
     * 注册
     * @param name
     * @param password
     * @param email
     * @param telephone
     * @return
     */
    @Override
    public ResultUtil register(String name, String password, String email, String telephone) {
        //检测是否用户是否存在
        String sql="select 1 from "+userTable+" where name='"+name+"'";
        String meesage;
        ResultUtil result=dbHelperService.select(sql,DEFAULT_PRODUCT);
        if(HttpStatus.OK.value() != (int)result.get("code")){
            meesage=MessageUtil.getMessage(Message.USER_INFO_GET_ERROR.getCode());
            logger.error(meesage);
            return ResultUtil.error(meesage);
        }
        ArrayList list= (ArrayList) result.get("result");
        if(list.size()>0){
            meesage=MessageUtil.getMessage(Message.USER_IS_EXISTED.getCode());
            logger.error(meesage);
            return ResultUtil.error(meesage);
        }
        //进行注册
        String insertSql="insert into "+userTable+"(name,password,email,telephone) " +
                "values('"+name+"','"+password+"','"+email+"','"+telephone+"')";
        ResultUtil insertResult=dbHelperService.insert(insertSql, DEFAULT_PRODUCT);
        if(HttpStatus.OK.value()!= (int)insertResult.get("code")) {
            meesage=MessageUtil.getMessage(Message.REGISTER_ERROR.getCode());
            logger.error(meesage);
            return ResultUtil.error(meesage);
        }
        meesage=MessageUtil.getMessage(Message.REGISTER_SUCCESS.getCode());
        logger.info(meesage);
        return ResultUtil.ok(meesage);
    }

    /**
     * 上传文件
     * @param file
     * @return
     */
    @Override
    public ResultUtil upload(MultipartFile file) {
        String message;
        DateTime dateTime = new DateTime();
        //设定日期样式
        String day = dateTime.toString("yyyyMMdd");
        String path = "";
        //根据日期加随机数创建文件
        String filePath =   diskPath + day + "/" + RandomStringUtils.randomAlphanumeric(32) +  "." + FilenameUtils.getExtension(file.getOriginalFilename());
        try {
            //在保存路径中创建文件
            File saveFile = new File(filePath);
            if (saveFile.exists()) {
                message=MessageUtil.getMessage(Message.FILE_EXISTED.getCode());
                logger.error(message);
                return ResultUtil.error(message);
            }
            Files.createParentDirs(saveFile);
            Files.write(file.getBytes(), saveFile);
            //获取文件的绝对路径
            String absolutePath = saveFile.getAbsolutePath();
            path = absolutePath.substring(diskPath.length());
            //替换格式
            path = path.replaceAll("\\\\", "/");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResultUtil.ok().put("path",path);
    }

    /**
     * 登入用户名单
     * @return
     */
    @Override
    public ResultUtil loggedList() {
        Set<String> loginSet=redisTemplate.opsForSet().members(LOGIN_SET);
        List<Map<String,Object>> resultList=new ArrayList<>();
        Iterator<String> iterator = loginSet.iterator();
        while (iterator.hasNext()){
            Map resultMap=new HashMap();
            resultMap.put("name",iterator.next());
            resultList.add(resultMap);
        }
        return ResultUtil.ok().put("list",resultList);
    }

    /**
     * 获取用户信息
     * @param name
     * @return
     */
    @Override
    public ResultUtil getUserInfo(String name) {
         String sql="select user_name as \"username\" ,user_userid as \"userId\" ,user_phone as \"phone\" ," +
                    "user_mail_address as \"email\" ,user_lang as \"language\", user_type as \"type\", " +
                    "user_country as \"country\", user_actived as \"isActive\" ,user_depart as \"depart\"," +
                    "user_post as \"post\" , user_qqnum as \"qqNum\" " +
                    " from "+postgres_user_table+" where user_name = '"+name+"';";
        String message;
        ResultUtil result=dbHelperService.select(sql,"postgres_test");
        ArrayList list= (ArrayList) result.get("result");
        if(list.size()==0){
            message=MessageUtil.getMessage(Message.USER_NOT_EXIST.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        HashMap resultMap= (HashMap) list.get(0);
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            message=MessageUtil.getMessage(Message.USER_INFO_GET_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.USER_INFO_GET_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message).put("result",resultMap);
    }

    /**
     * 删除用户信息
     * @param name
     * @return
     */
    @Override
    public ResultUtil deleteUserInfo(String name) {
        String message;
        String sql="delete from "+postgres_user_table+" where user_name='"+name+"'";
        ResultUtil result=dbHelperService.delete(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            message=MessageUtil.getMessage(Message.USER_INFO_DELETE_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.USER_INFO_DELETE_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message);
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
     */
    @Override
    public ResultUtil updateUserInfo(String userId, String username, String language, String email,String type, String phone, String country, boolean isActive, String depart, String post, String qqNum) {
        DateTime dateTime=new DateTime();
        String changeTime=dateTime.toString("yyyyMMdd");
        String message;
        String sql="update "+postgres_user_table+" set user_name='"+username+"',user_lang='"+language+"', user_mail_address='"+email+"',user_last_chg_date='"+changeTime+"'," +
                "user_country='"+country+"',user_actived="+isActive+",user_depart='"+depart+"',user_post='"+post+"',user_type='"+type+"', "+
                "user_phone='"+phone+"',user_qqnum='"+qqNum+"' "+
                "where user_userid='"+userId+"' ;";
        ResultUtil result=dbHelperService.update(sql,"postgres_test");
        if(HttpStatus.OK.value() != (int)result.get("code")){
            message=MessageUtil.getMessage(Message.USER_INFO_UPDATE_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.USER_INFO_UPDATE_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message);
    }

    /**
     * 插入用户信息
     * @param userId
     * @param username
     * @param language 用户语言
     * @param email
     * @param phone
     * @param country
     * @param isActive 是否可用
     * @param depart 部门
     * @param post 岗位
     * @param qqNum QQ号码
     * @return
     */
    @Override
    public ResultUtil insertUserInfo(String userId, String username,String password, String language, String email,String type, String phone, String country, boolean isActive, String depart, String post, String qqNum) {
        String message;
        DateTime date=new DateTime();
        String changeTime=date.toString("yyyyMMdd");
        Md5Util md5Util=new Md5Util();
        String encodePassword=md5Util.encodePassword(password);
        String salt=md5Util.getSalt();
        String sql="insert into "+postgres_user_table+
                "(user_userid,user_name,user_lang,user_password,user_last_chg_date,user_mail_address,user_type," +
                "user_country,user_actived,user_depart,user_post,user_phone,user_qqnum,user_salt) " +
                "values('"+userId+"','"+username+"','"+language+"','"+encodePassword+"','"+changeTime+"','"+email+"','"+type+"'," +
                "'"+country+"',"+isActive+",'"+depart+"','"+post+"','"+phone+"','"+qqNum+"','"+salt+"');";
        ResultUtil result=dbHelperService.insert(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            message=MessageUtil.getMessage(Message.USER_INFO_INSERT_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.USER_INFO_INSERT_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message);
    }




    /**
     * 通过Excel文件批量注册用户
     * @param workbook
     * @return
     */
    @Override
    public ResultUtil batchRegister(Workbook workbook) {
        //获取ExceL文档第一个表格
        Sheet sheet=workbook.getSheetAt(0);
        //使用PoiUtils工具类处理Excel表格
        List<List<Object>> dataList= PoiUtils.getAllData(sheet,1);
        List<Map<String,Object>> resultList=new ArrayList<>();
        for (int i = 0; i <dataList.size() ; i++) {
            Map<String,Object> result=new HashMap<>();
            //获取相应行的数据，转换为list
            List<Object> cellList=dataList.get(i);
            //分别获取第一列，第二列，第三列，第四列的数据
            String username=cellList.get(0).toString();
            String password=cellList.get(1).toString();
            String email=cellList.get(2).toString();
            String telephone=cellList.get(3).toString();
            //进行注册
            ResultUtil resultUtil=register(username,password,email,telephone);
           //将结果写入到list中
            result.put("msg",resultUtil.get("msg"));
            result.put("name",username);
            resultList.add(result);
        }
        return ResultUtil.ok().put("list",resultList);
    }

    /**
     * 获取路由信息
     * @param groupId
     * @return
     */
    @Override
    public ResultUtil getRouter(int groupId) {
        String sql;
        //判断groupId是否为默认值
        if (groupId == -1){
            sql= "select id as \"id\", router as \"router\" , router_description as \"description\", " +
                    "parent_id as \"parentId\", is_public as \"isPublic\", is_active as \"isActive\" " +
                    " from "+router_table+" ;";

        }
        else {
            //筛选出用户组所拥有的有效路由
            sql="select "+router_table+".id as \"id\","+router_group_table+".router as \"router\", group_id as \"groupId\", " +
                    "router_description as \"description\",parent_id as \"parentId\", is_public as \"isPublic\", is_active as \"isActive\"  " +
                    "from "+router_group_table+" left join "+router_table+" on "+router_table+".router ="+router_group_table+".router " +
                    "where "+router_group_table+".group_id = '"+groupId+"'  and "+router_table+".is_active = 'true' ;";
        }
        String message;
        ResultUtil result=dbHelperService.select(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            message=MessageUtil.getMessage(Message.ROUTER_GET_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        ArrayList<HashMap> list= (ArrayList) result.get("result");
        if (HttpStatus.OK.value()!= (int)result.get("code")){
            message=MessageUtil.getMessage(Message.ROUTER_GET_ERROR.getCode());
            logger.error(message);

            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.ROUTER_GET_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("result",list);
    }


    /**
     * 添加路由
     * @param list
     * @return
     */
    @Override
    public ResultUtil addRouter(List list) {
//        String sql;
//        String message;
//        String router="test";
//        for () {
//
//        }
        //检测路由是否存在
//        sql = "insert into "+router_table+"(router,router_description,is_public,is_Active)  values('"+router+"','"+description+"','"+isPublic+"','"+isActive+"')";
//        ResultUtil result=dbHelperService.insert(sql,"postgres_test");
//        if(HttpStatus.OK.value()!= (int)result.get("code")){
//            message=MessageUtil.getMessage(Message.ROUTER_ADD_ERROR.getCode());
//            logger.error(message);
//            return ResultUtil.error(message);
//        }
//        message=MessageUtil.getMessage(Message.ROUTER_ADD_SUCCESS.getCode());
//        logger.info(message);
        return ResultUtil.ok().put("msg","Test");
    }

    /**
     * 删除路由
     * @param router
     * @return
     */
    @Override
    public ResultUtil deleteRouter(String router) {
        String sql;
        String message;
        //检测路由是否存在
        if(!checkRouterExist(-1,router)){
            message=MessageUtil.getMessage(Message.ROUTER_NOT_EXIST.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        sql = "delete from "+router_table+" where router ='"+router+"';";
        ResultUtil result=dbHelperService.insert(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            message=MessageUtil.getMessage(Message.ROUTER_DELETE_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.ROUTER_DELETE_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message);
    }

    /**
     * 检测路由是否存在
     * @param groupId
     * @param router
     * @return
     */
    public boolean checkRouterExist(int groupId,String router){
        String sql;
        String message;
        if (groupId== -1){
            sql="select 1 from "+router_table+" where router ='"+router+"';";
        }
        else {
            sql="select 1 from "+router_group_table+" where router='"+router+"' and user_groupid='"+groupId+"';";
        }
        ResultUtil result=dbHelperService.select(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            message=MessageUtil.getMessage(Message.ROUTER_ADD_ERROR.getCode());
            logger.error(message);
            throw new CustomHttpException(message);
        }
        ArrayList<HashMap> list= (ArrayList) result.get("result");
        if(list.size()>0){
            return true;
        }
        return  false;
    }

    /**
     * 获取用户路由表
     * @param name
     * @return
     */
    @Override
    public ResultUtil routerList(String name) {
        String sql="select router from "+router_table+" where user_groupid=(select user_groupid from "+postgres_user_table+" where user_name = '"+name+"');";
        String message;
        ResultUtil result=dbHelperService.select(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            message=MessageUtil.getMessage(Message.ROUTER_GET_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        ArrayList<HashMap> list= (ArrayList) result.get("result");
        if (list.size()<1){
            message=MessageUtil.getMessage(Message.ROUTER_GET_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        ArrayList routerList=new ArrayList();
        for (int i = 0; i <list.size() ; i++) {
            routerList.add(list.get(i).get("router"));
        }
        message=MessageUtil.getMessage(Message.ROUTER_GET_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("result",routerList);
    }

  /**
   * 修改密码
   * @param username
   * @param newPassword
   * @return
   */
    @Override
    public ResultUtil updatePassword(String username,String password, String newPassword) {
        Md5Util md5Util=new Md5Util();
        String message;
        //获取盐
        String saltSql="select user_salt,user_password from "+postgres_user_table+" where user_name='"+username+"';";
        ResultUtil saltResult=dbHelperService.select(saltSql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)saltResult.get("code")){
        message=MessageUtil.getMessage(Message.GET_SALT_FAIL.getCode());
        logger.error(message);
        return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.GET_SALT_SUCCESS.getCode());
        logger.info(message);
        ArrayList<Map<String,Object>> list= (ArrayList) saltResult.get("result");
        String userSalt=list.get(0).get("user_salt").toString();
        String user_password=list.get(0).get("user_password").toString();
        //校验密码是否相同
        if(!md5Util.checkPassword(password,userSalt,user_password)){
          message=MessageUtil.getMessage(Message.PASSWORD_ERROR.getCode());
          logger.error(username+":"+message);
          return ResultUtil.error(message);
        }
        //使用MD5进行加密
        String encodePassword= md5Util.encodePassword(newPassword);
        String salt=md5Util.getSalt();
        String sql="update "+postgres_user_table+" set user_password= '"+encodePassword+"',user_salt= '"+salt+"'"+
                "where user_name = '"+username+"' ;";
        ResultUtil result=dbHelperService.update(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            message=MessageUtil.getMessage(Message.UPDATE_PASSWORD_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        message=MessageUtil.getMessage(Message.UPDATE_PASSWORD_SUCCESS.getCode());
        logger.info(message);
        return ResultUtil.ok().put("msg",message);
    }

    /**
     * 分页获取用户信息
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public ResultUtil getUserInfoList(int pageIndex, int pageSize,String userName,String criteria,int sort) {
        String sql;
        if (sort == 0){
             sql="select user_name as \"username\" ,user_userid as \"userId\" ,user_phone as \"phone\" ," +
                    "user_mail_address as \"email\" ,user_lang as \"language\", user_type as \"type\", " +
                    "user_country as \"country\", user_actived as \"isActive\" ,user_depart as \"depart\"," +
                    "user_post as \"post\" , user_qqnum as \"qqNum\" " +
                    " from "+postgres_user_table+" where user_name like '%25"+userName+"%25' order by "+criteria+";";
        }
        else {
            sql="select user_name as \"username\" ,user_userid as \"userId\" ,user_phone as \"phone\" ," +
                    "user_mail_address as \"email\" ,user_lang as \"language\", user_type as \"type\", " +
                    "user_country as \"country\", user_actived as \"isActive\" ,user_depart as \"depart\"," +
                    "user_post as \"post\" , user_qqnum as \"qqNum\" " +
                    " from "+postgres_user_table+" where user_name like '%25"+userName+"%25' order by "+criteria+" desc;";
        }
        String message;
        ResultUtil result=dbHelperService.selectPage(sql,"postgres_test",pageIndex,pageSize);
        if(HttpStatus.OK.value()!= (int)result.get("code")){
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
     * 批量插入更新用户信息
     * @param workbook
     * @return
     */
    @Override
    public ResultUtil batchUserInfoInsertOrUpdate(Workbook workbook) {
        String defaultPassword="123456";
        //获取ExceL文档第一个表格
        Sheet sheet=workbook.getSheetAt(0);
        //获取表格标题列表
        List titleList=PoiUtils.getTitleList(PoiUtils.getRow(sheet,0));

        //请求结果列表
        List<Map<String,Object>> resultList=new ArrayList<>();

        Map<String,Object> userInfo=new HashMap<>();

        //循环遍历用户信息写入列表
        for (int i = 1; i <=sheet.getLastRowNum() ; i++) {
            //获取相应行的数据，转换为list
            userInfo=PoiUtils.getRowData(PoiUtils.getRow(sheet,i),titleList);
            String username=userInfo.get("username").toString();
            String userId=userInfo.get("userId").toString();
            String language=userInfo.get("language").toString();
            String email=userInfo.get("email").toString();
            String type=userInfo.get("type").toString();
            String phone=userInfo.get("phone").toString();
            String country=userInfo.get("country").toString();
            boolean isActive= Boolean.parseBoolean(userInfo.get("isActive").toString()) ;
            String depart=userInfo.get("depart").toString();
            String post=userInfo.get("post").toString();
            String qqNum=userInfo.get("qqNum").toString();
            //用户运行结果Map
            Map<String,Object> resultMap=new HashMap<>();
            resultMap.put("username",username);
            //查看该用户是否存在
            if (!checkUserExist(username)){
                ResultUtil insertResult= insertUserInfo(userId,username,defaultPassword,language,email,type,phone,country,isActive,depart,post,qqNum);
                resultMap.put("code",insertResult.get("code"));
                resultMap.put("msg",insertResult.get("msg"));
            }
            else {
                ResultUtil updateResult=updateUserInfo(userId,username,language,email,type,phone,country,isActive,depart,post,qqNum);
                resultMap.put("code",updateResult.get("code"));
                resultMap.put("msg",updateResult.get("msg"));
            }
            resultList.add(resultMap);
        }

        return ResultUtil.ok().put("result",resultList);
    }


  /**
   * 批量删除用户信息
   * @param list
   * @return
   */
    @Override
    public ResultUtil batchUserInfoDelete(List<Map<String, Object>> list) {
      String message;
      List<Map<String,Object>> resultList=new ArrayList<>();
      Map<String,Object> resultMap=new HashMap<>();
      for (int i = 0; i <list.size() ; i++) {
        String username=list.get(i).get("username").toString();
        resultMap.put("username",username);
        //检查用户是否存在
        if (!checkUserExist(username)){
          message=MessageUtil.getMessage(Message.USER_NOT_EXIST.getCode());
          resultMap.put("code",HttpStatus.BAD_REQUEST.value());
          resultMap.put("msg",message);
        }
        else {
          ResultUtil deleteResult=deleteUserInfo(username);
          resultMap.put("code",deleteResult.get("code"));
          resultMap.put("msg",deleteResult.get("msg"));
        }
        resultList.add(resultMap);
      }
      return ResultUtil.ok().put("msg","执行完毕").put("result",resultList);
     }

    /**
     * 根据EXCEL文件批量删除用户
     * @param workbook
     * @return
     */
    @Override
    public ResultUtil batchUserInfoDeleteWithExcel(Workbook workbook) {
        //获取ExceL文档第一个表格
        Sheet sheet=workbook.getSheetAt(0);
        //获取表格标题列表
        List titleList=PoiUtils.getTitleList(PoiUtils.getRow(sheet,0));

        //请求结果列表
        List<Map<String,Object>> resultList=new ArrayList<>();
        Map<String,Object> userInfo=new HashMap<>();
        //循环遍历用户信息写入列表
        for (int i = 1; i <=sheet.getLastRowNum() ; i++) {
            //获取相应行的数据，转换为list
            userInfo=PoiUtils.getRowData(PoiUtils.getRow(sheet,i),titleList);
            String username=userInfo.get("username").toString();
            //用户运行结果Map
            Map<String,Object> resultMap=new HashMap<>();
            resultMap.put("username",username);
            //查看该用户是否存在
            if (!checkUserExist(username)){
                String message=MessageUtil.getMessage(Message.USER_NOT_EXIST.getCode());
                resultMap.put("code",HttpStatus.BAD_REQUEST.value());
                resultMap.put("msg",message);
            }
            else {
                ResultUtil deleteResult=deleteUserInfo(username);
                resultMap.put("code",deleteResult.get("code"));
                resultMap.put("msg",deleteResult.get("msg"));
            }
            resultList.add(resultMap);
        }
        return ResultUtil.ok().put("result",resultList);
    }

    /**
     * 检查用户是否存在
     * @param username
     * @return
     */
    public boolean checkUserExist(String username){
        String sql ="select 1 from  "+postgres_user_table+" where user_name='"+username+"'";
        ResultUtil result=dbHelperService.select(sql,"postgres_test");
        String meesage;
        if(HttpStatus.OK.value() != (int)result.get("code")){
            meesage=MessageUtil.getMessage(Message.USER_INFO_GET_ERROR.getCode());
            logger.error(meesage);
        }
        ArrayList list= (ArrayList) result.get("result");
        if(list.size()>0){
           return  true;
        }
        return false;
    }

    /**
     * 导出用户信息
     * @param username
     * @return
     */
    @Override
    public ResultUtil exportUserInfo(String username) {
        String sql="select user_name as \"username\" ,user_userid as \"userId\" ,user_phone as \"phone\" ," +
                "user_mail_address as \"email\" ,user_lang as \"language\", user_type as \"type\", " +
                "user_country as \"country\", user_actived as \"isActive\" ,user_depart as \"depart\"," +
                "user_post as \"post\" , user_qqnum as \"qqNum\" " +
                " from "+postgres_user_table+" where user_name like '%25"+username+"%25';";
        String message;
        ResultUtil result=dbHelperService.select(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
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
        String file=ExcelUtils.createMapListExcel(list,diskPath);
        String ftpFile=RandomStringUtils.randomAlphanumeric(32)+".xls";
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


}
