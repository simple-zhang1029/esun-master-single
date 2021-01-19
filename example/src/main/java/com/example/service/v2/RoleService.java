package com.example.service.v2;

import com.example.entity.MenuEntity;
import com.example.entity.RoleMstr;
import com.example.utils.ResultUtil;
import net.sf.json.JSONArray;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

public interface RoleService {
    /**
     * 分页模糊查询用户，
     *
     * @param pageIndex 页码数
     * @param pageSize  分页大小
     * @param roleName  角色名
     * @return 结果封装类
     */
    ResultUtil getRoleInfoList(int pageIndex, int pageSize, String roleName, List<Map<String, Object>> criteriaList);

    /**
     * 插入单条信息
     * @param roleMstr
     * @return 结果封装类
     */
    ResultUtil insertRoleInfo(RoleMstr roleMstr);

    /**
     * 删除单条信息
     * @param roleMstr
     * @return 结果封装类
     */
    ResultUtil deleteRoleInfo(RoleMstr roleMstr);

    /**
     * 更新单条信息
     * @param roleMstr
     * @return 结果封装类
     */
    ResultUtil updateRoleInfo(RoleMstr roleMstr);


    /**
     * 添加角色
     * @param roleMstrList
     * @return 结果封装
     */
    ResultUtil insertRoleInfoList(List<RoleMstr> roleMstrList);

    /**
     * 删除角色
     * @param roleMstrList
     * @return
     */

    ResultUtil deleteRoleInfolist(List<RoleMstr> roleMstrList);

    /**
     * 更新角色
     * @param roleMstrList
     * @return
     */
    ResultUtil updateRoleInfolist(List<RoleMstr> roleMstrList);

    /**
     * 获取用户角色信息
     * @param userUserId
     * @return
     */
    ResultUtil getUserRoleInfoList(String userUserId);

    /**
     * 更新用户信息
     * @param roleMstrList
     * @param userUserId
     * @return
     */
    ResultUtil updateUserRoleInfoList(List<RoleMstr> roleMstrList, String userUserId);

    /**
     * 单条更新用户角色
     * @param roleMstr
     * @param userUserId
     * @return
     */
    ResultUtil updateUserRoleInfo(RoleMstr roleMstr, String userUserId);

    /**
     * 导入
     * @param workbook
     * @return
     */
    ResultUtil batchRoleInfoInsertOrUpdate(Workbook workbook);

    /**
     * 导出
     * @param roleName
     */
    void exportRoleInfo(String roleName);


}


