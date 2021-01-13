package com.example.controller.v1;

import com.example.constant.Message;
import com.example.service.v1.ExampleService;
import com.example.utils.MessageUtil;
import com.example.utils.ResultUtil;
import com.example.utils.SpringUtil;
import net.sf.json.JSONArray;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;


/**
 * @author test
 */
@RequestMapping("test/example")
@RestController
public class ExampleController {
	Logger logger= LoggerFactory.getLogger(ExampleController.class);
	@Autowired
	ExampleService exampleService;

	/**
	 * 模板程序登入
	 * @param userId
	 * @param password
	 * @return
	 */
	@PostMapping("login")
	//使用Router标签指定用户访问权限,拥有该权限才可请求此接口,否者返回错误,router权限在存储在数据库中
	//所有接口的返回类型固定为ResultUtil封装类
	//请求参数使用@RequestParam标签获取。
	public ResultUtil login(@RequestParam("userId")String userId,
	                        @RequestParam("password")String password){

		//检查密码长度
		if(password.length()<6 || password.length()>20){
			String message= MessageUtil.getMessage(Message.PASSWORD_NOT_STANDARD.getCode());
			logger.error(userId+":"+message);
			return ResultUtil.error(message);
		}
		return exampleService.login(userId,password);
	}

	/**
	 * 获取发货数据信息
	 * @return
	 * @author john.xiao
	 * @date 2020-10-16 17:06
	 */
	@GetMapping("/deliveryGoods")
	public ResultUtil getDeliveryGoods(@RequestParam(value = "startDate",required = false,defaultValue = "2000-01-01")String startDate,
	                                   @RequestParam(value = "endDate",required = false,defaultValue = "2099-01-01")String endDate,
	                                   @RequestParam(value = "pageIndex",required = false,defaultValue = "1")int pageIndex,
	                                   @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
	                                   @RequestParam(value = "criteriaList",required = false,defaultValue = "[]")String criteriaList,	                                   @RequestParam(value = "sort",required = false,defaultValue = "0")int sort,
	                                   @RequestParam(value = "customer",required = false,defaultValue = "")String customer,
	                                   @RequestParam(value = "orderId",required = false,defaultValue = "")String orderId,
	                                   @RequestParam(value = "wharfList",required = false,defaultValue = "[]")String wharfList){
		String sortParam;
		//排序条件json转化列表
		JSONArray criteriaArray=JSONArray.fromObject(criteriaList);
		//码头json转化列表
		JSONArray wharfArray=JSONArray.fromObject(wharfList);
		//如果有排序列表则以列表条件为优先
		if(criteriaArray.size()>0) {
			Optional criteriaOptional;
			for (int i = 0; i < criteriaArray.size(); i++) {
				Map<String, Object> listMap = (Map<String, Object>) criteriaArray.get(i);
				criteriaOptional = Optional.ofNullable(listMap.get("criteria"));
				switch (criteriaOptional.orElse("").toString()) {
					case "orderId":
						sortParam = "order_id";
						break;
					case "customer":
						sortParam = "customer";
						break;
					case "wharf":
						sortParam = "wharf";
						break;
					case "carNo":
						sortParam = "car_no";
						break;
					case "planDeliveryNo":
						sortParam = "plan_delivery_no";
						break;
					case "deliveryNo":
						sortParam = "delivery_no";
						break;
					case "planArrivedTime":
						sortParam = "plan_arrived_time";
						break;
					case "arrivedTime":
						sortParam = "arrived_time";
						break;
					case "planLeaveTime":
						sortParam = "plan_leave_time";
						break;
					case "leaveTime":
						sortParam = "leave_time";
						break;
					case "deliveryStatus":
						sortParam = "delivery_status";
						break;
					case "planDate":
						sortParam = "plan_date";
						break;
					default:
						sortParam = "id";
				}
				listMap.put("criteria", sortParam);
			}
		}
		return exampleService.getDeliveryGoods(startDate, endDate, pageIndex, pageSize,customer,orderId, criteriaArray, wharfArray);
	}

	/**
	 * 添加发货信息
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-16 17:06
	 */
	@PutMapping("/deliveryGoods")
	public ResultUtil addDeliveryGoods(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return exampleService.addDeliveryGoods(jsonArray);
	}

	/**
	 * 删除发货信息
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@DeleteMapping("/deliveryGoods")
	public ResultUtil deleteDeliveryGoods(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return exampleService.deleteDeliveryGoods(jsonArray);
	}

	/**
	 * 更新发货信息
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@PostMapping("/deliveryGoods")
	public ResultUtil updateDeliveryGoods(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return exampleService.updateDeliveryGoods(jsonArray);
	}

	/**
	 * 导入发货信息
	 * @param file
	 * @return
	 */
	@PostMapping("exportDeliveryGoods")
	public ResultUtil exportDeliveryGoods(@RequestParam("file") MultipartFile file){
		//初步处理Excel文件
		Workbook workbook=null;
		try {
			InputStream inputStream=file.getInputStream();
			workbook= WorkbookFactory.create(inputStream);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return exampleService.exportDeliveryGoods(workbook);
	}

	/**
	 * 导出发货信息
	 * @param isDelete
	 * @param startDate
	 * @param endDate
	 * @param customer
	 * @param orderId
	 * @return
	 */
	@GetMapping("deriveDeliveryHistory")
	public ResultUtil deriveHistory(@RequestParam(value = "isDelete",required = false,defaultValue = "false")boolean isDelete,
	                                @RequestParam(value = "startDate",required = false,defaultValue = "2000-01-01")String startDate,
	                                @RequestParam(value = "endDate",required = false,defaultValue = "2099-01-01")String endDate,
	                                @RequestParam(value = "customer",required = false,defaultValue = "[]")String customer,
	                                @RequestParam(value = "orderId",required = false,defaultValue = "")String orderId){
		JSONArray jsonArray=JSONArray.fromObject(customer);
		return exampleService.deriveDeliveryHistory(startDate,endDate,jsonArray,orderId,isDelete);
	}

	@RequestMapping("test")
	public void test(){
		HashSet<Object> loginSet= (HashSet<Object>) SpringUtil.getBean("loginSet");
		loginSet.add("test");
	}

}
