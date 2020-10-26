package esun.wharf.controller;

import esun.wharf.service.DeliveryService;
import esun.wharf.utils.ResultUtil;
import net.sf.json.JSONArray;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * @author test
 */
@RequestMapping("v1/wharf")
@RestController
public class DeliveryController {

	@Autowired
	DeliveryService deliveryService;

	/**
	 * 获取客户信息及订单
	 * @param customerList 客户列表
	 * @param customerName 客户名称
	 * @author john.xiao
	 * @date 2020-10-16 17:06
	 */
	@GetMapping("customer")
	public ResultUtil getCustomerInfo(@RequestParam(value = "list",required = false,defaultValue = "[]")String customerList,
	                                  @RequestParam(value = "customerName",required = false,defaultValue = "")String customerName){
		JSONArray jsonArray=JSONArray.fromObject(customerList);
		if (jsonArray.size()>0){
			return deliveryService.getCustomerInfo(jsonArray);
		}
		return deliveryService.getCustomerInfo(customerName);
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
	                                   @RequestParam(value = "criteria",required = false,defaultValue = "dataType")String criteria,
	                                   @RequestParam(value = "sort",required = false,defaultValue = "0")int sort,
	                                   @RequestParam(value = "customer",required = false,defaultValue = "")String customer,
	                                   @RequestParam(value = "orderId",required = false,defaultValue = "")String orderId,
	                                   @RequestParam(value = "wharfList",required = false,defaultValue = "[]")String wharf){
		String sortParam;
		switch (criteria){
			case "orderId":
				sortParam="order_id";
				break;
			case "customer":
				sortParam="customer";
				break;
			case "wharf":
				sortParam="wharf";
				break;
			case "carNo":
				sortParam="car_no";
				break;
			case "planDeliveryNo":
				sortParam="plan_delivery_no";
				break;
			case "deliveryNo":
				sortParam="delivery_no";
				break;
			case "planArrivedTime":
				sortParam="plan_arrived_time";
				break;
			case "arrivedTime":
				sortParam="arrived_time";
				break;
			case "planLeaveTime":
				sortParam="plan_leave_time";
				break;
			case "leaveTime":
				sortParam="leave_time";
				break;
			case "deliveryStatus":
				sortParam="delivery_status";
				break;
			case "planDate":
				sortParam="plan_date";
				break;
			default:
				sortParam="id";
		}
		JSONArray jsonArray=JSONArray.fromObject(wharf);
		return deliveryService.getDeliveryGoods(startDate,endDate,pageIndex,pageSize,sortParam,sort,customer,orderId,jsonArray);
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
		return deliveryService.addDeliveryGoods(jsonArray);
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
		return deliveryService.deleteDeliveryGoods(jsonArray);
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
		return deliveryService.updateDeliveryGoods(jsonArray);
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
		return deliveryService.exportDeliveryGoods(workbook);
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
		return deliveryService.deriveDeliveryHistory(startDate,endDate,jsonArray,orderId,isDelete);
	}

	/**
	 * 校验发货信息码头占用
	 * @param wharf
	 * @return
	 */
	@PostMapping("deliveryWharf")
	public ResultUtil checkWharfEmploy(@RequestParam("wharf")String wharf,
	                                   @RequestParam(value = "startDate",required = false,defaultValue = "2000-01-01")String startDate,
	                                   @RequestParam(value = "endDate",required = false,defaultValue = "2099-01-01")String endDate) {
		return deliveryService.checkWharf(wharf,startDate,endDate);
	}

	/**
	 * 获取发货状态
	 * @return
	 */
	@GetMapping("deliveryStatus")
	public ResultUtil getDeliveryStatus(){
		return deliveryService.getDeliveryStatus();
	}


	/**
	 * 看板信息查询
	 * @param startDate
	 * @param endDate
	 * @param pageIndex
	 * @param pageSize
	 * @param criteria
	 * @param sort
	 * @param wharfList
	 * @return
	 */
	@GetMapping("deliveryBoard")
	public ResultUtil getBoardInfo(@RequestParam(value = "startDate",required = false,defaultValue = "2000-01-01")String startDate,
	                               @RequestParam(value = "endDate",required = false,defaultValue = "2099-01-01")String endDate,
	                               @RequestParam(value = "pageIndex",required = false,defaultValue = "1")int pageIndex,
	                               @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
	                               @RequestParam(value = "criteriaList",required = false,defaultValue = "[]")String criteriaList,
	                               @RequestParam(value = "criteria",required = false,defaultValue = "dataType")String criteria,
	                               @RequestParam(value = "sort",required = false,defaultValue = "0")int sort,
	                               @RequestParam(value = "wharfList",required = false,defaultValue = "[{}]")String wharfList){
		String sortParam;
		//排序条件json转化列表
		JSONArray criteriaArray=JSONArray.fromObject(criteriaList);
		//码头json转化列表
		JSONArray wharfArray=JSONArray.fromObject(wharfList);
		//如果有排序列表则以列表条件为优先
		if(criteriaArray.size()>0){
			Optional criteriaOptional;
			for (int i = 0; i < criteriaArray.size() ; i++)
			{
				Map<String,Object> listMap= (Map<String, Object>) criteriaArray.get(i);
				criteriaOptional=Optional.ofNullable(listMap.get("criteria"));
				switch (criteriaOptional.orElse("").toString()){
					case "orderId":
						sortParam="order_id";
						break;
					case "customer":
						sortParam="customer";
						break;
					case "wharf":
						sortParam="wharf";
						break;
					case "carNo":
						sortParam="car_no";
						break;
					case "planDeliveryNo":
						sortParam="plan_delivery_no";
						break;
					case "deliveryNo":
						sortParam="delivery_no";
						break;
					case "planArrivedTime":
						sortParam="plan_arrived_time";
						break;
					case "arrivedTime":
						sortParam="arrived_time";
						break;
					case "planLeaveTime":
						sortParam="plan_leave_time";
						break;
					case "leaveTime":
						sortParam="leave_time";
						break;
					case "deliveryStatus":
						sortParam="delivery_status";
						break;
					case "planDate":
						sortParam="plan_date";
						break;
					default:
						sortParam="id";
				}
				listMap.put("criteria",sortParam);
			}
			return deliveryService.getBoardInfo(startDate,endDate,pageIndex,pageSize,criteriaArray,wharfArray);
		}
		switch (criteria){
			case "orderId":
				sortParam="order_id";
				break;
			case "customer":
				sortParam="customer";
				break;
			case "wharf":
				sortParam="wharf";
				break;
			case "carNo":
				sortParam="car_no";
				break;
			case "planDeliveryNo":
				sortParam="plan_delivery_no";
				break;
			case "deliveryNo":
				sortParam="delivery_no";
				break;
			case "planArrivedTime":
				sortParam="plan_arrived_time";
				break;
			case "arrivedTime":
				sortParam="arrived_time";
				break;
			case "planLeaveTime":
				sortParam="plan_leave_time";
				break;
			case "leaveTime":
				sortParam="leave_time";
				break;
			case "deliveryStatus":
				sortParam="delivery_status";
				break;
			case "planDate":
				sortParam="plan_date";
				break;
			default:
				sortParam="id";
		}
		return deliveryService.getBoardInfo(startDate,endDate,pageIndex,pageSize,sortParam,sort,wharfArray);
	}

}
