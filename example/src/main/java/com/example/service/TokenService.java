package com.example.service;


import com.example.utils.ResultUtil;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "token",url = "http://127.0.0.1:9082")
//@FeignClient(name = "token",url = "http://10.124.0.47:30013")
@RequestMapping("/esun")
public interface TokenService {
    /**
     * 调用token服务
     * @param token
     * @return
     */
    @RequestMapping(value = "/checkToken",method = RequestMethod.GET)
    ResultUtil checkToken(@RequestParam("token") String token);
    @PostMapping(value = "/token")
    ResultUtil updateToken(@RequestParam("user") String user);
    @GetMapping(value = "/token")
    ResultUtil getToken(@RequestParam("user") String user);


}
