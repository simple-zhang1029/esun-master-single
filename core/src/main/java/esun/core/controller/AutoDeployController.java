package esun.core.controller;

import esun.core.annotation.LoginRequire;
import esun.core.service.AutoDeployService;
import esun.core.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/autoDeploy")
public class AutoDeployController {
    @Autowired
    AutoDeployService autoDeployService;
    @GetMapping("projectInfoList")
    @LoginRequire
    public ResultUtil getProjectInfoList(){
        return autoDeployService.getProjectInfoList();
    }


    @GetMapping("projectInfo")
    @LoginRequire
    public ResultUtil getProjectInfo(@RequestParam("projectName") String projectName){
        return autoDeployService.getProjectInfo(projectName);
    }
}
