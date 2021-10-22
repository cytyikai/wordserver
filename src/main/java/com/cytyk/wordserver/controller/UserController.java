package com.cytyk.wordserver.controller;

import com.alibaba.fastjson.JSON;
import com.cytyk.wordserver.controller.vo.UserVO;
import com.cytyk.wordserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author yuankai
 * @Description
 * @date 2020/10/28 9:34
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Resource
    private UserService userService;

    @PostMapping("/add")
    public Integer add(@RequestBody UserVO userVO) {
        log.info("function: add, params: {}", JSON.toJSONString(userVO));
        
        return userService.add(userVO);
    }

    @PostMapping("/update")
    public Integer update(@RequestBody UserVO userVO) {
        log.info("function: update, params: {}", JSON.toJSONString(userVO));

        return userService.update(userVO);
    }

    @GetMapping("/{id}")
    public UserVO get(@PathVariable Integer id) {
        return userService.get(id);
    }
}
