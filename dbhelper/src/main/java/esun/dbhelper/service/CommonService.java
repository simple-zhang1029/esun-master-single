package esun.dbhelper.service;

import java.util.List;
import java.util.Map;

public interface CommonService {
    void  test(String sql);
    /**
     * 用于update SQL语句
     */
    int post(String sql);

    /**
     * 用于select SQL语句
     * @param sql
     * @return
     */
    List<Map<String,Object>> get(String sql);

    /**
     * 用于delete SQL语句
     */
    int delete(String sql);

    /**
     * 用于insert SQL语句
     */
    int put(String sql);

    /**
     * 用于update SQL存储过程
     */
    int postProcedures(String sql);

    /**
     * 用于select SQL存储过程
     * @param sql
     * @return
     */
    List<Map<String,Object>> getProcedures(String sql);

    /**
     * 用于delete SQL存储过程
     */
    int deleteProcedures(String sql);

    /**
     * 用于insert SQL存储过程
     */
    int putProcedures(String sql);


    /**
     * 获取产品数据源
     * @param product
     * @return
     */
    String dataSource(String product);

    /**
     * 获取索引
     * @param table
     * @param dataSource
     * @return
     */
    List<String> indexes(String table, String dataSource);

    List<Map<String,Object>> selectPage(String sql,int pageIndex,int pageSize);


}
