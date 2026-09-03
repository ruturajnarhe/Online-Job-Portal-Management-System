import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import JobCard from "../components/JobCard";

function Jobs() {
  const navigate = useNavigate();

  const [jobs, setJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [locationFilter, setLocationFilter] = useState("");
  const [jobTypeFilter, setJobTypeFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("OPEN");

  /* =========================================
        LOAD JOBS
     ========================================= */
  useEffect(() => {
    loadJobs();
  }, []);

  const loadJobs = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/jobs");

      setJobs(response.data || []);

      await loadApplications();
    } catch (error) {
      console.error("Unable to load jobs:", error);
      setError("Unable to load jobs.");
    } finally {
      setLoading(false);
    }
  };

  /* =========================================
        LOAD CURRENT USER APPLICATIONS
     ========================================= */
  const loadApplications = async () => {
    const storedUser = localStorage.getItem("user");

    if (!storedUser) {
      setApplications([]);
      return;
    }

    try {
      const user = JSON.parse(storedUser);

      if (user.role !== "JOB_SEEKER") {
        setApplications([]);
        return;
      }

      /*
       * The backend identifies the logged-in applicant
       * from the JWT token.
       *
       * No applicant ID is required in the URL.
       */
      const response = await api.get("/applications/my-applications");

      setApplications(response.data || []);
    } catch (error) {
      console.error("Unable to load applications:", error);
      setApplications([]);
    }
  };

  /* =========================================
        CHECK APPLICATION
     ========================================= */
  const hasApplied = (jobId) => {
    return applications.some((application) => application.job?.id === jobId);
  };

  /* =========================================
        FILTER OPTIONS
     ========================================= */
  const locations = useMemo(() => {
    return [...new Set(jobs.map((job) => job.location).filter(Boolean))].sort();
  }, [jobs]);

  const jobTypes = useMemo(() => {
    return [...new Set(jobs.map((job) => job.jobType).filter(Boolean))].sort();
  }, [jobs]);

  /* =========================================
        FILTER JOBS
     ========================================= */
  const filteredJobs = useMemo(() => {
    const searchValue = search.trim().toLowerCase();

    return jobs.filter((job) => {
      const title = job.title?.toLowerCase() || "";
      const recruiter = job.recruiter?.name?.toLowerCase() || "";
      const location = job.location?.toLowerCase() || "";

      const matchesSearch =
        !searchValue ||
        title.includes(searchValue) ||
        recruiter.includes(searchValue) ||
        location.includes(searchValue);

      const matchesLocation =
        !locationFilter || job.location === locationFilter;

      const matchesJobType = !jobTypeFilter || job.jobType === jobTypeFilter;

      const matchesStatus =
        !statusFilter || job.status?.toUpperCase() === statusFilter;

      return (
        matchesSearch && matchesLocation && matchesJobType && matchesStatus
      );
    });
  }, [jobs, search, locationFilter, jobTypeFilter, statusFilter]);

  /* =========================================
        CLEAR FILTERS
     ========================================= */
  const clearFilters = () => {
    setSearch("");
    setLocationFilter("");
    setJobTypeFilter("");
    setStatusFilter("OPEN");
  };

  return (
    <div className="page-container">
      {/* =========================================
            PAGE HEADER
         ========================================= */}
      <div className="jobs-page-header">
        <div>
          <h1 className="page-title">Find Your Next Job</h1>

          <p className="jobs-page-subtitle">
            Explore job opportunities and find the right career for you.
          </p>
        </div>

        <div className="jobs-total-count">{filteredJobs.length} Jobs</div>
      </div>

      {/* =========================================
            SEARCH + FILTERS
         ========================================= */}
      <div className="jobs-filter-panel">
        {/* SEARCH */}
        <div className="jobs-search-wrapper">
          <span className="jobs-search-icon">🔍</span>

          <input
            type="text"
            className="jobs-search-input"
            placeholder="Search by job title, company or location..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        {/* FILTER ROW */}
        <div className="jobs-filter-row">
          {/* LOCATION */}
          <select
            className="jobs-filter-select"
            value={locationFilter}
            onChange={(e) => setLocationFilter(e.target.value)}
          >
            <option value="">All Locations</option>

            {locations.map((location) => (
              <option key={location} value={location}>
                {location}
              </option>
            ))}
          </select>

          {/* JOB TYPE */}
          <select
            className="jobs-filter-select"
            value={jobTypeFilter}
            onChange={(e) => setJobTypeFilter(e.target.value)}
          >
            <option value="">All Job Types</option>

            {jobTypes.map((jobType) => (
              <option key={jobType} value={jobType}>
                {jobType}
              </option>
            ))}
          </select>

          {/* STATUS */}
          <select
            className="jobs-filter-select"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="OPEN">Open Jobs</option>

            <option value="CLOSED">Closed Jobs</option>

            <option value="">All Status</option>
          </select>

          {/* CLEAR */}
          <button className="jobs-clear-button" onClick={clearFilters}>
            Clear Filters
          </button>
        </div>
      </div>

      {/* =========================================
            ERROR
         ========================================= */}
      {error && <div className="error-message">{error}</div>}

      {/* =========================================
            LOADING
         ========================================= */}
      {loading && (
        <div className="jobs-loading">
          <div className="jobs-loading-icon">⏳</div>

          <p>Loading jobs...</p>
        </div>
      )}

      {/* =========================================
            NO JOBS
         ========================================= */}
      {!loading && jobs.length === 0 && (
        <div className="jobs-empty-state">
          <div className="jobs-empty-icon">💼</div>

          <h2>No Jobs Available</h2>

          <p>There are currently no job postings available.</p>
        </div>
      )}

      {/* =========================================
            NO FILTER RESULTS
         ========================================= */}
      {!loading && jobs.length > 0 && filteredJobs.length === 0 && (
        <div className="jobs-empty-state">
          <div className="jobs-empty-icon">🔍</div>

          <h2>No Matching Jobs</h2>

          <p>Try changing your search or filters.</p>

          <button className="btn btn-secondary" onClick={clearFilters}>
            Clear Filters
          </button>
        </div>
      )}

      {/* =========================================
            JOB LIST
         ========================================= */}
      {!loading && filteredJobs.length > 0 && (
        <div className="jobs-list">
          {filteredJobs.map((job) => (
            <JobCard
              key={job.id}
              job={job}
              applied={hasApplied(job.id)}
              showApplyButton={true}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export default Jobs;
