package com.example.service.v1.impl;

import com.example.constant.Message;
import com.example.exception.CustomHttpException;
import com.example.service.feign.DbHelperService;
import com.example.service.v1.DomainService;
import com.example.utils.*;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 域管理服务实现
 * @author john.xiao
 * @date 2020-12-17 09:48
 */
@Service
public class DomainServiceImpl  implements DomainService {
	private  static Logger logger= LoggerFactory.getLogger(DomainServiceImpl.class);

	@Autowired
	@Lazy
	DbHelperService dbHelperService;
	/**
	 * 	Postgres数据源
	 */
	private final static String DATASOURCE_POSTGRES="postgres";
	/**
	 * Mysql数据源
	 *
	 */
	private final static String DATASOURCE_MYSQL="mysql";
	@Value("${file.diskPath}")
	String diskPath;

	@Value("${ftp.url}")
	String ftpUrl;

	@Value("${ftp.port}")
	int ftpPort;

	@Value("${ftp.username}")
	String ftpUsername;

	@Value("${ftp.password}")
	String ftpPassword;

	@Value("${ftp.ftpPath}")
	String ftpPath;
	/**
	 * 获取发货信息
	 * @param pageIndex 页码数
	 * @param pageSize 分页大小
	 * @param criteriaList 排序条件
	 * @param domain 域
	 * @return 返回信息工具类
	 */
	@Override
//	@Cached(name = "domainCache",key = "#domain",cacheType = CacheType.LOCAL,expire = 300)
	public ResultUtil getDomain(int pageIndex, int pageSize, List<?> criteriaList,String domain) {
		String message;
		String sortString=getSortString(criteriaList);
		//判断排序正序倒序
		//postgres使用ilike进行不区分的大小写的模糊查询
		String sql = "select domain_domain as \"domain\", domain_name as \"domainName\", domain_corp as \"domainCorp\", domain_sname as \"shortName\", " +
				"domain_db as \"dataBase\", domain_active as \"active\", domain_propath as \"domainProPath\", " +
				"domain_type as \"domainType\", domain_max_users as \"maxUser\", domain_admin as  \"domainAdmin\" " +
				"from domain_mstr " +
				"where domain_domain ilike '%"+domain+"%' " +
				"order by "+sortString+" ";
		ResultUtil result=dbHelperService.selectPage(sql,DATASOURCE_POSTGRES,pageIndex,pageSize);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message= MessageUtil.getMessage(Message.DELIVERY_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList list= (ArrayList) result.get("result");
		message=MessageUtil.getMessage(Message.DELIVERY_GET_SUCCESS.getCode());
		logger.info(message);
		int pageCount= (int) result.get("pageCount");
		//获取总条数
		int count= (int) result.get("count");
		return ResultUtil.ok().put("msg",message).put("result",list).put("pageCount",pageCount).put("count",count);
	}

	/**
	 * 获取排序条件字符串
	 * @param criteriaList 排序列表
	 * @return 排序后的字符
	 * @author john.xiao
	 * @date 2020-12-17 11-27
	 */
	private String getSortString(List<?> criteriaList){
		StringBuilder criteriaBuilder=new StringBuilder();
		if(criteriaList.size()>0){
			for (int i = 0; i < criteriaList.size(); i++) {
				Map<String,Object> listMap= (Map<String, Object>) criteriaList.get(i);
				Optional<Object> sort=Optional.ofNullable(listMap.get("sort"));
				Optional<Object> criteria=Optional.ofNullable(listMap.get("criteria"));
				criteriaBuilder.append(criteria.orElse("order_id"));
				if (!"0".equals(sort.orElse("0"))){
					criteriaBuilder.append(" desc");
				}
				criteriaBuilder.append(" ,");
			}
		}
		else {
			criteriaBuilder.append("domain_domain");
		}
		return  criteriaBuilder.toString();
	}
	/**
	 * 添加域信息
	 * 通过解析List进行批量插入
	 * @param list 添加域信息列表
	 * @return
	 */
	@Override
	public ResultUtil addDomain(List<?> list) {
		Optional domain;
		Optional domainName;
		Optional domainCorp;
		Optional shortName;
		Optional dataBase;
		Optional active;
		Optional domainProPath;
		Optional domainType;
		Optional maxUser;
		Optional domainAdmin;
		String message;
		StringBuilder stringBuilder=new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			domain = Optional.ofNullable(listMap.get("domain"));
			domainName = Optional.ofNullable(listMap.get("domainName"));
			domainCorp = Optional.ofNullable(listMap.get("domainCorp"));
			shortName = Optional.ofNullable(listMap.get("shortName"));
			dataBase = Optional.ofNullable(listMap.get("dataBase"));
			active = Optional.ofNullable(listMap.get("active"));
			domainProPath = Optional.ofNullable(listMap.get("domainProPath"));
			domainType = Optional.ofNullable(listMap.get("domainType"));
			maxUser = Optional.ofNullable(listMap.get("maxUser"));
			domainAdmin = Optional.ofNullable(listMap.get("domainAdmin"));
			stringBuilder.append("('"+domain.orElse("")+"','"+domainCorp.orElse("")+"','"+domainName.orElse("")+"','"+shortName.orElse("")+"','"+dataBase.orElse("")+"'," +
					"'"+active.orElse("false")+"','"+domainProPath.orElse("")+"','"+domainType.orElse("")+"'," +
					"'"+maxUser.orElse("0")+"','"+domainAdmin.orElse("")+"'),");
			((Map<String, Object>) list.get(i)).put("result","数据添加成功");
		}
		//删除最后一个,
		stringBuilder.setLength(stringBuilder.length()-1);
		String sql = "insert into domain_mstr " +
				"( domain_domain, domain_corp, domain_name,domain_sname, domain_db, domain_active, domain_propath, " +
				"domain_type, domain_max_users, domain_admin) " +
				"values"+stringBuilder.toString()+";";
		ResultUtil result=dbHelperService.insert(sql,DATASOURCE_POSTGRES);
		//判断是否SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.DELIVERY_ADD_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.DELIVERY_ADD_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",list);
	}

	/**
	 * 删除发货信息
	 * @param list
	 * @return
	 */
	@Override
	public ResultUtil deleteDomain(List<?> list) {
		String sql;
		String message;
		//使用Optional类来防止NPE错误
		Optional domain;

		//删除计数
		int deleteCount=0;
		//使用StringBuilder进行字符串的拼接
		StringBuilder deleteBuild=new StringBuilder();
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap = (Map<String, Object>) list.get(i);
			domain = Optional.ofNullable(listMap.get("domain"));
			boolean isDomain= checkDomainExist( domain.orElse("").toString());
			//检测发货信息是否存在
			if(!isDomain){
				message=MessageUtil.getMessage(Message.DELIVERY_NOT_EXIST.getCode());
				logger.error(message);
				listMap.put("result",message);
			}
			else {
				message=MessageUtil.getMessage(Message.DELIVERY_DELETE_SUCCESS.getCode());
				logger.info(message);
				listMap.put("result",message);
				deleteBuild.append("'"+domain.orElse("")+"',");
				deleteCount++;
			}
		}
		//如果没有信息被删除，则返回结果
		if (deleteCount == 0 ){
			message=MessageUtil.getMessage(Message.DELIVERY_NOT_EXIST.getCode());
			logger.error(message);
			return  ResultUtil.ok().put("msg",message).put("result",list);
		}
		//移除字符串最后一个,
		deleteBuild.setLength(deleteBuild.length()-1);
//		sql = "delete from domain_mstr where domain_domain in ("+deleteBuild.toString()+")";
		sql = "delete from domain_mstr where domain_domain = "+deleteBuild.toString()+"";
		ResultUtil result=dbHelperService.delete(sql,DATASOURCE_POSTGRES);
		//判断SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.DELIVERY_DELETE_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.DELIVERY_DELETE_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",list);
	}

	/**
	 * 更新发货信息
	 * @param list
	 * @return
	 */
	@Override
	public ResultUtil updateDomain(List<?> list) {
		String sql;
		String message;
		//使用Optional类来防止NPE错误
		Optional domain;
		Optional domainCorp;
		Optional domainName;
		Optional shortName;
		Optional dataBase;
		Optional active;
		Optional domainProPath;
		Optional domainType;
		Optional maxUser;
		Optional domainAdmin;
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap = (Map<String, Object>) list.get(i);
			domain = Optional.ofNullable(listMap.get("domain"));
			domainCorp = Optional.ofNullable(listMap.get("domainCorp"));
			domainName = Optional.ofNullable(listMap.get("domainName"));
			shortName = Optional.ofNullable(listMap.get("shortName"));
			dataBase = Optional.ofNullable(listMap.get("dataBase"));
			active = Optional.ofNullable(listMap.get("active"));
			domainProPath = Optional.ofNullable(listMap.get("domainProPath"));
			domainType = Optional.ofNullable(listMap.get("domainType"));
			maxUser = Optional.ofNullable(listMap.get("maxUser"));
			domainAdmin = Optional.ofNullable(listMap.get("domainAdmin"));
			boolean isDomain= checkDomainExist( domain.orElse("").toString());
			if(!isDomain){
				message=MessageUtil.getMessage(Message.DELIVERY_NOT_EXIST.getCode());
				logger.error(message);
				listMap.put("result",message);
			}
			else {
				sql = "update domain_mstr set " +
						"domain_domain = '"+domain.orElse("")+"', domain_corp = '"+domainCorp.orElse("")+"', domain_name='"+domainName.orElse("")+"',domain_sname = '"+shortName.orElse("")+"', " +
						"domain_db ='"+dataBase.orElse("")+"', domain_active="+active.orElse("false")+", domain_propath='"+domainProPath.orElse("")+"'," +
						"domain_type ='"+domainType.orElse("")+"', domain_max_users='"+maxUser.orElse("0")+"', domain_admin='"+domainAdmin.orElse("")+"' " +
						" where domain_domain= '"+domain.orElse("")+"' ;";
				ResultUtil result=dbHelperService.update(sql,"postgres_test");
				//判断SQL语句是否执行成功
				if(HttpStatus.OK.value()!= (int)result.get("code")){
					message= MessageUtil.getMessage(Message.DELIVERY_UPDATE_ERROR.getCode());
					logger.error(message);
					return ResultUtil.error(message);
				}
				message=MessageUtil.getMessage(Message.DELIVERY_UPDATE_SUCCESS.getCode());
				logger.info(message);
				listMap.put("result",message);
			}
		}
		return ResultUtil.ok().put("result",list);
	}
	/**
	 * 导入发货信息
	 * @param workbook
	 * @return
	 */
	@Override
	public ResultUtil exportDomain(Workbook workbook) {
		//获取ExceL文档第一个表格
		Sheet sheet=workbook.getSheetAt(0);
		//获取表格标题列表
		List titleList= PoiUtils.getTitleList(PoiUtils.getRow(sheet,0));
		//请求结果列表
		List<Map<String,Object>> resultList=new ArrayList<>();
		Map<String,Object> info;

		//循环遍历用户信息写入列表
		for (int i = 1; i <=sheet.getLastRowNum() ; i++) {
			//用户运行结果Map
			Map<String,Object> resultMap=new HashMap<>();
			//获取相应行的数据，转换为list
			info=PoiUtils.getRowData(PoiUtils.getRow(sheet,i),titleList);
			Optional domain=Optional.ofNullable(info.get("domain"));
			Optional domainCorp=Optional.ofNullable(info.get("domainCorp"));
			Optional domainName=Optional.ofNullable(info.get("domainName"));
			Optional dataBase=Optional.ofNullable(info.get("dataBase"));
			Optional active=Optional.ofNullable(info.get("active"));
			Optional domainProPath=Optional.ofNullable(info.get("domainProPath"));
			Optional domainType=Optional.ofNullable(info.get("domainType"));
			Optional maxUser=Optional.ofNullable(info.get("maxUser"));
			Optional domainAdmin=Optional.ofNullable(info.get("domainAdmin"));
			Optional shortName=Optional.ofNullable(info.get("shortName"));

			resultMap.put("domain",domain.orElse(""));
			resultMap.put("domainCorp",domainCorp.orElse(""));
			resultMap.put("domainName",domainName.orElse(""));
			resultMap.put("dataBase",dataBase.orElse(""));
			resultMap.put("active",active.orElse("false"));
			resultMap.put("domainProPath",domainProPath.orElse(""));
			resultMap.put("domainType",domainType.orElse(""));
			resultMap.put("maxUser",maxUser.orElse("0"));
			resultMap.put("domainAdmin",domainAdmin.orElse(""));
			resultMap.put("shortName",shortName.orElse(""));
			resultList.add(resultMap);
		}
		ResultUtil result=addDomain(resultList);
		String message=MessageUtil.getMessage(Message.EXPORT_DELIVERY_SUCCESS.getCode());
		return ResultUtil.ok(message).put("result",result.get("result"));
	}

	/**
	 * 导出发货历史记录
	 * @param isDelete
	 * @return
	 */
	@Override
	public ResultUtil deriveDomain (String domain, boolean isDelete) {
		String sql = "select domain_domain as \"domain\", domain_name as \"domainName\", domain_corp as \"domainCorp\", domain_sname as \"shortName\", " +
				"domain_db as \"dataBase\", domain_active as \"active\", domain_propath as \"domainProPath\", " +
				"domain_type as \"domainType\", domain_max_users as \"maxUser\", domain_admin as  \"domainAdmin\" " +
				"from domain_mstr " +
				" where domain_domain ilike '%"+domain+"%' ";
		String message;
		ResultUtil result=dbHelperService.select(sql,DATASOURCE_POSTGRES);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList list= (ArrayList) result.get("result");
		//判断导出记录是否存在
		if (list.size()==0){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			return ResultUtil.ok(message);
		}

		List<String> titleList=new ArrayList<>();
		titleList.add("domain");
		titleList.add("domainCorp");
		titleList.add("domainName");
		titleList.add("shortName");
		titleList.add("domainProPath");
		titleList.add("domainType");
		titleList.add("maxUser");
		titleList.add("domainAdmin");
		titleList.add("dataBase");
		titleList.add("active");

		String file= ExcelUtils.createMapListExcel(list,diskPath,titleList);
		String ftpFile= RandomStringUtils.randomAlphanumeric(32)+".xls";
		//FTP上传文件
		FTPUtils ftpUtils=new FTPUtils();
		ftpUtils.setHostname(ftpUrl);
		ftpUtils.setPort(ftpPort);
		ftpUtils.setUsername(ftpUsername);
		ftpUtils.setPassword(ftpPassword);
		if(!ftpUtils.uploadFile(ftpPath,ftpFile,file)){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		String path="ftp://"+ftpUrl+ftpPath+ftpFile;
		//判断是否删除
		if(isDelete){
			String 	deleteSql = "delete  from domain_mstr " +
					"where domain_domain in " +
					"(" +
					"select id from domain_mstr   " +
					"where domain_domain ilike '%"+domain+"%'" +
					");";
			ResultUtil deleteResult=dbHelperService.delete(deleteSql,DATASOURCE_POSTGRES);
			if(HttpStatus.OK.value()!= (int)deleteResult.get("code")){
				message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
				logger.error(message);
				return ResultUtil.error(message);
			}
		}
		message=MessageUtil.getMessage(Message.DERIVE_DELIVERY_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",path);
	}





	/**
	 * 检测域是否存在
	 * @param domain 域
	 * @return true:存在改该域 false :不存在该域
	 * @author john.xiao
	 * @date 2020-12-17 11:05
	 */
	private  boolean checkDomainExist(String domain){
		String message;
		String sql = "select 1 from domain_mstr where domain_domain='"+domain+"';";
		ResultUtil result=dbHelperService.select(sql,DATASOURCE_POSTGRES);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			throw new CustomHttpException(message);
		}
		ArrayList list= (ArrayList) result.get("result");
		if(list.size()>0){
			return true;
		}
		return  false;
	}

	/**
	 * 获取用户域
	 * @param user 用户名
	 * @return
	 */
	@Override
	public ResultUtil getUserDomain(String user) {
		String message;
		String sql="select userdomain_userid as user,userdomain_corp as domainCorp,userdomain_domain as domain from userdomain_ref where lower(userdomain_userid) = lower('"+user+"')";
		ResultUtil result=dbHelperService.select(sql,DATASOURCE_POSTGRES);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message= MessageUtil.getMessage(Message.DELIVERY_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList list= (ArrayList) result.get("result");
		message=MessageUtil.getMessage(Message.DELIVERY_GET_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",list);
	}

	/**
	 * 更新用户域
	 * @param user 用户名
	 * @param list 域信息列表
	 * @return 返回结果工具类
	 */
	@Override
	public ResultUtil updateUserDomain(String user, List<?> list) {
		String message;
//		String deleteSql="create table test_table( id int,c1 varchar(20));";
		String deleteSql="delete from userdomain_ref where userdomain_userid = '"+user+"';";
		ResultUtil deleteResult=dbHelperService.delete(deleteSql,DATASOURCE_POSTGRES);
		if(HttpStatus.OK.value()!= (int)deleteResult.get("code")){
			message=MessageUtil.getMessage(Message.UPDATE_PASSWORD_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		// 添加用户域
		StringBuilder stringBuilder=new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			Optional<String> domain = Optional.ofNullable(listMap.get("domain").toString());
			Optional<String> domainCorp= Optional.ofNullable(listMap.get("domainCorp").toString());
			stringBuilder.append("('"+user+"','"+domainCorp.orElse("")+"','"+domain.orElse("")+"'),");
			((Map<String, Object>) list.get(i)).put("result","数据添加成功");
		}
		//删除最后一个,
		stringBuilder.setLength(stringBuilder.length()-1);
		String addSql="insert into userdomain_ref(userdomain_userid,userdomain_corp,userdomain_domain) values "+stringBuilder.toString()+"";
		ResultUtil addResult=dbHelperService.insert(addSql,DATASOURCE_POSTGRES);
		if(HttpStatus.OK.value()!= (int)addResult.get("code")){
			message=MessageUtil.getMessage(Message.UPDATE_PASSWORD_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.UPDATE_PASSWORD_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message);
	}
}
