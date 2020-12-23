package com.example.controller;

import com.example.service.DomainService;
import com.example.utils.ResultUtil;
import com.example.utils.SpringUtil;
import net.sf.json.JSONArray;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 域管理控制器
 * @author john.xiao
 * @date 2020-12-17 09:46
 */
@RestController
@RequestMapping("/v1/domainManage")
public class DomainController {
	@Autowired
	DomainService domainService;

	/**
	 * 获取域信息
	 * @param pageIndex 页码数
	 * @param pageSize  分页大小
	 * @param criteriaList 排序列表
	 * @param domain  域名
	 * @return
	 *
	 */
	@GetMapping("/domain")
	public ResultUtil getDomain(@RequestParam(value = "pageIndex",required = false,defaultValue = "1")int pageIndex,
	                            @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize,
	                            @RequestParam(value = "criteriaList",required = false,defaultValue = "[]")String criteriaList, @RequestParam(value = "sort",required = false,defaultValue = "0")int sort,
	                            @RequestParam(value = "domain",required =  false,defaultValue = "")String domain){
		//排序条件json转化列表
		JSONArray criteriaArray=JSONArray.fromObject(criteriaList);
		//如果有排序列表则以列表条件为优先
		Optional criteriaOptional;
		String sortParam;
		for (int i = 0; i < criteriaArray.size(); i++) {
			Map<String, Object> listMap = (Map<String, Object>) criteriaArray.get(i);
			criteriaOptional = Optional.ofNullable(listMap.get("criteria"));
			switch (criteriaOptional.orElse("").toString()) {
				case "domainName":
					sortParam = "domain_name";
					break;
				case "domainCorp":
					sortParam = "domain_corp";
					break;
				case "shortName":
					sortParam = "domain_sname";
					break;
				case "dataBase":
					sortParam = "domain_db";
					break;
				case "active":
					sortParam = "domain_active";
					break;
				case "domainProPath":
					sortParam = "domain_propath";
					break;
				case "domainType":
					sortParam = "domain_type";
					break;
				case "maxUser":
					sortParam = "domain_max_user";
					break;
				case "domainAdmin":
					sortParam = "domain_admin";
					break;
				default:
					sortParam = "domain_domain";
			}
			listMap.put("criteria", sortParam);
		}
		return domainService.getDomain(pageIndex, pageSize, criteriaArray,domain);
	}



	/**
	 * 添加域信息
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-16 17:06
	 */
	@PutMapping("/domain")
	public ResultUtil addDomain(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return domainService.addDomain(jsonArray);
	}

	/**
	 * 删除域信息
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@DeleteMapping("/domain")
	public ResultUtil deleteDomain(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return domainService.deleteDomain(jsonArray);
	}

	/**
	 * 更新域信息
	 * @param dataList
	 * @return
	 * @author john.xiao
	 * @date 2020-10-13 17:06
	 */
	@PostMapping("/domain")
	public ResultUtil updateDomain(@RequestParam(value = "list") String dataList){
		JSONArray jsonArray=JSONArray.fromObject(dataList);
		return domainService.updateDomain(jsonArray);
	}

	/**
	 * 导入域信息
	 * @param file
	 * @return
	 * @author john.xiao
	 * @date 2020-12-17 15:07
	 */
	@PostMapping("exportDomain")
	public ResultUtil exportDomain(@RequestParam("file") MultipartFile file){
		//初步处理Excel文件
		Workbook workbook=null;
		try {
			InputStream inputStream=file.getInputStream();
			workbook= WorkbookFactory.create(inputStream);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return domainService.exportDomain(workbook);
	}

	/**
	 * 导出域信息
	 * @param isDelete
	 * @return
	 * @author john.xiao
	 * @date 2020-12-17 15:07
	 */
	@GetMapping("deriveDomain")
	public ResultUtil deriveDomain(@RequestParam(value = "isDelete",required = false,defaultValue = "false")boolean isDelete,
	                               @RequestParam(value = "domain",required = false,defaultValue = "")String domain){
		return domainService.deriveDomain(domain,isDelete);
	}

	/**
	 * 获取用户域
	 * @param user 用户名
	 * @return 返回结果工具类
	 * @author john.xiao
	 * @date 2020-12-22 09:37
	 */
	@GetMapping("userDomain")
	public ResultUtil getUserDomain(@RequestParam("user")String user){
		return domainService.getUserDomain(user);
	}

	/**
	 * 更新用户域
	 * @param user 用户名
	 * @param list 域信息列表
	 * @return 返回结果工具类
	 * @author john.xiao
	 * @date 2020-12-22 11:25
	 */
	@PostMapping("userDomain")
	public ResultUtil updateUserDomain(@RequestParam("user")String user,
	                                   @RequestParam("list")String list){
		JSONArray jsonArray=JSONArray.fromObject(list);
		return domainService.updateUserDomain(user,jsonArray);

	}


}
