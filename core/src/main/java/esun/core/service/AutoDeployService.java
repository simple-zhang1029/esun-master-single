package esun.core.service;

import esun.core.utils.ResultUtil;

public interface AutoDeployService {

    ResultUtil getProjectInfoList();

    ResultUtil getProjectInfo(String projectName);

}
