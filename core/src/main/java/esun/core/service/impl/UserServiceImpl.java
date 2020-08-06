package esun.core.service.impl;


import esun.core.constant.Message;
import esun.core.service.DbHelperService;
import esun.core.service.UserService;
import esun.core.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RefreshScope
@Service
public class UserServiceImpl implements UserService {
    @Value("${user.table}")
    String userTable;

    @Autowired
    @Lazy
    DbHelperService dbHelperService;
    @Override
    public ResultUtil userInfo(String user) {
        String sql="select * from "+userTable+" where username='"+user+"'";
        ResultUtil result=dbHelperService.select(sql,"default");
        if("200".equals(result.get("code").toString())==false){
            return ResultUtil.error(Message.LOGIN_SUCCESS.getCode());
        }
        return result;
    }

    @Override
    public ResultUtil saveImg(String path, String name) {
        String sql="update "+userTable+" set img='"+path+"' where username='"+name+"'" ;
        ResultUtil result=dbHelperService.update(sql,"default");
        if(HttpStatus.OK.equals(result.get("code").toString())==false){
            return ResultUtil.error("保存图片路径失败");
        }
        return ResultUtil.ok("保存图片路径成功");
    }

    @Override
    public ResultUtil updateInfo(String name, String email, String telephone) {
        String sql="update "+userTable+" set email='"+email+"',telephone='"+telephone+"' " +
                   "where username='"+name+"'";
        ResultUtil result=dbHelperService.update(sql,"default");
        if("200".equals(result.get("code").toString())==false){
            return ResultUtil.error("更新用户信息失败");
        }
        return ResultUtil.ok("更新用户信息成功");
    }

    @Override
    public ResultUtil updatePassword(String name, String password) {
        String sql="update "+userTable+" set password='"+password+"' where username='"+name+"'" ;
        ResultUtil result=dbHelperService.update(sql,"default");
        if("200".equals(result.get("code").toString())==false){
            return ResultUtil.error("修改密码成功");
        }
        return ResultUtil.ok("修改密码成功");
    }




}
