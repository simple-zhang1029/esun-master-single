package com.example.service;

import com.example.utils.ResultUtil;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 调用dbHelper服务
 */
@FeignClient(name = "dbHelper",url = "http://127.0.0.1:9081")
//@FeignClient(name = "dbHelper",url = "http://10.124.0.47:30012")
@RequestMapping("/common")
public interface DbHelperService {

    /**
     * 调用select服务
     * @param sql
     * @param product
     * @return
     */
    @GetMapping("dbHelper")
    ResultUtil select(@RequestParam("sql") String sql, @RequestParam("product") String product);

    /**
     * 调用update服务
     * @param sql
     * @param product
     * @return
     */
    @PostMapping("dbHelper")
    ResultUtil update(@RequestParam("sql") String sql, @RequestParam("product") String product);

    /**
     * 调用delete服务
     * @param sql
     * @param product
     * @return
     */
    @DeleteMapping("dbHelper")
    ResultUtil delete(@RequestParam("sql") String sql, @RequestParam("product") String product);

    /**
     * 调用insert服务
     * @param sql
     * @param product
     * @return
     */
    @PutMapping("dbHelper")
    ResultUtil insert(@RequestParam("sql") String sql, @RequestParam("product") String product);

    @GetMapping("dbHelperPage")
    ResultUtil selectPage(@RequestParam("sql") String sql,
                          @RequestParam("product") String product,
                          @RequestParam("pageIndex") int pageIndex,
                          @RequestParam("pageSize") int pageSize);
}
