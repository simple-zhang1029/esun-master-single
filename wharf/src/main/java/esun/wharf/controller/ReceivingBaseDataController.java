package esun.wharf.controller;

import esun.wharf.service.ReceivingBaseDataService;
import esun.wharf.utils.ResultUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author test
 */
@RequestMapping("v1/wharf")
@RestController
public class ReceivingBaseDataController {
	@Autowired
	ReceivingBaseDataService receivingBaseDataService;
	/**
	 * 获取基础数据信息
	 * @return
	 * @author john.xiao
	 * @date 2020-10-15 17:06
	 */
	@GetMapping("/receivingBaseData")
	public ResultUtil getBaseDataList(@RequestParam(value = "dataName" ,required = false,defaultValue = "")String dataName,
	                                  @RequestParam(value = "pageIndex",required = false,defaultValue = "1")int pageIndex,
	                                  @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
	                                  @RequestParam(value = "criteria",required = false,defaultValue = "id")String criteria,
	                                  @RequestParam(value = "sort",required = false,defaultValue = "0")int sort){
		String sortParam;
		switch (criteria){
			case "dataType":
				sortParam="data_type";
				break;
			case "dataName":
				sortParam="data_name";
				break;
			case "value1":
				sortParam="value_1";
				break;
			case "value2":
				sortParam="value_2";
				break;
			default:
				sortParam="id";
		}
		return receivingBaseDataService.getBaseData(dataName,pageIndex,pageSize,sortParam,sort);
	}

	/**
	 * 添加菜单
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@PutMapping("/receivingBaseData")
	public ResultUtil addBaseData(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return receivingBaseDataService.addBaseData(jsonArray);
	}

	/**
	 * 删除菜单
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@DeleteMapping("/receivingBaseData")
	public ResultUtil deleteMenuList(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return receivingBaseDataService.deleteBaseData(jsonArray);
	}

	/**
	 * 更新菜单信息
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-16 17:06
	 */
	@PostMapping("/receivingBaseData")
	public ResultUtil updateMenuList(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return receivingBaseDataService.updateBaseData(jsonArray);
	}

	/**
	 * 获取码头信息
	 * @return
	 */
	@GetMapping("receivingWharf")
	public ResultUtil getWharf(){
		return receivingBaseDataService.getWharf();
	}
}
