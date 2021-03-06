package com.example.controller.v2;

import com.example.entity.DomainMstr;
import com.example.service.v2.DomainService;
import com.example.utils.FileUtils;
import com.example.utils.ResultUtil;
import net.sf.json.JSONArray;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController("DomainV2Controller")
@RequestMapping("v2/domainManage")
public class DomainController {
    @Resource(name = "DomainV2Service")
    DomainService domainService;

    @GetMapping("domainList")
    public ResultUtil getDomain(@RequestParam(value="pageIndex",required = false,defaultValue = "1")int pageIndex ,
                                @RequestParam(value="pageSize",required = false,defaultValue = "5")int pageSize,
                                @RequestParam(value="criteriaList",required = false,defaultValue ="[]" )String criteriaList,
                                @RequestParam(value ="domain",required = false,defaultValue = "")String domain){
        JSONArray criteriaArray = JSONArray.fromObject(criteriaList);
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
                case "maxDomain":
                    sortParam = "domain_max_domain";
                    break;
                case "domainAdmin":
                    sortParam = "domain_admin";
                    break;
                case "maxUser":
                    sortParam = "domain_max_users";
                    break;
                default:
                    sortParam = "domain_domain";
            }
            listMap.put("criteria", sortParam);
        }return domainService.getDomainInfoList(pageIndex,pageSize,domain,criteriaArray);
    }
    /**
     * 批量插入信息
     * @param domainMstrList 用实体类列表
     * @return 结果封装类
     */
    @PostMapping("domainList")
    public ResultUtil insertDomainInfoList(@RequestBody List<DomainMstr> domainMstrList){
        return domainService.insertDomainInfoList(domainMstrList);
    }

    /**
     * 批量更新信息
     * @param domainMstrList 用实体类列表
     * @return 结果封装类
     */
    @PutMapping("domainList")
    public ResultUtil updateDomainInfoList(@RequestBody List<DomainMstr> domainMstrList){
        return domainService.updateDomainInfoList(domainMstrList);
    }

    /**
     * 导入Excel插入或更新
     * @param file Excel文件
     * @return 结果封装类
     */
    @PostMapping("domainExcel")
    public ResultUtil insertDomainInfoByExcel(MultipartFile file){
        //初步处理Excel文件
        Workbook workbook=null;
        try {
            InputStream inputStream=file.getInputStream();
            workbook= WorkbookFactory.create(inputStream);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return domainService.batchDomainInfoInsertOrUpdate(workbook);
    }

    /**
     * 批量删除
     * @param domainMstrList
     * @return
     */
    @DeleteMapping("domainList")
    public ResultUtil deleteDomainInfoList(@RequestBody List<DomainMstr> domainMstrList){
        return domainService.deleteDomainInfoList(domainMstrList);
    }
    /**
     * 导出信息
     * @param domain 用户Id
     * @return
     */
    @GetMapping("domainExcel")
    public ResultUtil getDomainInfoByExcel(
            @RequestParam(value="domainDomain",required = false,defaultValue = "")String domain){
     return domainService.exportDomainInfo(domain);
    }
    /**
     * 获取导入模板
     */
    @GetMapping("template")
    public void getTemplate(){
        String path="E:/template/domain.xls";
        FileUtils fileUtils=new FileUtils();
        fileUtils.downLoad(path);
    }
//    /**
//     * 获取用户域信息
//     */
//    @GetMapping("userdomain")
//    public ResultUtil getUserdomain(@RequestParam("user") String user ){
//        return domainService.getUserDomainInfoList(user);
//    }
}
