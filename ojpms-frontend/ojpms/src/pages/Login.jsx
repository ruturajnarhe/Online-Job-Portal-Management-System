import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api";

function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    setMessage("");
    setLoading(true);

    try {
      const response = await api.post("/users/login", null, {
        params: {
          email: email,
          password: password,
        },
      });

      // Backend returns:
      // {
      //   token: "...",
      //   user: {
      //     id: ...,
      //     name: "...",
      //     email: "...",
      //     role: "..."
      //   }
      // }
      const { token, user } = response.data;

      console.log("Logged in user:", user);

      // ==========================================
      // CHECK LOGIN RESULT
      // ==========================================
      if (!token || !user) {
        setMessage("Invalid email or password");
        return;
      }

      // ==========================================
      // STORE LOGGED-IN USER + JWT TOKEN
      // ==========================================
      // Keep user information and token together so
      // api.js can automatically attach the JWT
      // to future backend requests.
      const loggedInUser = {
        ...user,
        token: token,
      };

      localStorage.setItem("user", JSON.stringify(loggedInUser));

      setMessage("Login successful!");

      // ==========================================
      // REDIRECT BASED ON ROLE
      // ==========================================
      if (user.role === "RECRUITER") {
        navigate("/recruiter-dashboard");
      } else if (user.role === "JOB_SEEKER") {
        navigate("/jobs");
      } else {
        // Unknown role
        setMessage("Invalid user role");
        localStorage.removeItem("user");
      }
    } catch (error) {
      console.error("Login error:", error);

      setMessage(error.response?.data?.message || "Invalid email or password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-container">
        {/* Logo */}
        <div className="auth-logo">OJPMS</div>

        {/* Heading */}
        <h1>Welcome Back</h1>
        <p className="auth-subtitle">Login to your Online Job Portal account</p>

        {/* Message */}
        {message && (
          <div
            className={
              message === "Login successful!"
                ? "success-message"
                : "error-message"
            }
          >
            {message}
          </div>
        )}

        {/* Login Form */}
        <form onSubmit={handleSubmit}>
          {/* Email */}
          <div className="form-group">
            <label className="form-label">Email Address</label>

            <input
              className="form-input"
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          {/* Password */}
          <div className="form-group">
            <label className="form-label">Password</label>

            <div className="password-wrapper">
              <input
                className="form-input"
                type={showPassword ? "text" : "password"}
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />

              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? "Hide" : "Show"}
              </button>
            </div>
          </div>

          {/* Login Button */}
          <button
            type="submit"
            className="btn btn-primary auth-submit"
            disabled={loading}
          >
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        {/* Register Link */}
        <div className="auth-footer">
          <span>Don't have an account?</span>
          <Link to="/register">Create an account</Link>
        </div>
      </div>
    </div>
  );
}

export default Login;
