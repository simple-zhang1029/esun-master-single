package esun.menu.service.impl;

import esun.menu.constant.Message;
import esun.menu.exception.CustomHttpException;
import esun.menu.service.DbHelperService;
import esun.menu.service.MenuService;
import esun.menu.utils.MessageUtil;
import esun.menu.utils.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author test
 */
@Service
public class MenuServiceImpl implements MenuService {
	
	private  static Logger logger= LoggerFactory.getLogger(MenuServiceImpl.class);
	@Autowired
	@Lazy
	DbHelperService dbHelperService;

	/**
	 * 获取菜单表
	 * @return
	 * @author john.xiao
	 * @date 2020-09-21 14:41
	 * @author john.xiao
	 * @date 2020-10-13 15:47
	 */
	@Override
	public ResultUtil getMenuList(String roleName,String username,String language) {
		String sql;
		//如果用户名和角色名都为空则查询所有菜单
		if (StringUtils.isBlank(roleName) && StringUtils.isBlank(username)){
			sql="select distinct menud_nbr as \"menuNo\",menu_select as \"menuSelect\",menu_program as \"menuProgram\",pgrm_url as \"programUrl\",menud_label as \"menuLabel\" from menu_mstr ms\n" +
					"    left join menud_det md on ms.menu_nbr = md.menud_nbr and ms.menu_select = md.menud_select\n" +
					"    left join pgrm_mstr pm on ms.menu_program = pm.pgrm_exec\n" +
					"where md.menud_lang='"+language+"'";
		}
		//如果只有用户名为空则查询角色所拥有的菜单
		else if (StringUtils.isBlank(username)){
			sql="select distinct menud_nbr as \"menuNo\",menu_select as \"menuSelect\",menu_program as \"menuProgram\",pgrm_url as \"programUrl\",menud_label as \"menuLabel\" from menu_mstr ms\n" +
					"    left join menud_det md on ms.menu_nbr = md.menud_nbr and ms.menu_select = md.menud_select\n" +
					"    left join roled_det rd on ms.menu_nbr = rd.roled_nbr and ms.menu_select = rd.roled_select\n" +
					"    left join pgrm_mstr pm on ms.menu_program = pm.pgrm_exec\n" +
					"where md.menud_lang='"+language+"' and rd.roled_role = '"+roleName+"'";
		}
		//如果用户名不为空则查询用户所拥有的菜单
		else {
			sql = "select distinct menud_nbr as \"menuNo\",menu_select as \"menuSelect\",menu_program as \"menuProgram\",pgrm_url as \"programUrl\",menud_label as \"menuLabel\" from menu_mstr ms\n" +
					"    left join menud_det md on ms.menu_nbr = md.menud_nbr and ms.menu_select = md.menud_select\n" +
					"    left join roled_det rd on ms.menu_nbr = rd.roled_nbr and ms.menu_select = rd.roled_select\n" +
					"    left join pgrm_mstr pm on ms.menu_program = pm.pgrm_exec\n" +
					"where md.menud_lang='"+language+"' and rd.roled_role in (\n" +
					"    select ud.userd_role from  userd_det ud\n" +
					"        left join user_mstr um on ud.userd_userid = um.user_userid\n" +
					"    where  um.user_name  = '"+username+"'\n" +
					"    )";
		}
		String message;
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message= MessageUtil.getMessage(Message.MENU_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		message= MessageUtil.getMessage(Message.MENU_GET_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("result",list);
	}

	/**
	 * 添加菜单
	 * @param roleName 角色名
	 * @param language 语言
	 * @param dataList 菜单数据表
	 * @author john.xiao
	 * @date 2020-10-13 15:47
	 * @return
	 */
	@Override
	public ResultUtil addMenuList(String roleName, String language, List<?> dataList) {
		String sql;
		//Optional类防止NPE错误
		Optional menuNo;
		Optional menuSelect;
		Optional menuProgram;
		Optional programUrl;
		Optional menuLabel;
		String message;
		for (int i = 0; i < dataList.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) dataList.get(i);
			menuNo = Optional.ofNullable(listMap.get("menuNo"));
			menuSelect = Optional.ofNullable(listMap.get("menuSelect"));
			menuProgram = Optional.ofNullable(listMap.get("menuProgram"));
			programUrl = Optional.ofNullable(listMap.get("programUrl"));
			menuLabel = Optional.ofNullable(listMap.get("menuLabel"));
			if (!isMenuExist(roleName,"",menuNo.orElse("").toString(),menuSelect.orElse("").toString())){
				//如果角色名都为空则插入菜单表及菜单语言表
				if (StringUtils.isBlank(roleName)){
					sql="call insertMenu('"+menuNo.orElse("")+"','"+menuSelect.orElse("")+"','"+menuProgram.orElse("")+"','"+programUrl.orElse("")+"','"+menuLabel.orElse("")+"','"+language+"');\n";
				}
				//给角色添加菜单
				else{
					sql="insert into roled_det(roled_role,roled_nbr,roled_select) values ('"+roleName+"','"+menuNo.orElse("")+"','"+menuSelect.orElse("")+"');\n";
				}
				ResultUtil result=dbHelperService.insert(sql,"postgres_test");
				if(HttpStatus.OK.value()!= (int)result.get("code")){
					message= MessageUtil.getMessage(Message.MENU_ADD_ERROR.getCode());
					logger.error(message);
					return ResultUtil.error(message);
				}
				ArrayList<HashMap> list= (ArrayList) result.get("result");
				message= MessageUtil.getMessage(Message.MENU_ADD_SUCCESS.getCode());
				logger.info(message);
				((Map<String, Object>) dataList.get(i)).put("result",message);
			}
			else {
				message=MessageUtil.getMessage(Message.MENU_IS_EXIST.getCode());
				logger.info(menuNo.orElse("")+" "+menuSelect.orElse("")+":"+message);
				((Map<String, Object>) dataList.get(i)).put("result",message);
			}
		}
		return ResultUtil.ok().put("result",dataList);
	}

	/**
	 * 删除菜单
	 * @param roleName 角色名
	 * @param dataList 数据列表
	 * @author john.xiao
	 * @date 2020-10-13 17:05
	 * @return
	 */
	@Override
	public ResultUtil deleteMenuList(String roleName, List<?> dataList) {
		String sql;
		//Optional类防止NPE错误
		Optional menuNo;
		Optional menuSelect;
		String message;
		for (int i = 0; i < dataList.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) dataList.get(i);
			menuNo = Optional.ofNullable(listMap.get("menuNo"));
			menuSelect = Optional.ofNullable(listMap.get("menuSelect"));
			if (isMenuExist(roleName,"",menuNo.orElse("").toString(),menuSelect.orElse("").toString())){
				//如果角色名为空则删除菜单表及相关表
				if (StringUtils.isBlank(roleName)){
					sql="call deleteMenu('"+menuNo.orElse("")+"','"+menuSelect.orElse("")+"');\n";
				}
				//删除角色菜单关联
				else{
					sql="delete from roled_det where roled_nbr = ('"+menuNo.orElse("")+"') and roled_select = ('"+menuSelect.orElse("")+"');\n";
				}
				ResultUtil result=dbHelperService.delete(sql,"postgres_test");
				if(HttpStatus.OK.value()!= (int)result.get("code")){
					message= MessageUtil.getMessage(Message.MENU_DELETE_ERROR.getCode());
					logger.error(message);
					return ResultUtil.error(message);
				}
				message= MessageUtil.getMessage(Message.MENU_DELETE_SUCCESS.getCode());
				logger.info(message);
				((Map<String, Object>) dataList.get(i)).put("result",message);
			}
			else {
				message=MessageUtil.getMessage(Message.MENU_NOT_EXIST.getCode());
				logger.info(menuNo.orElse("")+" "+menuSelect.orElse("")+":"+message);
				((Map<String, Object>) dataList.get(i)).put("result",message);
			}
		}
		return ResultUtil.ok().put("result",dataList);

	}

	/**
	 * 更新菜单
	 * @param language 语言
	 * @param dataList 数据表
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 * @return
	 */
	@Override
	public ResultUtil updateMenuList(String language, List<?> dataList) {
		String sql;
		//Optional类防止NPE错误
		Optional menuNo;
		Optional menuSelect;
		Optional menuProgram;
		Optional programUrl;
		Optional menuLabel;
		Optional oldMenuNo;
		Optional oldMenuSelect;
		String message;
		for (int i = 0; i < dataList.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) dataList.get(i);
			menuNo = Optional.ofNullable(listMap.get("menuNo"));
			menuSelect = Optional.ofNullable(listMap.get("menuSelect"));
			menuProgram = Optional.ofNullable(listMap.get("menuProgram"));
			programUrl = Optional.ofNullable(listMap.get("programUrl"));
			menuLabel = Optional.ofNullable(listMap.get("menuLabel"));
			oldMenuNo = Optional.ofNullable(listMap.get("oldMenuNo"));
			oldMenuSelect = Optional.ofNullable(listMap.get("oldMenuSelect"));
			if (isMenuExist("","",menuNo.orElse("").toString(),menuSelect.orElse("").toString())){
				sql="call updateMenu('"+menuNo.orElse("")+"','"+menuSelect.orElse("")+"','"+menuProgram.orElse("")+"','"+programUrl.orElse("")+"','"+menuLabel.orElse("")+"','"+language+"','"+oldMenuNo+"','"+oldMenuSelect+"');\n";
				ResultUtil result=dbHelperService.insert(sql,"postgres_test");
				if(HttpStatus.OK.value()!= (int)result.get("code")){
					message= MessageUtil.getMessage(Message.MENU_UPDATE_ERROR.getCode());
					logger.error(message);
					return ResultUtil.error(message);
				}
				message= MessageUtil.getMessage(Message.MENU_UPDATE_SUCCESS.getCode());
				logger.info(message);
				((Map<String, Object>) dataList.get(i)).put("result",message);
			}
			else {
				message=MessageUtil.getMessage(Message.MENU_NOT_EXIST.getCode());
				logger.info(menuNo.orElse("")+" "+menuSelect.orElse("")+":"+message);
				((Map<String, Object>) dataList.get(i)).put("result",message);
			}
		}
		return ResultUtil.ok().put("result",dataList);
	}

	/**
	 * 更新角色菜单
	 * @param roleName
	 * @param language 语言
	 * @param dataList 数据表
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 * @return
	 */
	@Override
	public ResultUtil updateMenuList(String roleName, String language, List<?> dataList) {
		//Optional类防止NPE错误
		Optional menuNo;
		Optional menuSelect;
		String message;
		StringBuilder stringBuilder=new StringBuilder();
		for (int i = 0; i < dataList.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) dataList.get(i);
			menuNo = Optional.ofNullable(listMap.get("menuNo"));
			menuSelect = Optional.ofNullable(listMap.get("menuSelect"));
			stringBuilder.append("('"+roleName+"','"+menuNo.orElse("")+"','"+menuSelect.orElse("1")+"'),");
		}
		stringBuilder.setLength(stringBuilder.length()-1);
		String deleteSql = "delete from roled_det where roled_role = '"+roleName+"'";
		String addSql = "insert into roled_det(roled_role,roled_nbr,roled_select) values "+stringBuilder.toString();
		ResultUtil deleteResult=dbHelperService.delete(deleteSql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)deleteResult.get("code")){
			message=MessageUtil.getMessage(Message.MENU_UPDATE_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ResultUtil addResult=dbHelperService.insert(addSql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)addResult.get("code")){
			message=MessageUtil.getMessage(Message.MENU_UPDATE_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.MENU_UPDATE_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message);
	}


	/**
	 * 获取全部角色信息
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 * @return
	 */
	@Override
	public ResultUtil getRoleInfo() {
		String message;
		String sql= "select role_name,role_desc from role_mstr;";
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.ROLE_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		message=MessageUtil.getMessage(Message.ROLE_GET_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("list",list);
	}

	/**
	 * 添加角色
	 * @param roleList 角色列表
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 * @return
	 */
	@Override
	public ResultUtil addRole(List<?> roleList) {
		Optional roleName;
		Optional roleDesc;
		String message;
		StringBuilder stringBuilder=new StringBuilder();
		int addCount=0;
		for (int i = 0; i < roleList.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) roleList.get(i);
			roleName = Optional.ofNullable(listMap.get("roleName"));
			roleDesc = Optional.ofNullable(listMap.get("roleDesc"));
			boolean isRoleExist= checkRoleExist(roleName.orElse("").toString());
			if (!isRoleExist){
				stringBuilder.append("('"+roleName.orElse("")+"','"+roleDesc.orElse("1")+"'),");
				addCount++;
				((Map<String, Object>) roleList.get(i)).put("result","角色添加成功");
			}
			else {
				((Map<String, Object>) roleList.get(i)).put("result","角色添加失败,角色已存在");
			}
		}
		//如果插入用户均存在,则不进行插入
		if (addCount == 0){
			message=MessageUtil.getMessage(Message.ROLE_IS_EXIST.getCode());
			logger.error(message);
			return ResultUtil.error(message).put("result",roleList);
		}
		//删除最后一个,
		stringBuilder.setLength(stringBuilder.length()-1);
		String sql = "insert into role_mstr(role_name,role_desc)  values"+stringBuilder.toString()+";";
		ResultUtil result=dbHelperService.insert(sql,"postgres_test");
		//判断是否SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.ROLE_ADD_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.ROLE_ADD_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",roleList);
	}

	/**
	 * 删除角色
	 * @param roleList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@Override
	public ResultUtil deleteRole(List<?> roleList) {
		String sql;
		String message;
		//使用Optional类来防止NPE错误
		Optional<Object> roleName;
		//删除计数
		int deleteCount=0;
		//使用StringBuilder进行字符串的拼接
		StringBuilder deleteBuild=new StringBuilder();
		for (int i = 0; i <roleList.size() ; i++) {
			Map<String,Object> listMap = (Map<String, Object>) roleList.get(i);
			roleName = Optional.ofNullable(listMap.get("roleName"));
			boolean isRouterExist=checkRoleExist(roleName.orElse("").toString());
			//检测路由是否存在
			if(!isRouterExist){
				message=MessageUtil.getMessage(Message.ROLE_NOT_EXIST.getCode());
				logger.error(roleName.orElse("")+":"+message);
				listMap.put("result",message);
			}
			else {
				message=MessageUtil.getMessage(Message.ROLE_DELETE_SUCCESS.getCode());
				logger.info(roleName.orElse("")+":"+message);
				listMap.put("result",message);
				deleteBuild.append(" '"+roleName.orElse("")+"',");
				deleteCount++;
			}
		}
		//如果没有路由被删除，则返回结果
		if (deleteCount == 0 ){
			message=MessageUtil.getMessage(Message.ROLE_DELETE_ERROR.getCode());
			logger.error(message);
			return  ResultUtil.error().put("msg",message).put("result",roleList);
		}
		//移除字符串最后一个,
		deleteBuild.setLength(deleteBuild.length()-1);
		sql = "delete from role_mstr where role_name in ("+deleteBuild.toString()+");";
		ResultUtil result=dbHelperService.insert(sql,"postgres_test");
		//判断SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.ROLE_DELETE_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.ROLE_DELETE_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",roleList);
	}

	/**
	 * 获取用户角色
	 * @param username
	 * @return
	 */
	@Override
	public ResultUtil getUserRole(String username) {
		String message;
		String sql= "select  rm.role_name as \"roleName\", role_desc as \"roleDesc\" from userd_det ud left join role_mstr rm on ud.userd_role = rm.role_name \n" +
				"where ud.userd_userid = (\n" +
				"    select user_userid from user_mstr um where um.user_name = '"+username+"') ;";
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.ROLE_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		message=MessageUtil.getMessage(Message.ROLE_GET_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("list",list);
	}

	/**
	 * 更新用户角色
	 * @param username
	 * @param roleList
	 * @author john.xiao
	 * @date 2020-10-13 15:47
	 * @return
	 */
	@Override
	public ResultUtil updateUserRole(String username, List<?> roleList) {
		//Optional类防止NPE错误
		Optional roleName;
		String message;
		StringBuilder stringBuilder=new StringBuilder();
		int addCount=0;
		String userId  = getUserIdByName(username);
		for (int i = 0; i < roleList.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) roleList.get(i);
			roleName = Optional.ofNullable(listMap.get("roleName"));
			boolean isRoleExist= checkRoleExist(roleName.orElse("").toString());
			if (isRoleExist){
				stringBuilder.append("('"+userId+"','"+roleName.orElse("")+"'),");
				addCount++;
				((Map<String, Object>) roleList.get(i)).put("result","角色添加成功");
			}
			else {
				((Map<String, Object>) roleList.get(i)).put("result","角色添加失败,角色不存在");
			}
		}
		if (addCount == 0){
			message=MessageUtil.getMessage(Message.ROLE_ADD_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message).put("result",roleList);
		}
		stringBuilder.setLength(stringBuilder.length()-1);
		String deleteSql = "delete from userd_det where userd_userid = (\n" +
				"    select user_userid from user_mstr where user_name = '"+username+"');";
		String addSql = "insert into userd_det(userd_userid,userd_role) values "+stringBuilder.toString();
		ResultUtil deleteResult=dbHelperService.delete(deleteSql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)deleteResult.get("code")){
			message=MessageUtil.getMessage(Message.ROLE_DELETE_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ResultUtil addResult=dbHelperService.insert(addSql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)addResult.get("code")){
			message=MessageUtil.getMessage(Message.ROLE_ADD_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.ROLE_ADD_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",roleList);
	}

	/**
	 * 判断菜单是否存在
	 * @param roleName 角色名
	 * @param username 用户名
	 * @param menuNo   菜单编号
	 * @param menuSelect 菜单子编号
	 * @author john.xiao
	 * @date 2020-10-13 15:47
	 * @return
	 */
	private boolean isMenuExist(String roleName,String username,String menuNo,String menuSelect){
		String message;
		String sql;
		if (StringUtils.isBlank(roleName) && StringUtils.isBlank(username)){
			sql = "select 1 from  menu_mstr where  menu_nbr = '"+menuNo+"' and  menu_select = '"+menuSelect+"';";
		}
		//如果只有用户名为空则查询角色所拥有的菜单
		else if (StringUtils.isBlank(username)){
			sql = "select 1 from roled_det where roled_role = '"+roleName+"' and  roled_nbr = '"+menuNo+"' and roled_select = '"+menuSelect+"';";
		}
		//如果用户名不为空则查询用户所拥有的菜单
		else {
			sql = "select 1 from  roled_det rd where rd.roled_nbr='"+menuNo+"' and rd.roled_select = '"+menuSelect+"' and roled_role = (\n" +
					"    select ud.userd_role from  userd_det ud\n" +
					"        left join user_mstr um on ud.userd_userid = um.user_userid\n" +
					"    where  um.user_name  = '"+username+"'\n" +
					" );";
		}
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			throw new CustomHttpException(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		if(list.size()>0){
			return true;
		}
		return  false;
	}

	/**
	 * 检查角色是否存在
	 * @param roleName
	 * @author john.xiao
	 * @date 2020-10-13 15:47
	 * @return
	 */
	private  boolean checkRoleExist(String roleName){
		String message;
		String sql = "select 1 from role_mstr where role_name = '"+roleName+"';";
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			throw new CustomHttpException(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		if(list.size()>0){
			return true;
		}
		return  false;

	}

	private String getUserIdByName(String username){
		String message;
		String sql = "select user_userid from user_mstr where user_name = '"+username+"';";
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			throw new CustomHttpException(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		String userId=list.get(0).get("user_userid").toString();
		return userId;
	}


}
