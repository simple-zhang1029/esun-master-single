package esun.core.service;

import esun.core.utils.ResultUtil;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    /**
     * 获取用户信息
     * @param user
     * @return
     */
    ResultUtil userInfo(String user);

    /**
     * 保存头像路径
     * @param path
     * @param name
     * @return
     */
    ResultUtil saveImg(String path,String name);

    /**
     * 更新用户信息
     * @param name
     * @param email
     * @param telephone
     * @return
     */
    ResultUtil updateInfo(String name,String email,String telephone);

    /**
     * 修改密码
     * @param name
     * @param password
     * @return
     */
    ResultUtil updatePassword(String name,String password);


}
