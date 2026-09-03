import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Navbar() {
  const navigate = useNavigate();

  const [sidebarOpen, setSidebarOpen] = useState(false);

  const storedUser = localStorage.getItem("user");

  let user = null;

  try {
    user = storedUser ? JSON.parse(storedUser) : null;
  } catch {
    user = null;
  }

  // =========================================
  // LOGOUT
  // =========================================

  const logout = () => {
    localStorage.removeItem("user");

    setSidebarOpen(false);

    navigate("/login");
  };

  // =========================================
  // HOME / LOGO
  // =========================================

  const goHome = () => {
    if (!user) {
      navigate("/");
      return;
    }

    if (user.role === "RECRUITER") {
      navigate("/recruiter-dashboard");
    } else {
      navigate("/jobs");
    }
  };

  // =========================================
  // MOBILE NAVIGATION
  // =========================================

  const navigateMobile = (path) => {
    navigate(path);
    setSidebarOpen(false);
  };

  return (
    <header className="top-navbar">
      {/* =================================
          MOBILE MENU BUTTON
      ================================= */}

      {user && (
        <button
          className="menu-button"
          onClick={() => setSidebarOpen(true)}
          aria-label="Open menu"
        >
          ☰
        </button>
      )}

      {/* =================================
          LOGO
      ================================= */}

      <div className="top-navbar-logo" onClick={goHome}>
        OJPMS
      </div>

      {/* =================================
          DESKTOP RIGHT SIDE
      ================================= */}

      <div className="top-navbar-right">
        {user && (
          <div className="top-navbar-user">
            <div className="top-user-avatar">
              {user.name ? user.name.charAt(0).toUpperCase() : "U"}
            </div>

            <div className="top-user-info">
              <strong>{user.name || "User"}</strong>

              <span>
                {user.role === "RECRUITER" ? "Recruiter" : "Job Seeker"}
              </span>
            </div>
          </div>
        )}

        {user && (
          <button className="top-logout" onClick={logout}>
            Logout
          </button>
        )}
      </div>

      {/* =================================
          MOBILE SIDEBAR
      ================================= */}

      {user && (
        <>
          {sidebarOpen && (
            <div
              className="mobile-sidebar-overlay"
              onClick={() => setSidebarOpen(false)}
            />
          )}

          <div
            className={`mobile-sidebar ${
              sidebarOpen ? "mobile-sidebar-open" : ""
            }`}
          >
            {/* Header */}

            <div className="mobile-sidebar-header">
              <strong>OJPMS</strong>

              <button
                onClick={() => setSidebarOpen(false)}
                aria-label="Close menu"
              >
                ×
              </button>
            </div>

            {/* User */}

            <div className="mobile-sidebar-user">
              <div className="mobile-sidebar-avatar">
                {user.name ? user.name.charAt(0).toUpperCase() : "U"}
              </div>

              <div>
                <strong>{user.name || "User"}</strong>

                <span>
                  {user.role === "RECRUITER" ? "Recruiter" : "Job Seeker"}
                </span>
              </div>
            </div>

            {/* Menu title */}

            <div className="mobile-sidebar-title">MENU</div>

            {/* =================================
                JOB SEEKER
            ================================= */}

            {user.role === "JOB_SEEKER" && (
              <>
                <button onClick={() => navigateMobile("/jobs")}>
                  <span>💼</span>
                  <span>Browse Jobs</span>
                </button>

                <button onClick={() => navigateMobile("/my-applications")}>
                  <span>📋</span>
                  <span>My Applications</span>
                </button>
              </>
            )}

            {/* =================================
                RECRUITER
            ================================= */}

            {user.role === "RECRUITER" && (
              <>
                <button onClick={() => navigateMobile("/recruiter-dashboard")}>
                  <span>📊</span>
                  <span>Dashboard</span>
                </button>

                <button onClick={() => navigateMobile("/create-job")}>
                  <span>➕</span>
                  <span>Create Job</span>
                </button>

                <button
                  onClick={() => navigateMobile("/recruiter/all-applications")}
                >
                  <span>📋</span>
                  <span>Applications</span>
                </button>
              </>
            )}

            {/* =================================
                MOBILE LOGOUT
            ================================= */}

            <div className="mobile-sidebar-footer">
              <button className="mobile-sidebar-logout" onClick={logout}>
                <span>🚪</span>
                <span>Logout</span>
              </button>
            </div>
          </div>
        </>
      )}
    </header>
  );
}

export default Navbar;
