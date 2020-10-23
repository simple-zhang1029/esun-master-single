package esun.core.service.impl;

import esun.core.constant.Message;
import esun.core.service.AutoDeployService;
import esun.core.service.DbHelperService;
import esun.core.utils.MessageUtil;
import esun.core.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class AutoDeployServiceImpl implements AutoDeployService {
    @Autowired
    DbHelperService dbHelperService;
    //Jenkins地址
    String jenkinsUrl="http://10.124.0.47:30964";
    private static Logger logger= LoggerFactory.getLogger(ExampleServiceImpl.class);
    //获取自动部署信息
    @Override
    public ResultUtil getProjectInfoList(String project) {
        String message ;
        String sql ="select id as \"projectId\",project_name as \"projectName\"," +
                    "project_description as \"projectDescription\",with_params as \"withParams\" " +
                    "from auto_deploy_project  where  project_name like '%"+project+"%'";
        ResultUtil result=dbHelperService.select(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            message= MessageUtil.getMessage(Message.GET_PROJECT_INFO_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        ArrayList<Map<String,Object>> list= (ArrayList) result.get("result");
        message=MessageUtil.getMessage(Message.GET_PROJECT_INFO_SUCCESS.getCode());
        return ResultUtil.ok().put("msg",message).put("result",list);
    }

    /**
     * 自动部署
     * @param project
     * @param version
     * @return
     */
    @Override
    public ResultUtil deploy(String project, String version,String name) {
        return null;
    }
}
