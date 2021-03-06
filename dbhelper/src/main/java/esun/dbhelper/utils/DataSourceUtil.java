package esun.dbhelper.utils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
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
                dbType="sqlserver";
                break;
            case "mysql" :
                dbType="mysql";
                break;
            default:
                dbType="postgresql";
        }
        //解析SQL
        sql= SQLUtils.format(sql,dbType);
        List<SQLStatement> sqlStatementList= SQLUtils.parseStatements(sql,dbType);
        for (int i = 0; i <sqlStatementList.size() ; i++) {
            SQLStatement sqlStatement=sqlStatementList.get(i);
            PGSchemaStatVisitor visitor=new PGSchemaStatVisitor();
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
            }
        }
        //检测where条件是否存在索引
        for (int i = 0; i <conditionList.size() ; i++) {
            if(listMap.size()==0){
                createIndex(conditionList.get(i));
            }
            else if(!doCheckIndex(listMap,conditionList.get(i))){
                logger.error(conditionList.get(i).getColumn().getName()+"没有索引");
                createIndex(conditionList.get(i));
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

    /**
     * 创建缺失索引
     * @param condition
     */
    public  static  void createIndex(TableStat.Condition condition){
        String table=condition.getColumn().getTable();
        String name=condition.getColumn().getName();
        if(crateIndex(table,name)==false){
            logger.error(table+":"+name+"添加索引失败");
            throw new CustomHttpException(table+":"+name+"添加索引失败");
        }
        logger.info(table+":"+name+"添加索引成功");

    }
    /**
     * 创建缺失的索引
     * @param name
     * @return
     */
    public static boolean crateIndex(String table,String name){
        String indexName=table+"_"+name+"_index";
        String sql="create index "+indexName+" on "+table+" ("+name+");";
        CommonService commonService=SpringContextUtils.getBean(CommonService.class);
        int i=commonService.put(sql);
        if(i>=0){
            return  true;
        }
        return  false;
    }


}
