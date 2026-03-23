package stp.lab1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import stp.lab1.model.entity.User;
import stp.lab1.model.enums.UserRole;
import stp.lab1.service.UserService;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, @RequestParam String rawPassword) {
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(UserRole.User);
        user.setCreatedAt(LocalDateTime.now());
        
        userService.save(user);
        
        return "redirect:/login?registered";
    }
}