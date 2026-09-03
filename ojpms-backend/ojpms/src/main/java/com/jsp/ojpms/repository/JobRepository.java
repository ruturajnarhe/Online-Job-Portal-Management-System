package com.jsp.ojpms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.ojpms.entity.Job;

public interface JobRepository extends JpaRepository<Job, Integer> {

	
	List<Job> findByTitleContainingIgnoreCase(String title);
	
	List<Job> findByLocationIgnoreCase(String location);
	
	List<Job> findByJobTypeIgnoreCase(String jobType);
	
	List<Job> findByRecruiterId(int recruiterId);
	
}
