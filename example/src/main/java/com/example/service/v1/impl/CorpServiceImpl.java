package com.example.service.v1.impl;

import com.example.constant.Message;
import com.example.exception.CustomHttpException;
import com.example.service.feign.DbHelperService;
import com.example.service.v1.CorpService;
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
 * 公司管理服务实现
 * @author john.xiao
 * @date 2020-12-17 09:48
 */
@Service
public class CorpServiceImpl implements CorpService {
	public static final String CODE = "code";
	private  static Logger logger= LoggerFactory.getLogger(CorpServiceImpl.class);

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
	 * 获取公司信息
	 * @param pageIndex 页码数
	 * @param pageSize 分页大小
	 * @param criteriaList 排序条件
	 * @param corp 公司
	 * @return 返回信息工具类
	 */
	@Override
//	@Cached(name = "corpCache",key = "#corp",cacheType = CacheType.LOCAL,expire = 300)
	public ResultUtil getCorp(int pageIndex, int pageSize, List<?> criteriaList,String corp) {
		String message;
		String sortString=getSortString(criteriaList);
		//判断排序正序倒序
		//postgres使用ilike进行不区分的大小写的模糊查询
		String sql = "select\n" +
				"         corp_id, corp_name, corp_sname, corp_type, corp_max_users, corp_admin, corp_db, corp_host, corp_os, corp_port, corp_scrpt_timeout, corp_idle_timeout, corp_mod_date, corp_mod_prog, corp_mod_user, corp__chr01, corp__chr02, corp__chr03, corp__int01, corp__int02, corp__int03, corp__dte01, corp__dte02, corp__dte03, corp__dec01, corp__dec02, corp__dec03, corp__log01, corp__log02, corp_mod_time\n" +
				"        from public.corp_mstr\n" +
				"        where corp_id ilike '%"+corp+"%' " +
				"        order by "+sortString+" ";
		ResultUtil result=dbHelperService.selectPage(sql,DATASOURCE_POSTGRES,pageIndex,pageSize);
		if(HttpStatus.OK.value()!= (int)result.get(CODE)){
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
			criteriaBuilder.append("corp_id");
		}
		return  criteriaBuilder.toString();
	}
	/**
	 * 添加公司信息
	 * 通过解析List进行批量插入
	 * @param list 添加公司信息列表
	 * @return
	 */
	@Override
	public ResultUtil addCorp(List<?> list) {
		String message;
		StringBuilder stringBuilder=new StringBuilder();
		Optional<Object> corpId;
		Optional<Object> corpName;
		Optional<Object> corpSname;
		Optional<Object> corpType;
		Optional<Object> corpMaxUsers;
		Optional<Object> corpAdmin;
		Optional<Object> corpDb;
		Optional<Object> corpHost;
		Optional<Object> corpOs;
		Optional<Object> corpPort;
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			corpId = Optional.ofNullable(listMap.get("corp_id"));
			corpName = Optional.ofNullable(listMap.get("corp_name"));
			corpSname = Optional.ofNullable(listMap.get("corp_sname"));
			corpType = Optional.ofNullable(listMap.get("corp_type"));
			corpMaxUsers = Optional.ofNullable(listMap.get("corp_max_users"));
			corpAdmin =Optional.ofNullable( listMap.get("corp_admin"));
			corpDb = Optional.ofNullable(listMap.get("corp_db"));
			corpHost = Optional.ofNullable(listMap.get("corp_host"));
			corpOs = Optional.ofNullable(listMap.get("corp_os"));
			corpPort = Optional.ofNullable(listMap.get("corp_port"));
			stringBuilder.append("('"+corpId.orElse("")+"','"+corpName.orElse("")+"','"+corpSname.orElse("")+"','"+corpType.orElse("")+"',"+corpMaxUsers.orElse("0")+",'"+corpAdmin.orElse("")+"'," +
					"'"+corpDb.orElse("")+"','"+corpHost.orElse("")+"','"+corpOs.orElse("")+"'," +
					"'"+corpPort.orElse("")+"'),");
			((Map<String, Object>) list.get(i)).put("result","数据添加成功");
		}
		//删除最后一个,
		stringBuilder.setLength(stringBuilder.length()-1);
		String sql = "insert into public.corp_mstr " +
				"(corp_id,corp_name, corp_sname, corp_type, corp_max_users, corp_admin, corp_db, corp_host, corp_os, corp_port) " +
				"values"+stringBuilder.toString()+";";
		ResultUtil result=dbHelperService.insert(sql,DATASOURCE_POSTGRES);
		//判断是否SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get(CODE)){
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
	public ResultUtil deleteCorp(List<?> list) {
		String sql;
		String message;
		//使用Optional类来防止NPE错误
		Optional corp;

		//删除计数
		int deleteCount=0;
		//使用StringBuilder进行字符串的拼接
		StringBuilder deleteBuild=new StringBuilder();
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap = (Map<String, Object>) list.get(i);
			corp = Optional.ofNullable(listMap.get("corp_id"));
			boolean isCorp= checkCorpExist( corp.orElse("").toString());
			//检测发货信息是否存在
			if(!isCorp){
				message=MessageUtil.getMessage(Message.DELIVERY_NOT_EXIST.getCode());
				logger.error(message);
				listMap.put("result",message);
			}
			else {
				message=MessageUtil.getMessage(Message.DELIVERY_DELETE_SUCCESS.getCode());
				logger.info(message);
				listMap.put("result",message);
				deleteBuild.append("'"+corp.orElse("")+"',");
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
		sql = "delete from corp_mstr where corp_id in ("+deleteBuild.toString()+")";
		ResultUtil result=dbHelperService.delete(sql,DATASOURCE_POSTGRES);
		//判断SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get(CODE)){
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
	public ResultUtil updateCorp(List<?> list) {
		String sql;
		String message;
		Optional<Object> corpId;
		Optional<Object> corpName;
		Optional<Object> corpSname;
		Optional<Object> corpType;
		Optional<Object> corpMaxUsers;
		Optional<Object> corpAdmin;
		Optional<Object> corpDb;
		Optional<Object> corpHost;
		Optional<Object> corpOs;
		Optional<Object> corpPort;
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap = (Map<String, Object>) list.get(i);
			corpId = Optional.ofNullable(listMap.get("corp_id"));
			corpName = Optional.ofNullable(listMap.get("corp_name"));
			corpSname = Optional.ofNullable(listMap.get("corp_sname"));
			corpType = Optional.ofNullable(listMap.get("corp_type"));
			corpMaxUsers = Optional.ofNullable(listMap.get("corp_max_users"));
			corpAdmin =Optional.ofNullable( listMap.get("corp_admin"));
			corpDb = Optional.ofNullable(listMap.get("corp_db"));
			corpHost = Optional.ofNullable(listMap.get("corp_host"));
			corpOs = Optional.ofNullable(listMap.get("corp_os"));
			corpPort = Optional.ofNullable(listMap.get("corp_port"));
			boolean isCorp= checkCorpExist(corpId.orElse("").toString());
			if(!isCorp){
				message=MessageUtil.getMessage(Message.DELIVERY_NOT_EXIST.getCode());
				logger.error(message);
			}
			else {
				sql = "update public.corp_mstr set " +
						"corp_name = '"+corpName.orElse("")+"', corp_sname = '"+corpSname.orElse("")+"', corp_type='"+corpType.orElse("")+"',corp_max_users = "+corpMaxUsers.orElse("0")+", " +
						"corp_admin ='"+corpAdmin.orElse("")+"', corp_db='"+corpDb.orElse("")+"', corp_host='"+corpHost.orElse("")+"'," +
						"corp_os ='"+corpOs.orElse("")+"', corp_port='"+corpPort.orElse("")+"' " +
						" where corp_id= '"+corpId.orElse("")+"' ;";
				ResultUtil result=dbHelperService.update(sql,DATASOURCE_POSTGRES);
				//判断SQL语句是否执行成功
				if(HttpStatus.OK.value()!= (int)result.get(CODE)){
					message= MessageUtil.getMessage(Message.DELIVERY_UPDATE_ERROR.getCode());
					logger.error(message);
					return ResultUtil.error(message);
				}
				message=MessageUtil.getMessage(Message.DELIVERY_UPDATE_SUCCESS.getCode());
				logger.info(message);
			}
			listMap.put("result",message);
		}
		return ResultUtil.ok().put("result",list);
	}
	/**
	 * 导入发货信息
	 * @param workbook Excel文件
	 * @return 返回数据工具类
	 */
	@Override
	public ResultUtil exportCorp(Workbook workbook) {
		//获取ExceL文档第一个表格
		Sheet sheet=workbook.getSheetAt(0);
		//获取表格标题列表
		List<String> titleList= PoiUtils.getTitleList(PoiUtils.getRow(sheet,0));
		//请求结果列表
		List<Map<String,Object>> resultList=new ArrayList<>();
		Map<String,Object> info;

		Optional<Object> corpId;
		Optional<Object> corpName;
		Optional<Object> corpSname;
		Optional<Object> corpType;
		Optional<Object> corpMaxUsers;
		Optional<Object> corpAdmin;
		Optional<Object> corpDb;
		Optional<Object> corpHost;
		Optional<Object> corpOs;
		Optional<Object> corpPort;
		//循环遍历用户信息写入列表
		for (int i = 1; i <=sheet.getLastRowNum() ; i++) {
			//用户运行结果Map
			Map<String,Object> resultMap=new HashMap<>();
			//获取相应行的数据，转换为list
			info=PoiUtils.getRowData(PoiUtils.getRow(sheet,i),titleList);
			corpId = Optional.ofNullable(info.get("corp_id"));
			corpName = Optional.ofNullable(info.get("corp_name"));
			corpSname = Optional.ofNullable(info.get("corp_sname"));
			corpType = Optional.ofNullable(info.get("corp_type"));
			corpMaxUsers = Optional.ofNullable(info.get("corp_max_users"));
			corpAdmin =Optional.ofNullable( info.get("corp_admin"));
			corpDb = Optional.ofNullable(info.get("corp_db"));
			corpHost = Optional.ofNullable(info.get("corp_host"));
			corpOs = Optional.ofNullable(info.get("corp_os"));
			corpPort = Optional.ofNullable(info.get("corp_port"));

			resultMap.put("corp_id",corpId.orElse(""));
			resultMap.put("corp_name",corpName.orElse(""));
			resultMap.put("corp_type",corpType.orElse(""));
			resultMap.put("corp_max_users",corpMaxUsers.orElse("0"));
			resultMap.put("corp_admin",corpAdmin.orElse(""));
			resultMap.put("corp_db",corpDb.orElse(""));
			resultMap.put("corp_host",corpHost.orElse(""));
			resultMap.put("corp_port",corpPort.orElse(""));
			resultMap.put("corp_os",corpOs.orElse(""));
			resultMap.put("corp_sname",corpSname.orElse(""));
			resultList.add(resultMap);
		}
		ResultUtil result=addCorp(resultList);
		String message=MessageUtil.getMessage(Message.EXPORT_DELIVERY_SUCCESS.getCode());
		return ResultUtil.ok(message).put("result",result.get("result"));
	}

	/**
	 * 导出发货历史记录
	 * @param isDelete
	 * @return
	 */
	@Override
	public ResultUtil deriveCorp (String corp, boolean isDelete) {
		String sql = "select\n" +
				"         corp_id, corp_name, corp_sname, corp_type, corp_max_users, corp_admin, corp_db, corp_host, corp_os, corp_port, corp_scrpt_timeout, corp_idle_timeout, corp_mod_date, corp_mod_prog, corp_mod_user, corp__chr01, corp__chr02, corp__chr03, corp__int01, corp__int02, corp__int03, corp__dte01, corp__dte02, corp__dte03, corp__dec01, corp__dec02, corp__dec03, corp__log01, corp__log02, corp_mod_time\n" +
				"        from public.corp_mstr\n" +
				"        where corp_id ilike '%"+corp+"%' ";
		String message;
		ResultUtil result=dbHelperService.select(sql,DATASOURCE_POSTGRES);
		if(HttpStatus.OK.value()!= (int)result.get(CODE)){
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
		titleList.add("corp_id");
		titleList.add("corp_name");
		titleList.add("corp_sname");
		titleList.add("corp_type");
		titleList.add("corp_max_users");
		titleList.add("corp_admin");
		titleList.add("corp_db");
		titleList.add("corp_host");
		titleList.add("corp_os");
		titleList.add("corp_port");

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
			String 	deleteSql = "delete  from corp_mstr " +
					"where corp_id ilike "+corp+" " ;

			ResultUtil deleteResult=dbHelperService.delete(deleteSql,DATASOURCE_POSTGRES);
			if(HttpStatus.OK.value()!= (int)deleteResult.get(CODE)){
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
	 * 检测公司是否存在
	 * @param corp 公司
	 * @return true:存在改该公司 false :不存在该公司
	 * @author john.xiao
	 * @date 2020-12-17 11:05
	 */
	private  boolean checkCorpExist(String corp){
		String message;
		String sql = "select 1 from corp_mstr where corp_id='"+corp+"';";
		ResultUtil result=dbHelperService.select(sql,DATASOURCE_POSTGRES);
		if(HttpStatus.OK.value()!= (int)result.get(CODE)){
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


}
