package esun.wharf.controller;

import esun.wharf.service.ReceivingService;
import esun.wharf.utils.ResultUtil;
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
@RequestMapping("v1/wharf")
@RestController
public class ReceivingController {

	@Autowired
	ReceivingService receivingService;


	/**
	 * 获取客户信息及订单
	 * @param supplierList 客户列表
	 * @param supplierName 客户名称
	 * @author john.xiao
	 * @date 2020-10-16 17:06
	 */
	@GetMapping("supplier")
	public ResultUtil getSupplierInfo(@RequestParam(value = "list",required = false,defaultValue = "[]")String supplierList,
	                                  @RequestParam(value = "supplierName",required = false,defaultValue = "")String supplierName){
		JSONArray jsonArray=JSONArray.fromObject(supplierList);
		if (jsonArray.size()>0){
			return receivingService.getSupplierInfo(jsonArray);
		}
		return receivingService.getSupplierInfo(supplierName);
	}

	/**
	 * 获取发货数据信息
	 * @return
	 * @author john.xiao
	 * @date 2020-10-16 17:06
	 */
	@GetMapping("/receivingGoods")
	public ResultUtil getReceivingGoods(@RequestParam(value = "startDate",required = false,defaultValue = "2000-01-01")String startDate,
	                                   @RequestParam(value = "endDate",required = false,defaultValue = "2099-01-01")String endDate,
	                                   @RequestParam(value = "pageIndex",required = false,defaultValue = "1")int pageIndex,
	                                   @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
	                                   @RequestParam(value = "criteria",required = false,defaultValue = "dataType")String criteria,
	                                   @RequestParam(value = "sort",required = false,defaultValue = "0")int sort,
	                                   @RequestParam(value = "supplier",required = false,defaultValue = "")String supplier,
	                                   @RequestParam(value = "orderId",required = false,defaultValue = "")String orderId,
	                                   @RequestParam(value = "wharfList",required = false,defaultValue = "[]")String wharf){
		String sortParam;
		switch (criteria){
			case "orderId":
				sortParam="order_id";
				break;
			case "supplier":
				sortParam="supplier";
				break;
			case "wharf":
				sortParam="wharf";
				break;
			case "carNo":
				sortParam="car_no";
				break;
			case "planReceivingNo":
				sortParam="plan_receiving_no";
				break;
			case "receivingNo":
				sortParam="receiving_no";
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
			case "receivingStatus":
				sortParam="receiving_status";
				break;
			case "planDate":
				sortParam="plan_date";
				break;
			default:
				sortParam="id";
		}
		JSONArray jsonArray=JSONArray.fromObject(wharf);
		return receivingService.getReceivingGoods(startDate,endDate,pageIndex,pageSize,sortParam,sort,supplier,orderId,jsonArray);
	}

	/**
	 * 添加发货信息
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-16 17:06
	 */
	@PutMapping("/receivingGoods")
	public ResultUtil addReceivingGoods(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return receivingService.addReceivingGoods(jsonArray);
	}

	/**
	 * 删除发货信息
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@DeleteMapping("/receivingGoods")
	public ResultUtil deleteReceivingGoods(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return receivingService.deleteReceivingGoods(jsonArray);
	}

	/**
	 * 更新发货信息
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@PostMapping("/receivingGoods")
	public ResultUtil updateReceivingGoods(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return receivingService.updateReceivingGoods(jsonArray);
	}

	/**
	 * 导入发货信息
	 * @param file
	 * @return
	 */
	@PostMapping("exportReceivingGoods")
	public ResultUtil exportReceivingGoods(@RequestParam("file") MultipartFile file){
		//初步处理Excel文件
		Workbook workbook=null;
		try {
			InputStream inputStream=file.getInputStream();
			workbook= WorkbookFactory.create(inputStream);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return receivingService.exportReceivingGoods(workbook);
	}


	/**
	 * 导出发货信息
	 * @param isDelete
	 * @param startDate
	 * @param endDate
	 * @param supplier
	 * @param orderId
	 * @return
	 */
	@GetMapping("deriveReceivingHistory")
	public ResultUtil deriveHistory(@RequestParam(value = "isDelete",required = false,defaultValue = "false")boolean isDelete,
	                                @RequestParam(value = "startDate",required = false,defaultValue = "2000-01-01")String startDate,
	                                @RequestParam(value = "endDate",required = false,defaultValue = "2099-01-01")String endDate,
	                                @RequestParam(value = "supplier",required = false,defaultValue = "[]")String supplier,
	                                @RequestParam(value = "orderId",required = false,defaultValue = "")String orderId){
		JSONArray jsonArray=JSONArray.fromObject(supplier);
		return receivingService.deriveReceivingHistory(startDate,endDate,jsonArray,orderId,isDelete);
	}


	/**
	 * 校验发货信息码头占用
	 * @param wharf
	 * @return
	 */
	@PostMapping("receivingWharf")
	public ResultUtil checkWharfEmploy(@RequestParam("wharf")String wharf,
	                                   @RequestParam(value = "startDate",required = false,defaultValue = "2000-01-01")String startDate,
	                                   @RequestParam(value = "endDate",required = false,defaultValue = "2099-01-01")String endDate){
		return receivingService.checkWharf(wharf,startDate,endDate);
	}


	/**
	 * 获取发货状态
	 * @return
	 */
	@GetMapping("receivingStatus")
	public ResultUtil getReceivingStatus(){
		return receivingService.getReceivingStatus();
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
	@GetMapping("receivingBoard")
	public ResultUtil getBoardInfo(@RequestParam(value = "startDate",required = false,defaultValue = "2000-01-01")String startDate,
	                               @RequestParam(value = "endDate",required = false,defaultValue = "2099-01-01")String endDate,
	                               @RequestParam(value = "pageIndex",required = false,defaultValue = "1")int pageIndex,
	                               @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
	                               @RequestParam(value = "criteria",required = false,defaultValue = "dataType")String criteria,
	                               @RequestParam(value = "sort",required = false,defaultValue = "0")int sort,
	                               @RequestParam(value = "wharfList",required = false,defaultValue = "[{}]")String wharfList){
		String sortParam;
		switch (criteria){
			case "orderId":
				sortParam="order_id";
				break;
			case "supplier":
				sortParam="supplier";
				break;
			case "wharf":
				sortParam="wharf";
				break;
			case "carNo":
				sortParam="car_no";
				break;
			case "planReceivingNo":
				sortParam="plan_receiving_no";
				break;
			case "receivingNo":
				sortParam="receiving_no";
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
			case "receivingStatus":
				sortParam="receiving_status";
				break;
			case "planDate":
				sortParam="plan_date";
				break;
			default:
				sortParam="id";
		}
		JSONArray jsonArray=JSONArray.fromObject(wharfList);
		return receivingService.getBoardInfo(startDate,endDate,pageIndex,pageSize,sortParam,sort,jsonArray);
	}

}
