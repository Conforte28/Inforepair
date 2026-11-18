package bj.inforepair.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/acces-refuse")
    public String accesRefuse() {
        return "acces-refuse";
    }
}
