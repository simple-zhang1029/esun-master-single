package com.example.controller;

import com.example.service.MenuService;
import com.example.utils.ResultUtil;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author test
 */
@RequestMapping("v1/menu")
@RestController
public class MenuController {
	@Autowired
	MenuService menuService;

	/**
	 * 获取菜单信息
	 * 当如角色名和用户ID均为空时，该接口返回所有的菜单信息
	 * 当有角色名且用户ID为空时，该接口返回该角色所拥有的菜单信息
	 * 当有用户ID时，该接口返回该用户所拥有的菜单信息
	 * 用户名的优先级大于角色名
	 * @param roleName 角色名
	 * @param userId
	 * @param language
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@GetMapping("/menu")
	public ResultUtil getMenuList(@RequestParam(value = "roleName",required = false,defaultValue = "") String roleName,
	                              @RequestParam(value = "userId",required = false,defaultValue = "")String userId,
	                              @RequestParam(value = "language",required = false,defaultValue = "CH")String language){
		return menuService.getMenuList(roleName,userId,language);
	}

	/**
	 * 添加菜单
	 * @param roleName
	 * @param language
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@PutMapping("/menu")
	public ResultUtil addMenuList(@RequestParam(value = "roleName",required = false,defaultValue = "") String roleName,
	                              @RequestParam(value = "language",required = false,defaultValue = "CH")String language,
	                              @RequestParam(value = "dataList") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return menuService.addMenuList(roleName,language,jsonArray);
	}

	/**
	 * 删除菜单
	 * @param roleName
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@DeleteMapping("/menu")
	public ResultUtil deleteMenuList(@RequestParam(value = "roleName",required = false,defaultValue = "") String roleName,
	                                 @RequestParam(value = "dataList") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return menuService.deleteMenuList(roleName,jsonArray);
	}

	/**
	 * 更新菜单信息
	 * @param roleName
	 * @param language
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@PostMapping("/menu")
	public ResultUtil updateMenuList(@RequestParam(value = "roleName",required = false,defaultValue = "") String roleName,
	                              @RequestParam(value = "language",required = false,defaultValue = "CH")String language,
	                              @RequestParam(value = "dataList") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		if(StringUtils.isBlank(roleName)){
			return menuService.updateMenuList(language,jsonArray);
		}
		return menuService.updateMenuList(roleName,language,jsonArray);
	}


	/**
	 * 获取角色信息
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@GetMapping("/role")
	public ResultUtil getRoleList(){
		return menuService.getRoleInfo();
	}

	/**
	 * 添加角色
	 * @param roleList 角色表
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@PutMapping("/role")
	public ResultUtil addRole(@RequestParam("roleList")String roleList){
		JSONArray jsonArray=JSONArray.fromObject(roleList);
		return menuService.addRole(jsonArray);
	}

	/**
	 * 删除角色
	 * @param roleList 角色表
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@DeleteMapping("/role")
	public ResultUtil deleteRole(@RequestParam("roleList")String roleList){
		JSONArray jsonArray=JSONArray.fromObject(roleList);
		return menuService.deleteRole(jsonArray);
	}

	/**
	 * 获取用户角色信息
	 * @param userId
	 * @return
	 */
	@GetMapping("/userRole")
	public ResultUtil getUserRole(@RequestParam("userId")String userId){
		return menuService.getUserRole(userId);
	}

	/**
	 * 更新用户角色
	 * @param roleList
	 * @param userId
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@PostMapping("/userRole")
	public ResultUtil updateUserRole(@RequestParam("roleList")String roleList,
	                                 @RequestParam("userId")String userId){
		JSONArray jsonArray=JSONArray.fromObject(roleList);
		return menuService.updateUserRole(userId,jsonArray);

	}
}
