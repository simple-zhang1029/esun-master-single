package esun.core.service;

import esun.core.utils.ResultUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;


public interface CommonService {
    /**
     * 登入
     * @param name
     * @param password
     * @return
     */
    ResultUtil login(String name,String password);

    /**
     * 获取登入人数
     * @return
     */
    ResultUtil loginNum();

    /**
     * 登出
     * @return
     */
    ResultUtil logout(String name);

    /**
     * 获取登入用户名单
     * @return
     */
    ResultUtil queryLoginUser();


    /**
     * 注册
     * @param name
     * @param password
     * @param email
     * @param telephone
     * @return
     */
    ResultUtil register(String name, String password, String email, String telephone);

    /**
     * 匹配注册
     * @param
     * @return
     */
    ResultUtil batchRegister(Workbook workbook);

    /**
     * 文件上传
     * @param file
     * @return
     */
    ResultUtil upload(MultipartFile file);
            ;

}
