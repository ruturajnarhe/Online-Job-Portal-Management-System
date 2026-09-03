package com.jsp.ojpms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "job_applications")
@AllArgsConstructor
@NoArgsConstructor
public class JobApplication {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String status;
	private String appliedDate;
	
	@ManyToOne
	@JoinColumn(name = "job_id")
	private Job job;
	
	@ManyToOne
	@JoinColumn(name = "applicant_id")
	private User applicant;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAppliedDate() {
		return appliedDate;
	}

	public void setAppliedDate(String appliedDate) {
		this.appliedDate = appliedDate;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public User getApplicant() {
		return applicant;
	}

	public void setApplicant(User applicant) {
		this.applicant = applicant;
	}

	@Override
	public String toString() {
		return "JobApplication [id=" + id + ", status=" + status + ", appliedDate=" + appliedDate + ", job=" + job
				+ ", applicant=" + applicant + "]";
	}
	
}
