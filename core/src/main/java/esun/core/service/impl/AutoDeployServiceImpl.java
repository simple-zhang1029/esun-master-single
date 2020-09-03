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

@Service
public class AutoDeployServiceImpl implements AutoDeployService {
    @Autowired
    DbHelperService dbHelperService;
    private static Logger logger= LoggerFactory.getLogger(ExampleServiceImpl.class);
    @Override
    public ResultUtil getProjectInfoList() {
        String message ;
        String sql ="select project_name,with_params,params_name,project_description,params_description from auto_deploy left join auto_deploy_params adp on auto_deploy.id = adp.project_id";
        ResultUtil result=dbHelperService.select(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            message= MessageUtil.getMessage(Message.GET_PROJECT_INFO_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        ArrayList list= (ArrayList) result.get("result");
        message=MessageUtil.getMessage(Message.GET_PROJECT_INFO_SUCCESS.getCode());
        return ResultUtil.ok().put("msg",message).put("result",list);
    }


    @Override
    public ResultUtil getProjectInfo(String projectName) {
        String message;
        String sql ="select project_name,with_params,params_name,project_description,params_description from auto_deploy left join auto_deploy_params adp on auto_deploy.id = adp.project_id where auto_deploy.project_name= '"+projectName+"'";
        ResultUtil result=dbHelperService.select(sql,"postgres_test");
        if(HttpStatus.OK.value()!= (int)result.get("code")){
            message= MessageUtil.getMessage(Message.GET_PROJECT_INFO_ERROR.getCode());
            logger.error(message);
            return ResultUtil.error(message);
        }
        ArrayList list= (ArrayList) result.get("result");
        message=MessageUtil.getMessage(Message.GET_PROJECT_INFO_SUCCESS.getCode());
        return ResultUtil.ok().put("msg",message).put("result",list);
    }
}
