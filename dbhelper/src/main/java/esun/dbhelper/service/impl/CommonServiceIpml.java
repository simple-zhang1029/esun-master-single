package esun.dbhelper.service.impl;


import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.util.JdbcConstants;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import esun.dbhelper.dao.mapper.BaseMapper;
import esun.dbhelper.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommonServiceIpml implements CommonService {
    @Autowired
    private BaseMapper baseMapper;

    @Override
    public void test(String sql) {
       Object result= baseMapper.select(sql);
       System.out.println(result);
    }

    @Override
    public int post(String sql) {
        int result=baseMapper.update(sql);
        return result;
    }

    @Override
    public List<Map<String, Object>> get(String sql) {
        List<Map<String,Object>> result=baseMapper.select(sql);
        return result;
    }

    @Override
    public int delete(String sql) {
        int result=baseMapper.delete(sql);
        return result;
    }

    @Override
    public int put(String sql) {
       int result=baseMapper.insert(sql);
       return result;
    }

    @Override
    public int postProcedures(String sql) {
        int result=baseMapper.update(sql);
        return result;
    }

    @Override
    public List<Map<String, Object>> getProcedures(String sql) {
        List<Map<String,Object>> result=baseMapper.select(sql);
        return result;
    }

    @Override
    public int deleteProcedures(String sql) {
        int result=baseMapper.delete(sql);
        return result;
    }

    @Override
    public int putProcedures(String sql) {
        int result=baseMapper.insert(sql);
        return result;
    }

    @Override
    public String dataSource(String product) {
        return baseMapper.datasource(product);
    }

    @Override
    public List<String> indexes(String table,String dataSource) {
        List<String> indexList;
        switch (dataSource){
            case "mysql":
                indexList=baseMapper.mysqlIndexes(table);
                break;
            default:
                indexList=baseMapper.postgresIndexes(table);
        }
        return indexList;
    }

    /**
     * select（分页）
     * @param sql
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo selectPage(String sql, int pageIndex, int pageSize) {
        PageHelper.startPage(pageIndex,pageSize);
        List<Map<String,Object>> result=baseMapper.select(sql);
        PageInfo pageInfo=new PageInfo(result);
        PageHelper.clearPage();
        return pageInfo;
    }

    @Override
    public List<Map<String, Object>> test() {
        String sql="select * from user_mstr where user_name like '%1234%';";
        List<Map<String,Object>> list=baseMapper.test();
        return list;
    }
}
