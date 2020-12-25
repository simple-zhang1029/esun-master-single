package com.example.service;

import com.example.utils.ResultUtil;

import java.util.List;

/**
 * @author test
 */
public interface MenuService {
	ResultUtil getMenuList(String roleName, String userId, String language);

	ResultUtil addMenuList(String roleName, String language, List<?> dataList);

	ResultUtil deleteMenuList(String roleName, List<?> dataList);

	ResultUtil updateMenuList(String roleName, String language, List<?> dataList);

	ResultUtil updateMenuList(String language, List<?> dataList);

	ResultUtil getRoleInfo();

	ResultUtil addRole(List<?> roleList);

	ResultUtil deleteRole(List<?> roleList);

	ResultUtil getUserRole(String userId);

	ResultUtil updateUserRole(String userId, List<?> roleList);

}
