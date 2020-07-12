package com.sun.manager.controller.login;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zcm
 */
@RestController
public class LoginController {

    @PostMapping("/login")
    public String login(String username,String password){
        System.out.println(username + "---" + password);
        return "success";
    }

}
