package com.jsp.ojpms.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.jsp.ojpms.entity.Job;
import com.jsp.ojpms.entity.JobApplication;
import com.jsp.ojpms.entity.User;
import com.jsp.ojpms.exception.InvalidApplicationException;
import com.jsp.ojpms.exception.JobApplicationNotFoundException;
import com.jsp.ojpms.exception.JobNotFoundException;
import com.jsp.ojpms.exception.UserNotFoundException;
import com.jsp.ojpms.repository.JobApplicationRepository;
import com.jsp.ojpms.repository.JobRepository;
import com.jsp.ojpms.repository.UserRepository;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobApplicationService(
            JobApplicationRepository jobApplicationRepository,
            JobRepository jobRepository,
            UserRepository userRepository) {

        this.jobApplicationRepository = jobApplicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // =====================================================
    // APPLY FOR JOB
    // =====================================================
    //
    // Applicant is identified using JWT email.
    // We DO NOT trust applicant ID from request body.
    //
    public JobApplication applyForJob(
            JobApplication application,
            String applicantEmail) {

        if (application == null
                || application.getJob() == null) {

            throw new InvalidApplicationException(
                    "Job details are required"
            );
        }

        // Get Job ID from request body
        int jobId = application.getJob().getId();

        // Find logged-in applicant using JWT email
        User applicant = userRepository.findByEmail(applicantEmail);

        if (applicant == null) {

            throw new UserNotFoundException(
                    "User not found with email: " + applicantEmail
            );
        }

        // Only JOB_SEEKER can apply
        if (!"JOB_SEEKER".equalsIgnoreCase(
                applicant.getRole())) {

            throw new InvalidApplicationException(
                    "Only JOB_SEEKER can apply for a job"
            );
        }

        // Find Job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job not found with id : " + jobId
                        )
                );

        // Job must be OPEN
        if (!"OPEN".equalsIgnoreCase(
                job.getStatus())) {

            throw new InvalidApplicationException(
                    "Cannot apply for a CLOSED job"
            );
        }

        // Prevent duplicate application
        boolean alreadyApplied =
                jobApplicationRepository
                        .existsByJobIdAndApplicantId(
                                jobId,
                                applicant.getId()
                        );

        if (alreadyApplied) {

            throw new InvalidApplicationException(
                    "You have already applied for this job"
            );
        }

        // Associate actual database Job
        application.setJob(job);

        // Associate actual logged-in User
        application.setApplicant(applicant);

        // Automatically set application date
        application.setAppliedDate(
                LocalDate.now().toString()
        );

        // Automatically set initial status
        application.setStatus("APPLIED");

        return jobApplicationRepository.save(application);
    }

    // =====================================================
    // GET ALL APPLICATIONS
    // =====================================================
    //
    // Only RECRUITER can access all applications.
    //
    public List<JobApplication> getAllApplications(
            String email) {

        User user = getUserByEmail(email);

        if (!"RECRUITER".equalsIgnoreCase(
                user.getRole())) {

            throw new InvalidApplicationException(
                    "Only RECRUITER can access all applications"
            );
        }

        return jobApplicationRepository.findAll();
    }

    // =====================================================
    // GET APPLICATION BY ID
    // =====================================================
    //
    // Applicant can access their own application.
    //
    // Recruiter can access an application only when
    // the application belongs to one of their jobs.
    //
    public JobApplication getApplicationById(
            int id,
            String email) {

        JobApplication application =
                findApplication(id);

        User user = getUserByEmail(email);

        // Applicant owns this application
        if (application.getApplicant() != null
                && application.getApplicant()
                        .getEmail()
                        .equalsIgnoreCase(email)) {

            return application;
        }

        // Recruiter owns the job
        if ("RECRUITER".equalsIgnoreCase(
                user.getRole())
                && application.getJob() != null
                && application.getJob().getRecruiter() != null
                && application.getJob().getRecruiter()
                        .getEmail()
                        .equalsIgnoreCase(email)) {

            return application;
        }

        throw new AccessDeniedException(
                "You are not authorized to view this application"
        );
    }

    // =====================================================
    // DELETE APPLICATION
    // =====================================================
    //
    // Only the applicant who created the application
    // can delete it.
    //
    public void deleteApplication(
            int id,
            String applicantEmail) {

        JobApplication application =
                findApplication(id);

        if (application.getApplicant() == null
                || !application.getApplicant()
                        .getEmail()
                        .equalsIgnoreCase(applicantEmail)) {

            throw new AccessDeniedException(
                    "You are not authorized to delete this application"
            );
        }

        jobApplicationRepository.delete(application);
    }

    // =====================================================
    // GET APPLICATIONS BY JOB
    // =====================================================
    //
    // Only the recruiter who owns the job can access
    // its applications.
    //
    public List<JobApplication> getApplicationsByJob(
            int jobId,
            String recruiterEmail) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job not found with id : " + jobId
                        )
                );

        // Check job ownership
        validateRecruiterOwnership(
                job,
                recruiterEmail
        );

        return jobApplicationRepository.findByJobId(
                jobId
        );
    }

    // =====================================================
    // GET MY APPLICATIONS
    // =====================================================
    //
    // JWT identifies the applicant.
    //
    public List<JobApplication> getApplicationsByApplicant(
            String applicantEmail) {

        User applicant = getUserByEmail(
                applicantEmail
        );

        // Only JOB_SEEKER can access their applications
        if (!"JOB_SEEKER".equalsIgnoreCase(
                applicant.getRole())) {

            throw new InvalidApplicationException(
                    "Only JOB_SEEKER can access applicant applications"
            );
        }

        return jobApplicationRepository
                .findByApplicantId(
                        applicant.getId()
                );
    }

    // =====================================================
    // GET MY RECRUITER APPLICATIONS
    // =====================================================
    //
    // JWT identifies the recruiter.
    //
    public List<JobApplication> getApplicationsByRecruiter(
            String recruiterEmail) {

        User recruiter = getUserByEmail(
                recruiterEmail
        );

        // Only RECRUITER can access recruiter applications
        if (!"RECRUITER".equalsIgnoreCase(
                recruiter.getRole())) {

            throw new InvalidApplicationException(
                    "Only RECRUITER can access recruiter applications"
            );
        }

        return jobApplicationRepository
                .findByJobRecruiterId(
                        recruiter.getId()
                );
    }

    // =====================================================
    // UPDATE APPLICATION STATUS
    // =====================================================
    //
    // Only the recruiter who owns the job can update
    // the application status.
    //
    public JobApplication updateApplicationStatus(
            int applicationId,
            String status,
            String recruiterEmail) {

        JobApplication application =
                findApplication(applicationId);

        if (application.getJob() == null) {

            throw new InvalidApplicationException(
                    "Application is not associated with a job"
            );
        }

        // Verify recruiter owns the job
        validateRecruiterOwnership(
                application.getJob(),
                recruiterEmail
        );

        // Validate status
        if (status == null
                || (
                    !status.equalsIgnoreCase("APPLIED")
                    && !status.equalsIgnoreCase("SHORTLISTED")
                    && !status.equalsIgnoreCase("REJECTED")
                    && !status.equalsIgnoreCase("SELECTED")
                )) {

            throw new InvalidApplicationException(
                    "Invalid application status. "
                    + "Allowed values: APPLIED, SHORTLISTED, "
                    + "REJECTED, SELECTED"
            );
        }

        application.setStatus(
                status.toUpperCase()
        );

        return jobApplicationRepository.save(
                application
        );
    }

    // =====================================================
    // FIND APPLICATION
    // =====================================================

    private JobApplication findApplication(int id) {

        return jobApplicationRepository
                .findById(id)
                .orElseThrow(() ->
                        new JobApplicationNotFoundException(
                                "Application not found with id : "
                                        + id
                        )
                );
    }

    // =====================================================
    // GET USER BY EMAIL
    // =====================================================

    private User getUserByEmail(String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {

            throw new UserNotFoundException(
                    "User not found with email: " + email
            );
        }

        return user;
    }

    // =====================================================
    // RECRUITER OWNERSHIP VALIDATION
    // =====================================================

    private void validateRecruiterOwnership(
            Job job,
            String recruiterEmail) {

        if (job.getRecruiter() == null) {

            throw new InvalidApplicationException(
                    "This job does not have a recruiter"
            );
        }

        User recruiter = getUserByEmail(
                recruiterEmail
        );

        if (!"RECRUITER".equalsIgnoreCase(
                recruiter.getRole())) {

            throw new InvalidApplicationException(
                    "Only RECRUITER can perform this operation"
            );
        }

        String jobRecruiterEmail =
                job.getRecruiter().getEmail();

        if (!recruiterEmail.equalsIgnoreCase(
                jobRecruiterEmail)) {

            throw new AccessDeniedException(
                    "You are not authorized to access this job's applications"
            );
        }
    }
}