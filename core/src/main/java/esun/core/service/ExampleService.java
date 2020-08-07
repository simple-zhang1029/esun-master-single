package esun.core.service;

import com.sun.istack.Nullable;
import esun.core.utils.ResultUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

//Service接口类，用于隔离Service具体实现代码，所有Service都需要在接口类声明，调用接口
public interface ExampleService {
    //接口类返回值固定为ResultUtil
    ResultUtil login(String name,String password);

    ResultUtil register(String name,String password,String email,String telephone);

    ResultUtil upload(MultipartFile file);

    ResultUtil loggedList();

    ResultUtil getUserInfo(String name);

    ResultUtil deleteUserInfo(String name);

    ResultUtil updateUserInfo(String userId, String username,String language,String email,String type,String phone,
                              String country,boolean isActive,String depart,String post,String qqNum,int groupId);

    ResultUtil insertUserInfo(String userId, String username,String password,String language,String email,String type,String phone,
                              String country,boolean isActive,String depart,String post,String qqNum,int groupId);

    ResultUtil batchRegister(Workbook workbook);

    ResultUtil getUserInfoList(int pageIndex,int pageSize);

    ResultUtil queryPage(int pageIndex,int pageSize);

    ResultUtil routerList(String name);

    ResultUtil updatePassword(String username,String newPassword);
}