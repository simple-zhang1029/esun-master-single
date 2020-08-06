package esun.dbhelper.utils;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.List;

//SQL读写分离解析工具
public class ExecutionEventUtil {

    public boolean isDML(String sql){
        return isDML(sql,"mysql");
    }

    /**
     * 判断是否为写操作
     * @param sql
     * @param dataSource
     * @return
     */
    public boolean isDML(String sql,String dataSource){
        String dbType;
        //根据数据源选择解析数据库类型
        switch (dataSource){
            case "SQLServer":
                dbType="JdbcConstants.SQLServer";
                break;
            case "MySQL" :
                dbType="JdbcConstants.MYSQL";
                break;
            default:
                dbType="JdbcConstants.POSTGRES";
        }
        //解析SQL
        List<SQLStatement> sqlStatementList= SQLUtils.parseStatements(sql,dbType);
        for (int i = 0; i <sqlStatementList.size() ; i++) {
            SQLStatement sqlStatement=sqlStatementList.get(i);
            SchemaStatVisitor visitor=new SchemaStatVisitor();
            sqlStatement.accept(visitor);
        }
        return false;
    }
}
