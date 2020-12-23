package esun.wharf.service.impl;

import esun.wharf.constant.Message;
import esun.wharf.exception.CustomHttpException;
import esun.wharf.service.BaseDataService;
import esun.wharf.service.DbHelperService;
import esun.wharf.service.DeliveryService;
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
public class DeliveryServiceImpl  implements DeliveryService {
	private  static Logger logger= LoggerFactory.getLogger(DeliveryServiceImpl.class);
	@Autowired
	@Lazy
	DbHelperService dbHelperService;

	@Autowired
	BaseDataService baseDataService;

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
	 * 获取客户信息
	 * @param customerName
	 * @return
	 */
	@Override
	public ResultUtil getCustomerInfo(String customerName) {
		String message;
		String customerSql= "select id,data_type as \"dataType\",data_name as \"dataName\",value_1 as \"customer\" " +
				"from base_data where data_type =1 and value_1 like '%"+customerName+"%'";
		ResultUtil customerResult=dbHelperService.select(customerSql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)customerResult.get("code")){
			message= MessageUtil.getMessage(Message.CUSTOMER_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> customerList= (ArrayList) customerResult.get("result");
		int listSize=customerList.size();
		for (int j = 0; j <listSize ; j++) {
			String customer = customerList.get(j).get("customer").toString();
			//获取订单信息
			String orderSql="select id,data_type as \"dataType\",data_name as \"dataName\",value_1 as \"orderNo\",value_2 as \"customer\" " +
					"from base_data where data_type ='3' and value_2 ='"+customer+"'";
			ResultUtil orderResult=dbHelperService.select(orderSql,"postgres_test");
			if(HttpStatus.OK.value()!= (int)orderResult.get("code")){
				message=MessageUtil.getMessage(Message.ORDER_GET_ERROR.getCode());
				logger.error(message);
				return ResultUtil.error(message);
			}
			ArrayList<HashMap> orderList= (ArrayList) orderResult.get("result");
			customerList.get(j).put("order",orderList);
		}
		message=MessageUtil.getMessage(Message.CUSTOMER_GET_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("result",customerList);
	}

	@Override
	public ResultUtil getCustomerInfo(List<?> list) {
		String message;
		int i=0;
		do {
			Map<String,Object> listMap = (Map<String, Object>) list.get(i);
			Optional<Object> customerName=Optional.ofNullable(listMap.get("customerName"));
			String customerSql= "select id,data_type as \"dataType\",data_name as \"dataName\",value_1 as \"customer\" " +
					"from base_data where data_type =1 and value_1 = '"+customerName.orElse("")+"';";
			ResultUtil customerResult=dbHelperService.select(customerSql,"postgres_test");
			if(HttpStatus.OK.value()!= (int)customerResult.get("code")){
				message=MessageUtil.getMessage(Message.CUSTOMER_GET_ERROR.getCode());
				logger.error(message);
				((Map<String, Object>) list.get(i)).put("msg",message);
			}
			ArrayList<HashMap> customerList= (ArrayList) customerResult.get("result");
			ArrayList<HashMap> orderList=new ArrayList<>();
			int listSize=customerList.size();
			for (int j = 0; j <listSize ; j++) {
				String customer = customerList.get(j).get("customer").toString();
				//获取订单信息
				String orderSql="select id,data_type as \"dataType\",data_name as \"dataName\",value_1 as \"orderNo\",value_2 as \"customer\" " +
						"from base_data where data_type ='3' and value_2 ='"+customer+"'";
				ResultUtil orderResult=dbHelperService.select(orderSql,"postgres_test");
				if(HttpStatus.OK.value()!= (int)orderResult.get("code")){
					message=MessageUtil.getMessage(Message.ORDER_GET_ERROR.getCode());
					logger.error(message);
					return ResultUtil.error(message);
				}
				orderList= (ArrayList<HashMap>) orderResult.get("result");
//				customerList.get(j).put("order",orderList);
			}
			message= "获取客户信息成功";
			logger.info(message);
			((Map<String, Object>) list.get(i)).put("msg",message);
			((Map<String, Object>) list.get(i)).put("order",orderList);
			i++;
		}
		while(i<list.size());
		return ResultUtil.ok().put("result",list);
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
	public ResultUtil getDeliveryGoods(String startDate,String endDate,int pageIndex,int pageSize,String criteria,int sort,String customer,String orderId,List<?> wharfList) {
		String message;
		String sql;
		StringBuilder builder=new StringBuilder();
		Optional<Object> wharf;
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
			sql = "select id, order_id as \"orderId\", customer, wharf, car_no as \"carNo\", plan_delivery_no as \"planDeliveryNo\", " +
					"delivery_no as \"deliveryNo\", plan_arrived_time as \"planArrivedTime\", arrived_time as \"arrivedTime\", " +
					"plan_leave_time as \"planLeaveTime\", leave_time as \"leaveTime\", " +
					"delivery_status as \"deliveryStatus\", plan_date \"planDate\"" +
					" from delivery_goods " +
					"where plan_date between '" + startDate + "' and '" + endDate + "' " +
					"and customer like '%"+customer+"%' and order_id like '%"+orderId+"%' and wharf "+builder.toString()+"" +
					"order by "+criteria+" ";
		}
		else {
			sql = "select id, order_id as \"orderId\", customer, wharf, car_no as \"carNo\", plan_delivery_no as \"planDeliveryNo\", " +
					"delivery_no as \"deliveryNo\", plan_arrived_time as \"planArrivedTime\", arrived_time as \"arrivedTime\", " +
					"plan_leave_time as \"planLeaveTime\", leave_time as \"leaveTime\", " +
					"delivery_status as \"deliveryStatus\", plan_date \"planDate\"" +
					" from delivery_goods " +
					"where plan_date between '" + startDate + "' and '" + endDate + "' " +
					"and customer like '%"+customer+"%' and order_id like '%"+orderId+"%' and wharf  "+builder.toString()+"" +
					"order by "+criteria+" desc";
		}
		ResultUtil result=dbHelperService.selectPage(sql,"postgres_test",pageIndex,pageSize);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.DELIVERY_GET_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		//输出起始序号
		int no=(pageIndex-1)*pageSize+1;
		for (int i = 0; i <list.size() ; i++) {
			list.get(i).put("no",no+i);
		}
		//校验是否超时
		//获取装卸超时小时数
		int loadTimeOut=baseDataService.getLoadTimeOut();
		//获取等待超时小时数
		int waitTimeOut=baseDataService.getWaitTimeOut();
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			Optional<Object> planArrivedTime = Optional.ofNullable(listMap.get("planArrivedTime"));
			Optional<Object> arrivedTime = Optional.ofNullable(listMap.get("arrivedTime"));
			Optional<Object> planLeaveTime = Optional.ofNullable(listMap.get("planLeaveTime"));
			Optional<Object> leaveTime = Optional.ofNullable(listMap.get("leaveTime"));
			Optional<Object> planDeliveryNo = Optional.ofNullable(listMap.get("planDeliveryNo"));
			Optional<Object> deliveryNo = Optional.ofNullable(listMap.get("deliveryNo"));
			//发货码头
			Optional<Object> deliveryWharf= Optional.ofNullable(listMap.get("wharf"));
			Optional<Object> deliveryStatus=Optional.ofNullable(listMap.get("deliveryStatus"));
			//校验是否装卸超时
			boolean isLoadTimeOUt=false;
			//校验码头是否占用
			boolean isWharfEmploy=false;

			//校验数量是否一致
			boolean isConsistent=true;
			boolean isWaitTimeOut=false;
			boolean isArrivedTimeOut=false;
			boolean isLeaveTimeOut=false;
			//判断状态
			//等待发货
			if("0".equals(deliveryStatus.orElse("0"))){
				isWharfEmploy=checkWharfEmploy(deliveryWharf.orElse("").toString(),startDate,endDate);

			}
			//正在发货
			if("1".equals(deliveryStatus.orElse("0"))){
				isWaitTimeOut=TimeUtil.checkTimeOut(arrivedTime.orElse("00:00:00").toString(),waitTimeOut);
				isArrivedTimeOut=TimeUtil.checkTimeOut(planArrivedTime.orElse("00:00:00").toString(),arrivedTime.orElse("00:00:00").toString(),loadTimeOut);
				isWharfEmploy=checkWharfEmploy(deliveryWharf.orElse("").toString(),startDate,endDate);

			}
			//发货完成
			if("2".equals(deliveryStatus.orElse("0"))){
				isConsistent=planDeliveryNo.equals(deliveryNo);
				isArrivedTimeOut=TimeUtil.checkTimeOut(planArrivedTime.orElse("00:00:00").toString(),arrivedTime.orElse("00:00:00").toString(),loadTimeOut);
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
		list= (ArrayList<HashMap>) addDeliveryStatusName(list);
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
		Optional<Object> orderId;
		Optional<Object> customer;
		Optional<Object> wharf;
		Optional<Object> carNo;
		Optional<Object> planDeliveryNo;
		Optional<Object> deliveryNo;
		Optional<Object> planArrivedTime;
		Optional<Object> arrivedTime;
		Optional<Object> planLeaveTime;
		Optional<Object> leaveTime;
		Optional<Object> deliveryStatus;
		Optional<Object> planDate;
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
			//检测路由是否存在
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
		//如果没有路由被删除，则返回结果
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
		Optional<Object> orderId;
		Optional<Object> customer;
		Optional<Object> wharf;
		Optional<Object> carNo;
		Optional<Object> planDeliveryNo;
		Optional<Object> deliveryNo;
		Optional<Object> planArrivedTime;
		Optional<Object> arrivedTime;
		Optional<Object> planLeaveTime;
		Optional<Object> leaveTime;
		Optional<Object> deliveryStatus;
		Optional<Object> planDate;
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
					message=MessageUtil.getMessage(Message.DELIVERY_UPDATE_ERROR.getCode());
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
		List<String> titleList= PoiUtils.getTitleList(PoiUtils.getRow(sheet,0));
		//请求结果列表
		List<Map<String,Object>> resultList=new ArrayList<>();
		Map<String,Object> info;
		//获取状态信息列表
		ResultUtil statusResult= baseDataService.getBaseData("状态",1,10,"id",0);
		ArrayList statusList= (ArrayList) statusResult.get("list");
		//循环遍历用户信息写入列表
		for (int i = 1; i <=sheet.getLastRowNum() ; i++) {
			//用户运行结果Map
			Map<String,Object> resultMap=new HashMap<>();
			//获取相应行的数据，转换为list
			info=PoiUtils.getRowData(PoiUtils.getRow(sheet,i),titleList);
			Optional<Object> orderId=Optional.ofNullable(info.get("订单号"));
			Optional<Object> customer=Optional.ofNullable(info.get("客户"));
			Optional<Object> wharf=Optional.ofNullable(info.get("码头"));
			Optional<Object> planDeliveryNo=Optional.ofNullable(info.get("计划发货数量"));
			Optional<Object> deliveryNo=Optional.ofNullable(info.get("实际发货数量"));
			Optional<Object> planArrivedTime=Optional.ofNullable(info.get("计划到达时间"));
			Optional<Object> arrivedTime=Optional.ofNullable(info.get("实际到达时间"));
			Optional<Object> carNo=Optional.ofNullable(info.get("车牌号"));
			Optional<Object> planLeaveTime=Optional.ofNullable(info.get("计划离开时间"));
			Optional<Object> leaveTime=Optional.ofNullable(info.get("实际离开时间"));
			Optional<Object> deliveryStatus=Optional.ofNullable(info.get("发货状态"));
			Optional<Object> planDate=Optional.ofNullable(info.get("计划日期"));
			//根据状态名获取状态码test
			for (int j = 0; j < statusList.size(); j++) {
				Map<String,Object> status= (Map<String, Object>) statusList.get(j);
				Optional<Object> statusNo=Optional.ofNullable(status.get("value1"));
				Optional<Object> statusName=Optional.ofNullable(status.get("value2"));
				if(statusName.equals(deliveryStatus)){
					resultMap.put("deliveryStatus",statusNo.orElse("0"));
				}
			}
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
			//查看该用户是否存在
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
			Optional<Object> customer = Optional.ofNullable(listMap.get("customer"));
			customerBuilder.append("'"+customer.orElse("")+"',");
		}
		customerBuilder.setLength(customerBuilder.length()-1);
		String sql= "select id as \"ID\", order_id as \"订单号\", customer as \"客户\", wharf as \"码头\", car_no as \"车牌号\", plan_delivery_no as \"计划发货数量\", " +
				"delivery_no as \"实际发货数量\", plan_arrived_time as \"计划到达时间\", arrived_time as \"实际到达时间\", " +
				"plan_leave_time as \"计划离开时间\", leave_time as \"实际离开时间\", " +
				"delivery_status as \"发货状态\", plan_date \"计划日期\"" +
				" from delivery_goods " +
				"where plan_date between '"+startDate+"' and '"+endDate+"' and order_id like '%"+orderId+"%' and customer in ("+customerBuilder+") \n";
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
		list= (ArrayList<HashMap>) addDeliveryStatusName(list);
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			listMap.put("发货状态",listMap.get("statusName").toString());
			list.set(i, listMap);
		}
		List<String> titleList=new ArrayList<>();
//		titleList.add("ID");
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
//		String ftpPath="/wharf/";
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
					"where plan_date between '"+startDate+"' and '"+endDate+"' and order_id like '%"+orderId+"%' and customer in ("+customerBuilder+") " +
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
	 * 获取状态描述信息
	 * @return
	 */
	@Override
	public ResultUtil getDeliveryStatus() {
		String sql= "select value_1 as \"statusNo\", value_2 as \"statusName\" from  base_data where data_type = 0 ";
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
	 * 获取看板信息
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
	public ResultUtil getBoardInfo(String startDate,String endDate,int pageIndex,int pageSize,String criteria,int sort,List<?> wharfList) {
		String message;
		String sql;
		StringBuilder builder=new StringBuilder();
		Optional<Object> wharf;
		for (int i = 0; i < wharfList.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) wharfList.get(i);
			wharf =Optional.ofNullable(listMap.get("wharf"));
			builder.append("'"+wharf.orElse("")+"',");
		}
		builder.setLength(builder.length()-1);
		//判断排序正序倒序
		if(sort == 0) {
			sql = "select id, order_id as \"orderId\", customer, wharf, car_no as \"carNo\", plan_delivery_no as \"planDeliveryNo\", " +
					"delivery_no as \"deliveryNo\", plan_arrived_time as \"planArrivedTime\", arrived_time as \"arrivedTime\", " +
					"plan_leave_time as \"planLeaveTime\", leave_time as \"leaveTime\", " +
					"delivery_status as \"deliveryStatus\", plan_date \"planDate\"" +
					" from delivery_goods " +
					"where plan_date between '" + startDate + "' and '" + endDate + "' " +
					"and wharf in  ("+builder.toString()+")" +
					"order by "+criteria+" ";
		}
		else {
			sql = "select id, order_id as \"orderId\", customer, wharf, car_no as \"carNo\", plan_delivery_no as \"planDeliveryNo\", " +
					"delivery_no as \"deliveryNo\", plan_arrived_time as \"planArrivedTime\", arrived_time as \"arrivedTime\", " +
					"plan_leave_time as \"planLeaveTime\", leave_time as \"leaveTime\", " +
					"delivery_status as \"deliveryStatus\", plan_date \"planDate\"" +
					" from delivery_goods " +
					"where plan_date between '" + startDate + "' and '" + endDate + "' " +
					" and wharf in  ("+builder.toString()+")" +
					"order by "+criteria+" desc";
		}
		//进行查询
		ResultUtil result=dbHelperService.selectPage(sql,"postgres_test",pageIndex,pageSize);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.DELIVERY_GET_ERROR.getCode());
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
		int loadTimeOut=baseDataService.getLoadTimeOut();
		//获取等待超时小时数
		int waitTimeOut=baseDataService.getWaitTimeOut();

		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			Optional<Object> planArrivedTime = Optional.ofNullable(listMap.get("planArrivedTime"));
			Optional<Object> arrivedTime = Optional.ofNullable(listMap.get("arrivedTime"));
			Optional<Object> planLeaveTime = Optional.ofNullable(listMap.get("planLeaveTime"));
			Optional<Object> leaveTime = Optional.ofNullable(listMap.get("leaveTime"));
			Optional<Object> planDeliveryNo = Optional.ofNullable(listMap.get("planDeliveryNo"));
			Optional deliveryNo = Optional.ofNullable(listMap.get("deliveryNo"));
			//发货码头
			Optional<Object> deliveryWharf= Optional.ofNullable(listMap.get("wharf"));
			Optional<Object> deliveryStatus=Optional.ofNullable(listMap.get("deliveryStatus"));
			//校验是否装卸超时
			boolean isLoadTimeOUt=false;
			//校验码头是否占用
			boolean isWharfEmploy=false;
			//校验数量是否一致
			boolean isConsistent=true;
			boolean isWaitTimeOut=false;
			boolean isArrivedTimeOut=false;
			boolean isLeaveTimeOut=false;
			//判断状态
			//等待发货
			if("0".equals(deliveryStatus.orElse("0"))){
				//将到达时间，离开时间，发货数量设置空
				listMap.put("arrivedTime","");
				listMap.put("leaveTime","");
				listMap.put("deliveryNo","0".equals(deliveryNo.orElse("0").toString())?"":deliveryNo.orElse("0"));
				isWharfEmploy=checkWharfEmploy(deliveryWharf.orElse("").toString(),startDate,endDate);
			}
			//正在发货
			if("1".equals(deliveryStatus.orElse("0"))){
				listMap.put("leaveTime","");
				listMap.put("deliveryNo","0".equals(deliveryNo.orElse("0").toString())?"":deliveryNo.orElse("0"));
				isWaitTimeOut=TimeUtil.checkTimeOut(arrivedTime.orElse("00:00:00").toString(),waitTimeOut);
				isArrivedTimeOut=TimeUtil.checkTimeOut(planArrivedTime.orElse("00:00:00").toString(),arrivedTime.orElse("00:00:00").toString(),loadTimeOut);
				isWharfEmploy=checkWharfEmploy(deliveryWharf.orElse("").toString(),startDate,endDate);

			}
			//发货完成
			if("2".equals(deliveryStatus.orElse("0"))){
				isArrivedTimeOut=TimeUtil.checkTimeOut(planArrivedTime.orElse("00:00:00").toString(),arrivedTime.orElse("00:00:00").toString(),loadTimeOut);
				isLeaveTimeOut=TimeUtil.checkTimeOut(planLeaveTime.orElse("00:00:00").toString(),leaveTime.orElse("00:00:00").toString(),loadTimeOut);
			}
			isConsistent=planDeliveryNo.equals(deliveryNo);
			listMap.put("isWaitTimeOut",isWaitTimeOut);
			listMap.put("isLoadTimeOUt",isLoadTimeOUt);
			listMap.put("isConsistent",isConsistent);
			listMap.put("isWharfEmploy",isWharfEmploy);
			listMap.put("isArrivedTimeOut",isArrivedTimeOut);
			listMap.put("isLeaveTimeOut",isLeaveTimeOut);
		}
		list= (ArrayList<HashMap>) addDeliveryStatusName(list);
		message=MessageUtil.getMessage(Message.DELIVERY_GET_SUCCESS.getCode());
		logger.info(message);
		int pageCount= (int) result.get("pageCount");
		//获取总条数
		int count= (int) result.get("count");
		return ResultUtil.ok().put("msg",message).put("result",list).put("pageCount",pageCount).put("count",count);
	}

	/**
	 * 获取看板信息(组合排序)
	 * @param startDate
	 * @param endDate
	 * @param pageIndex
	 * @param pageSize
	 * @param criteriaList
	 * @param wharfList
	 * @return
	 */
	@Override
	public ResultUtil getBoardInfo(String startDate, String endDate, int pageIndex, int pageSize, List<?> criteriaList, List<?> wharfList) {
		String message;
		String sql;
		//码头字符串
		StringBuilder builder=new StringBuilder();
		Optional<Object> wharf;
		//循环遍历写入码头
		for (int i = 0; i < wharfList.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) wharfList.get(i);
			wharf =Optional.ofNullable(listMap.get("wharf"));
			builder.append("'"+wharf.orElse("")+"',");
		}
		//删除最后一个,
		builder.setLength(builder.length()-1);
		//排序条件字符串
		StringBuilder criteriaBuilder=new StringBuilder();
		for (int i = 0; i < criteriaList.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) criteriaList.get(i);
			Optional<Object> sort=Optional.ofNullable(listMap.get("sort"));
			criteriaBuilder.append(listMap.get("criteria"));
			if (!"0".equals(sort.orElse("0"))){
				criteriaBuilder.append(" desc");
			}
			criteriaBuilder.append(" ,");
		}
		//删除最后一个，
		criteriaBuilder.setLength(criteriaBuilder.length()-1);
		sql = "select id, order_id as \"orderId\", customer, wharf, car_no as \"carNo\", plan_delivery_no as \"planDeliveryNo\", " +
				"delivery_no as \"deliveryNo\", plan_arrived_time as \"planArrivedTime\", arrived_time as \"arrivedTime\", " +
				"plan_leave_time as \"planLeaveTime\", leave_time as \"leaveTime\", " +
				"delivery_status as \"deliveryStatus\", plan_date \"planDate\"" +
				" from delivery_goods " +
				"where plan_date between '" + startDate + "' and '" + endDate + "' " +
				"and wharf in  ("+builder.toString()+")" +
				"order by "+criteriaBuilder+" ";
		//进行查询
		ResultUtil result=dbHelperService.selectPage(sql,"postgres_test",pageIndex,pageSize);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.DELIVERY_GET_ERROR.getCode());
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
		int loadTimeOut=baseDataService.getLoadTimeOut();
		//获取等待超时小时数
		int waitTimeOut=baseDataService.getWaitTimeOut();
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			Optional<Object> planArrivedTime = Optional.ofNullable(listMap.get("planArrivedTime"));
			Optional<Object> arrivedTime = Optional.ofNullable(listMap.get("arrivedTime"));
			Optional<Object> planLeaveTime = Optional.ofNullable(listMap.get("planLeaveTime"));
			Optional<Object> leaveTime = Optional.ofNullable(listMap.get("leaveTime"));
			Optional<Object> planDeliveryNo = Optional.ofNullable(listMap.get("planDeliveryNo"));
			Optional deliveryNo = Optional.ofNullable(listMap.get("deliveryNo"));
			//发货码头
			Optional<Object> deliveryWharf= Optional.ofNullable(listMap.get("wharf"));
			Optional<Object> deliveryStatus=Optional.ofNullable(listMap.get("deliveryStatus"));
			//校验是否装卸超时
			boolean isLoadTimeOUt=false;
			//校验码头是否占用
			boolean isWharfEmploy=false;

			//校验数量是否一致
			boolean isConsistent=true;
			boolean isWaitTimeOut=false;
			boolean isArrivedTimeOut=false;
			boolean isLeaveTimeOut=false;
			//判断状态
			//等待发货
			if("0".equals(deliveryStatus.orElse("0"))){
				//将到达时间，离开时间，发货数量设置空
				listMap.put("arrivedTime","");
				listMap.put("leaveTime","");
				listMap.put("deliveryNo","0".equals(deliveryNo.orElse("0").toString())?"":deliveryNo.orElse("0"));
				isWharfEmploy=checkWharfEmploy(deliveryWharf.orElse("").toString(),startDate,endDate);

			}
			//正在发货
			if("1".equals(deliveryStatus.orElse("0"))){
				listMap.put("leaveTime","");
				listMap.put("deliveryNo","0".equals(deliveryNo.orElse("0").toString())?"":deliveryNo.orElse("0"));
				isWaitTimeOut=TimeUtil.checkTimeOut(arrivedTime.orElse("00:00:00").toString(),waitTimeOut);
				isArrivedTimeOut=TimeUtil.checkTimeOut(planArrivedTime.orElse("00:00:00").toString(),arrivedTime.orElse("00:00:00").toString(),loadTimeOut);
				isWharfEmploy=checkWharfEmploy(deliveryWharf.orElse("").toString(),startDate,endDate);

			}
			//发货完成
			if("2".equals(deliveryStatus.orElse("0"))){
				isArrivedTimeOut=TimeUtil.checkTimeOut(planArrivedTime.orElse("00:00:00").toString(),arrivedTime.orElse("00:00:00").toString(),loadTimeOut);
				isLeaveTimeOut=TimeUtil.checkTimeOut(planLeaveTime.orElse("00:00:00").toString(),leaveTime.orElse("00:00:00").toString(),loadTimeOut);
			}
			isConsistent=planDeliveryNo.equals(deliveryNo);
			//添加超时判断
			listMap.put("isWaitTimeOut",isWaitTimeOut);
			listMap.put("isLoadTimeOUt",isLoadTimeOUt);
			listMap.put("isConsistent",isConsistent);
			listMap.put("isWharfEmploy",isWharfEmploy);
			listMap.put("isArrivedTimeOut",isArrivedTimeOut);
			listMap.put("isLeaveTimeOut",isLeaveTimeOut);
		}
		list= (ArrayList<HashMap>) addDeliveryStatusName(list);
		message=MessageUtil.getMessage(Message.DELIVERY_GET_SUCCESS.getCode());
		logger.info(message);
		int pageCount= (int) result.get("pageCount");
		//获取总条数
		int count= (int) result.get("count");
		return ResultUtil.ok().put("msg",message).put("result",list).put("pageCount",pageCount).put("count",count);
	}

	/**
	 * 添加状态信息描述
	 * @param list
	 * @return
	 */
	private List<HashMap> addDeliveryStatusName(ArrayList<HashMap> list){
		//调用方法获取状态信息列表
		ResultUtil statusResult=getDeliveryStatus();
		ArrayList statusList=(ArrayList) statusResult.get("result");
		String statusName="";
		for (int i = 0; i < list.size() ; i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			Optional<Object> deliveryStatus=Optional.ofNullable(listMap.get("deliveryStatus"));
			if(!deliveryStatus.isPresent())
			{
				deliveryStatus=Optional.ofNullable(listMap.get("发货状态"));
			}
			//获取发货状态描述
			for (int j = 0; j < statusList.size() ; j++) {
				Map<String,Object> statusMap= (Map<String, Object>) statusList.get(j);
				Optional<Object> statusNo=Optional.ofNullable(statusMap.get("statusNo"));
				if(deliveryStatus.get().toString().equals(statusNo.get().toString())){
					statusName= statusMap.get("statusName").toString();
					break;
				}
			}
			list.get(i).put("statusName",statusName);
		}
		return list;
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

	/**
	 * 校验码头是否被占用
	 * @param wharf
	 * @return
	 */
	private boolean checkWharfEmploy(String wharf,String startDate,String endDate){
		String message;
		String sql = "select 1 from delivery_goods where plan_date between '" + startDate + "' and '" + endDate + "'  and wharf='"+wharf+"' and delivery_status = '1'";
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			throw new CustomHttpException(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		if(list.size()>1){
			return true;
		}
		return  false;
	}


	/**
	 * 校验码头占用
	 * @param wharf
	 * @return
	 */
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

}
