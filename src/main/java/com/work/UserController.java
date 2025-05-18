package com.work;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
	
	
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String submitForm(@ModelAttribute("user") User user, Model model) {
        boolean emailExists = userRepository.findByEmail(user.getEmail()).isPresent();
        boolean phoneExists = userRepository.findByPhone(user.getPhone()).isPresent();

        if (emailExists || phoneExists) {
            model.addAttribute("error", "Email or Phone number already exists.");
            return "register";
        }

        userRepository.save(user);
        
     // ✅ Send email
        emailService.sendRegistrationEmail(user.getEmail(), user.getName(), user.getDesignation());

        model.addAttribute("name", user.getName());
        model.addAttribute("designation", user.getDesignation());
        return "success";
    }
	
	@GetMapping("/home")
	public String Home() {
		return "index";
	}
	
	@GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam String designation,
                        Model model) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(password) && user.getDesignation().equalsIgnoreCase(designation)) {
                model.addAttribute("name", user.getName());
                switch (designation) {
                    case "Manager" :
                    	List<String> domains = Arrays.asList("Full Stack Development", "Data Science", "AI and ML", "Networking", "Embedded", "Cloud");
                        model.addAttribute("domains", domains);
                        model.addAttribute("project", new Project());
                    	
                    	
                    	return "manager";
                    case "PM Team": return "pmteam";
                    case "Employee": return "employee";
                }
            }
        }
        model.addAttribute("error", "Invalid credentials or designation");
        return "login";
    }
    
    

    @GetMapping("/manager")
    public String managerDashboard(Model model) {
//        List<String> domains = Arrays.asList("Full Stack Development", "Data Science", "AI and ML", "Networking", "Embedded", "Cloud");
//        model.addAttribute("domains", domains);
//        model.addAttribute("project", new Project());
        return "manager";
    }

    @PostMapping("/submitProject")
    public String submitProject(@ModelAttribute Project project, Model model) {
        projectRepository.save(project);
        model.addAttribute("message", "Project submitted successfully!");
        return "manager";
    }

}
