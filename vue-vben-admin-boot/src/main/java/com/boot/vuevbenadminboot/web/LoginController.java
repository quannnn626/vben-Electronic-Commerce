package com.boot.vuevbenadminboot.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class LoginController {
    @RequestMapping("/login")
    public String login() {
        return "login";
    }
}
