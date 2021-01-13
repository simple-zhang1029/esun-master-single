package com.example.service.v1.impl;

import com.example.constant.Message;
import com.example.exception.CustomHttpException;
import com.example.service.feign.DbHelperService;
import com.example.service.v1.ExampleService;
import com.example.service.feign.TokenService;
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
 * @author test
 */
@Service
public class ExampleServiceImpl implements ExampleService {
	private  static Logger logger= LoggerFactory.getLogger(ExampleServiceImpl.class);

	@Autowired
	@Lazy
	DbHelperService dbHelperService;
	@Autowired
	@Lazy
	TokenService tokenService;
	//Postgres数据源
	private final static String DATASOURCE_POSTGRES="postgres";
	//Mysql数据源
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
	 * 登入
	 * @param userId 用户Id
	 * @param password 登入密码
	 * @return
	 */
	@Override
	public ResultUtil login(String userId, String password) {

		// 获取用户信息
		//SQL语句
		//调用Postgres数据库使用lower函数进行大小写忽略
		//调用Mysql函数则默认忽略大小写
		String sql = "select user_password as \"password\",user_salt \"salt\" from user_mstr where lower(user_userId) = lower('"+userId+"') ";
		//结果信息
		String message;
		//调用DbHelper中间件服务，所有对数据库的请求均使用该中间件调用
		ResultUtil result=dbHelperService.select(sql,DATASOURCE_POSTGRES);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			//使用MessageUtil.getMessage方法从数据库中获取信息，不允许自己写返回信息
			//返回信息的code在Message枚举类中创建，在数据库中插入相应语言版本的返回信息
			message= MessageUtil.getMessage(Message.USER_INFO_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList list= (ArrayList) result.get("result");
		HashMap resultmap= (HashMap) list.get(0);
		if(resultmap.size()==0){
			message=MessageUtil.getMessage(Message.USER_NOT_EXIST.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		//使用Optional类防止NPE
		Optional<Object> salt= Optional.ofNullable(resultmap.get("salt"));
		Optional<Object> userPassword=Optional.ofNullable(resultmap.get("password"));
		//MD5工具类
		Md5Util md5Util=new Md5Util();
		//
		if(!md5Util.checkPassword(password,salt.orElse("").toString(),userPassword.orElse("").toString())){
//			message=MessageUtil.getMessage(Message.PASSWORD_ERROR.getCode());
			message="test";
			logger.error(message);
			return ResultUtil.error(message);
		}

		//更新token
		ResultUtil tokenResult=tokenService.updateToken(userId);
		if(HttpStatus.OK.value()!= (int)tokenResult.get("code")){
			message=MessageUtil.getMessage(Message.TOKEN_UPDATE_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		//获取token
		Optional token=Optional.ofNullable(tokenResult.get("token"));
		//ResultUtil.put()传输返回结果
		message=MessageUtil.getMessage(Message.LOGIN_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok(message).put("token",token.orElse(""));
	}

	/**
	 * 获取发货信息
	 * @param startDate
	 * @param endDate
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@Override
//	@Cached(name = "deliveryCache",key = "new String[]{#orderId,#customer}",cacheType = CacheType.LOCAL,expire = 300)
	public ResultUtil getDeliveryGoods(String startDate, String endDate, int pageIndex, int pageSize, String customer, String orderId, List<?> criteriaList,List<?> wharfList) {
		String message;
		StringBuilder builder=new StringBuilder();
		Optional wharf;
		if (wharfList.size()>0){
			builder.append(" in ( ");
			for (int i = 0; i < wharfList.size() ; i++) {
				Map<String,Object> listMap= (Map<String, Object>) wharfList.get(i);
				wharf =Optional.ofNullable(listMap.get("wharf"));
				builder.append("'"+wharf.orElse("")+"',");
			}
			builder.setLength(builder.length()-1);
			builder.append(" ) ");
		}
		else {
			builder.append(" like '%%' ");
		}

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
			criteriaBuilder.append("order_id");
		}
		//判断排序正序倒序
		//postgres使用ilike进行不区分的大小写的模糊查询
		String sql = "select id, order_id as \"orderId\", customer, wharf, car_no as \"carNo\", plan_delivery_no as \"planDeliveryNo\", " +
				"delivery_no as \"deliveryNo\", plan_arrived_time as \"planArrivedTime\", arrived_time as \"arrivedTime\", " +
				"plan_leave_time as \"planLeaveTime\", leave_time as \"leaveTime\", " +
				"delivery_status as \"deliveryStatus\", plan_date \"planDate\"" +
				" from delivery_goods " +
				"where plan_date between '" + startDate + "' and '" + endDate + "' " +
				"and customer ilike '%"+customer+"%' and order_id ilike '%"+orderId+"%' and wharf "+builder.toString()+"" +
				"order by "+criteriaBuilder+" ";
		ResultUtil result=dbHelperService.selectPage(sql,DATASOURCE_POSTGRES,pageIndex,pageSize);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.DELIVERY_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		message=MessageUtil.getMessage(Message.DELIVERY_GET_SUCCESS.getCode());
		logger.info(message);
		int pageCount= (int) result.get("pageCount");
		//获取总条数
		int count= (int) result.get("count");
		return ResultUtil.ok().put("msg",message).put("result",list).put("pageCount",pageCount).put("count",count);
	}

	/**
	 * 添加发货信息
	 * @param list
	 * @return
	 */
	@Override
	public ResultUtil addDeliveryGoods(List<?> list) {
		Optional orderId;
		Optional customer;
		Optional wharf;
		Optional carNo;
		Optional planDeliveryNo;
		Optional deliveryNo;
		Optional planArrivedTime;
		Optional arrivedTime;
		Optional planLeaveTime;
		Optional leaveTime;
		Optional deliveryStatus;
		Optional planDate;
		String message;
		StringBuilder stringBuilder=new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			orderId = Optional.ofNullable(listMap.get("orderId"));
			customer = Optional.ofNullable(listMap.get("customer"));
			wharf = Optional.ofNullable(listMap.get("wharf"));
			carNo = Optional.ofNullable(listMap.get("carNo"));
			planDeliveryNo = Optional.ofNullable(listMap.get("planDeliveryNo"));
			deliveryNo = Optional.ofNullable(listMap.get("deliveryNo"));
			planArrivedTime = Optional.ofNullable(listMap.get("planArrivedTime"));
			arrivedTime = Optional.ofNullable(listMap.get("arrivedTime"));
			planLeaveTime = Optional.ofNullable(listMap.get("planLeaveTime"));
			leaveTime = Optional.ofNullable(listMap.get("leaveTime"));
			deliveryStatus = Optional.ofNullable(listMap.get("deliveryStatus"));
			planDate = Optional.ofNullable(listMap.get("planDate"));
			stringBuilder.append("('"+orderId.orElse("")+"','"+customer.orElse("")+"','"+wharf.orElse("")+"','"+carNo.orElse("")+"'," +
					""+planDeliveryNo.orElse("0")+","+deliveryNo.orElse("0")+",'"+planArrivedTime.orElse("00:00:00")+"'," +
					"'"+arrivedTime.orElse("00:00:00")+"','"+planLeaveTime.orElse("00:00:00")+"','"+leaveTime.orElse("00:00:00")+"','"+deliveryStatus.orElse("0")+"','"+planDate.orElse("2000-01-01")+"'),");
			((Map<String, Object>) list.get(i)).put("result","数据添加成功");
		}
		//删除最后一个,
		stringBuilder.setLength(stringBuilder.length()-1);
		String sql = "insert into delivery_goods " +
				"( order_id, customer, wharf, car_no, plan_delivery_no, delivery_no, " +
				"plan_arrived_time, arrived_time, plan_leave_time, leave_time, delivery_status, plan_date) " +
				"values"+stringBuilder.toString()+";";
		ResultUtil result=dbHelperService.insert(sql,"postgres_test");
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
	public ResultUtil deleteDeliveryGoods(List<?> list) {
		String sql;
		String message;
		//使用Optional类来防止NPE错误
		Optional id;

		//删除计数
		int deleteCount=0;
		//使用StringBuilder进行字符串的拼接
		StringBuilder deleteBuild=new StringBuilder();
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap = (Map<String, Object>) list.get(i);
			id = Optional.ofNullable(listMap.get("id"));
			boolean isDelivery= checkDeliveryExist((Integer) id.orElse(-1));
			//检测发货信息是否存在
			if(!isDelivery){
				message=MessageUtil.getMessage(Message.DELIVERY_NOT_EXIST.getCode());
				logger.error(message);
				listMap.put("result",message);
			}
			else {
				message=MessageUtil.getMessage(Message.DELIVERY_DELETE_SUCCESS.getCode());
				logger.info(message);
				listMap.put("result",message);
				deleteBuild.append(" '"+id.orElse(-1)+"',");
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
		sql = "delete from delivery_goods where id in ("+deleteBuild.toString()+");";
		ResultUtil result=dbHelperService.delete(sql,"postgres_test");
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
	public ResultUtil updateDeliveryGoods(List<?> list) {
		String sql;
		String message;
		//使用Optional类来防止NPE错误
		Optional id;
		Optional orderId;
		Optional customer;
		Optional wharf;
		Optional carNo;
		Optional planDeliveryNo;
		Optional deliveryNo;
		Optional planArrivedTime;
		Optional arrivedTime;
		Optional planLeaveTime;
		Optional leaveTime;
		Optional deliveryStatus;
		Optional planDate;
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap = (Map<String, Object>) list.get(i);
			id = Optional.ofNullable(listMap.get("id"));
			orderId = Optional.ofNullable(listMap.get("orderId"));
			customer = Optional.ofNullable(listMap.get("customer"));
			wharf = Optional.ofNullable(listMap.get("wharf"));
			carNo = Optional.ofNullable(listMap.get("carNo"));
			planDeliveryNo = Optional.ofNullable(listMap.get("planDeliveryNo"));
			deliveryNo = Optional.ofNullable(listMap.get("deliveryNo"));
			planArrivedTime = Optional.ofNullable(listMap.get("planArrivedTime"));
			arrivedTime = Optional.ofNullable(listMap.get("arrivedTime"));
			planLeaveTime = Optional.ofNullable(listMap.get("planLeaveTime"));
			leaveTime = Optional.ofNullable(listMap.get("leaveTime"));
			deliveryStatus = Optional.ofNullable(listMap.get("deliveryStatus"));
			planDate = Optional.ofNullable(listMap.get("planDate"));
			boolean isDeliveryExist= checkDeliveryExist((Integer) id.orElse(-1));
			if(!isDeliveryExist){
				message=MessageUtil.getMessage(Message.DELIVERY_NOT_EXIST.getCode());
				logger.error(message);
				listMap.put("result",message);
			}
			else {
				sql = "update delivery_goods set " +
						"order_id = '"+orderId.orElse("")+"', customer = '"+customer.orElse("")+"', wharf = '"+wharf.orElse("")+"', " +
						"car_no ='"+carNo.orElse("")+"', plan_delivery_no="+planDeliveryNo.orElse("0")+", delivery_no="+deliveryNo.orElse("0")+"," +
						"plan_arrived_time ='"+planArrivedTime.orElse("00:00:00")+"', arrived_time='"+arrivedTime.orElse("00:00:00")+"', plan_leave_time='"+planLeaveTime.orElse("00:00:00")+"', " +
						"leave_time='"+leaveTime.orElse("00:00:00")+"', delivery_status ='"+deliveryStatus.orElse("0")+"', plan_date='"+planDate.orElse("2000-01-01")+"'" +
						" where id= "+id.orElse(-1)+" ;";
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
	public ResultUtil exportDeliveryGoods(Workbook workbook) {
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
			Optional orderId=Optional.ofNullable(info.get("订单号"));
			Optional customer=Optional.ofNullable(info.get("客户"));
			Optional wharf=Optional.ofNullable(info.get("码头"));
			Optional planDeliveryNo=Optional.ofNullable(info.get("计划发货数量"));
			Optional deliveryNo=Optional.ofNullable(info.get("实际发货数量"));
			Optional planArrivedTime=Optional.ofNullable(info.get("计划到达时间"));
			Optional arrivedTime=Optional.ofNullable(info.get("实际到达时间"));
			Optional carNo=Optional.ofNullable(info.get("车牌号"));
			Optional planLeaveTime=Optional.ofNullable(info.get("计划离开时间"));
			Optional leaveTime=Optional.ofNullable(info.get("离开时间"));
			Optional deliveryStatus=Optional.ofNullable(info.get("发货状态"));
			Optional planDate=Optional.ofNullable(info.get("计划日期"));

			resultMap.put("orderId",orderId.orElse(""));
			resultMap.put("customer",customer.orElse(""));
			resultMap.put("wharf",wharf.orElse(""));
			resultMap.put("planDeliveryNo",planDeliveryNo.orElse("0"));
			resultMap.put("deliveryNo",deliveryNo.orElse("0"));
			resultMap.put("planArrivedTime",planArrivedTime.orElse("00:00:00"));
			resultMap.put("arrivedTime",arrivedTime.orElse("00:00:00"));
			resultMap.put("carNo",carNo.orElse(""));
			resultMap.put("planLeaveTime",planLeaveTime.orElse("00:00:00"));
			resultMap.put("leaveTime",leaveTime.orElse("00:00:00"));
			resultMap.put("planDate",planDate.orElse("2000-01-01"));
			resultMap.put("deliveryStatus",deliveryStatus.orElse("0"));
			resultList.add(resultMap);
		}
		ResultUtil result=addDeliveryGoods(resultList);
		String message=MessageUtil.getMessage(Message.EXPORT_DELIVERY_SUCCESS.getCode());
		return ResultUtil.ok(message).put("result",result.get("result"));
	}

	/**
	 * 导出发货历史记录
	 * @param startDate
	 * @param endDate
	 * @param orderId
	 * @param isDelete
	 * @return
	 */
	@Override
	public ResultUtil deriveDeliveryHistory(String startDate, String endDate, List<?> customerList, String orderId, boolean isDelete) {
		StringBuilder customerBuilder=new StringBuilder();
		for (int i = 0; i < customerList.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) customerList.get(i);
			Optional customer = Optional.ofNullable(listMap.get("customer"));
			customerBuilder.append("'"+customer.orElse("")+"',");
		}
		customerBuilder.setLength(customerBuilder.length()-1);
		String sql= "select id as \"ID\", order_id as \"订单号\", customer as \"客户\", wharf as \"码头\", car_no as \"车牌号\", plan_delivery_no as \"计划发货数量\", " +
				"delivery_no as \"实际发货数量\", plan_arrived_time as \"计划到达时间\", arrived_time as \"实际到达时间\", " +
				"plan_leave_time as \"计划离开时间\", leave_time as \"实际离开时间\", " +
				"delivery_status as \"发货状态\", plan_date \"计划日期\"" +
				" from delivery_goods " +
				"where plan_date between '"+startDate+"' and '"+endDate+"' and order_id ilike '%"+orderId+"%' and customer in ("+customerBuilder+") \n";
		String message;
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
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
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			listMap.put("发货状态",listMap.get("statusName").toString());
			list.set(i, listMap);
		}
		List<String> titleList=new ArrayList<>();
		titleList.add("订单号");
		titleList.add("客户");
		titleList.add("码头");
		titleList.add("计划发货数量");
		titleList.add("实际发货数量");
		titleList.add("计划到达时间");
		titleList.add("实际到达时间");
		titleList.add("车牌号");
		titleList.add("计划离开时间");
		titleList.add("实际离开时间");
		titleList.add("发货状态");
		titleList.add("计划日期");
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
			String 	deleteSql = "delete  from delivery_goods " +
					"where id in " +
					"(" +
					"select id from delivery_goods   " +
					"where plan_date between '"+startDate+"' and '"+endDate+"' and order_id ilike '%"+orderId+"%' and customer in ("+customerBuilder+") " +
					");";
			ResultUtil deleteResult=dbHelperService.delete(deleteSql,"postgres_test");
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
	 * 检测发货信息是否存在
	 * @param id
	 * @return
	 */
	private  boolean checkDeliveryExist(int id){
		String message;
		String sql = "select 1 from delivery_goods where id='"+id+"';";
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
