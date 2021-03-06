package esun.core.service;

import com.sun.istack.Nullable;
import esun.core.utils.ResultUtil;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

//Service接口类，用于隔离Service具体实现代码，所有Service都需要在接口类声明，调用接口
public interface ExampleService {
    //接口类返回值固定为ResultUtil
    ResultUtil login(String name,String password);

    ResultUtil register(String name,String password,String email,String telephone);

    ResultUtil upload(MultipartFile file);


    ResultUtil getUserInfo(String name);

    ResultUtil deleteUserInfo(String name);

    ResultUtil updateUserInfo(String userId, String username,String language,String email,String type,String phone,
                              String country,boolean isActive,String depart,String post,String qqNum);

    ResultUtil updateUserInfo(String userId, String username,String password,String language,String email,String type,String phone,
                              String country,boolean isActive,String depart,String post,String qqNum);

    ResultUtil insertUserInfo(String userId, String username,String password,String language,String email,String type,String phone,
                              String country,boolean isActive,String depart,String post,String qqNum);

    ResultUtil batchRegister(Workbook workbook);

    ResultUtil getUserInfoList(int pageIndex,int pageSize,String userName,String criteria,int sort);

    ResultUtil updatePassword(String username,String password,String newPassword);

    ResultUtil batchUserInfoInsertOrUpdate(Workbook workbook);

    ResultUtil batchUserInfoDelete(List<Map<String,Object>> list);

    ResultUtil batchUserInfoDeleteWithExcel(Workbook workbook);

    ResultUtil exportUserInfo(String username);
}
