package com.example.service.v1;

import com.example.utils.ResultUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

//Service接口类，用于隔离Service具体实现代码，所有Service都需要在接口类声明，调用接口

/**
 * 用户业务实现
 * 完成对用户表的增、删、改、查、批量导入、导出功能
 * @author john.xiao
 * @date 2020-12-24 14:45
 */
public interface UserService {






    /**
     * 根据用户ID删除用户，该方法为精确查询
     * @param userId 用户Id
     * @return 结果封装类
     */
    ResultUtil deleteUserInfo(String userId);

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
    ResultUtil updateUserInfo(String userId, String username, String language, String email, String type, String phone,
                              String country, boolean isActive, String depart, String post, String qqNum);


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
     * @param password 用户密码
     * @return 结果封装类
     */
    ResultUtil updateUserInfo(String userId, String username, String password, String language, String email, String type, String phone,
                              String country, boolean isActive, String depart, String post, String qqNum);

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
    ResultUtil insertUserInfo(String userId, String username, String password, String language, String email, String type, String phone,
                              String country, boolean isActive, String depart, String post, String qqNum);


    /**
     * 分页模糊查询用户，
     * @param pageIndex 页码数
     * @param pageSize 分页大小
     * @param userId 用户Id
     * @param criteriaList  排序列表
     * @return 结果封装类
     */
    ResultUtil getUserInfoList(int pageIndex, int pageSize, String userId, List<Map<String,Object>> criteriaList);

    /**
     * 修改密码
     * @param userId 用户Id
     * @param password 用户密码
     * @param newPassword 新密码
     * @return 结果封装类
     */
    ResultUtil updatePassword(String userId, String password, String newPassword);

    /**
     * 批量删除用户信息
     * @param list 用户信息列表
     * @return 结果封装类
     */
    ResultUtil batchUserInfoDelete(List<Map<String, Object>> list);

    /**
     * 导入Excel更新或添加用户信息
     * @param workbook Excel文件
     * @return 结果封装类
     */
    ResultUtil batchUserInfoInsertOrUpdate(Workbook workbook);

    /**
     * 模糊查询后导出用户信息
     * @param userId
     * @return
     */
    ResultUtil exportUserInfo(String userId);
}
