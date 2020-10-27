package esun.wharf.service.impl;

import esun.wharf.constant.Message;
import esun.wharf.exception.CustomHttpException;
import esun.wharf.service.DbHelperService;
import esun.wharf.service.ReceivingBaseDataService;
import esun.wharf.service.ReceivingService;
import esun.wharf.utils.*;
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
public class ReceivingServiceImpl implements ReceivingService {
	private  static Logger logger= LoggerFactory.getLogger(ReceivingServiceImpl.class);
	@Autowired
	@Lazy
	DbHelperService dbHelperService;

	@Value("${file.diskPath}")
	String diskPath;

	@Value("${ftp.url}")
	String ftpUrl;

	@Value("${ftp.port}")
	int ftpPort;

	@Value(("${ftp.username}"))
	String ftpUsername;

	@Value(("${ftp.password}"))
	String ftpPassword;


	@Autowired
	ReceivingBaseDataService receivingBaseDataService;
	/**
	 *获取供应商信息
	 * @param supplierName
	 * @return
	 */
	@Override
	public ResultUtil getSupplierInfo(String supplierName) {
		String message;
		String SupplierSql= "select id,data_type as \"dataType\",data_name as \"dataName\",value_1 as \"supplier\" " +
				"from receiving_base_data where data_type =1 and value_1 like '%"+supplierName+"%'";
		ResultUtil supplierResult=dbHelperService.select(SupplierSql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)supplierResult.get("code")){
			message= MessageUtil.getMessage(Message.CUSTOMER_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> supplierList= (ArrayList) supplierResult.get("result");
		int listSize=supplierList.size();
		for (int j = 0; j <listSize ; j++) {
			String supplier = supplierList.get(j).get("supplier").toString();
			//获取订单信息
			String orderSql="select id,data_type as \"dataType\",data_name as \"dataName\",value_1 as \"orderNo\",value_2 as \"supplier\" " +
					"from receiving_base_data where data_type ='3' and value_2 ='"+supplier+"'";
			ResultUtil orderResult=dbHelperService.select(orderSql,"postgres_test");
			if(HttpStatus.OK.value()!= (int)orderResult.get("code")){
				message=MessageUtil.getMessage(Message.ORDER_GET_ERROR.getCode());
				logger.error(message);
				return ResultUtil.error(message);
			}
			ArrayList<HashMap> orderList= (ArrayList) orderResult.get("result");
			supplierList.get(j).put("order",orderList);
		}
		message=MessageUtil.getMessage(Message.CUSTOMER_GET_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("result",supplierList);
	}

	/**
	 * 获取供应商信息
	 * @param list
	 * @return
	 */
	@Override
	public ResultUtil getSupplierInfo(List<?> list) {
		String message;
		int i=0;
		do {
			Map<String,Object> listMap = (Map<String, Object>) list.get(i);
			Optional supplierName=Optional.ofNullable(listMap.get("supplierName"));
			String supplierSql= "select id,data_type as \"dataType\",data_name as \"dataName\",value_1 as \"supplier\" " +
					"from receiving_base_data where data_type =1 and value_1 = '"+supplierName.orElse("")+"';";
			ResultUtil supplierResult=dbHelperService.select(supplierSql,"postgres_test");
			if(HttpStatus.OK.value()!= (int)supplierResult.get("code")){
				message=MessageUtil.getMessage(Message.CUSTOMER_GET_ERROR.getCode());
				logger.error(message);
				((Map<String, Object>) list.get(i)).put("msg",message);
			}
			ArrayList<HashMap> supplierList= (ArrayList) supplierResult.get("result");
			ArrayList<HashMap> orderList=new ArrayList<>();
			int listSize=supplierList.size();
			for (int j = 0; j <listSize ; j++) {
				String supplier = supplierList.get(j).get("supplier").toString();
				//获取订单信息
				String orderSql="select id,data_type as \"dataType\",data_name as \"dataName\",value_1 as \"orderNo\",value_2 as \"supplier\" " +
						"from receiving_base_data where data_type ='3' and value_2 ='"+supplier+"'";
				ResultUtil orderResult=dbHelperService.select(orderSql,"postgres_test");
				if(HttpStatus.OK.value()!= (int)orderResult.get("code")){
					message=MessageUtil.getMessage(Message.ORDER_GET_ERROR.getCode());
					logger.error(message);
					return ResultUtil.error(message);
				}
				orderList= (ArrayList) orderResult.get("result");
//				supplierList.get(j).put("order",orderList);
			}
			message= "获取供应商信息成功";
			logger.info(message);
			((Map<String, Object>) list.get(i)).put("msg",message);
			((Map<String, Object>) list.get(i)).put("order",orderList);
			i++;
		}
		while(i<list.size());
		return ResultUtil.ok().put("result",list);
	}

	/**
	 * 获取收货信息
	 * @param startDate
	 * @param endDate
	 * @param pageIndex
	 * @param pageSize
	 * @param criteria
	 * @param sort
	 * @param supplier
	 * @param orderId
	 * @param wharfList
	 * @return
	 */
	@Override
	public ResultUtil getReceivingGoods(String startDate, String endDate, int pageIndex, int pageSize, String criteria, int sort, String supplier, String orderId, List<?> wharfList) {
		String message;
		String sql;
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
		//判断排序正序倒序
		if(sort == 0) {
			sql = "select id, order_id as \"orderId\", supplier, wharf, car_no as \"carNo\", plan_receiving_no as \"planReceivingNo\", " +
					"receiving_no as \"receivingNo\", plan_arrived_time as \"planArrivedTime\", arrived_time as \"arrivedTime\", " +
					"plan_leave_time as \"planLeaveTime\", leave_time as \"leaveTime\", " +
					"receiving_status as \"receivingStatus\", plan_date \"planDate\"" +
					" from receiving_goods " +
					"where plan_date between '" + startDate + "' and '" + endDate + "' " +
					"and supplier like '%"+supplier+"%' and order_id like '%"+orderId+"%' and wharf "+builder.toString()+"" +
					"order by "+criteria+" ";
		}
		else {
			sql = "select id, order_id as \"orderId\", supplier, wharf, car_no as \"carNo\", plan_receiving_no as \"planReceivingNo\", " +
					"receiving_no as \"receivingNo\", plan_arrived_time as \"planArrivedTime\", arrived_time as \"arrivedTime\", " +
					"plan_leave_time as \"planLeaveTime\", leave_time as \"leaveTime\", " +
					"receiving_status as \"receivingStatus\", plan_date \"planDate\"" +
					" from receiving_goods " +
					"where plan_date between '" + startDate + "' and '" + endDate + "' " +
					"and supplier like '%"+supplier+"%' and order_id like '%"+orderId+"%' and wharf  "+builder.toString()+"" +
					"order by "+criteria+" desc";
		}
		ResultUtil result=dbHelperService.selectPage(sql,"postgres_test",pageIndex,pageSize);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.RECEIVING_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		//输出起始序号
		int no=(pageIndex-1)*pageSize+1;
		for (int i = 0; i <list.size() ; i++) {
			list.get(i).put("no",no+i);
		}
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			Optional receivingStatus=Optional.ofNullable(listMap.get("receivingStatus"));
			//判断状态
			//等待发货
			if("0".equals(receivingStatus.orElse("0"))){
				//将到达时间，离开时间，发货数量设置空
				listMap.put("arrivedTime","");
				listMap.put("leaveTime","");
				listMap.put("receivingNo","");
			}
			//正在发货
			if("1".equals(receivingStatus.orElse("0"))){
				listMap.put("leaveTime","");
				listMap.put("receivingNo","");
			}
		}
		list= (ArrayList<HashMap>) addReceivingStatusName(list);
		message=MessageUtil.getMessage(Message.RECEIVING_GET_SUCCESS.getCode());
		logger.info(message);
		int pageCount= (int) result.get("pageCount");
		//获取总条数
		int count= (int) result.get("count");
		return ResultUtil.ok().put("msg",message).put("result",list).put("pageCount",pageCount).put("count",count);
	}

	/**
	 * 获取收货信息
	 * @param list
	 * @return
	 */
	@Override
	public ResultUtil addReceivingGoods(List<?> list) {
		Optional orderId;
		Optional supplier;
		Optional wharf;
		Optional carNo;
		Optional planReceivingNo;
		Optional receivingNo;
		Optional planArrivedTime;
		Optional arrivedTime;
		Optional planLeaveTime;
		Optional leaveTime;
		Optional receivingStatus;
		Optional planDate;
		String message;
		StringBuilder stringBuilder=new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			orderId = Optional.ofNullable(listMap.get("orderId"));
			supplier = Optional.ofNullable(listMap.get("supplier"));
			wharf = Optional.ofNullable(listMap.get("wharf"));
			carNo = Optional.ofNullable(listMap.get("carNo"));
			planReceivingNo = Optional.ofNullable(listMap.get("planReceivingNo"));
			receivingNo = Optional.ofNullable(listMap.get("receivingNo"));
			planArrivedTime = Optional.ofNullable(listMap.get("planArrivedTime"));
			arrivedTime = Optional.ofNullable(listMap.get("arrivedTime"));
			planLeaveTime = Optional.ofNullable(listMap.get("planLeaveTime"));
			leaveTime = Optional.ofNullable(listMap.get("leaveTime"));
			receivingStatus = Optional.ofNullable(listMap.get("receivingStatus"));
			planDate = Optional.ofNullable(listMap.get("planDate"));
			stringBuilder.append("('"+orderId.orElse("")+"','"+supplier.orElse("")+"','"+wharf.orElse("")+"','"+carNo.orElse("")+"'," +
					""+planReceivingNo.orElse("0")+","+receivingNo.orElse("0")+",'"+planArrivedTime.orElse("00:00:00")+"'," +
					"'"+arrivedTime.orElse("00:00:00")+"','"+planLeaveTime.orElse("00:00:00")+"','"+leaveTime.orElse("00:00:00")+"','"+receivingStatus.orElse("0")+"','"+planDate.orElse("2000-01-01")+"'),");
			((Map<String, Object>) list.get(i)).put("result","数据添加成功");
		}
		//删除最后一个,
		stringBuilder.setLength(stringBuilder.length()-1);
		String sql = "insert into receiving_goods " +
				"( order_id, supplier, wharf, car_no, plan_receiving_no, receiving_no, " +
				"plan_arrived_time, arrived_time, plan_leave_time, leave_time, receiving_status, plan_date) " +
				"values"+stringBuilder.toString()+";";
		ResultUtil result=dbHelperService.insert(sql,"postgres_test");
		//判断是否SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.RECEIVING_ADD_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.RECEIVING_ADD_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",list);
	}

	/**
	 * 删除收货信息
	 * @param list
	 * @return
	 */
	@Override
	public ResultUtil deleteReceivingGoods(List<?> list) {
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
			boolean isReceiving= checkReceivingExist((Integer) id.orElse(-1));
			//检测路由是否存在
			if(!isReceiving){
				message=MessageUtil.getMessage(Message.RECEIVING_NOT_EXIST.getCode());
				logger.error(message);
				listMap.put("result",message);
			}
			else {
				message=MessageUtil.getMessage(Message.RECEIVING_DELETE_SUCCESS.getCode());
				logger.info(message);
				listMap.put("result",message);
				deleteBuild.append(" '"+id.orElse(-1)+"',");
				deleteCount++;
			}
		}
		//如果没有路由被删除，则返回结果
		if (deleteCount == 0 ){
			message=MessageUtil.getMessage(Message.RECEIVING_NOT_EXIST.getCode());
			logger.error(message);
			return  ResultUtil.ok().put("msg",message).put("result",list);
		}
		//移除字符串最后一个,
		deleteBuild.setLength(deleteBuild.length()-1);
		sql = "delete from receiving_goods where id in ("+deleteBuild.toString()+");";
		ResultUtil result=dbHelperService.delete(sql,"postgres_test");
		//判断SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.RECEIVING_DELETE_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.RECEIVING_DELETE_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",list);
	}

	/**
	 * 更新收货信息
	 * @param list
	 * @return
	 */
	@Override
	public ResultUtil updateReceivingGoods(List<?> list) {
		String sql;
		String message;
		//使用Optional类来防止NPE错误
		Optional id;
		Optional orderId;
		Optional supplier;
		Optional wharf;
		Optional carNo;
		Optional planReceivingNo;
		Optional receivingNo;
		Optional planArrivedTime;
		Optional arrivedTime;
		Optional planLeaveTime;
		Optional leaveTime;
		Optional receivingStatus;
		Optional planDate;
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap = (Map<String, Object>) list.get(i);
			id = Optional.ofNullable(listMap.get("id"));
			orderId = Optional.ofNullable(listMap.get("orderId"));
			supplier = Optional.ofNullable(listMap.get("supplier"));
			wharf = Optional.ofNullable(listMap.get("wharf"));
			carNo = Optional.ofNullable(listMap.get("carNo"));
			planReceivingNo = Optional.ofNullable(listMap.get("planReceivingNo"));
			receivingNo = Optional.ofNullable(listMap.get("receivingNo"));
			planArrivedTime = Optional.ofNullable(listMap.get("planArrivedTime"));
			arrivedTime = Optional.ofNullable(listMap.get("arrivedTime"));
			planLeaveTime = Optional.ofNullable(listMap.get("planLeaveTime"));
			leaveTime = Optional.ofNullable(listMap.get("leaveTime"));
			receivingStatus = Optional.ofNullable(listMap.get("receivingStatus"));
			planDate = Optional.ofNullable(listMap.get("planDate"));
			boolean isReceivingExist= checkReceivingExist((Integer) id.orElse(-1));
			if(!isReceivingExist){
				message=MessageUtil.getMessage(Message.RECEIVING_NOT_EXIST.getCode());
				logger.error(message);
				listMap.put("result",message);
			}
			else {
				sql = "update receiving_goods set " +
						"order_id = '"+orderId.orElse("")+"', supplier = '"+supplier.orElse("")+"', wharf = '"+wharf.orElse("")+"', " +
						"car_no ='"+carNo.orElse("")+"', plan_receiving_no="+planReceivingNo.orElse("0")+", receiving_no="+receivingNo.orElse("0")+"," +
						"plan_arrived_time ='"+planArrivedTime.orElse("00:00:00")+"', arrived_time='"+arrivedTime.orElse("00:00:00")+"', plan_leave_time='"+planLeaveTime.orElse("00:00:00")+"', " +
						"leave_time='"+leaveTime.orElse("00:00:00")+"', receiving_status ='"+receivingStatus.orElse("0")+"', plan_date='"+planDate.orElse("2000-01-01")+"'" +
						" where id= "+id.orElse(-1)+" ;";
				ResultUtil result=dbHelperService.update(sql,"postgres_test");
				//判断SQL语句是否执行成功
				if(HttpStatus.OK.value()!= (int)result.get("code")){
					message=MessageUtil.getMessage(Message.RECEIVING_UPDATE_ERROR.getCode());
					logger.error(message);
					return ResultUtil.error(message);
				}
				message=MessageUtil.getMessage(Message.RECEIVING_UPDATE_SUCCESS.getCode());
				logger.info(message);
				listMap.put("result",message);
			}
		}

		return ResultUtil.ok().put("result",list);
	}

	/**
	 * 导入收货列表
	 * @param workbook
	 * @return
	 */
	@Override
	public ResultUtil exportReceivingGoods(Workbook workbook) {
		//获取ExceL文档第一个表格
		Sheet sheet=workbook.getSheetAt(0);
		//获取表格标题列表
		List titleList= PoiUtils.getTitleList(PoiUtils.getRow(sheet,0));
		//请求结果列表
		List<Map<String,Object>> resultList=new ArrayList<>();
		Map<String,Object> info;
		ResultUtil statusResult= receivingBaseDataService.getBaseData("状态",1,10,"id",0);
		ArrayList statusList= (ArrayList) statusResult.get("list");
		//循环遍历用户信息写入列表
		for (int i = 1; i <=sheet.getLastRowNum() ; i++) {
			//获取相应行的数据，转换为list
			info=PoiUtils.getRowData(PoiUtils.getRow(sheet,i),titleList);
			Optional orderId=Optional.ofNullable(info.get("订单号"));
			Optional supplier=Optional.ofNullable(info.get("供应商"));
			Optional wharf=Optional.ofNullable(info.get("码头"));
			Optional planReceivingNo=Optional.ofNullable(info.get("计划收货数量"));
			Optional ReceivingNo=Optional.ofNullable(info.get("实际收货数量"));
			Optional planArrivedTime=Optional.ofNullable(info.get("计划到达时间"));
			Optional arrivedTime=Optional.ofNullable(info.get("实际到达时间"));
			Optional carNo=Optional.ofNullable(info.get("车牌号"));
			Optional planLeaveTime=Optional.ofNullable(info.get("计划离开时间"));
			Optional leaveTime=Optional.ofNullable(info.get("离开时间"));
			Optional receivingStatus=Optional.ofNullable(info.get("收货状态"));
			Optional planDate=Optional.ofNullable(info.get("计划日期"));
			Map<String,Object> resultMap=new HashMap<>();
			//用户运行结果Map
			//根据状态名获取状态码
			for (int j = 0; j < statusList.size(); j++) {
				Map<String,Object> status= (Map<String, Object>) statusList.get(j);
				Optional statusNo=Optional.ofNullable(status.get("value1"));
				Optional statusName=Optional.ofNullable(status.get("value2"));
				if(statusName.equals(receivingStatus)){
					resultMap.put("receivingStatus",statusNo.orElse("0"));
				}
			}
			resultMap.put("orderId",orderId.orElse(""));
			resultMap.put("supplier",supplier.orElse(""));
			resultMap.put("wharf",wharf.orElse(""));
			resultMap.put("planReceivingNo",planReceivingNo.orElse("0"));
			resultMap.put("ReceivingNo",ReceivingNo.orElse("0"));
			resultMap.put("planArrivedTime",planArrivedTime.orElse("00:00:00"));
			resultMap.put("arrivedTime",arrivedTime.orElse("00:00:00"));
			resultMap.put("carNo",carNo.orElse(""));
			resultMap.put("planLeaveTime",planLeaveTime.orElse("00:00:00"));
			resultMap.put("leaveTime",leaveTime.orElse("00:00:00"));
			resultMap.put("planDate",planDate.orElse("2000-01-01"));
			resultList.add(resultMap);
		}
		ResultUtil result=addReceivingGoods(resultList);
		String message=MessageUtil.getMessage(Message.EXPORT_RECEIVING_SUCCESS.getCode());
		return ResultUtil.ok(message).put("result",result.get("result"));
	}

	/**
	 * 导出收货历史记录
	 * @param startDate
	 * @param endDate
	 * @param supplierList
	 * @param orderId
	 * @param isDelete
	 * @return
	 */
	@Override
	public ResultUtil deriveReceivingHistory(String startDate, String endDate, List<?> supplierList, String orderId, boolean isDelete) {
		StringBuilder supplierBuilder=new StringBuilder();
		for (int i = 0; i < supplierList.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) supplierList.get(i);
			Optional supplier = Optional.ofNullable(listMap.get("supplier"));
			supplierBuilder.append("'"+supplier.orElse("")+"',");
		}
		supplierBuilder.setLength(supplierBuilder.length()-1);
		String sql= "select id as \"ID\", order_id as \"订单号\", supplier as \"供应商\", wharf as \"码头\", car_no as \"车牌号\", plan_receiving_no as \"计划收货数量\", " +
				"receiving_no as \"实际收货数量\", plan_arrived_time as \"计划到达时间\", arrived_time as \"实际到达时间\", " +
				"plan_leave_time as \"计划离开时间\", leave_time as \"实际离开时间\", " +
				"receiving_status as \"收货状态\", plan_date \"计划日期\"" +
				" from receiving_goods " +
				"where plan_date between '"+startDate+"' and '"+endDate+"' and order_id like '%"+orderId+"%' and supplier in ("+supplierBuilder+") \n";
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
		list= (ArrayList<HashMap>) addReceivingStatusName(list);
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			Optional deliveryStatus=Optional.ofNullable(listMap.get("收货状态"));
			//判断状态
			//等待发货
			if("0".equals(deliveryStatus.orElse("0"))){
				//将到达时间，离开时间，发货数量设置空
				listMap.put("实际到达时间","");
				listMap.put("实际离开时间","");
				listMap.put("实际收货数量","");
			}
			//正在发货
			if("1".equals(deliveryStatus.orElse("0"))){
				listMap.put("实际离开时间","");
				listMap.put("实际收货数量","");
			}
			listMap.put("收货状态",listMap.get("statusName").toString());
			list.set(i, listMap);
		}
		List<String> titleList=new ArrayList<>();
		titleList.add("ID");
		titleList.add("订单号");
		titleList.add("供应商");
		titleList.add("码头");
		titleList.add("计划收货数量");
		titleList.add("实际收货数量");
		titleList.add("计划到达时间");
		titleList.add("实际到达时间");
		titleList.add("车牌号");
		titleList.add("计划离开时间");
		titleList.add("实际离开时间");
		titleList.add("收货状态");
		titleList.add("计划日期");
		String file= ExcelUtils.createMapListExcel(list,diskPath,titleList);
		String ftpFile= RandomStringUtils.randomAlphanumeric(32)+".xls";
		String ftpPath="/wharf/";
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
			String 	deleteSql = "delete  from receiving_goods " +
					"where id in " +
					"(" +
					"select id from receiving_goods   " +
					"where plan_date between '"+startDate+"' and '"+endDate+"' and order_id like '%"+orderId+"%' and supplier in ("+supplierBuilder+") " +
					");";
			ResultUtil deleteResult=dbHelperService.delete(deleteSql,"postgres_test");
			if(HttpStatus.OK.value()!= (int)deleteResult.get("code")){
				message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
				logger.error(message);
				return ResultUtil.error(message);
			}
		}
		message=MessageUtil.getMessage(Message.DERIVE_RECEIVING_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",path);
	}

	/**
	 * 获取收货状态
	 * @return
	 */
	@Override
	public ResultUtil getReceivingStatus() {
		String sql= "select value_1 as \"statusNo\", value_2 as \"statusName\" from  receiving_base_data where data_type = 0 ";
		String message;
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		message=MessageUtil.getMessage(Message.BASE_GET_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",list);
	}

	/**
	 * 获取收货看板信息
	 * @param startDate
	 * @param endDate
	 * @param pageIndex
	 * @param pageSize
	 * @param criteria
	 * @param sort
	 * @param wharfList
	 * @return
	 */
	@Override
	public ResultUtil getBoardInfo(String startDate, String endDate, int pageIndex, int pageSize, String criteria, int sort, List<?> wharfList) {
		String message;
		String sql;
		StringBuilder builder=new StringBuilder();
		Optional wharf;
		for (int i = 0; i < wharfList.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) wharfList.get(i);
			wharf =Optional.ofNullable(listMap.get("wharf"));
			builder.append("'"+wharf.orElse("")+"',");
		}
		builder.setLength(builder.length()-1);
		//判断排序正序倒序
		if(sort == 0) {
			sql = "select id, order_id as \"orderId\", supplier, wharf, car_no as \"carNo\", plan_receiving_no as \"planReceivingNo\", " +
					"receiving_no as \"receivingNo\", plan_arrived_time as \"planArrivedTime\", arrived_time as \"arrivedTime\", " +
					"plan_leave_time as \"planLeaveTime\", leave_time as \"leaveTime\", " +
					"receiving_status as \"receivingStatus\", plan_date \"planDate\"" +
					" from receiving_goods " +
					"where plan_date between '" + startDate + "' and '" + endDate + "' " +
					"and wharf in  ("+builder.toString()+")" +
					"order by "+criteria+" ";
		}
		else {
			sql = "select id, order_id as \"orderId\", supplier, wharf, car_no as \"carNo\", plan_receiving_no as \"planReceivingNo\", " +
					"receiving_no as \"receivingNo\", plan_arrived_time as \"planArrivedTime\", arrived_time as \"arrivedTime\", " +
					"plan_leave_time as \"planLeaveTime\", leave_time as \"leaveTime\", " +
					"receiving_status as \"receivingStatus\", plan_date \"planDate\"" +
					" from receiving_goods " +
					"where plan_date between '" + startDate + "' and '" + endDate + "' " +
					" and wharf in  ("+builder.toString()+")" +
					"order by "+criteria+" desc";
		}
		//进行查询
		ResultUtil result=dbHelperService.selectPage(sql,"postgres_test",pageIndex,pageSize);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.RECEIVING_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}

		//添加状态描述
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		//输出起始序号
		int no=(pageIndex-1)*pageSize+1;
		for (int i = 0; i <list.size() ; i++) {
			list.get(i).put("no",no+i);
		}
		//校验是否超时
		//获取装卸超时小时数
		int loadTimeOut=receivingBaseDataService.getLoadTimeOut();
		//获取等待超时小时数
		int waitTimeOut=receivingBaseDataService.getWaitTimeOut();
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			Optional planArrivedTime = Optional.ofNullable(listMap.get("planArrivedTime"));
			Optional arrivedTime = Optional.ofNullable(listMap.get("arrivedTime"));
			Optional planLeaveTime = Optional.ofNullable(listMap.get("planArrivedTime"));
			Optional leaveTime = Optional.ofNullable(listMap.get("leaveTime"));
			Optional planReceivingNo = Optional.ofNullable(listMap.get("planReceivingNo"));
			Optional receivingNo = Optional.ofNullable(listMap.get("receivingNo"));
			//发货码头
			Optional receivingWharf= Optional.ofNullable(listMap.get("wharf"));
			Optional receivingStatus=Optional.ofNullable(listMap.get("receivingStatus"));
			//校验是否装卸超时
			boolean isLoadTimeOUt=false;
			//校验码头是否占用
			boolean isWharfEmploy=checkWharfEmploy(receivingWharf.orElse("").toString(),startDate,endDate);
			//校验数量是否一致
			boolean isConsistent=true;
			boolean isWaitTimeOut=false;
			boolean isArrivedTimeOut=false;
			boolean isLeaveTimeOut=false;
			//判断状态
			//等待发货
			if("0".equals(receivingStatus.orElse("0"))){
				//将到达时间，离开时间，发货数量设置空
				listMap.put("arrivedTime","");
				listMap.put("leaveTime","");
				listMap.put("receivingNo","");
			}
			//正在发货
			if("1".equals(receivingStatus.orElse("0"))){
				listMap.put("leaveTime","");
				listMap.put("receivingNo","");
				isWaitTimeOut=TimeUtil.checkTimeOut(arrivedTime.orElse("00:00:00").toString(),waitTimeOut);
				isArrivedTimeOut=TimeUtil.checkTimeOut(planArrivedTime.orElse("00:00:00").toString(),arrivedTime.orElse("00:00:00").toString(),loadTimeOut);
			}
			//发货完成
			if("2".equals(receivingStatus.orElse("0"))){
				isConsistent=planReceivingNo.equals(receivingNo);
				isLoadTimeOUt=TimeUtil.checkTimeOut(leaveTime.orElse("00:00:00").toString(),arrivedTime.orElse("00:00:00").toString(),loadTimeOut);
				isLeaveTimeOut=TimeUtil.checkTimeOut(planLeaveTime.orElse("00:00:00").toString(),leaveTime.orElse("00:00:00").toString(),loadTimeOut);
			}
			//添加超时判断
			listMap.put("isWaitTimeOut",isWaitTimeOut);
			listMap.put("isLoadTimeOUt",isLoadTimeOUt);
			listMap.put("isConsistent",isConsistent);
			listMap.put("isWharfEmploy",isWharfEmploy);
			listMap.put("isArrivedTimeOut",isArrivedTimeOut);
			listMap.put("isLeaveTimeOut",isLeaveTimeOut);
		}
		list= (ArrayList<HashMap>) addReceivingStatusName(list);
		message=MessageUtil.getMessage(Message.DELIVERY_GET_SUCCESS.getCode());
		logger.info(message);
		int pageCount= (int) result.get("pageCount");
		//获取总条数
		int count= (int) result.get("count");
		return ResultUtil.ok().put("msg",message).put("result",list).put("pageCount",pageCount).put("count",count);
	}

	@Override
	public ResultUtil getBoardInfo(String startDate, String endDate, int pageIndex, int pageSize, List<?> criteriaList, List<?> wharfList) {
		String message;
		String sql;
		StringBuilder builder=new StringBuilder();
		Optional wharf;
		for (int i = 0; i < wharfList.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) wharfList.get(i);
			wharf =Optional.ofNullable(listMap.get("wharf"));
			builder.append("'"+wharf.orElse("")+"',");
		}
		builder.setLength(builder.length()-1);
		//排序条件字符串
		StringBuilder criteriaBuilder=new StringBuilder();
		for (int i = 0; i < criteriaList.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) criteriaList.get(i);
			Optional sort=Optional.ofNullable(listMap.get("sort"));
			criteriaBuilder.append(listMap.get("criteria"));
			if (!"0".equals(sort.orElse("0"))){
				criteriaBuilder.append(" desc");
			}
			criteriaBuilder.append(" ,");
		}
		//删除最后一个，
		criteriaBuilder.setLength(criteriaBuilder.length()-1);
		//判断排序正序倒序
		sql = "select id, order_id as \"orderId\", supplier, wharf, car_no as \"carNo\", plan_receiving_no as \"planReceivingNo\", " +
				"receiving_no as \"receivingNo\", plan_arrived_time as \"planArrivedTime\", arrived_time as \"arrivedTime\", " +
				"plan_leave_time as \"planLeaveTime\", leave_time as \"leaveTime\", " +
				"receiving_status as \"receivingStatus\", plan_date \"planDate\"" +
				" from receiving_goods " +
				"where plan_date between '" + startDate + "' and '" + endDate + "' " +
				"and wharf in  ("+builder.toString()+")" +
				"order by "+criteriaBuilder+" ";
		//进行查询
		ResultUtil result=dbHelperService.selectPage(sql,"postgres_test",pageIndex,pageSize);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.RECEIVING_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}

		//添加状态描述
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		//输出起始序号
		int no=(pageIndex-1)*pageSize+1;
		for (int i = 0; i <list.size() ; i++) {
			list.get(i).put("no",no+i);
		}
		//校验是否超时
		//获取装卸超时小时数
		int loadTimeOut=receivingBaseDataService.getLoadTimeOut();
		//获取等待超时小时数
		int waitTimeOut=receivingBaseDataService.getWaitTimeOut();
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			Optional planArrivedTime = Optional.ofNullable(listMap.get("planArrivedTime"));
			Optional arrivedTime = Optional.ofNullable(listMap.get("arrivedTime"));
			Optional planLeaveTime = Optional.ofNullable(listMap.get("planArrivedTime"));
			Optional leaveTime = Optional.ofNullable(listMap.get("leaveTime"));
			Optional planReceivingNo = Optional.ofNullable(listMap.get("planReceivingNo"));
			Optional receivingNo = Optional.ofNullable(listMap.get("receivingNo"));
			//发货码头
			Optional receivingWharf= Optional.ofNullable(listMap.get("wharf"));
			Optional receivingStatus=Optional.ofNullable(listMap.get("receivingStatus"));
			//校验是否装卸超时
			boolean isLoadTimeOUt=false;
			//校验码头是否占用
			boolean isWharfEmploy=checkWharfEmploy(receivingWharf.orElse("").toString(),startDate,endDate);
			//校验数量是否一致
			boolean isConsistent=true;
			boolean isWaitTimeOut=false;
			boolean isArrivedTimeOut=false;
			boolean isLeaveTimeOut=false;
			//判断状态
			//等待发货
			if("0".equals(receivingStatus.orElse("0"))){
				//将到达时间，离开时间，发货数量设置空
				listMap.put("arrivedTime","");
				listMap.put("leaveTime","");
				listMap.put("receivingNo","");
			}
			//正在发货
			if("1".equals(receivingStatus.orElse("0"))){
				listMap.put("leaveTime","");
				listMap.put("receivingNo","");
				isWaitTimeOut=TimeUtil.checkTimeOut(arrivedTime.orElse("00:00:00").toString(),waitTimeOut);
				isArrivedTimeOut=TimeUtil.checkTimeOut(planArrivedTime.orElse("00:00:00").toString(),arrivedTime.orElse("00:00:00").toString(),loadTimeOut);
			}
			//发货完成
			if("2".equals(receivingStatus.orElse("0"))){
				isConsistent=planReceivingNo.equals(receivingNo);
				isLoadTimeOUt=TimeUtil.checkTimeOut(leaveTime.orElse("00:00:00").toString(),arrivedTime.orElse("00:00:00").toString(),loadTimeOut);
				isLeaveTimeOut=TimeUtil.checkTimeOut(planLeaveTime.orElse("00:00:00").toString(),leaveTime.orElse("00:00:00").toString(),loadTimeOut);
			}
			//添加超时判断
			listMap.put("isWaitTimeOut",isWaitTimeOut);
			listMap.put("isLoadTimeOUt",isLoadTimeOUt);
			listMap.put("isConsistent",isConsistent);
			listMap.put("isWharfEmploy",isWharfEmploy);
			listMap.put("isArrivedTimeOut",isArrivedTimeOut);
			listMap.put("isLeaveTimeOut",isLeaveTimeOut);
		}
		list= (ArrayList<HashMap>) addReceivingStatusName(list);
		message=MessageUtil.getMessage(Message.DELIVERY_GET_SUCCESS.getCode());
		logger.info(message);
		int pageCount= (int) result.get("pageCount");
		//获取总条数
		int count= (int) result.get("count");
		return ResultUtil.ok().put("msg",message).put("result",list).put("pageCount",pageCount).put("count",count);
	}

	/**
	 * 校验码头是否被占用
	 * @param wharf
	 * @return
	 */
	private boolean checkWharfEmploy(String wharf,String startDate,String endDate){
		String message;
		String sql = "select 1 from receiving_goods where plan_date between '" + startDate + "' and '" + endDate + "' and wharf='"+wharf+"' and receiving_status = '1';";
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
	 * 校验码头占用
	 * @param wharf
	 * @return
	 */
	@Override
	public  ResultUtil checkWharf(String wharf,String startDate,String endDate){
		String message;
		boolean isWharfEmploy = checkWharfEmploy(wharf,startDate,endDate);
		if(isWharfEmploy){
			message=MessageUtil.getMessage(Message.WHARF_IS_USED.getCode());
			logger.error(wharf+message);
			return  ResultUtil.ok(message).put("isWharfEmploy",isWharfEmploy);
		}
		message=MessageUtil.getMessage(Message.WHARF_NOT_USER.getCode());
		logger.info(wharf+message);
		return ResultUtil.ok(message).put("isWharfEmploy",isWharfEmploy);
	}


	/**
	 * 添加状态信息描述
	 * @param list
	 * @return
	 */
	private List<HashMap> addReceivingStatusName(ArrayList<HashMap> list){
		//调用方法获取状态信息列表
		ResultUtil statusResult=getReceivingStatus();
		ArrayList statusList=(ArrayList) statusResult.get("result");
		String statusName="";
		for (int i = 0; i < list.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			Optional receivingStatus=Optional.ofNullable(listMap.get("receivingStatus"));
			if(!receivingStatus.isPresent())
			{
				receivingStatus=Optional.ofNullable(listMap.get("收货状态"));
			}
			//获取收货状态描述
			for (int j = 0; j < statusList.size() ; j++) {
				Map<String,Object> statusMap= (Map<String, Object>) statusList.get(j);
				Optional statusNo=Optional.ofNullable(statusMap.get("statusNo"));
				if(receivingStatus.get().toString().equals(statusNo.get().toString())){
					statusName= statusMap.get("statusName").toString();
					break;
				}
			}
			list.get(i).put("statusName",statusName);
		}
		return list;
	}

	/**
	 * 检测收货信息是否存在
	 * @param id
	 * @return
	 */
	private  boolean checkReceivingExist(int id){
		String message;
		String sql = "select 1 from receiving_goods where id='"+id+"';";
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
