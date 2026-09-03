import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function RecruiterAllApplications() {
  const navigate = useNavigate();

  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  // =========================================
  // CHECK LOGIN + LOAD APPLICATIONS
  // =========================================

  useEffect(() => {
    const storedUser = localStorage.getItem("user");

    if (!storedUser) {
      navigate("/login");
      return;
    }

    try {
      const user = JSON.parse(storedUser);

      if (user.role !== "RECRUITER") {
        navigate("/jobs");
        return;
      }

      loadApplications();
    } catch (error) {
      console.error("Invalid user data:", error);

      localStorage.removeItem("user");
      navigate("/login");
    }
  }, [navigate]);

  // =========================================
  // LOAD ALL RECRUITER APPLICATIONS
  // =========================================

  const loadApplications = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/applications/my-recruiter-applications");

      setApplications(response.data || []);
    } catch (error) {
      console.error("Unable to load applications:", error);

      setError(error.response?.data?.message || "Unable to load applications.");
    } finally {
      setLoading(false);
    }
  };

  // =========================================
  // FILTER APPLICATIONS
  // =========================================

  const filteredApplications = useMemo(() => {
    const searchValue = search.trim().toLowerCase();

    return applications.filter((application) => {
      const applicantName = application.applicant?.name?.toLowerCase() || "";

      const applicantEmail = application.applicant?.email?.toLowerCase() || "";

      const jobTitle = application.job?.title?.toLowerCase() || "";

      const location = application.job?.location?.toLowerCase() || "";

      const status = application.status?.toUpperCase() || "";

      const matchesSearch =
        !searchValue ||
        applicantName.includes(searchValue) ||
        applicantEmail.includes(searchValue) ||
        jobTitle.includes(searchValue) ||
        location.includes(searchValue);

      const matchesStatus = !statusFilter || status === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [applications, search, statusFilter]);

  // =========================================
  // STATUS CLASS
  // =========================================

  const getStatusClass = (status) => {
    const value = status?.toUpperCase();

    if (value === "SHORTLISTED") {
      return "recruiter-all-status status-shortlisted";
    }

    if (value === "REJECTED") {
      return "recruiter-all-status status-rejected";
    }

    if (value === "SELECTED") {
      return "recruiter-all-status status-selected";
    }

    return "recruiter-all-status status-applied";
  };

  // =========================================
  // FORMAT DATE
  // =========================================

  const formatDate = (date) => {
    if (!date) {
      return "Not available";
    }

    try {
      return new Date(date).toLocaleDateString();
    } catch {
      return date;
    }
  };

  // =========================================
  // CLEAR FILTERS
  // =========================================

  const clearFilters = () => {
    setSearch("");
    setStatusFilter("");
  };

  // =========================================
  // COUNTS
  // =========================================

  const totalApplications = applications.length;

  const shortlistedApplications = applications.filter(
    (application) => application.status?.toUpperCase() === "SHORTLISTED",
  ).length;

  const selectedApplications = applications.filter(
    (application) => application.status?.toUpperCase() === "SELECTED",
  ).length;

  const rejectedApplications = applications.filter(
    (application) => application.status?.toUpperCase() === "REJECTED",
  ).length;

  // =========================================
  // UI
  // =========================================

  return (
    <div className="page-container recruiter-all-applications-page">
      {/* =========================================
          HEADER
      ========================================= */}

      <div className="recruiter-all-applications-header">
        <div>
          <h1 className="page-title">Applications</h1>

          <p className="recruiter-all-applications-subtitle">
            View and manage applications received for your job postings.
          </p>
        </div>

        <button
          className="btn btn-secondary"
          onClick={() => navigate("/recruiter-dashboard")}
        >
          ← Dashboard
        </button>
      </div>

      {/* =========================================
          STATISTICS
      ========================================= */}

      {!loading && !error && (
        <div className="recruiter-all-application-stats">
          <div className="recruiter-all-stat-card">
            <div className="recruiter-all-stat-icon">📋</div>

            <div>
              <span>Total Applications</span>
              <strong>{totalApplications}</strong>
            </div>
          </div>

          <div className="recruiter-all-stat-card">
            <div className="recruiter-all-stat-icon">⭐</div>

            <div>
              <span>Shortlisted</span>
              <strong>{shortlistedApplications}</strong>
            </div>
          </div>

          <div className="recruiter-all-stat-card">
            <div className="recruiter-all-stat-icon">✅</div>

            <div>
              <span>Selected</span>
              <strong>{selectedApplications}</strong>
            </div>
          </div>

          <div className="recruiter-all-stat-card">
            <div className="recruiter-all-stat-icon">❌</div>

            <div>
              <span>Rejected</span>
              <strong>{rejectedApplications}</strong>
            </div>
          </div>
        </div>
      )}

      {/* =========================================
          SEARCH + FILTER
      ========================================= */}

      <div className="recruiter-all-applications-filter">
        <div className="recruiter-all-search">
          <span>🔍</span>

          <input
            type="text"
            placeholder="Search applicant, email, job or location..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="recruiter-all-status-filter"
        >
          <option value="">All Status</option>
          <option value="APPLIED">Applied</option>
          <option value="SHORTLISTED">Shortlisted</option>
          <option value="SELECTED">Selected</option>
          <option value="REJECTED">Rejected</option>
        </select>

        <button className="btn btn-secondary" onClick={clearFilters}>
          Clear
        </button>
      </div>

      {/* =========================================
          ERROR
      ========================================= */}

      {error && <div className="error-message">{error}</div>}

      {/* =========================================
          LOADING
      ========================================= */}

      {loading && (
        <div className="recruiter-all-empty-card">
          <p>Loading applications...</p>
        </div>
      )}

      {/* =========================================
          EMPTY
      ========================================= */}

      {!loading && !error && applications.length === 0 && (
        <div className="recruiter-all-empty-card">
          <div className="recruiter-all-empty-icon">📋</div>

          <h3>No Applications Yet</h3>

          <p>
            You haven't received any applications for your job postings yet.
          </p>

          <button
            className="btn btn-primary"
            onClick={() => navigate("/recruiter-dashboard")}
          >
            View My Jobs
          </button>
        </div>
      )}

      {/* =========================================
          NO FILTER RESULTS
      ========================================= */}

      {!loading &&
        !error &&
        applications.length > 0 &&
        filteredApplications.length === 0 && (
          <div className="recruiter-all-empty-card">
            <div className="recruiter-all-empty-icon">🔍</div>

            <h3>No Matching Applications</h3>

            <p>Try changing your search or status filter.</p>

            <button className="btn btn-secondary" onClick={clearFilters}>
              Clear Filters
            </button>
          </div>
        )}

      {/* =========================================
          APPLICATION LIST
      ========================================= */}

      {!loading && !error && filteredApplications.length > 0 && (
        <div className="recruiter-all-applications-section">
          <div className="recruiter-all-section-header">
            <div>
              <h2>All Applications</h2>

              <p>
                Showing {filteredApplications.length} of {applications.length}{" "}
                applications
              </p>
            </div>

            <span className="recruiter-all-count-badge">
              {filteredApplications.length}
            </span>
          </div>

          <div className="recruiter-all-applications-list">
            {filteredApplications.map((application) => {
              const applicantName =
                application.applicant?.name || "Unknown Applicant";

              const applicantEmail =
                application.applicant?.email || "Email not available";

              const jobTitle = application.job?.title || "Job not available";

              const location =
                application.job?.location || "Location not specified";

              const status = application.status || "APPLIED";

              return (
                <div
                  className="recruiter-all-application-card"
                  key={application.id}
                >
                  {/* Applicant */}
                  <div className="recruiter-all-applicant">
                    <div className="recruiter-all-avatar">
                      {applicantName.charAt(0).toUpperCase()}
                    </div>

                    <div className="recruiter-all-applicant-info">
                      <h3>{applicantName}</h3>

                      <p>{applicantEmail}</p>
                    </div>
                  </div>

                  {/* Job information */}
                  <div className="recruiter-all-job-info">
                    <div>
                      <span>Job</span>
                      <strong>{jobTitle}</strong>
                    </div>

                    <div>
                      <span>Location</span>
                      <strong>{location}</strong>
                    </div>

                    <div>
                      <span>Applied On</span>
                      <strong>
                        {formatDate(
                          application.appliedDate ||
                            application.createdAt ||
                            application.applicationDate,
                        )}
                      </strong>
                    </div>
                  </div>

                  {/* Status */}
                  <div className="recruiter-all-status-column">
                    <span className="recruiter-all-status-label">Status</span>

                    <span className={getStatusClass(status)}>{status}</span>
                  </div>

                  {/* Action */}
                  <div className="recruiter-all-action">
                    <button
                      className="btn btn-primary"
                      onClick={() =>
                        navigate(
                          `/recruiter/applications/${application.job?.id}`,
                        )
                      }
                    >
                      View Details
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}

export default RecruiterAllApplications;
