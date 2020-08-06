package esun.dbhelper.utils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import esun.dbhelper.dataSources.DataSourceNames;
import esun.dbhelper.dataSources.DynamicDataSource;
import esun.dbhelper.exception.CustomHttpException;
import esun.dbhelper.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSourceUtil {
    private static Logger logger= LoggerFactory.getLogger(DataSourceUtil.class);



    public  static String setDatasourceByProductAndSql(String product,String sql){
        return "1";
    }

    public static  void setDatasource(String datasource,String datasourceTyp){
        switch (datasource){
            case "postgres":{
                if (!datasourceTyp.isEmpty() && "master".equals(datasourceTyp)){
                        DynamicDataSource.setDataSource(DataSourceNames.POSTGRES_MASTER);
                }
                else {
                    DynamicDataSource.setDataSource(DataSourceNames.POSTGRES_SLAVE);
                }
                break;
            }
            case "mysql":{
                DynamicDataSource.setDataSource(DataSourceNames.MYSQL);
                break;
            }
            default:
                DynamicDataSource.setDataSource(DataSourceNames.MYSQL);
        }
    }

    /**
     * 检验索引
     * @param sql
     */
    public static void checkIndex(String sql,String dataSource){

        String dbType;
        //根据数据源选择解析数据库类型
        switch (dataSource){
            case "SQLServer":
                dbType="JdbcConstants.SQLServer";
                break;
            case "mysql" :
                dbType="JdbcConstants.MYSQL";
                break;
            default:
                dbType="JdbcConstants.POSTGRES";
        }
        //解析SQL
        sql= SQLUtils.format(sql,dbType);
        List<SQLStatement> sqlStatementList= SQLUtils.parseStatements(sql,dbType);
        for (int i = 0; i <sqlStatementList.size() ; i++) {
            SQLStatement sqlStatement=sqlStatementList.get(i);
            SchemaStatVisitor visitor=new SchemaStatVisitor();
            sqlStatement.accept(visitor);
            //将set转换为list
            List<TableStat.Name> tableList=new ArrayList<>();
            tableList.addAll(visitor.getTables().keySet());
            //将list类型转为String
            List<String> tableStringList=new ArrayList<>();
            for (int j = 0; j <tableList.size() ; j++) {
                tableStringList.add(tableList.get(j).toString());
            }
            checkIndex(tableStringList,visitor.getConditions(),dataSource);
        }
    }



    /**
     * 检测索引
     * @param tableList
     * @param conditionList
     */
    public static void checkIndex(List<String> tableList,List<TableStat.Condition> conditionList,String dataSource){
        Map<String,List<String>> listMap=new HashMap<>();
        CommonService commonService=SpringContextUtils.getBean(CommonService.class);
        //获取相关表的全部索引
        for (int i = 0; i <tableList.size() ; i++) {
            List<String> indexList=commonService.indexes(tableList.get(i),dataSource);
            if(indexList.size()>0){
                listMap.put(tableList.get(i),indexList);
            }
            else {
                logger.error(tableList.get(i)+"表中没有任何索引");
                throw new CustomHttpException(tableList.get(i)+"表中没有任何索引");
            }
        }
        //检测where条件是否存在索引
        for (int i = 0; i <conditionList.size() ; i++) {
            if(!doCheckIndex(listMap,conditionList.get(i))){
                logger.error(conditionList.get(i).getColumn().getName()+"没有索引");
                throw new CustomHttpException(conditionList.get(i).getColumn().getName()+"没有索引");
            }
        }
    }


    /**
     * 检测where条件是否存在索引
     * @param indexMap
     * @param condition
     * @return
     */
    public static boolean doCheckIndex(Map<String,List<String>> indexMap,TableStat.Condition condition){
        String table=condition.getColumn().getTable();
        String name=condition.getColumn().getName();
        List<String> indexList=indexMap.get(table);
        return doCheckIndex(indexList,name);
    }

    /**
     * 检测是否存在索引
     * @param indexList
     * @param name
     * @return
     */
    public static boolean doCheckIndex(List<String> indexList,String name){
        for (int i = 0; i <indexList.size() ; i++) {
            String index=indexList.get(i);
            if(index.contains(name)){
                return true;
            }
        }
        return false;
    }


}
