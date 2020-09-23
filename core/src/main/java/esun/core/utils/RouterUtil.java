package esun.core.utils;

import esun.core.service.DbHelperService;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

/**
 * 路由工具类
 */
public class RouterUtil {


    private static String GROUP_ROUTER_TABLE="group_router_table";

    private static String USER_GROUP_TABLE="user_group_table";

    private static String USER_TABLE="user_mstr";

    private static String DEFAULT_PRODUCT="postgres_test";



    /**
     * 校验路由
     * @return
     */
    public static boolean verifyRouter(String name,String router){
        DbHelperService dbHelperService=SpringContextUtils.getBean(DbHelperService.class);
        String sql="select 1 from "+GROUP_ROUTER_TABLE+" grt left join "+USER_GROUP_TABLE+" ugt on grt.group_id = ugt.group_id  where ugt.user_userid  = (select user_userid from "+USER_TABLE+" where user_name = '"+name+"')" +
                " and grt.router ='"+ router+"' ;";
        ResultUtil result=dbHelperService.select(sql,DEFAULT_PRODUCT);
        if(HttpStatus.OK.value() != (int)result.get("code")){
            return false;
        }
        ArrayList list= (ArrayList) result.get("result");
        if(list.size()>0){
            return true;
        }
        return false;
    }

}

