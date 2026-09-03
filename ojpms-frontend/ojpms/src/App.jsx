import { useState } from "react";
import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  useLocation,
} from "react-router-dom";

import Navbar from "./components/Navbar";
import Sidebar from "./components/Sidebar";

import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Jobs from "./pages/Jobs";
import JobDetails from "./pages/JobDetails";
import MyApplications from "./pages/MyApplications";

import RecruiterDashboard from "./pages/RecruiterDashboard";
import CreateJob from "./pages/CreateJob";
import EditJob from "./pages/EditJob";
import RecruiterApplications from "./pages/RecruiterApplications";
import RecruiterAllApplications from "./pages/RecruiterAllApplications";

import "./App.css";

function AppLayout() {
  const location = useLocation();

  const [sidebarOpen, setSidebarOpen] = useState(false);

  // =========================================
  // GET LOGGED-IN USER
  // =========================================

  const storedUser = localStorage.getItem("user");

  let user = null;

  try {
    user = storedUser ? JSON.parse(storedUser) : null;
  } catch (error) {
    console.error("Invalid user data in localStorage");
    user = null;
  }

  // =========================================
  // AUTH PAGES
  // =========================================

  const isAuthPage =
    location.pathname === "/login" || location.pathname === "/register";

  // =========================================
  // HOME PAGE
  // =========================================

  const isHomePage = location.pathname === "/";

  // =========================================
  // AUTH PAGES
  // =========================================

  if (isAuthPage) {
    return (
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route path="/register" element={<Register />} />
      </Routes>
    );
  }

  // =========================================
  // HOME PAGE
  // =========================================
  // Home is public, so Navbar/Sidebar are not
  // required here.
  // =========================================

  if (isHomePage) {
    return (
      <Routes>
        <Route path="/" element={<Home />} />
      </Routes>
    );
  }

  // =========================================
  // APPLICATION LAYOUT
  // =========================================

  return (
    <div className="app-layout">
      {/* Navbar */}

      <Navbar />

      {/* Sidebar */}

      {user && (
        <Sidebar
          isOpen={sidebarOpen}
          closeSidebar={() => setSidebarOpen(false)}
        />
      )}

      {/* Main Content */}

      <main className={user ? "main-content" : "main-content-full"}>
        <Routes>
          {/* =====================================
              JOB SEEKER ROUTES
          ===================================== */}

          <Route path="/jobs" element={<Jobs />} />

          <Route path="/job/:id" element={<JobDetails />} />

          <Route path="/my-applications" element={<MyApplications />} />

          {/* =====================================
              RECRUITER ROUTES
          ===================================== */}

          <Route path="/recruiter-dashboard" element={<RecruiterDashboard />} />

          <Route path="/create-job" element={<CreateJob />} />

          <Route path="/edit-job/:id" element={<EditJob />} />

          <Route
            path="/recruiter/applications/:jobId"
            element={<RecruiterApplications />}
          />

          <Route
            path="/recruiter/all-applications"
            element={<RecruiterAllApplications />}
          />

          {/* =====================================
              UNKNOWN ROUTES
          ===================================== */}

          <Route
            path="*"
            element={
              <Navigate
                to={
                  user?.role === "RECRUITER"
                    ? "/recruiter-dashboard"
                    : user?.role === "JOB_SEEKER"
                      ? "/jobs"
                      : "/"
                }
                replace
              />
            }
          />
        </Routes>
      </main>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AppLayout />
    </BrowserRouter>
  );
}

export default App;
