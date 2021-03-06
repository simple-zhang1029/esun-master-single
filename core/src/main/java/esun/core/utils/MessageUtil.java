package esun.core.utils;

import esun.core.exception.CustomHttpException;
import esun.core.service.DbHelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class MessageUtil {

    private static String DEFAULT_LANGUAGE="English";

    private static String MESSAGE_TABLE="message_table";

    private static String DEFAULT_PRODUCT="default";


    public static String getMessage(String code){

        return getMessage(code,DEFAULT_LANGUAGE);
    }

    /**
     * 获取返回信息
     * @param code
     * @param language
     * @return
     */
    public static String getMessage(String code,String language){
        DbHelperService dbHelperService=SpringContextUtils.getBean(DbHelperService.class);
        String sql="select message from "+MESSAGE_TABLE+" where code='"+code+"' and language ='"+ language+"' ;";
        ResultUtil result=dbHelperService.select(sql,DEFAULT_PRODUCT);
        if(HttpStatus.OK.value()!= (int)result.get("code")) {
           throw new CustomHttpException("获取信息失败");
        }
        ArrayList list= (ArrayList) result.get("result");
        if (list.size()>0){
            HashMap resultMap= (HashMap) list.get(0);
            String message=resultMap.get("message").toString();
            return message;
        }
        String message="获取消息失败，数据库中不存在该信息";
        return message;
    }

    public static String getDefaultLanguage() {
        return DEFAULT_LANGUAGE;
    }

    @Value("${system.language}")
    public  void setDefaultLanguage(String defaultLanguage) {
        DEFAULT_LANGUAGE = defaultLanguage;
    }
}