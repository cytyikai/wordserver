package com.cytyk.wordserver.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/wordserver/role")
@RestController
public class RoleController{


  @GetMapping("/get")
  public String get(String userId) {
    return "admin";
  }

}
