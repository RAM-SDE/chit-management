package com.chit_management.chit.controller.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    @GetMapping
    public String listPage() {
        return "customer/view-customer"; // ← just render the page
    }

    @GetMapping("/new")
    public String newPage(Model model) {
        model.addAttribute("uuid", null);
        return "customer/save-customer";
    }

    @GetMapping("/edit/{uuid}")
    public String editPage(@PathVariable String uuid, Model model)  {
        model.addAttribute("uuid", uuid);
        return "customer/save-customer";
    }

    @GetMapping("/view/{uuid}")
    public String viewPage(@PathVariable String uuid, Model model) {
        model.addAttribute("uuid", uuid);
        return "customer/show-customer";
    }

}