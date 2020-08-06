package esun.core.service.impl;

import com.google.common.io.Files;
import esun.core.service.CommonService;
import esun.core.service.DbHelperService;
import esun.core.service.TokenService;
import esun.core.utils.PoiUtils;
import esun.core.utils.ResultUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    @Lazy
    DbHelperService dbHelperService;

    @Autowired
    @Lazy
    TokenService tokenService;

    @Autowired
    @Lazy
    private RedisTemplate redisTemplate;

    private final static String LOGIN_SET="loginSet";
    /**
     * 从配置文件中获取用户表信息
     */
    @Value("${user.table}")
    String userTable;

    @Value("${file.diskPath}")
    String diskPath;

    /**
     * 登入
     * @param name
     * @param password
     * @return
     */
    @Override
    public ResultUtil login(String name, String password) {
        // 获取用户信息
        String sql = "select password from "+userTable+" where username= '"+name+"' ";
        String product="default";
        ResultUtil result=dbHelperService.select(sql,product);
        if(Integer.parseInt(result.get("code").toString())!=200){
            return ResultUtil.error("获取数据库信息失败");
        }
        ArrayList list= (ArrayList) result.get("result");
        HashMap resultmap= (HashMap) list.get(0);
        if(resultmap.size()==0){
            return ResultUtil.error("该账号不存在");
        }
        if(!password.equals(resultmap.get("password").toString())){
            return ResultUtil.error("账号或密码错误");
        }
        // 检验是否登入
        if(redisTemplate.opsForSet().add(LOGIN_SET,name)!=1){
            return ResultUtil.error("该账号已登入");
        }
        //更新token
        ResultUtil tokenResult=tokenService.updateToken(name);
        if(Integer.parseInt(tokenResult.get("code").toString())!=200){
            return ResultUtil.error("更新token失败");
        }
        String token=tokenResult.get("token").toString();
        return ResultUtil.ok("登入成功").put("token",token);
    }

    /**
     * 获取登入人数
     * @return
     */
    @Override
    public ResultUtil loginNum() {
        return ResultUtil.ok().put("result",redisTemplate.opsForSet().members(LOGIN_SET).size());
    }

    /**
     * 登出
     * @param name
     * @return
     */
    @Override
    public ResultUtil logout(String name) {
        redisTemplate.opsForSet().remove(LOGIN_SET,name);
        return ResultUtil.ok();
    }

    /**
     * 获取登入用户名单
     * @return
     */
    @Override
    public ResultUtil queryLoginUser() {
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
        String sql="select 1 from "+userTable+" where username='"+name+"'";
        ResultUtil result=dbHelperService.select(sql,"default");
        if("200".equals(result.get("code").toString())==false){
            return ResultUtil.error("查询用户信息失败");
        }
        ArrayList list= (ArrayList) result.get("result");
        if(list.size()>0){
            return ResultUtil.error("该用户已存在");
        }
        sql="insert into "+userTable+"(username,password,email,telephone) " +
                "values('"+name+"','"+password+"','"+email+"','"+telephone+"')";
        result=dbHelperService.insert(sql,"default");
        if("200".equals(result.get("code").toString())==false) {
            return ResultUtil.error("注册用户信息失败");
        }
        return ResultUtil.ok("注册成功");
    }

    /**
     * 上传文件
     * @param file
     * @return
     */
    @Override
    public ResultUtil upload(MultipartFile file) {
        Map params = new HashMap();
        DateTime dateTime = new DateTime();
        String day = dateTime.toString("yyyyMMdd");
        String filePath =   diskPath + day + "/" + RandomStringUtils.randomAlphanumeric(32) +  "." + FilenameUtils.getExtension(file.getOriginalFilename());
        try {
            File saveFile = new File(filePath);
            if (saveFile.exists()) {
                return ResultUtil.error("文件已存在");
            }
            Files.createParentDirs(saveFile);
            Files.write(file.getBytes(), saveFile);

            String absolutePath = saveFile.getAbsolutePath();
            String path = absolutePath.substring(diskPath.length());
            path = path.replaceAll("\\\\", "/");
            params.put("path", path);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResultUtil.ok(params);
    }

    @Override
    public ResultUtil batchRegister(Workbook workbook) {
        Sheet sheet=workbook.getSheetAt(0);
        List<List<Object>> dataList= PoiUtils.getAllData(sheet,1);
        List<Map<String,Object>> resultList=new ArrayList<>();
        for (int i = 0; i <dataList.size() ; i++) {
            Map<String,Object> result=new HashMap<>();
            List<Object> cellList=dataList.get(i);
            String username=cellList.get(0).toString();
            String password=cellList.get(1).toString();
            String email=cellList.get(2).toString();
            String telephone=cellList.get(3).toString();
            ResultUtil resultUtil=register(username,password,email,telephone);
            result.put("result",resultUtil.get("result"));
            result.put("name",username);
            resultList.add(result);
        }
        return ResultUtil.ok().put("list",resultList);
    }
}
