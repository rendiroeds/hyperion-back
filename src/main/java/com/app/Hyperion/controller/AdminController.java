package com.app.Hyperion.controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/home")
    public String home() {
        return "Bienvenido al panel de administración";
    }
}
