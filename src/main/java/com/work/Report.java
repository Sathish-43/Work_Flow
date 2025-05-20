package com.work;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String features;
    private String methodology;
    private String programming;
    @Column(name = "database_work") 
    private String database;
    private String workDescription;
   

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    // Constructors
    public Report() {}

    public Report(LocalDate date, String features, String methodology,
                  String programming, String database, Project project, User employee) {
        this.date = date;
        this.features = features;
        this.methodology = methodology;
        this.programming = programming;
        this.database = database;
        this.project = project;
        this.employee = employee;
    }
    
    

	public Long getId() {
		return id;
	}

	

	public String getWorkDescription() {
		return workDescription;
	}

	public void setWorkDescription(String workDescription) {
		this.workDescription = workDescription;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	public String getMethodology() {
		return methodology;
	}

	public void setMethodology(String methodology) {
		this.methodology = methodology;
	}

	public String getProgramming() {
		return programming;
	}

	public void setProgramming(String programming) {
		this.programming = programming;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public User getEmployee() {
		return employee;
	}

	public void setEmployee(User employee) {
		this.employee = employee;
	}

    // Getters and Setters
    // (Add all getter and setter methods here)
    
    

}
