import { useNavigate } from "react-router-dom";

function JobCard({ job, applied = false, showApplyButton = true }) {
  const navigate = useNavigate();

  const isOpen = job.status?.toUpperCase() === "OPEN";

  const getStatusClass = () => {
    return isOpen ? "status status-open" : "status status-closed";
  };

  const handleViewDetails = () => {
    navigate(`/job/${job.id}`);
  };

  const handleApply = () => {
    navigate(`/job/${job.id}`);
  };

  return (
    <div className="job-list-card">
      {/* =========================
          LEFT SIDE
      ========================= */}
      <div className="job-list-main">
        <div className="job-list-title-row">
          <h2>{job.title}</h2>

          <span className={getStatusClass()}>{job.status || "UNKNOWN"}</span>
        </div>

        <p className="job-list-company">
          🏢 {job.recruiter?.name || "Company / Recruiter"}
        </p>

        {/* DETAILS */}
        <div className="job-list-details">
          <span>📍 {job.location || "Not specified"}</span>

          <span>💰 {job.salary || "Not specified"}</span>

          <span>💼 {job.experience || "Not specified"}</span>

          <span>🏢 {job.jobType || "Not specified"}</span>
        </div>

        {/* DESCRIPTION */}
        <p className="job-list-description">
          {job.description || "No description available."}
        </p>

        {/* END DATE */}
        <div className="job-list-footer">
          <span>
            ⏳ Apply by: <strong>{job.endDate || "Not specified"}</strong>
          </span>
        </div>
      </div>

      {/* =========================
          RIGHT SIDE
      ========================= */}
      <div className="job-list-actions">
        {/* APPLICATION STATUS */}
        {applied && <div className="job-applied-badge">✓ Applied</div>}

        {/* VIEW DETAILS */}
        <button className="btn btn-primary" onClick={handleViewDetails}>
          View Details
        </button>

        {/* APPLY */}
        {showApplyButton && (
          <>
            {applied ? (
              <button className="btn btn-secondary" disabled>
                Already Applied
              </button>
            ) : !isOpen ? (
              <button className="btn btn-danger" disabled>
                Job Closed
              </button>
            ) : (
              <button className="btn btn-success" onClick={handleApply}>
                Apply Now
              </button>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default JobCard;
