package com.example.controller.v1;

import com.example.service.v1.CorpService;
import com.example.utils.ResultUtil;
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
 * 公司管理控制器
 * @author john.xiao
 * @date 2020-12-17 09:46
 */
@RestController
@RequestMapping("/v1/corpManage")
public class CorpController {
	@Autowired
	CorpService corpService;

	/**
	 * 获取公司信息
	 * @param pageIndex 页码数
	 * @param pageSize  分页大小
	 * @param criteriaList 排序列表
	 * @param corp  公司名
	 * @return 结果封装类
	 *
	 */
	@GetMapping("corp")
	public ResultUtil getCorp(@RequestParam(value = "pageIndex",required = false,defaultValue = "1")int pageIndex,
	                            @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
	                            @RequestParam(value = "criteriaList",required = false,defaultValue = "[]")String criteriaList, @RequestParam(value = "sort",required = false,defaultValue = "0")int sort,
	                            @RequestParam(value = "corp",required =  false,defaultValue = "")String corp){
		//排序条件json转化列表
		JSONArray criteriaArray=JSONArray.fromObject(criteriaList);
		//如果有排序列表则以列表条件为优先
		Optional criteriaOptional;
		String sortParam;
		for (int i = 0; i < criteriaArray.size(); i++) {
			Map<String, Object> listMap = (Map<String, Object>) criteriaArray.get(i);
			criteriaOptional = Optional.ofNullable(listMap.get("criteria"));
			switch (criteriaOptional.orElse("").toString()) {
				case "corpName":
					sortParam = "corp_name";
					break;
				case "corpCorp":
					sortParam = "corp_corp";
					break;
				case "shortName":
					sortParam = "corp_sname";
					break;
				case "dataBase":
					sortParam = "corp_db";
					break;
				case "active":
					sortParam = "corp_active";
					break;
				case "corpProPath":
					sortParam = "corp_propath";
					break;
				case "corpType":
					sortParam = "corp_type";
					break;
				case "maxUser":
					sortParam = "corp_max_user";
					break;
				case "corpAdmin":
					sortParam = "corp_admin";
					break;
				default:
					sortParam = "corp_corp";
			}
			listMap.put("criteria", sortParam);
		}
		return corpService.getCorp(pageIndex, pageSize, criteriaArray,corp);
	}



	/**
	 * 添加公司信息
	 * @param dataList 用户信息列表
	 * @return 结果封装类
	 * @author john.xiao
	 * @date 2020-10-16 17:06
	 */
	@PutMapping("corp")
	public ResultUtil addCorp(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return corpService.addCorp(jsonArray);
	}

	/**
	 * 删除公司信息
	 * @param dataList 公司信息列表
	 * @return 结果封装类
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@DeleteMapping("corp")
	public ResultUtil deleteCorp(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return corpService.deleteCorp(jsonArray);
	}

	/**
	 * 更新公司信息
	 * @param dataList  公司信息列表
	 * @return 结果封装类
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@PostMapping("corp")
	public ResultUtil updateCorp(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return corpService.updateCorp(jsonArray);
	}

	/**
	 * 导入公司信息
	 * @param file Excel文件
	 * @return 结果封装类
	 * @author john.xiao
	 * @date 2020-12-17 15:07
	 */
	@PostMapping("exportCorp")
	public ResultUtil exportCorp(@RequestParam("file") MultipartFile file){
		//初步处理Excel文件
		Workbook workbook=null;
		try {
			InputStream inputStream=file.getInputStream();
			workbook= WorkbookFactory.create(inputStream);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return corpService.exportCorp(workbook);
	}

	/**
	 * 导出公司信息
	 * @param isDelete 是否删除
	 * @return 结果封装类
	 * @author john.xiao
	 * @date 2020-12-17 15:07
	 */
	@GetMapping("deriveCorp")
	public ResultUtil deriveCorp(@RequestParam(value = "isDelete",required = false,defaultValue = "false")boolean isDelete,
	                               @RequestParam(value = "corp",required = false,defaultValue = "")String corp){
		return corpService.deriveCorp(corp,isDelete);
	}




}
