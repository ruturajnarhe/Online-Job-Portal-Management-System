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

import com.jsp.ojpms.entity.JobApplication;
import com.jsp.ojpms.service.JobApplicationService;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(
            JobApplicationService jobApplicationService) {

        this.jobApplicationService = jobApplicationService;
    }

    // =====================================================
    // JOB SEEKER
    // Apply for a job
    // =====================================================
    //
    // Applicant is identified using JWT.
    // Frontend does NOT need to send applicant ID.
    //
    @PostMapping
    public JobApplication applyForJob(
            @RequestBody JobApplication application,
            Authentication authentication) {

        String applicantEmail = authentication.getName();

        return jobApplicationService.applyForJob(
                application,
                applicantEmail
        );
    }

    // =====================================================
    // AUTHENTICATED USERS
    // Get all applications
    // =====================================================
    //
    // Recruiter can see all applications.
    // Job seeker cannot access all applications.
    //
    @GetMapping
    public List<JobApplication> getAllApplications(
            Authentication authentication) {

        String email = authentication.getName();

        return jobApplicationService.getAllApplications(email);
    }

    // =====================================================
    // AUTHENTICATED USER
    // Get application by ID
    // =====================================================
    //
    // Applicant can view their own application.
    // Recruiter can view applications for their own jobs.
    //
    @GetMapping("/{id}")
    public JobApplication getApplicationById(
            @PathVariable int id,
            Authentication authentication) {

        String email = authentication.getName();

        return jobApplicationService.getApplicationById(
                id,
                email
        );
    }

    // =====================================================
    // AUTHENTICATED USER
    // Delete application
    // =====================================================
    //
    // Applicant can delete their own application.
    // Recruiter cannot delete someone else's application.
    //
    @DeleteMapping("/{id}")
    public String deleteApplication(
            @PathVariable int id,
            Authentication authentication) {

        String email = authentication.getName();

        jobApplicationService.deleteApplication(
                id,
                email
        );

        return "Job Application Deleted Successfully";
    }

    // =====================================================
    // RECRUITER
    // Get applications for a specific job
    // =====================================================
    //
    // Only the recruiter who owns the job can see
    // applications for that job.
    //
    @GetMapping("/job/{jobId}")
    public List<JobApplication> getApplicationsByJob(
            @PathVariable int jobId,
            Authentication authentication) {

        String recruiterEmail = authentication.getName();

        return jobApplicationService.getApplicationsByJob(
                jobId,
                recruiterEmail
        );
    }

    // =====================================================
    // JOB SEEKER
    // Get MY applications
    // =====================================================
    //
    // Applicant ID is NOT taken from URL.
    // JWT identifies the applicant.
    //
    @GetMapping("/my-applications")
    public List<JobApplication> getMyApplications(
            Authentication authentication) {

        String applicantEmail = authentication.getName();

        return jobApplicationService.getApplicationsByApplicant(
                applicantEmail
        );
    }

    // =====================================================
    // RECRUITER
    // Get MY JOB applications
    // =====================================================
    //
    // Recruiter ID is NOT taken from URL.
    // JWT identifies the recruiter.
    //
    @GetMapping("/my-recruiter-applications")
    public List<JobApplication> getMyRecruiterApplications(
            Authentication authentication) {

        String recruiterEmail = authentication.getName();

        return jobApplicationService.getApplicationsByRecruiter(
                recruiterEmail
        );
    }

    // =====================================================
    // RECRUITER
    // Update application status
    // =====================================================
    //
    // Only the recruiter who owns the job can update
    // the application status.
    //
    @PutMapping("/{applicationId}/status")
    public JobApplication updateApplicationStatus(
            @PathVariable int applicationId,
            @RequestParam String status,
            Authentication authentication) {

        String recruiterEmail = authentication.getName();

        return jobApplicationService.updateApplicationStatus(
                applicationId,
                status,
                recruiterEmail
        );
    }
}