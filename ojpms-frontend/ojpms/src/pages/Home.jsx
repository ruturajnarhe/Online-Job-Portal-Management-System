import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function Home() {
  const navigate = useNavigate();

  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");

  // =========================================
  // LOAD JOBS
  // =========================================

  useEffect(() => {
    loadJobs();
  }, []);

  const loadJobs = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/jobs");

      setJobs(response.data || []);
    } catch (error) {
      console.error("Unable to load jobs:", error);
      setError("Unable to load jobs at the moment.");
    } finally {
      setLoading(false);
    }
  };

  // =========================================
  // SEARCH
  // =========================================

  const handleSearch = () => {
    if (search.trim()) {
      navigate(`/jobs?search=${encodeURIComponent(search.trim())}`);
    } else {
      navigate("/jobs");
    }
  };

  const handleSearchKeyDown = (e) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  // =========================================
  // JOB DETAILS
  // =========================================

  const viewJob = (id) => {
    navigate(`/job/${id}`);
  };

  // =========================================
  // USER
  // =========================================

  const storedUser = localStorage.getItem("user");

  let user = null;

  try {
    user = storedUser ? JSON.parse(storedUser) : null;
  } catch {
    user = null;
  }

  const goToJobs = () => {
    navigate("/jobs");
  };

  const goToDashboard = () => {
    if (user?.role === "RECRUITER") {
      navigate("/recruiter-dashboard");
    } else if (user?.role === "JOB_SEEKER") {
      navigate("/jobs");
    } else {
      navigate("/login");
    }
  };

  // Show only first 6 jobs on Home page
  const latestJobs = jobs.slice(0, 6);

  return (
    <div className="home-page">
      {/* =========================================
          HERO SECTION
      ========================================= */}

      <section className="home-hero">
        <div className="home-container">
          <div className="home-hero-content">
            <span className="home-hero-label">
              ONLINE JOB PORTAL MANAGEMENT SYSTEM
            </span>

            <h1>
              Find the right job.
              <br />
              Build your future.
            </h1>

            <p>
              OJPMS connects job seekers with career opportunities and helps
              recruiters manage job postings and applications from one simple
              platform.
            </p>

            {/* Search */}

            <div className="home-search-box">
              <div className="home-search-input-wrapper">
                <span>🔍</span>

                <input
                  type="text"
                  placeholder="Search jobs by title..."
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                  onKeyDown={handleSearchKeyDown}
                />
              </div>

              <button className="home-search-button" onClick={handleSearch}>
                Search Jobs
              </button>
            </div>

            {/* CTA */}

            <div className="home-hero-actions">
              <button className="btn btn-primary" onClick={goToJobs}>
                Browse Jobs
              </button>

              {!user && (
                <button
                  className="home-outline-button"
                  onClick={() => navigate("/register")}
                >
                  Create Account
                </button>
              )}

              {user && (
                <button className="home-outline-button" onClick={goToDashboard}>
                  Go to Dashboard
                </button>
              )}
            </div>
          </div>

          {/* Hero information card */}

          <div className="home-hero-card">
            <div className="home-hero-card-header">
              <span className="home-hero-card-icon">💼</span>

              <div>
                <strong>OJPMS</strong>
                <span>Your career platform</span>
              </div>
            </div>

            <div className="home-hero-card-divider"></div>

            <div className="home-hero-feature">
              <span>✓</span>
              <div>
                <strong>Discover Opportunities</strong>
                <p>Explore available jobs based on your interests.</p>
              </div>
            </div>

            <div className="home-hero-feature">
              <span>✓</span>
              <div>
                <strong>Apply Easily</strong>
                <p>Submit applications through a simple process.</p>
              </div>
            </div>

            <div className="home-hero-feature">
              <span>✓</span>
              <div>
                <strong>Track Applications</strong>
                <p>Keep track of your application progress.</p>
              </div>
            </div>

            <div className="home-hero-feature">
              <span>✓</span>
              <div>
                <strong>Recruiter Management</strong>
                <p>Recruiters can manage jobs and applicants.</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* =========================================
          WHY OJPMS
      ========================================= */}

      <section className="home-section">
        <div className="home-container">
          <div className="home-section-heading">
            <span>WHY OJPMS</span>

            <h2>Everything you need for a simpler job search experience</h2>

            <p>
              OJPMS provides a straightforward platform for both job seekers and
              recruiters.
            </p>
          </div>

          <div className="home-benefits-grid">
            <div className="home-benefit-card">
              <div className="home-benefit-icon">🔎</div>

              <h3>Find Jobs Easily</h3>

              <p>
                Search and explore available job opportunities using job title,
                location and job type.
              </p>
            </div>

            <div className="home-benefit-card">
              <div className="home-benefit-icon">📝</div>

              <h3>Simple Applications</h3>

              <p>
                Apply for suitable jobs through a simple and organized
                application workflow.
              </p>
            </div>

            <div className="home-benefit-card">
              <div className="home-benefit-icon">📋</div>

              <h3>Track Applications</h3>

              <p>
                View your submitted applications and monitor their current
                status.
              </p>
            </div>

            <div className="home-benefit-card">
              <div className="home-benefit-icon">👥</div>

              <h3>Recruiter Management</h3>

              <p>
                Recruiters can create jobs, manage postings and review
                applications from candidates.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* =========================================
          LATEST JOBS
      ========================================= */}

      <section className="home-section home-jobs-section">
        <div className="home-container">
          <div className="home-section-heading home-jobs-heading">
            <div>
              <span>JOB OPPORTUNITIES</span>

              <h2>Latest Jobs</h2>

              <p>Explore the latest opportunities available on OJPMS.</p>
            </div>

            <button className="home-view-all-button" onClick={goToJobs}>
              View All Jobs →
            </button>
          </div>

          {loading && (
            <div className="home-loading">Loading latest jobs...</div>
          )}

          {!loading && error && (
            <div className="home-error">
              <p>{error}</p>

              <button className="btn btn-secondary" onClick={loadJobs}>
                Try Again
              </button>
            </div>
          )}

          {!loading && !error && latestJobs.length === 0 && (
            <div className="home-empty-jobs">
              <div>💼</div>

              <h3>No Jobs Available</h3>

              <p>There are currently no job opportunities available.</p>
            </div>
          )}

          {!loading && !error && latestJobs.length > 0 && (
            <div className="home-jobs-grid">
              {latestJobs.map((job) => (
                <div className="home-job-card" key={job.id}>
                  <div className="home-job-card-top">
                    <div className="home-job-icon">💼</div>

                    <span className="home-job-type">
                      {job.jobType || "Job"}
                    </span>
                  </div>

                  <h3>{job.title || "Job Opportunity"}</h3>

                  <p className="home-job-company">
                    {job.company || job.recruiter?.company || "Company"}
                  </p>

                  <div className="home-job-details">
                    <span>📍 {job.location || "Location not specified"}</span>

                    {job.experience && <span>🎓 {job.experience}</span>}

                    {job.salary && <span>💰 {job.salary}</span>}
                  </div>

                  {job.description && (
                    <p className="home-job-description">{job.description}</p>
                  )}

                  <button
                    className="home-job-details-button"
                    onClick={() => viewJob(job.id)}
                  >
                    View Job Details →
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      </section>

      {/* =========================================
          HOW IT WORKS
      ========================================= */}

      <section className="home-section">
        <div className="home-container">
          <div className="home-section-heading">
            <span>HOW IT WORKS</span>

            <h2>Start your job journey in four steps</h2>
          </div>

          <div className="home-steps">
            <div className="home-step">
              <div className="home-step-number">01</div>

              <h3>Create Account</h3>

              <p>
                Register as a job seeker or recruiter based on your
                requirements.
              </p>
            </div>

            <div className="home-step">
              <div className="home-step-number">02</div>

              <h3>Find Opportunities</h3>

              <p>
                Browse available jobs and view detailed information about each
                opportunity.
              </p>
            </div>

            <div className="home-step">
              <div className="home-step-number">03</div>

              <h3>Apply</h3>

              <p>
                Submit your application for jobs that match your skills and
                interests.
              </p>
            </div>

            <div className="home-step">
              <div className="home-step-number">04</div>

              <h3>Track Progress</h3>

              <p>Check your applications and follow their current status.</p>
            </div>
          </div>
        </div>
      </section>

      {/* =========================================
          FOR JOB SEEKERS / RECRUITERS
      ========================================= */}

      <section className="home-section home-audience-section">
        <div className="home-container">
          <div className="home-audience-grid">
            {/* Job Seeker */}

            <div className="home-audience-card">
              <div className="home-audience-icon">👨‍💻</div>

              <span>FOR JOB SEEKERS</span>

              <h2>Find opportunities that match your career goals</h2>

              <p>
                OJPMS helps job seekers discover jobs, view complete job
                information, submit applications and track their progress.
              </p>

              <button
                className="btn btn-primary"
                onClick={() => navigate("/register")}
              >
                Register as Job Seeker
              </button>
            </div>

            {/* Recruiter */}

            <div className="home-audience-card">
              <div className="home-audience-icon">🏢</div>

              <span>FOR RECRUITERS</span>

              <h2>Manage your recruitment process efficiently</h2>

              <p>
                Recruiters can create job postings, manage their jobs and review
                applications submitted by candidates.
              </p>

              <button
                className="btn btn-primary"
                onClick={() => navigate("/register")}
              >
                Register as Recruiter
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* =========================================
          ABOUT OJPMS
      ========================================= */}

      <section className="home-section home-about-section">
        <div className="home-container">
          <div className="home-about">
            <div className="home-about-label">ABOUT OJPMS</div>

            <h2>A simple platform connecting candidates and recruiters</h2>

            <p>
              The Online Job Portal Management System (OJPMS) is designed to
              simplify the interaction between job seekers and recruiters.
            </p>

            <p>
              Job seekers can explore available opportunities, view job details,
              apply for suitable positions and track their applications.
              Recruiters can create and manage job postings and review
              applications received from candidates.
            </p>

            <p>
              The goal of OJPMS is to provide an organized, easy-to-use platform
              that brings the complete job application workflow into one place.
            </p>
          </div>
        </div>
      </section>

      {/* =========================================
          FINAL CTA
      ========================================= */}

      <section className="home-cta">
        <div className="home-container">
          <div className="home-cta-content">
            <h2>Ready to find your next opportunity?</h2>

            <p>
              Explore available jobs and take the next step toward your career.
            </p>

            <div className="home-cta-actions">
              <button className="btn btn-primary" onClick={goToJobs}>
                Explore Jobs
              </button>

              {!user && (
                <button
                  className="home-cta-outline"
                  onClick={() => navigate("/register")}
                >
                  Create Account
                </button>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* =========================================
          FOOTER
      ========================================= */}

      <footer className="home-footer">
        <div className="home-container">
          <div className="home-footer-content">
            <div>
              <strong className="home-footer-logo">OJPMS</strong>

              <p>Online Job Portal Management System</p>
            </div>

            <div className="home-footer-links">
              <button onClick={goToJobs}>Jobs</button>

              {!user && (
                <>
                  <button onClick={() => navigate("/login")}>Login</button>

                  <button onClick={() => navigate("/register")}>
                    Register
                  </button>
                </>
              )}
            </div>
          </div>

          <div className="home-footer-bottom">
            © {new Date().getFullYear()} OJPMS. All rights reserved.
          </div>
        </div>
      </footer>
    </div>
  );
}

export default Home;
