package com.jsp.ojpms.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jsp.ojpms.entity.Job;
import com.jsp.ojpms.repository.JobRepository;

@Component
public class JobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    private final JobRepository jobRepository;

    public JobScheduler(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    // Run every day at 12:00 AM
    @Scheduled(cron = "0 0 0 * * *", zone = "${app.scheduler.time-zone:Asia/Kolkata}")
    public void closeExpiredJobs() {

        LocalDate today = LocalDate.now();

        List<Job> jobs = jobRepository.findAll();

        for (Job job : jobs) {

            if ("OPEN".equalsIgnoreCase(job.getStatus())
                    && job.getEndDate() != null
                    && job.getEndDate().isBefore(today)) {

                job.setStatus("CLOSED");

                jobRepository.save(job);

                logger.info("Job ID {} automatically changed to CLOSED", job.getId());
            }
        }
    }

    // Run once when the application starts
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {

        logger.info("Application started. Checking expired jobs...");

        closeExpiredJobs();
    }
}