package com.example.controller.v2;

import com.example.service.v2.MenuService;
import com.example.service.v2.UserService;
import com.example.utils.ResultUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 角色管理接口控制器
 * 提供对角色信息的查询，插入，更新，删除
 * 提供对用户角色的查询，以及更新用户角色
 * @author john.xiao
 * @date 2020-1-4 09:49
 */
@RestController
@RequestMapping("v2/roleManage")
public class RoleController {
	@Autowired
	MenuService menuService;
	/**
	 * 获取角色信息
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@GetMapping("/role")
	public ResultUtil getRoleList(){
		return null;
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
		return null;
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
		return null;
	}

	/**
	 * 获取用户角色信息
	 * @param userId
	 * @return
	 */
	@GetMapping("/userRole")
	public ResultUtil getUserRole(@RequestParam("userId")String userId){
		return null;
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
		return null;

	}
}
