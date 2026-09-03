package com.jsp.ojpms.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.ojpms.entity.Job;
import com.jsp.ojpms.service.JobService;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // =====================================================
    // CREATE JOB
    // Recruiter identified from JWT
    // =====================================================

    @PostMapping
    public Job createJob(
            @RequestBody Job job,
            Authentication authentication) {

        String recruiterEmail = authentication.getName();

        return jobService.createJob(job, recruiterEmail);
    }

    // =====================================================
    // GET ALL JOBS
    // Public
    // =====================================================

    @GetMapping
    public List<Job> getAllJobs() {

        return jobService.getAllJobs();
    }

    // =====================================================
    // GET JOB BY ID
    // Public
    // =====================================================

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable int id) {

        return jobService.getJobById(id);
    }

    // =====================================================
    // SEARCH BY TITLE
    // Public
    // =====================================================

    @GetMapping("/search/title")
    public List<Job> searchByTitle(
            @RequestParam String title) {

        return jobService.searchByTitle(title);
    }

    // =====================================================
    // SEARCH BY LOCATION
    // Public
    // =====================================================

    @GetMapping("/search/location")
    public List<Job> searchByLocation(
            @RequestParam String location) {

        return jobService.searchByLocation(location);
    }

    // =====================================================
    // SEARCH BY JOB TYPE
    // Public
    // =====================================================

    @GetMapping("/search/type")
    public List<Job> searchByJobType(
            @RequestParam String jobType) {

        return jobService.searchByJobType(jobType);
    }

    // =====================================================
    // GET MY JOBS
    // Recruiter identified from JWT
    // =====================================================

    @GetMapping("/recruiter/my-jobs")
    public List<Job> getMyJobs(
            Authentication authentication) {

        String recruiterEmail = authentication.getName();

        return jobService.getJobsByRecruiter(recruiterEmail);
    }

    // =====================================================
    // DELETE JOB
    // Only owner recruiter
    // =====================================================

    @DeleteMapping("/{id}")
    public String deleteJob(
            @PathVariable int id,
            Authentication authentication) {

        String recruiterEmail = authentication.getName();

        jobService.deleteJob(id, recruiterEmail);

        return "Job Deleted Successfully";
    }

    // =====================================================
    // UPDATE JOB
    // Only owner recruiter
    // =====================================================

    @PutMapping("/{id}")
    public Job updateJob(
            @PathVariable int id,
            @RequestBody Job job,
            Authentication authentication) {

        String recruiterEmail = authentication.getName();

        return jobService.updateJob(
                id,
                job,
                recruiterEmail
        );
    }
}