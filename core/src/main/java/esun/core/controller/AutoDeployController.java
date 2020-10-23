package esun.core.controller;

import esun.core.annotation.LoginRequire;
import esun.core.service.AutoDeployService;
import esun.core.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/autoDeploy")
public class AutoDeployController {
    @Autowired
    AutoDeployService autoDeployService;
    /**
    * 获取自动部署项目信息
    * @param projectName 项目名称
    * @return
    */
    @GetMapping("projectInfoList")
    @LoginRequire
    public ResultUtil getProjectInfoList(@RequestParam(value = "projectName",required = false,defaultValue = "") String projectName){
        return autoDeployService.getProjectInfoList(projectName);
    }

    /**
    *自动部署
    * @param projectName 项目名称
    * @param version 版本
    * @return
    */
    @PostMapping("deploy")
    @LoginRequire
    public ResultUtil deploy(@RequestParam("projectName") String projectName,@RequestParam("version") String version,@RequestParam("name") String name){
      return autoDeployService.deploy(projectName,version,name);
    }
}
