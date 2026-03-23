package stp.lab1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import stp.lab1.model.enums.UserRole;
import stp.lab1.service.UserService;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/{id}")
    public String profile(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.findById(id));
        model.addAttribute("history", userService.getBorrowHistory(id));
        model.addAttribute("roles", UserRole.values());
        return "users/profile";
    }

    @PostMapping("/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam UserRole role) {
        userService.changeRole(id, role);
        return "redirect:/users/" + id;
    }
}
