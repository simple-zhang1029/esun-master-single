package esun.core.service.impl;

import esun.core.constant.Message;
import esun.core.exception.CustomHttpException;
import esun.core.service.DbHelperService;
import esun.core.service.RouterService;
import esun.core.service.TokenService;
import esun.core.utils.MessageUtil;
import esun.core.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 路由模块业务实现类
 * @author test
 * @date 2020-09-21 16:29
 */
@Service
public class RouterServiceImpl implements RouterService {

	@Autowired
	@Lazy
	DbHelperService dbHelperService;

	//用户表,从配置文件中获取
	@Value("${user.table}")
	String userTable;

	@Value("${message.table}")
	String messageTable;

	//路由表
	@Value("${router.table}")
	String routerTable;

	//用户组路由表
	@Value("${router.group.table}")
	String routerGroupTable;

	@Value(("${router.group.user.table}"))
	String userGroupTable;

	@Value("${postgres_user_table}")
	String postgres_user_table;

	@Value("${group.table}")
	String groupTable;




	//创建日志对象
	private static Logger logger= LoggerFactory.getLogger(ExampleServiceImpl.class);

	/**
	 * 获取路由信息
	 * @param groupId 用户组Id
	 * @auhtor john.xiao
	 * @date 2020-09-21 16:13
	 * @return
	 */
	@Override
	public ResultUtil getRouter(int groupId) {
		String sql;
		//判断groupId是否为默认值
		if (groupId == -1){
			sql= "select id as \"id\", router as \"router\" , router_description as \"description\", " +
					"parent_id as \"parentId\", is_public as \"isPublic\", is_active as \"isActive\" " +
					" from "+routerTable+" ;";

		}
		else {
			//筛选出用户组所拥有的有效路由
			sql="select "+routerTable+".id as \"id\","+routerGroupTable+".router as \"router\", group_id as \"groupId\", " +
					"router_description as \"description\",parent_id as \"parentId\", is_public as \"isPublic\", is_active as \"isActive\"  " +
					"from "+routerGroupTable+" left join "+routerTable+" on "+routerTable+".router ="+routerGroupTable+".router " +
					"where "+routerGroupTable+".group_id = '"+groupId+"'  and "+routerTable+".is_active = 'true' ;";
		}
		String message;
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message= MessageUtil.getMessage(Message.ROUTER_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		message=MessageUtil.getMessage(Message.ROUTER_GET_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("result",list);
	}


	/**
	 * 添加路由
	 * @param list 路由列表
	 * @auhtor john.xiao
	 * @date 2020-09-21 16:13
	 * @return
	 */
	@Override
	public ResultUtil addRouter(List list) {
		String message;
		StringBuilder stringBuilder=new StringBuilder();
		//判断有效插入的个数
		int addCount=0;
		//Optional类防止NPE错误
		Optional<Object> router;
		Optional<Object> description;
		Optional<Object> isPublic;
		Optional<Object> isActive;
		Optional<Object> parentId;
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			router= Optional.ofNullable(listMap.get("router"));
			description= Optional.ofNullable(listMap.get("description"));
			isPublic= Optional.ofNullable(listMap.get("isPublic"));
			isActive= Optional.ofNullable(listMap.get("isActive"));
			parentId=Optional.ofNullable(listMap.get("parentId"));
			boolean isRouterExist=checkRouterExist(-1,router.orElse("default").toString());
			//判断用户是否存在
			if (!isRouterExist){
				stringBuilder.append("('"+router.orElse("")+"','"+description.orElse("")+"','"+isPublic.orElse("1")+"','"+isActive.orElse("1")+"','"+parentId.orElse("-1")+"'),");
				addCount++;
				message=MessageUtil.getMessage(Message.ROUTER_ADD_SUCCESS.getCode());
				logger.info(router.orElse("")+":"+message);
				((Map<String, Object>) list.get(i)).put("result",message);
			}
			else {
				message=MessageUtil.getMessage(Message.ROUTER_IS_EXIST.getCode());
				logger.info(router.orElse("")+":"+message);
				((Map<String, Object>) list.get(i)).put("result",message);
			}
		}
		//如果插入用户均存在,则不进行插入
		if (addCount == 0){
			message=MessageUtil.getMessage(Message.ROUTER_IS_EXIST.getCode());
			logger.error(message);
			return ResultUtil.error(message).put("result",list);
		}
		//删除最后一个,
		stringBuilder.setLength(stringBuilder.length()-1);
		String sql = "insert into "+routerTable+"(router,router_description,is_public,is_Active,parent_id)  values"+stringBuilder.toString()+";";
		ResultUtil result=dbHelperService.insert(sql,"postgres_test");
		//判断是否SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.ROUTER_ADD_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.ROUTER_ADD_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",list);
	}

	/**
	 * 删除路由
	 * @param list 路由列表
	 * @auhtor john.xiao
	 * @date 2020-09-21 16:13
	 * @return
	 */
	@Override
	public ResultUtil deleteRouter(List list) {
		String sql;
		String message;
		//使用Optional类来防止NPE错误
		Optional<Object> router;
		//删除计数
		int deleteCount=0;
		//使用StringBuilder进行字符串的拼接
		StringBuilder deleteBuild=new StringBuilder();
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap = (Map<String, Object>) list.get(i);
			router = Optional.ofNullable(listMap.get("router"));
			boolean isRouterExist=checkRouterExist(-1,router.orElse("default").toString());
			//检测路由是否存在
			if(!isRouterExist){
				message=MessageUtil.getMessage(Message.ROUTER_NOT_EXIST.getCode());
				logger.error(router.orElse("")+":"+message);
				listMap.put("result",message);
			}
			else {
				message=MessageUtil.getMessage(Message.ROUTER_DELETE_SUCCESS.getCode());
				logger.info(router.orElse("")+":"+message);
				listMap.put("result",message);
				deleteBuild.append(" '"+router.orElse("")+"',");
				deleteCount++;
			}
		}
		//如果没有路由被删除，则返回结果
		if (deleteCount == 0 ){
			message=MessageUtil.getMessage(Message.ROUTER_NOT_EXIST.getCode());
			logger.error(message);
			return  ResultUtil.error().put("msg",message).put("result",list);
		}
		//移除字符串最后一个,
		deleteBuild.setLength(deleteBuild.length()-1);
		sql = "delete from "+routerTable+" where router in ("+deleteBuild.toString()+");";
		ResultUtil result=dbHelperService.insert(sql,"postgres_test");
		//判断SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.ROUTER_DELETE_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.ROUTER_DELETE_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",list);
	}



	/**
	 * 获取用户路由表
	 * @param name 用户名
	 * @author john.xiao
	 * @date 2020-09-21 16:13
	 * @return
	 */
	@Override
	public ResultUtil routerList(String name) {
		String sql="select  distinct  "+postgres_user_table+".user_userid as \"userId\","+postgres_user_table+".user_name as \"username\","+routerGroupTable+".router as \"router\" " +
				"from "+postgres_user_table+" left join "+userGroupTable+" on "+postgres_user_table+".user_userid = "+userGroupTable+".user_userid left join "+routerGroupTable+"  on "+userGroupTable+".group_id = "+routerGroupTable+".group_id " +
				"WHERE "+postgres_user_table+".user_name ='"+name+"' ;";
		String message;
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.ROUTER_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		if (list.size()<1){
			message=MessageUtil.getMessage(Message.ROUTER_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.ROUTER_GET_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("result",list);
	}

	/**
	 * 更新用户组路由表
	 * @param groupId 用户组ID
	 * @param routerList 路由列表
	 * @author john.xiao
	 * @date 2020-09-21 16:13
	 * @return
	 */
	@Override
	public ResultUtil updateUserRouter(int groupId, List routerList) {
		String message;
		Optional<Object> router;
		StringBuilder stringBuilder=new StringBuilder();
		for (int i = 0; i < routerList.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) routerList.get(i);
			router = Optional.ofNullable(listMap.get("router"));
			stringBuilder.append("("+groupId+",'"+router.orElse("")+"'),");
		}
		stringBuilder.setLength(stringBuilder.length()-1);
		String deleteSql= "delete from "+routerGroupTable+" where group_id = "+groupId+";" ;
		String addSql= "insert into "+routerGroupTable+"(group_id,router)  values"+stringBuilder+";" ;
		ResultUtil deleteResult=dbHelperService.delete(deleteSql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)deleteResult.get("code")){
			message=MessageUtil.getMessage(Message.ROUTER_DELETE_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ResultUtil addResult=dbHelperService.insert(addSql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)addResult.get("code")){
			message=MessageUtil.getMessage(Message.ROUTER_ADD_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.ROUTER_ADD_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message);
	}

	/**
	 * 获取用户组列表
	 * @param pageIndex 页码
	 * @param pageSize 分页大小
	 * @auhtor john.xiao
	 * @date 2020-09-22 16:48
	 */
	@Override
	public ResultUtil getGroup(int pageIndex,int pageSize) {
		String message;
		String sql = "select id as \"groupId\",name as \"groupName\" from  "+groupTable+" ";
		ResultUtil result = dbHelperService.selectPage(sql,"postgres_test",pageIndex,pageSize);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.GROUP_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		List groupList= (List) result.get("result");
		if (groupList.size()<1){
			message=MessageUtil.getMessage(Message.GROUP_IS_NOT_EXIST.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.GROUP_GET_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",result);

	}

	/**
	 * 添加用户组
	 * @param groupList 用户组列表
	 * @author john.xiao
	 * @date 2020-09-21 16:$2
	 * @return
	 */
	@Override
	public ResultUtil addGroup(List groupList) {
		String message;
		Optional<Object> groupName;
		int addCount = 0;
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < groupList.size(); i++) {
			Map<String, Object> groupMap = (Map<String, Object>) groupList.get(i);
			groupName = Optional.ofNullable(groupMap.get("groupName"));
			boolean isGroupExist = checkGroupExist(groupName.orElse("").toString());
			if (isGroupExist) {
				message = MessageUtil.getMessage(Message.GROUP_IS_EXIST.getCode());
				logger.error(message);
				groupMap.put("result", message);
			} else {
				stringBuilder.append("('" + groupName.orElse("") + "'),");
				message = MessageUtil.getMessage(Message.GROUP_ADD_SUCCESS.getCode());
				groupMap.put("result", message);
				addCount++;
			}
		}
		if (addCount == 0) {
			message = MessageUtil.getMessage(Message.GROUP_IS_EXIST.getCode());
			return ResultUtil.error(message).put("result",groupList);
		}
		stringBuilder.setLength(stringBuilder.length() - 1);
		String sql = "insert into " + groupTable + "(name)  values" + stringBuilder.toString() + ";";
		ResultUtil result = dbHelperService.insert(sql, "postgres_test");
		if (HttpStatus.OK.value() != (int) result.get("code")) {
			message = MessageUtil.getMessage(Message.GROUP_ADD_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message = MessageUtil.getMessage(Message.GROUP_ADD_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg", message).put("result",groupList);
	}

	/**
	 * 删除用户组
	 * @param groupList
	 * @return
	 * @author john.xiao
	 * @date 2020-09-22 10:54
	 */
	@Override
	public ResultUtil deleteGroup(List groupList) {
		String message;
		Optional<Object> groupName;
		Optional<Object> groupId;
		int deleteCount = 0;
		StringBuilder nameBuilder=new StringBuilder();
		StringBuilder idBuilder=new StringBuilder();
		for (int i = 0; i <groupList.size() ; i++) {
			Map<String,Object>  groupMap= (Map<String, Object>) groupList.get(i);
			groupName=Optional.ofNullable(groupMap.get("groupName"));
			groupId=Optional.ofNullable(groupMap.get("groupId"));
			boolean isGroupExist;
			if (groupId.get()!=null){
				isGroupExist=checkGroupExist(Integer.parseInt(groupId.orElse(-1).toString()));
			}
			else {
				isGroupExist=checkGroupExist(groupName.orElse("").toString());
			}

			if (!isGroupExist){
				message=MessageUtil.getMessage(Message.GROUP_IS_NOT_EXIST.getCode());
				logger.error(groupId.orElse("").toString()+groupName.orElse("").toString()+":"+message);
				groupMap.put("result",message);
			}
			else {
				nameBuilder.append(" '"+groupName.orElse("")+"',");
				idBuilder.append(" "+groupId.orElse(-1)+",");
				deleteCount++;
				message=MessageUtil.getMessage(Message.GROUP_DELETE_SUCCESS.getCode());
				logger.info(groupId.orElse("").toString()+groupName.orElse("").toString()+":"+message);
				groupMap.put("result",message);
			}
		}
		if (deleteCount == 0){
			message=MessageUtil.getMessage(Message.GROUP_IS_NOT_EXIST.getCode());
			logger.error(message);
			return ResultUtil.error().put("msg",message);
		}
		nameBuilder.setLength(nameBuilder.length()-1);
		idBuilder.setLength(idBuilder.length()-1);
		String sql = "delete from "+groupTable+" where id in ("+idBuilder.toString()+") or name in ("+nameBuilder+");";
		ResultUtil result=dbHelperService.delete(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			throw new CustomHttpException(message);
		}
		message = MessageUtil.getMessage(Message.GROUP_DELETE_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg", message).put("result",groupList);

	}

	/**
	 * 获取用户拥有的用户组
	 * @param name
	 * @return
	 * @auhtor john.xiao
	 * @date 2020-09-22 16:46
	 */
	@Override
	public ResultUtil getUserGroup(String name) {
		String message;
		Optional<Object> nameOptional=Optional.ofNullable(name);
		String sql="select group_id as \"groupId\",name as \"groupName\"" +
				"from  user_group_table left join "+groupTable+" on "+userGroupTable+".group_id = "+groupTable+".id where "+userGroupTable+".user_userid = (select user_userid from "+postgres_user_table+" where user_name ='"+nameOptional.orElse("")+"')";
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.GROUP_GET_SUCCESS.getCode());
			logger.error(message);
			throw new CustomHttpException(message);
		}
		message = MessageUtil.getMessage(Message.GROUP_GET_SUCCESS.getCode());
		logger.info(message);
		return result;
	}

	/**
	 * 更新用户用户组
	 * @param name 用户名
	 * @param groupList 用户组列表
	 * @return
	 * @author john.xiao
	 * @date 2020-09-22 16:46
	 */
	@Override
	public ResultUtil updateUserGroup(String name, List groupList) {
		String message;
		Optional<Object> groupId;
		StringBuilder addBuilder=new StringBuilder();
		for (int i = 0; i <groupList.size() ; i++) {
			Map<String,Object> groupMap= (Map<String, Object>) groupList.get(i);
			groupId=Optional.ofNullable(groupMap.get("groupId"));
			addBuilder.append("((select user_mstr.user_userid from user_mstr where  user_name = '"+name+"'),"+groupId.orElse(-1)+"),");
		}
		addBuilder.setLength(addBuilder.length()-1);
		String deleteSql="delete from "+userGroupTable+" where user_userid = (select user_userid from user_mstr where user_name = '"+name+"')";
		String addSql="insert into "+userGroupTable+"(user_userid,group_id) values"+addBuilder.toString()+";";
		ResultUtil deleteResult=dbHelperService.delete(deleteSql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)deleteResult.get("code")){
			message=MessageUtil.getMessage(Message.GROUP_DELETE_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ResultUtil addResult=dbHelperService.insert(addSql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)addResult.get("code")){
			message=MessageUtil.getMessage(Message.GROUP_ADD_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.GROUP_ADD_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok(message);
	}

	/**
	 * 检测路由是否存在
	 * @param groupId 用户组Id
	 * @param router 路由
	 * @author john.xiao
	 * @date 2020-09-21 16:13
	 * @return
	 */
	private boolean checkRouterExist(int groupId,String router){
		String sql;
		String message;
		if (groupId== -1){
			sql="select 1 from "+routerTable+" where router ='"+router+"';";
		}
		else {
			sql="select 1 from "+routerGroupTable+" where router='"+router+"' and user_groupid='"+groupId+"';";
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
	 * 检测用户组是否存在
	 * @param groupId 用户组Id
	 * @return
	 */
	private boolean checkGroupExist(int groupId){
		String message;
		String sql="select 1 from "+groupTable+" where id= "+groupId+"";
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
	 * 检测用户组是否存在
	 * @param groupName 用户组名称
	 * @return
	 */
	private boolean checkGroupExist(String groupName){
		String message;
		String sql="select 1 from "+groupTable+" where name= '"+groupName+"'";
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
}
