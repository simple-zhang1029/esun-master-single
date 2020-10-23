package esun.menu.service;

import esun.menu.utils.ResultUtil;

import java.util.List;

/**
 * @author test
 */
public interface MenuService {
	ResultUtil getMenuList(String roleName, String username, String language);

	ResultUtil addMenuList(String roleName, String language, List<?> dataList);

	ResultUtil deleteMenuList(String roleName, List<?> dataList);

	ResultUtil updateMenuList(String roleName, String language, List<?> dataList);

	ResultUtil updateMenuList(String language, List<?> dataList);

	ResultUtil getRoleInfo();

	ResultUtil addRole(List<?> roleList);

	ResultUtil deleteRole(List<?> roleList);

	ResultUtil getUserRole(String username);

	ResultUtil updateUserRole(String username, List<?> roleList);

}
