package esun.core.controller;

import esun.core.service.DeliveryService;
import esun.core.utils.ResultUtil;
import net.sf.json.JSONArray;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author test
 */
@RequestMapping("v2/example")
@RestController
public class ExampleV2Controller {

	@Autowired
	DeliveryService deliveryService;

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
}
