package esun.wharf.service.impl;

import esun.wharf.constant.Message;
import esun.wharf.exception.CustomHttpException;
import esun.wharf.service.DbHelperService;
import esun.wharf.service.ReceivingBaseDataService;
import esun.wharf.utils.MessageUtil;
import esun.wharf.utils.ResultUtil;
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
public class ReceivingBaseDataServiceImpl implements ReceivingBaseDataService {
	private  static Logger logger= LoggerFactory.getLogger(ReceivingServiceImpl.class);
	@Autowired
	@Lazy
	DbHelperService dbHelperService;

	/**
	 * 获取基础数据
	 * @param dataName
	 * @param pageIndex
	 * @param pageSize
	 * @param criteria
	 * @param sort
	 * @return
	 */
	@Override
	public ResultUtil getBaseData(String dataName, int pageIndex, int pageSize, String criteria, int sort) {
		String message;
		String sql;
		if (sort == 0){
			sql= "select distinct id,data_type as \"dataType\",data_name as \"dataName\",value_1 as \"value1\",value_2 as \"value2\" " +
					"from receiving_base_data where data_name like '%"+dataName+"%' order by "+criteria+" ";
		}
		else {
			sql= "select distinct id,data_type as \"dataType\",data_name as \"dataName\",value_1 as \"value1\",value_2 as \"value2\" " +
					"from receiving_base_data where data_name like '%"+dataName+"%' order by "+criteria+" desc ";
		}

		ResultUtil result=dbHelperService.selectPage(sql,"postgres_test",pageIndex,pageSize);
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message= MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		message=MessageUtil.getMessage(Message.BASE_GET_SUCCESS.getCode());
		logger.info(message);
		int pageCount= (int) result.get("pageCount");
		//获取总条数
		int count= (int) result.get("count");
		return ResultUtil.ok().put("list",list).put("pageCount",pageCount).put("count",count);
	}

	/**
	 * 添加基础数据
	 * @param list
	 * @return
	 */
	@Override
	public ResultUtil addBaseData(List<?> list) {
		Optional dataType;
		Optional value1;
		Optional value2;
		String message;
		StringBuilder stringBuilder=new StringBuilder();
		int addCount=0;
		for (int i = 0; i < list.size(); i++) {
			Map<String,Object> listMap= (Map<String, Object>) list.get(i);
			dataType = Optional.ofNullable(listMap.get("dataType"));
			value1 = Optional.ofNullable(listMap.get("value1"));
			value2 = Optional.ofNullable(listMap.get("value2"));
			String dataName=getBaseDataNameByType(dataType.orElse("").toString());
			boolean isBaseDataExist= checkBaseDataExist(dataType.orElse("").toString(),value1.orElse("").toString(),value2.orElse("").toString());
			if (!isBaseDataExist){
				stringBuilder.append("('"+dataType.orElse("")+"','"+dataName+"','"+value1.orElse("")+"','"+value2.orElse("")+"'),");
				addCount++;
				message=MessageUtil.getMessage(Message.BASE_ADD_SUCCESS.getCode());
			}
			else {
				message=MessageUtil.getMessage(Message.BASE_IS_EXIST.getCode());
			}
			((Map<String, Object>) list.get(i)).put("result",message);
		}
		//如果插入用户均存在,则不进行插入
		if (addCount == 0){
			message="用户均存在,停止插入";
			logger.error(message);
			return ResultUtil.ok(message).put("result",list);
		}
		//删除最后一个,
		stringBuilder.setLength(stringBuilder.length()-1);
		String sql = "insert into receiving_base_data (data_type, data_name, value_1, value_2) values"+stringBuilder.toString()+";";
		ResultUtil result=dbHelperService.insert(sql,"postgres_test");
		//判断是否SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.BASE_ADD_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",list);
	}

	/**
	 * 删除基础数据
	 * @param list
	 * @return
	 */
	@Override
	public ResultUtil deleteBaseData(List<?> list) {
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
			boolean isBaseDataExist= checkBaseDataExist((Integer) id.orElse(-1));
			if(!isBaseDataExist){
				message=MessageUtil.getMessage(Message.BASE_NOT_EXIST.getCode());
				logger.error(message);
				listMap.put("result",message);
			}
			else {
				message=MessageUtil.getMessage(Message.BASE_DELETE_SUCCESS.getCode());
				logger.info(message);
				listMap.put("result",message);
				deleteBuild.append(" '"+id.orElse(-1)+"',");
				deleteCount++;
			}
		}
		//如果没有数据被删除，则返回结果
		if (deleteCount == 0 ){
			message=MessageUtil.getMessage(Message.BASE_DELETE_ERROR.getCode());
			logger.error(message);
			return  ResultUtil.ok().put("msg",message).put("result",list);
		}
		//移除字符串最后一个,
		deleteBuild.setLength(deleteBuild.length()-1);
		sql = "delete from receiving_base_data where id in ("+deleteBuild.toString()+");";
		ResultUtil result=dbHelperService.delete(sql,"postgres_test");
		//判断SQL语句是否执行成功
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			return ResultUtil.error(message);
		}
		message=MessageUtil.getMessage(Message.BASE_DELETE_SUCCESS.getCode());
		logger.info(message);
		return ResultUtil.ok().put("msg",message).put("result",list);
	}

	/**
	 * 获取码头列表
	 * @return
	 */
	@Override
	public ResultUtil getWharf() {
		String sql= "select value_1 as \"wharf\" from  receiving_base_data where data_type = 4 ";
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
	 * 更新基础数据
	 * @param list
	 * @return
	 */
	@Override
	public ResultUtil updateBaseData(List<?> list) {
		String sql;
		String message;
		//使用Optional类来防止NPE错误
		Optional id;
		Optional dataType;
		Optional value1;
		Optional value2;
		for (int i = 0; i <list.size() ; i++) {
			Map<String,Object> listMap = (Map<String, Object>) list.get(i);
			id = Optional.ofNullable(listMap.get("id"));
			dataType = Optional.ofNullable(listMap.get("dataType"));
			value1 = Optional.ofNullable(listMap.get("value1"));
			value2 = Optional.ofNullable(listMap.get("value2"));
			String dataName=getBaseDataNameByType(dataType.orElse("").toString());
			boolean isBaseDataExist= checkBaseDataExist((Integer) id.orElse(-1));
			if(!isBaseDataExist){
				message=MessageUtil.getMessage(Message.BASE_NOT_EXIST.getCode());
				logger.error(message);
				listMap.put("result",message);
			}
			else {

				sql = "update receiving_base_data set data_type= "+dataType.orElse("-1")+",data_name = '"+dataName+"',value_1= '"+value1.orElse("")+"',value_2 = '"+value2.orElse("")+"' where id= "+id.orElse("-1")+" ;";
				ResultUtil result=dbHelperService.update(sql,"postgres_test");
				//判断SQL语句是否执行成功
				if(HttpStatus.OK.value()!= (int)result.get("code")){
					message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
					logger.error(message);
					listMap.put("result",message);
				}
				message=MessageUtil.getMessage(Message.BASE_UPDATE_SUCCESS.getCode());
				logger.info(message);
				listMap.put("result",message);
			}
		}

		return ResultUtil.ok().put("result",list);
	}
	/**
	 * 基于数据类型获取基础类型名
	 * @param dataType
	 * @return
	 */
	public String getBaseDataNameByType(String dataType){
		String dataName;
		switch (dataType){
			case "0":
				dataName="状态";
				break;
			case "1":
				dataName="供应商";
				break;

			case "3":
				dataName="订单";
				break;
			case "4":
				dataName="码头";
				break;
			case "5":
				dataName="装卸超小时数";
				break;
			case "6":
				dataName="等待车辆超小时数";
				break;
			default:
				dataName="未知";
		}
		return dataName;
	}

	/**
	 * 检测基础数据是否存在
	 * @param id
	 * @return
	 */
	public boolean checkBaseDataExist(int id){
		String message;
		String sql = "select 1 from receiving_base_data where id="+id+";";
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
	 * 检测基础数据是否存在
	 * @param dataType
	 * @param value1
	 * @param value2
	 * @return
	 */
	public boolean checkBaseDataExist(String dataType,String value1,String value2){
		String message;
		String sql = "select 1 from receiving_base_data where data_type='"+dataType+"' and value_1='"+value1+"' and value_2='"+value2+"';";
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
	 * 获取装修小时数
	 * @return
	 */
	@Override
	public int getLoadTimeOut(){
		String sql= "select  value_1  as \"loadTimeOut\" from  receiving_base_data where data_type = 5 order by id desc limit 1 ";
		String message;
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			return 0;
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		message=MessageUtil.getMessage(Message.BASE_GET_SUCCESS.getCode());
		logger.info(message);
		int waitTimeOut= Integer.parseInt( list.get(0).get("loadTimeOut").toString());
		return waitTimeOut;
	}

	/**
	 * 获取等待超时数
	 * @return
	 */
	@Override
	public int getWaitTimeOut(){
		String sql= "select  value_1  as \"waitTimeOut\" from  receiving_base_data where data_type = 6 order by id desc limit 1 ";
		String message;
		ResultUtil result=dbHelperService.select(sql,"postgres_test");
		if(HttpStatus.OK.value()!= (int)result.get("code")){
			message=MessageUtil.getMessage(Message.QUERY_ERROR.getCode());
			logger.error(message);
			return 0;
		}
		ArrayList<HashMap> list= (ArrayList) result.get("result");
		message=MessageUtil.getMessage(Message.BASE_GET_SUCCESS.getCode());
		logger.info(message);
		int waitTimeOut= Integer.parseInt(list.get(0).get("waitTimeOut").toString());
		return waitTimeOut;
	}
}
