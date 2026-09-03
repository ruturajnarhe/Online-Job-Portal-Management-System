import { useLocation, useNavigate } from "react-router-dom";

function Sidebar({ isOpen, closeSidebar }) {
  const navigate = useNavigate();
  const location = useLocation();
  const storedUser = localStorage.getItem("user");
  const user = storedUser ? JSON.parse(storedUser) : null;

  if (!user) {
    return null;
  }

  // =========================================
  // NAVIGATION
  // =========================================

  const navigateTo = (path) => {
    navigate(path);

    if (closeSidebar) {
      closeSidebar();
    }
  };

  // =========================================
  // ACTIVE MENU
  // =========================================

  const isActive = (path) => {
    if (path === "/recruiter-dashboard") {
      return location.pathname === path;
    }

    return location.pathname.startsWith(path);
  };

  // =========================================
  // LOGOUT
  // =========================================

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/login");

    if (closeSidebar) {
      closeSidebar();
    }
  };

  return (
    <>
      {/* =========================================
          MOBILE OVERLAY
      ========================================= */}

      {isOpen && <div className="sidebar-overlay" onClick={closeSidebar}></div>}

      <aside className={`sidebar ${isOpen ? "sidebar-open" : ""}`}>
        {/* =========================================
            USER INFORMATION
        ========================================= */}

        <div className="sidebar-user">
          <div className="sidebar-avatar">
            {user.name ? user.name.charAt(0).toUpperCase() : "U"}
          </div>

          <div className="sidebar-user-info">
            <strong>{user.name || "User"}</strong>

            <span>
              {user.role === "RECRUITER" ? "Recruiter" : "Job Seeker"}
            </span>
          </div>
        </div>

        {/* =========================================
            MENU TITLE
        ========================================= */}

        <div className="sidebar-section-title">MENU</div>

        {/* =========================================
            NAVIGATION
        ========================================= */}

        <nav className="sidebar-menu">
          {/* =========================================
              JOB SEEKER
          ========================================= */}

          {user.role === "JOB_SEEKER" && (
            <>
              <button
                className={`sidebar-link ${
                  isActive("/jobs") ? "sidebar-link-active" : ""
                }`}
                onClick={() => navigateTo("/jobs")}
              >
                <span className="sidebar-icon">💼</span>

                <span>Browse Jobs</span>
              </button>

              <button
                className={`sidebar-link ${
                  isActive("/my-applications") ? "sidebar-link-active" : ""
                }`}
                onClick={() => navigateTo("/my-applications")}
              >
                <span className="sidebar-icon">📋</span>

                <span>My Applications</span>
              </button>
            </>
          )}

          {/* =========================================
              RECRUITER
          ========================================= */}

          {user.role === "RECRUITER" && (
            <>
              {/* Dashboard */}

              <button
                className={`sidebar-link ${
                  isActive("/recruiter-dashboard") ? "sidebar-link-active" : ""
                }`}
                onClick={() => navigateTo("/recruiter-dashboard")}
              >
                <span className="sidebar-icon">📊</span>

                <span>Dashboard</span>
              </button>

              {/* Create Job */}

              <button
                className={`sidebar-link ${
                  isActive("/create-job") ? "sidebar-link-active" : ""
                }`}
                onClick={() => navigateTo("/create-job")}
              >
                <span className="sidebar-icon">➕</span>

                <span>Create Job</span>
              </button>

              {/* My Jobs */}

              <button
                className={`sidebar-link ${
                  location.pathname === "/recruiter-dashboard"
                    ? "sidebar-link-active"
                    : ""
                }`}
                onClick={() => navigateTo("/recruiter-dashboard")}
              >
                <span className="sidebar-icon">💼</span>

                <span>My Jobs</span>
              </button>

              {/* All Applications */}

              <button
                className={`sidebar-link ${
                  isActive("/recruiter/all-applications")
                    ? "sidebar-link-active"
                    : ""
                }`}
                onClick={() => navigateTo("/recruiter/all-applications")}
              >
                <span className="sidebar-icon">📋</span>

                <span>Applications</span>
              </button>
            </>
          )}
        </nav>

        {/* =========================================
            SIDEBAR FOOTER
        ========================================= */}

        <div className="sidebar-footer">
          <button
            className="sidebar-link sidebar-logout"
            onClick={handleLogout}
          >
            <span className="sidebar-icon">🚪</span>

            <span>Logout</span>
          </button>
        </div>
      </aside>
    </>
  );
}

export default Sidebar;
