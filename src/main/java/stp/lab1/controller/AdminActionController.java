package stp.lab1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import stp.lab1.service.AdminActionService;

@Controller
@RequestMapping("/admin/actions")
@RequiredArgsConstructor
public class AdminActionController {

    private final AdminActionService adminActionService;

    @GetMapping
    @PreAuthorize("hasAuthority('Admin')")
    public String listActions(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("actions", adminActionService.getFilteredActions(search));
        model.addAttribute("search", search);
        return "admin/actions";
    }
}