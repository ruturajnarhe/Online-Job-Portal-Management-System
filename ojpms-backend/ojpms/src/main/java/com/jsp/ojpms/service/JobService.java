package com.jsp.ojpms.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.jsp.ojpms.entity.Job;
import com.jsp.ojpms.entity.JobApplication;
import com.jsp.ojpms.entity.User;
import com.jsp.ojpms.exception.InvalidJobException;
import com.jsp.ojpms.exception.JobNotFoundException;
import com.jsp.ojpms.exception.UserNotFoundException;
import com.jsp.ojpms.repository.JobApplicationRepository;
import com.jsp.ojpms.repository.JobRepository;
import com.jsp.ojpms.repository.UserRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public JobService(
            JobRepository jobRepository,
            UserRepository userRepository,
            JobApplicationRepository jobApplicationRepository) {

        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    // =====================================================
    // CREATE JOB
    // =====================================================

    public Job createJob(
            Job job,
            String recruiterEmail) {

        User recruiter =
                userRepository.findByEmail(recruiterEmail);

        if (recruiter == null) {

            throw new UserNotFoundException(
                    "Recruiter not found with email: "
                            + recruiterEmail
            );
        }

        // Verify role
        if (!"RECRUITER".equalsIgnoreCase(
                recruiter.getRole())) {

            throw new InvalidJobException(
                    "Only RECRUITER can create a job"
            );
        }

        LocalDate today = LocalDate.now();

        // End date required
        if (job.getEndDate() == null) {

            throw new InvalidJobException(
                    "End Date is Required"
            );
        }

        // End date must be future
        if (!job.getEndDate().isAfter(today)) {

            throw new InvalidJobException(
                    "End date must be after today's date"
            );
        }

        // IMPORTANT:
        // Never trust recruiter from request body
        job.setRecruiter(recruiter);

        // Automatically set start date
        job.setStartDate(today);

        // New job is OPEN
        job.setStatus("OPEN");

        return jobRepository.save(job);
    }

    // =====================================================
    // GET ALL JOBS
    // =====================================================

    public List<Job> getAllJobs() {

        return jobRepository.findAll();
    }

    // =====================================================
    // GET JOB BY ID
    // =====================================================

    public Job getJobById(int id) {

        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job Not Found with id : " + id
                        )
                );
    }

    // =====================================================
    // DELETE JOB
    // =====================================================

    public void deleteJob(
            int id,
            String recruiterEmail) {

        Job job =
                jobRepository.findById(id)
                        .orElseThrow(() ->
                                new JobNotFoundException(
                                        "Job not found with id : "
                                                + id
                                )
                        );

        // Ownership check
        validateJobOwnership(
                job,
                recruiterEmail
        );

        // Don't delete job with applications
        List<JobApplication> applications =
                jobApplicationRepository.findByJobId(id);

        if (!applications.isEmpty()) {

            throw new InvalidJobException(
                    "Cannot delete this job because applicants "
                            + "have already applied"
            );
        }

        jobRepository.delete(job);
    }

    // =====================================================
    // UPDATE JOB
    // =====================================================

    public Job updateJob(
            int id,
            Job job,
            String recruiterEmail) {

        Job existingJob =
                jobRepository.findById(id)
                        .orElseThrow(() ->
                                new JobNotFoundException(
                                        "Job Not Found with id : "
                                                + id
                                )
                        );

        // Ownership check
        validateJobOwnership(
                existingJob,
                recruiterEmail
        );

        LocalDate today = LocalDate.now();

        if (job.getEndDate() == null) {

            throw new InvalidJobException(
                    "End Date is Required"
            );
        }

        if (!job.getEndDate().isAfter(today)) {

            throw new InvalidJobException(
                    "End date must be after today's date"
            );
        }

        // Update editable fields
        existingJob.setTitle(
                job.getTitle()
        );

        existingJob.setDescription(
                job.getDescription()
        );

        existingJob.setLocation(
                job.getLocation()
        );

        existingJob.setSalary(
                job.getSalary()
        );

        existingJob.setExperience(
                job.getExperience()
        );

        existingJob.setJobType(
                job.getJobType()
        );

        existingJob.setEndDate(
                job.getEndDate()
        );

        /*
         * Do NOT update:
         *
         * recruiter
         * startDate
         * status
         *
         * from request body.
         */

        return jobRepository.save(existingJob);
    }

    // =====================================================
    // SEARCH BY TITLE
    // =====================================================

    public List<Job> searchByTitle(String title) {

        return jobRepository
                .findByTitleContainingIgnoreCase(title);
    }

    // =====================================================
    // SEARCH BY LOCATION
    // =====================================================

    public List<Job> searchByLocation(String location) {

        return jobRepository
                .findByLocationIgnoreCase(location);
    }

    // =====================================================
    // SEARCH BY JOB TYPE
    // =====================================================

    public List<Job> searchByJobType(String jobType) {

        return jobRepository
                .findByJobTypeIgnoreCase(jobType);
    }

    // =====================================================
    // GET MY JOBS
    // =====================================================

    public List<Job> getJobsByRecruiter(
            String recruiterEmail) {

        User recruiter =
                userRepository.findByEmail(
                        recruiterEmail
                );

        if (recruiter == null) {

            throw new UserNotFoundException(
                    "Recruiter not found with email: "
                            + recruiterEmail
            );
        }

        if (!"RECRUITER".equalsIgnoreCase(
                recruiter.getRole())) {

            throw new InvalidJobException(
                    "Only RECRUITER can access recruiter jobs"
            );
        }

        return jobRepository.findByRecruiterId(
                recruiter.getId()
        );
    }

    // =====================================================
    // JOB OWNERSHIP CHECK
    // =====================================================

    private void validateJobOwnership(
            Job job,
            String recruiterEmail) {

        if (job.getRecruiter() == null) {

            throw new InvalidJobException(
                    "This job does not have a recruiter"
            );
        }

        String jobRecruiterEmail =
                job.getRecruiter().getEmail();

        if (!recruiterEmail.equalsIgnoreCase(
                jobRecruiterEmail)) {

            throw new AccessDeniedException(
                    "You are not authorized to modify this job"
            );
        }
    }
}