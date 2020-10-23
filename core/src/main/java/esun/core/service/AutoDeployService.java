package esun.core.service;

import esun.core.utils.ResultUtil;

public interface AutoDeployService {

    ResultUtil getProjectInfoList(String project);

    ResultUtil deploy(String project,String version,String name);


}
