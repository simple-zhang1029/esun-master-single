package com.example.service.v2;

import com.example.entity.MenuEntity;
import com.example.entity.MenuEntity;
import com.example.utils.ResultUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

/**
 * @author test
 */
public interface MenuService {

	/**
	 * 分页模糊查询
	 * @param pageIndex 页码数
	 * @param pageSize 分页大小
	 * @param menuNo 菜单编号
	 * @param menuSelect     
	 * @param criteriaList  排序列表
	 * @return 结果封装类
	 */
	ResultUtil getMenuInfoList(int pageIndex, int pageSize, String menuNo, String menuSelect, List<Map<String, Object>> criteriaList);

	/**
	 * 查询信息
	 * @param menuNo 菜单编号
	 * @param menuSelect 下级菜单号
	 * @return 结果封装类
	 */
	ResultUtil getMenuInfoList(String menuNo, String menuSelect);





	/**
	 * 插入单条信息
	 * @param menuEntity
	 * @return 结果封装类
	 */
	ResultUtil insertMenuInfo(MenuEntity menuEntity);

	/**
	 * 更新单条信息
	 * @param menuEntity
	 * @return 结果封装类
	 */
	ResultUtil updateMenuInfo(MenuEntity menuEntity);

	/**
	 * 删除单条信息
	 * @param menuEntity
	 * @return 结果封装类
	 */
	ResultUtil deleteMenuInfo(MenuEntity menuEntity);


	/**
	 * 根据ID批量删除，该方法为精确查询
	 * @param menuEntityList 实体类列表
	 * @return 结果封装类

	 */
	ResultUtil deleteMenuInfoList(List<MenuEntity> menuEntityList);

	/**
	 * 根据ID批量插入
	 * @param menuEntityList 实体类列表
	 * @return 结果封装类
	 */
	ResultUtil insertMenuInfoList(List<MenuEntity> menuEntityList);

	/**
	 * 根据ID批量更新
	 * @param menuEntityList 实体类列表
	 * @return 结果封装类
	 */
	ResultUtil updateMenuInfoList(List<MenuEntity> menuEntityList);

	/**
	 * 导入Excel更新或添加用户信息
	 * @param workbook Excel文件
	 * @return 结果封装类
	 */
	ResultUtil batchMenuInfoInsertOrUpdate(Workbook workbook);

	/**
	 * 模糊查询后导出菜单信息
	 * @param menuNo 菜单编号
	 * @param menuSelect 下级菜单号
	 * @return 结果封装类
	 */
	void exportUserInfo(String menuNo, String menuSelect);

	/**
	 * 查询角色菜单信息
	 * @param roleName 角色名
	 * @return 结果封装类
	 */
	ResultUtil getRoleMenuInfoList(String roleName);

	/**
	 * 批量更新角色菜单
	 * @param menuEntityList 菜单列表
	 * @param roleName 角色名
	 * @return 结果封装类
	 */
	ResultUtil updateRoleMenuInfoList(List<MenuEntity> menuEntityList, String roleName);

	/**
	 * 单条更新角色菜单
	 * @param menuEntity
	 * @param roleName
	 * @return
	 */
	ResultUtil updateRoleMenuInfo(MenuEntity menuEntity, String roleName);
	/**
	 * 查询用户菜单信息
	 * @param userUserId 用户ID
	 * @return
	 */
	ResultUtil getUserMenuInfoList(String userUserId);


}
