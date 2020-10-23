package esun.dbhelper.dao.mapper;

import java.util.List;
import java.util.Map;


public interface BaseMapper {
    /**
     * 执行select相关SQL语句
     * @param sql
     * @return
     */
    List<Map<String,Object>> select(String sql);

    /**
     * 执行insert相关SQL语句
     * @param sql
     * @return
     */
    int insert(String sql);

    /**
     * 执行update相关SQL语句
     * @param sql
     * @return
     */
    int update(String sql);

    /**
     * 执行delete相关sql语句
     * @param sql
     * @return
     */
    int delete(String sql);

    /**
     * 执行select存储过程
     * @param sql
     * @return
     */
    List<Map<String,Object>> selectProcedures(String sql);

    /**
     * 执行insert存储过程
     * @param sql
     * @return
     */
    int insertProcedures(String sql);

    /**
     * 执行update存储过程
     * @param sql
     * @return
     */
    int updateProcedures(String sql);

    /**
     * 执行delete存储过程
     * @param sql
     * @return
     */
    int deleteProcedures(String sql);


    /**
     * 获取传入产品的数据源
     * @param product
     * @return
     */
    String datasource(String product);

    /**
     * 获取Postgres相关表的索引
     * @param table
     * @return
     */
    List<String> postgresIndexes(String table);

    /**
     * 获取mysql相关表的索引
     * @param table
     * @return
     */
    List<String> mysqlIndexes(String table);

    List<Map<String,Object>> test();



}
