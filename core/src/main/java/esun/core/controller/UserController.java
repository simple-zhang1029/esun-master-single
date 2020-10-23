package esun.core.controller;

import esun.core.annotation.LoginRequire;
import esun.core.annotation.Router;

import esun.core.service.UserService;
import esun.core.utils.ResultUtil;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.MessageBuilder;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    UserService userService;
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;


    /**
     * 获取用户信息
     * @param name
     * @return
     */
    @LoginRequire
    @GetMapping("info")
    @Router(name = "user:info")
    public ResultUtil info(@RequestParam("name")String name){
        return  userService.userInfo(name);
    }

    /**
     * 保存头像路径
     * @param path
     * @param name
     * @return
     */
    @LoginRequire
    @PostMapping("saveImg")
    public ResultUtil saveImg(@RequestParam("path")String path,@RequestParam("name")String name){
        return userService.saveImg(path,name);
    }

    /**
     * 更新用户信息
     * @param name
     * @param email
     * @param telephone
     * @return
     */
    @LoginRequire
    @PostMapping("update")
    public ResultUtil update(@RequestParam("name")String name,
                             @RequestParam("email")String email,
                             @RequestParam("telephone")String telephone)
    {
        return userService.updateInfo(name,email,telephone);
    }

//    @PostMapping("test")
//    public void test(){
//        try {
//            rabbitTemplate.setExchange("esun.exchange");
//            rabbitTemplate.setRoutingKey("esun.router.key");
//            Message message= MessageBuilder.withBody("test".getBytes()).setHeader("product","postgres_test").build();
//            rabbitTemplate.sendAndReceive(message);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }
}
