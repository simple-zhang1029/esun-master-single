package com.example.controller.v1;

import com.example.constant.Message;
import com.example.service.v1.UserService;
import com.example.utils.MessageUtil;
import com.example.utils.ResultUtil;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 * @author john.xiao
 * @date 2020-12-24 15:48
 */
@RestController
@RequestMapping("/v1/userManage")
public class UserController {

	@Resource
	UserService userService;

	Logger logger= LoggerFactory.getLogger(UserController.class);

	/**
	 * 删除用户
	 * @param userId 用户Id
	 * @return 返回结果封装类
	 */
	@DeleteMapping("userInfo")
	public ResultUtil deleteUserInfo(@RequestParam("userId") String userId){
		return userService.deleteUserInfo(userId);
	}

	/**
	 * 更新用户信息
	 * @param userId 用户Id
	 * @param username 用户名
	 * @param language 语言
	 * @param email 电子邮箱
	 * @param type 用户类型
	 * @param phone 电话号码
	 * @param country 国家
	 * @param isActive 是否可用
	 * @param depart 部门
	 * @param post 岗位
	 * @param qqNum QQ号码
	 * @param password  用户密码
	 * @return 返回结果封装类
	 */
	@PostMapping("userInfo")
	public ResultUtil updateUserInfo(@RequestParam("userId") String userId,
	                                 @RequestParam("username") String username,
	                                 @RequestParam(value = "password",required = false,defaultValue = "") String password,
	                                 @RequestParam(value = "language") String language,
	                                 @RequestParam(value = "email")  String email,
	                                 @RequestParam(value = "type") String type,
	                                 @RequestParam(value = "phone")  String phone,
	                                 @RequestParam(value = "country") String country,
	                                 @RequestParam(value = "isActive" )boolean isActive,
	                                 @RequestParam(value = "depart")String depart,
	                                 @RequestParam(value = "post")String post,
	                                 @RequestParam(value = "qqNum")String qqNum){
		if(!StringUtils.isBlank(password)){
			return userService.updateUserInfo(userId,userId,password,language,email,type,phone,country,isActive,depart,post,qqNum);
		}
		return userService.updateUserInfo(userId,username,language,email,type,phone,country,isActive,depart,post,qqNum);
	}

	/**
	 * 插入用户信息
	 * @param userId 用户Id
	 * @param username 用户名
	 * @param language 语言
	 * @param email 电子邮箱
	 * @param type 用户类型
	 * @param phone 电话号码
	 * @param country 国家
	 * @param isActive 是否可用
	 * @param depart 部门
	 * @param post 岗位
	 * @param qqNum QQ号码
	 * @param password  用户密码
	 * @return 返回结果封装类
	 */
	@PutMapping("userInfo")
	public ResultUtil insertUserInfo(@RequestParam("userId") String userId,
	                                 @RequestParam("username") String username,
	                                 @RequestParam("password") String password,
	                                 @RequestParam(value = "language",required = false,defaultValue = "CH") String language,
	                                 @RequestParam(value = "email")  String email,
	                                 @RequestParam(value = "type",required = false,defaultValue = "外部用户") String type,
	                                 @RequestParam(value = "phone")  String phone,
	                                 @RequestParam(value = "country",required = false,defaultValue = "CH") String country,
	                                 @RequestParam(value = "isActive",required = false,defaultValue ="false" )boolean isActive,
	                                 @RequestParam(value = "depart",required = false,defaultValue = "DEFAULT")String depart,
	                                 @RequestParam(value = "post",required = false,defaultValue = "DEFAULT")String post,
	                                 @RequestParam(value = "qqNum",required = false,defaultValue = " ")String qqNum){
		return userService.insertUserInfo(userId,username,password,language,email,type,phone,country,isActive,depart,post,qqNum);
	}

	
	/**
	 * 分页获取用户信息列表
	 * @param pageIndex 页数。默认值为1
	 * @param pageSize  每页大小。默认值为10
	 * @param criteriaList 排序条件列表
	 * @param userId 查询用户名
	 * @return
	 * @author john.xiao
	 * @date 2020-09-21 14:41
	 */
	@GetMapping("userInfoList")
	public ResultUtil getUserInfoList(@RequestParam(value = "pageIndex",required = false,defaultValue = "1")int pageIndex,
	                                  @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
	                                  @RequestParam(value = "userId",required = false,defaultValue = "")String userId,
	                                  @RequestParam(value = "criteriaList",required = false,defaultValue = "[]")String criteriaList){
		String tableParam;
		String criteria;
		//排序条件json转化列表
		JSONArray criteriaArray=JSONArray.fromObject(criteriaList);
		for (int i = 0; i < criteriaArray.size(); i++) {
			Map<String, Object> listMap = (Map<String, Object>) criteriaArray.get(i);
			criteria=listMap.get("criteria").toString();
			switch (criteria){
				case "username":
					tableParam="user_name";
					break;
				case "language":
					tableParam="user_lang";
					break;
				case "email":
					tableParam="user_mail_address";
					break;
				case "phone":
					tableParam="user_phone";
					break;
				case "qqNum":
					tableParam="user_qqnum";
					break;
				default:
					tableParam="user_userid";
			}
			listMap.put("criteria", tableParam);
		}
		return  userService.getUserInfoList(pageIndex,pageSize,userId,criteriaArray);
	}



	/**
	 * 修改密码
	 * @param userId 用户Id
	 * @param password 用户密码
	 * @param newPassword 新密码
	 * @return
	 * @author john.xiao
	 */
	@PostMapping("password")
	public ResultUtil updatePassword(@RequestParam("userId")String userId,
	                                 @RequestParam("password")String password,
	                                 @RequestParam("newPassword")String newPassword){
		//检测密码是否相同
		if(password.equals(newPassword)){
			String message= MessageUtil.getMessage(Message.PASSWORD_IS_SAME.getCode());
			logger.warn(message);
			return ResultUtil.error(message);
		}
		return userService.updatePassword(userId,password,newPassword);
	}

	/**
	 * 通过Excel文件批量插入或更新数据
	 * @param file
	 * @return
	 * @author john.xiao
	 */
	@PostMapping("batchUserInfo")
	public ResultUtil batchUserInfoInsertOrUpdate(@RequestParam("file") MultipartFile file){
		//初步处理Excel文件
		Workbook workbook=null;
		try {
			InputStream inputStream=file.getInputStream();
			workbook=WorkbookFactory.create(inputStream);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return userService.batchUserInfoInsertOrUpdate(workbook);
	}



	/**
	 * 批量删除用户信息
	 * @param list 传入的用户信息列表
	 * @return
	 * @author john.xiao
	 */
	@DeleteMapping("batchUserInfo")
	public ResultUtil batchUserInfo(@RequestParam("list")String list ){
		//使用Json解析转换列表
		List<Map<String,Object>> jsonArray= JSONArray.fromObject(list);
		return userService.batchUserInfoDelete(jsonArray);
	}

	/**
	 * 通过userId导出用户信息
	 * @param userId
	 * @return
	 * @author john.xiao
	 */
	@GetMapping("batchUserInfo")
	public ResultUtil exportUserInfo(@RequestParam(value = "userId",required = false,defaultValue ="" )String userId){
		return userService.exportUserInfo(userId);
	}
}
