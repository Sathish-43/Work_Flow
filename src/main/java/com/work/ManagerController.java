package com.work;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ManagerController {

//    @Autowired
//    private ProjectRepository projectRepository;
//
//    @GetMapping("/manager")
//    public String managerDashboard(Model model) {
//        List<String> domains = Arrays.asList("Full Stack Development", "Data Science", "AI and ML", "Networking", "Embedded", "Cloud");
//        model.addAttribute("domains", domains);
//        model.addAttribute("project", new Project());
//        return "manager";
//    }
//
//    @PostMapping("/submitProject")
//    public String submitProject(@ModelAttribute Project project, Model model) {
//        projectRepository.save(project);
//        model.addAttribute("message", "Project submitted successfully!");
//        return "manager";
//    }
}
