package com.work;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
	
	
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
    private ProjectRepository projectRepository;
	
	@Autowired
	private DomainRepository domainRepository;
	
	@Autowired
	private ReportRepository reportRepository;

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
                    	//List<String> domains = Arrays.asList("Full Stack Development", "Data Science", "AI and ML", "Networking", "Embedded", "Cloud");
                    	List<Domain> domains = domainRepository.findAll();
                        model.addAttribute("domains", domains);
                        model.addAttribute("project", new Project());
                    	
                    	
                    	return "manager";
                    case "PM Team": 
                    	List<Project> allProjects = projectRepository.findAll(); // Assuming JPA Repository
                        model.addAttribute("projects", allProjects);
                    	
                    	return "pmteam";
                    case "Employee":
                    
                    	List<Project> assignedProjects = projectRepository.findByAssignedEmployee(user);

                        // Add this code here:
                        for (Project project : assignedProjects) {
                            int count = reportRepository.countByProjectIdAndEmployeeIdAndDate(
                                            project.getId(), user.getId(), LocalDate.now());
                            project.setReportCountToday(count);
                        }

                        model.addAttribute("assignedProjects", assignedProjects);
                        return "employee";

                    	
                }
            }
        }
        model.addAttribute("error", "Invalid credentials or designation");
        return "login";
    }
    
    

//    @GetMapping("/manager")
//    public String managerDashboard(Model model) {
////        List<String> domains = Arrays.asList("Full Stack Development", "Data Science", "AI and ML", "Networking", "Embedded", "Cloud");
////        model.addAttribute("domains", domains);
////        model.addAttribute("project", new Project());
//        return "manager";
//    }
    
    @GetMapping("/manager_success")
    public String mangeSuccess() {
    	return "manager_success";
    }

    @PostMapping("/submitProject")
    public String submitProject(@ModelAttribute Project project, RedirectAttributes redirectAttributes) {
        projectRepository.save(project);
        redirectAttributes.addFlashAttribute("message", "Project submitted successfully!");
        return "redirect:/manager_success";
    }

    @GetMapping("/pmteam")
    public String viewProjectsForPMTeam(Model model) {
//        List<Project> allProjects = projectRepository.findAll(); // Assuming JPA Repository
//        model.addAttribute("projects", allProjects);
        return "pmteam";
    }
    
    @GetMapping("/allocate")
    public String showAllocationPage(@RequestParam Long projectId, Model model) {
        List<Object[]> counts = projectRepository.countProjectsPerEmployee();
        List<User> employees = userRepository.findByDesignationIgnoreCase("Employee");

        // Mapping employee ID to count
        Map<Long, Long> projectCountMap = new HashMap<>();
        for (Object[] row : counts) {
            Long userId = (Long) row[0];
            Long count = (Long) row[1];
            projectCountMap.put(userId, count);
        }

        model.addAttribute("employees", employees);
        model.addAttribute("projectId", projectId);
        model.addAttribute("projectCountMap", projectCountMap);

        return "allocate"; // thymeleaf page
    }
    
    @GetMapping("/project_pm_upload_success")
    public String project_pm_upload() {
    	return "project_pm_upload_success";
    }

    @PostMapping("/allocateProject")
    public String allocateProject(@RequestParam Long projectId, @RequestParam Long employeeId, RedirectAttributes redirectAttributes) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<User> employeeOpt = userRepository.findById(employeeId);

        if (projectOpt.isPresent() && employeeOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setAssignedEmployee(employeeOpt.get());
            projectRepository.save(project);

            redirectAttributes.addFlashAttribute("message", "Project successfully allocated!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid allocation data!");
        }

        return "redirect:/project_pm_upload_success";
    }
    
    @PostMapping("/submitReport")
    public String submitReport(@ModelAttribute ReportForm form, Principal principal, RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userRepository.findByEmail(principal.getName());
        Optional<Project> projectOpt = projectRepository.findById(form.getProjectId());
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to submit a report.");
            return "redirect:/login";
        }

        if (userOpt.isPresent() && projectOpt.isPresent()) {
            User user = userOpt.get();
            Project project = projectOpt.get();

            // Check how many reports the employee submitted today for this project
            List<Report> todaysReports = reportRepository.findByProjectIdAndEmployeeIdAndDate(
                project.getId(), user.getId(), LocalDate.now()
            );

            if (todaysReports.size() >= 2) {
                redirectAttributes.addFlashAttribute("error", "You can only submit 2 reports per day per project.");
                return "redirect:/employee";
            }

            Report report = new Report();
            report.setWorkDescription(form.getWorkDescription());
            report.setDate(LocalDate.now());
            report.setProject(project);
            report.setEmployee(user);
            reportRepository.save(report);

            redirectAttributes.addFlashAttribute("message", "Report submitted successfully.");
            return "redirect:/employee";
        }

        redirectAttributes.addFlashAttribute("error", "Invalid report submission.");
        return "redirect:/employee";
    }
    @PostMapping("/markComplete")
    public String markComplete(@RequestParam Long projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            
            projectRepository.save(project);
        }
        return "redirect:/employee";
    }




    
    

}
