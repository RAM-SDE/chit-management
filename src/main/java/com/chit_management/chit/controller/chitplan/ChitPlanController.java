package com.chit_management.chit.controller.chitplan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/chit-plans")
@RequiredArgsConstructor
public class ChitPlanController {

    @GetMapping
    public String listPage() {
        return "chitplan/view-chitplan";
    }

    @GetMapping("/new")
    public String newPage(Model model) {
        model.addAttribute("id", null);
        return "chitplan/save-chitplan";
    }

    @GetMapping("/edit/{uuid}")
    public String editPage(
            @PathVariable String uuid,
            Model model) {

        model.addAttribute("uuid", uuid);

        return "chitplan/save-chitplan";
    }

    @GetMapping("/view/{uuid}")
    public String viewPage(@PathVariable String uuid, Model model) {

        model.addAttribute("uuid", uuid);

        return "chitplan/show-chitplan";
    }
}
