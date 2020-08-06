package esun.dbhelper.controller;

import com.sun.istack.Nullable;
import esun.dbhelper.dataSources.DataSource;
import esun.dbhelper.dataSources.DataSourceType;
import esun.dbhelper.service.CommonService;
import esun.dbhelper.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("common")
@RestController
public class CommonController {
    @Autowired
    private CommonService commonService;

    /**
     * update
     */
    @PostMapping("dbHelper")
    @DataSourceType(name = "master")
    public ResultUtil post(@RequestParam("sql") String sql,
                           @RequestParam(value = "product",required = false,defaultValue = "default")String product){
        commonService.post(sql);
        return ResultUtil.ok("sql语句执行成功");
    }

    /**
     * select
     * @param sql
     */
    @GetMapping("dbHelper")
    @DataSourceType(name = "salve")
    public ResultUtil get(@RequestParam("sql") String sql,
                          @RequestParam(value = "product",required = false,defaultValue = "default")String product){
        List<Map<String,Object>> result=commonService.get(sql);
        return ResultUtil.ok("sql语句执行成功").put("result",result);
    }

    /**
     * delete
     * @param sql
     */
    @DeleteMapping("dbHelper")
    @DataSourceType(name = "master")
    public ResultUtil delete(@RequestParam("sql") String sql,
                             @RequestParam(value = "product",required = false,defaultValue = "default")String product){
        commonService.delete(sql);
        return ResultUtil.ok("sql语句执行成功");
    }

    /**
     * insert
     * @param sql
     */
    @PutMapping("dbHelper")
    @DataSourceType(name = "master")
    public ResultUtil put(@RequestParam("sql") String sql,
                          @RequestParam(value = "product",required = false,defaultValue = "default")String product){
        commonService.put(sql);
        return ResultUtil.ok("sql语句执行成功");
    }

    /**
     * 执行 select存储过程
     * @param sql
     */
    @GetMapping("procedures")
    @DataSourceType(name = "slave")
    public ResultUtil selectProcedures(@RequestParam("sql")String sql,
                                       @RequestParam(value = "product",required = false,defaultValue = "default")String product){
        List<Map<String,Object>> result=commonService.get(sql);
        return ResultUtil.ok("sql语句执行成功").put("result",result);
    }

    /**
     * 执行 delete存储过程
     * @param sql
     * @return
     */
    @DeleteMapping("procedures")
    @DataSourceType(name = "master")
    public ResultUtil deleteProcedures(@RequestParam("sql")String sql,
                                       @RequestParam(value = "product",required = false,defaultValue = "default")String product){
        commonService.deleteProcedures(sql);
        return ResultUtil.ok("sql语句执行成功");
    }

    /**
     * 执行update 存储过程
     * @param sql
     */
    @PostMapping("procedures")
    @DataSourceType(name = "master")
    public ResultUtil updateProcedures(@RequestParam("sql")String sql,
                                       @RequestParam(value = "product",required = false,defaultValue = "default")String product){
        commonService.postProcedures(sql);
        return ResultUtil.ok("sql语句执行成功");
    }

    /**
     * 执行insert存储过程
     * @param sql
     */
    @PatchMapping("procedures")
    @DataSourceType(name = "master")
    public ResultUtil insertProcedures(@RequestParam("sql") String sql,
                                       @RequestParam(value = "product",required = false,defaultValue = "default")String product){
        commonService.putProcedures(sql);
        return ResultUtil.ok("sql语句执行成功");
    }


    /**
     * 分页查询
     * @param sql
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("dbHelperPage")
    @DataSourceType(name = "slave")
    public ResultUtil selectPage(@RequestParam("sql") String sql,
                                 @RequestParam(value = "product",required = false,defaultValue = "default")String product,
                                 @RequestParam(value = "pageIndex",required = false,defaultValue = "1") @Nullable int pageIndex,
                                 @RequestParam(value = "pageSize",required = false,defaultValue = "10") @Nullable int pageSize){
        List<Map<String,Object>> result=commonService.selectPage(sql,pageIndex,pageSize);
        return ResultUtil.ok("sql语句执行成功").put("result",result);
    }


    @PostMapping("test")
    public void  test(@RequestParam("sql")String sql){
        commonService.test(sql);
    }
}
