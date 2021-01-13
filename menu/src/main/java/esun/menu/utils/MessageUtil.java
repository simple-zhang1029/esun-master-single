package esun.menu.utils;


import esun.menu.exception.CustomHttpException;
import esun.menu.service.DbHelperService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class MessageUtil {

    private static String DEFAULT_LANGUAGE="English";

    private static String MESSAGE_TABLE="message_table";

    private static String DEFAULT_PRODUCT="mysql";

    public static final String CODE = "code";
    private static final String DATASOURCE_POSTGRES="postgres";

    public static final String SUCCESS_CODE="10000";


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
        if(!SUCCESS_CODE.equals(result.get(CODE).toString())) {
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