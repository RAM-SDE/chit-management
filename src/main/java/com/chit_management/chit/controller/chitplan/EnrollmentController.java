package com.chit_management.chit.controller.chitplan;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/enrollments")
public class EnrollmentController {

    @GetMapping
    public String listPage() {
        return "enrollment/view-enrollment";
    }
}
