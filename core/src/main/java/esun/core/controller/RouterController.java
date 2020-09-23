package esun.core.controller;

import esun.core.annotation.LoginRequire;
import esun.core.service.RouterService;
import esun.core.utils.ResultUtil;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**路由模块控制类
 * @author test
 */
@RestController
@RequestMapping("v1/router")
public class RouterController {
	@Autowired
	RouterService routerService;
	//创建日志对象
	private static Logger logger= LoggerFactory.getLogger(ExampleController.class);

	/**
	 * 获取路由表
	 * @return
	 * @author john.xiao
	 * @date 2020-09-21 14:41
	 */
	@LoginRequire
	@GetMapping("router")
	public ResultUtil getRouterList(@RequestParam(value = "groupId",required = false,defaultValue = "-1") int groupId){
		return routerService.getRouter(groupId);
	}

	/**
	 * 添加路由
	 * @param list 传入的路由列表
	 * @return
	 * @author john.xiao
	 * @date 2020-09-21 14:41
	 */
	@LoginRequire
	@PutMapping("router")
	public ResultUtil addRouterList(@RequestParam("list")String list){
		JSONArray jsonArray=JSONArray.fromObject(list);
		return routerService.addRouter(jsonArray);
	}

	/**
	 * 删除路由
	 * @param list 传入的路由列表
	 * @return
	 * @author john.xiao
	 * @date 2020-09-21 14:41
	 */
	@LoginRequire
	@DeleteMapping("router")
	public ResultUtil deleteRouterList(@RequestParam("list")String list){
		JSONArray jsonArray=JSONArray.fromObject(list);
		return routerService.deleteRouter(jsonArray);
	}



	/**
	 * 获取用户路由表
	 * @param username 查询用户名
	 * @return
	 * @author john.xiao
	 * @date 2020-09-21 14:41
	 */
	@LoginRequire
	@GetMapping("userRouter")
	public ResultUtil getUserRouter(@RequestParam("username") String username){
		return routerService.routerList(username);
	}

	/**
	 * 给用户组添加路由
	 * @param groupId 用户名
	 * @param list 路由列表
	 * @return
	 * @author john.xiao
	 * @date 2020-09-21 14:41
	 */
	@LoginRequire
	@PutMapping("groupRouter")
	public ResultUtil groupUserRouter(@RequestParam("groupId") int groupId,
	                                  @RequestParam("list")String list){
		JSONArray routerList=JSONArray.fromObject(list);
		return  routerService.updateUserRouter(groupId,routerList);

	}

	/**
	 * 获取用户组列表
	 * @return
	 * @author john.xiao
	 * @date 2020-09-22 10:23
	 */
	@LoginRequire
	@GetMapping("group")
	public ResultUtil getGroup(@RequestParam(value = "pageIndex",required = false,defaultValue = "1") int pageIndex,
	                           @RequestParam(value = "pageSize",required = false,defaultValue = "10") int pageSize){
		return routerService.getGroup(pageIndex,pageSize);
	}

	/**
	 * 添加用户组
	 * @param list
	 * @return
	 * @author john.xiao
	 * @date 2020-09-22 10:46
	 */
	@LoginRequire
	@PutMapping("group")
	public ResultUtil addGroup(@RequestParam("list") String list){
		JSONArray groupList=JSONArray.fromObject(list);
		return routerService.addGroup(groupList);

	}

	/**
	 * 删除用户组
	 * @param list
	 * @return
	 * @author john.xiao
	 * @date 2020-09-22 10:46
	 */
	@LoginRequire
	@DeleteMapping("group")
	public ResultUtil deleteGroup(@RequestParam("list") String list){
		JSONArray groupList=JSONArray.fromObject(list);
		return  routerService.deleteGroup(groupList);
	}

	/**
	 * 获取用户拥有的用户组
	 * @param name 用户名
	 * @return
	 * @auhtor john.xiao
	 * @date 2020-09-22 16:01
	 */
	@LoginRequire
	@GetMapping("userGroup")
	public ResultUtil getUserGroup(@RequestParam("username") String name){
		return routerService.getUserGroup(name);
	}

	/**
	 * 更新用户的用户组
	 * @param username
	 * @param list
	 * @return
	 * @auhtor john.xiao
	 * @date 2020-09-23 10:42
	 */
	@LoginRequire
	@PostMapping("userGroup")
	public ResultUtil updateUserGroup(@RequestParam("username")String username,
	                                  @RequestParam("list")String list){
		JSONArray groupList=JSONArray.fromObject(list);
		return routerService.updateUserGroup(username,groupList);
	}

}
