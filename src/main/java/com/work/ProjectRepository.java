package com.work;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	@Query("SELECT p.assignedEmployee.id, COUNT(p) FROM Project p WHERE p.assignedEmployee IS NOT NULL GROUP BY p.assignedEmployee.id")
	List<Object[]> countProjectsPerEmployee();
	
	List<Project> findByAssignedEmployee(User employee);


}
