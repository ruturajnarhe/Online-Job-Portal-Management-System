# 🌐 Online Job Portal Management System (OJPMS)

An online job portal web application that connects **Job Seekers** and **Recruiters** through a secure, role-based platform.

The system allows Job Seekers to browse and apply for jobs, while Recruiters can create and manage job postings and review and manage applications.

The application follows a **React.js frontend + Spring Boot REST API + PostgreSQL database** architecture and uses **JWT-based authentication and role-based authorization**.

---

## 📌 Table of Contents

* [Project Overview](#-project-overview)
* [Key Features](#-key-features)
* [User Roles](#-user-roles)
* [Technology Stack](#-technology-stack)
* [System Architecture](#-system-architecture)
* [Application Flow](#-application-flow)
* [Authentication and Security](#-authentication-and-security)
* [Modules](#-modules)
* [API Overview](#-api-overview)
* [Database](#-database)
* [Frontend Structure](#-frontend-structure)
* [Backend Structure](#-backend-structure)
* [Project Setup](#-project-setup)
* [Environment Configuration](#-environment-configuration)
* [Running the Application](#-running-the-application)
* [Testing](#-testing)
* [Security Testing](#-security-testing)
* [Project Highlights](#-project-highlights)
* [Future Enhancements](#-future-enhancements)
* [Author](#-author)

---

# 📖 Project Overview

**OJPMS (Online Job Portal Management System)** is a full-stack web application designed to simplify the interaction between employers and job seekers.

The application provides two primary roles:

### 👨‍💼 Recruiter

Recruiters can:

* Register and log in
* Create job postings
* View their jobs
* Edit jobs
* Delete jobs
* View applications received for their jobs
* View all applications associated with their jobs
* Update application status
* Manage their recruitment workflow

### 👨‍💻 Job Seeker

Job Seekers can:

* Register and log in
* Browse available jobs
* Search for jobs
* View job details
* Apply for jobs
* View their submitted applications
* Track application status
* Logout securely

---

# ✨ Key Features

## 🔐 Authentication

* User registration
* User login
* JWT token-based authentication
* Secure password hashing using BCrypt
* Protected REST APIs
* Automatic JWT attachment to API requests
* Automatic handling of unauthorized responses

## 👥 Role-Based Authorization

The system supports two roles:

```text
RECRUITER
JOB_SEEKER
```

Each role has access only to the functionality relevant to that role.

For example:

```text
JOB_SEEKER
    ↓
Browse Jobs
Apply for Jobs
View My Applications
```

```text
RECRUITER
    ↓
Create Jobs
Manage Jobs
View Applications
Update Application Status
```

---

# 💼 Job Management

Recruiters can:

* Create jobs
* View jobs
* Edit jobs
* Delete jobs
* Manage their own job postings

Each job is associated with the recruiter who created it.

The backend derives the recruiter from the authenticated JWT rather than trusting a recruiter ID supplied by the frontend.

---

# 📝 Job Application Management

Job Seekers can apply to available jobs.

The system:

* Associates the application with the authenticated Job Seeker
* Associates the application with the selected job
* Prevents duplicate applications
* Prevents applications to unavailable/closed jobs
* Stores application information in PostgreSQL
* Allows Recruiters to manage application status

---

# 📊 Application Status

Recruiters can update application status.

For example:

```text
Application
    ↓
Applied
    ↓
Shortlisted
    ↓
Selected / Rejected
```

The updated status is stored in the database and is visible to the Job Seeker through **My Applications**.

---

# 👤 User Roles

## Job Seeker

| Functionality            | Access |
| ------------------------ | ------ |
| Register                 | ✅      |
| Login                    | ✅      |
| View Jobs                | ✅      |
| Search Jobs              | ✅      |
| View Job Details         | ✅      |
| Apply for Job            | ✅      |
| View My Applications     | ✅      |
| Track Application Status | ✅      |
| Create Job               | ❌      |
| Edit Job                 | ❌      |
| Delete Job               | ❌      |
| Manage Applications      | ❌      |

---

## Recruiter

| Functionality                           | Access |
| --------------------------------------- | ------ |
| Register                                | ✅      |
| Login                                   | ✅      |
| Recruiter Dashboard                     | ✅      |
| Create Job                              | ✅      |
| View Own Jobs                           | ✅      |
| Edit Own Job                            | ✅      |
| Delete Own Job                          | ✅      |
| View Applications                       | ✅      |
| View Job-Specific Applications          | ✅      |
| Update Application Status               | ✅      |
| Apply for Jobs                          | ❌      |
| View Job Seeker's Personal Applications | ❌      |

---

# 🛠 Technology Stack

## Frontend

* React.js
* Vite
* JavaScript
* HTML5
* CSS3
* Axios
* React Router

## Backend

* Java
* Spring Boot
* Spring Web
* Spring Security
* REST APIs
* JWT Authentication
* BCrypt Password Encoding

## Database

* PostgreSQL

## Development Tools

* Eclipse / IDE
* Visual Studio Code
* PostgreSQL
* Git
* GitHub
* Maven
* Google Chrome
* REST API testing tools

---

# 🏗 System Architecture

The application follows a three-layer architecture:

```text
┌─────────────────────────────┐
│       React Frontend        │
│                             │
│  Components / Pages         │
│  React Router               │
│  Axios                      │
└──────────────┬──────────────┘
               │
               │ HTTP / REST API
               │ JWT Bearer Token
               ↓
┌─────────────────────────────┐
│       Spring Boot           │
│                             │
│ Controllers                 │
│ Services                    │
│ Repositories                │
│ Security / JWT              │
└──────────────┬──────────────┘
               │
               │ JPA / Hibernate
               ↓
┌─────────────────────────────┐
│         PostgreSQL          │
│                             │
│ Users                       │
│ Jobs                        │
│ Applications                │
└─────────────────────────────┘
```

---

# 🔄 Application Flow

## Registration Flow

```text
User
 ↓
Registration Page
 ↓
React
 ↓
POST /api/users
 ↓
Spring Boot
 ↓
Validate User
 ↓
Hash Password
 ↓
PostgreSQL
 ↓
Registration Success
```

---

# 🔑 Login Flow

```text
User
 ↓
Login Page
 ↓
React
 ↓
POST /api/users/login
 ↓
Spring Boot
 ↓
Validate Credentials
 ↓
Generate JWT
 ↓
Return JWT + User
 ↓
React
 ↓
Store User + Token
```

---

# 🔒 Protected API Flow

After login:

```text
React
 ↓
Axios Request
 ↓
Authorization: Bearer <JWT>
 ↓
Spring Security
 ↓
Validate JWT
 ↓
Identify User
 ↓
Check Role
 ↓
Check Ownership
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
PostgreSQL
```

---

# 🛡 Authentication and Security

OJPMS uses **JWT-based authentication**.

After successful login, the backend returns:

```json
{
  "token": "JWT_TOKEN",
  "user": {
    "id": 1,
    "name": "Test User",
    "email": "user@test.com",
    "role": "JOB_SEEKER"
  }
}
```

The frontend stores the authenticated user information and JWT.

For protected API requests, Axios automatically sends:

```text
Authorization: Bearer <JWT_TOKEN>
```

---

## 🔐 Password Security

Passwords are never stored as plain text.

The backend uses:

```text
BCrypt
```

for password hashing.

The password is also excluded from user API responses.

---

# 🔒 Role-Based Access Control

Spring Security protects the APIs according to user roles.

Example:

```text
POST /api/jobs
        ↓
Requires RECRUITER
```

While:

```text
POST /api/applications
        ↓
Requires JOB_SEEKER
```

This prevents users from performing operations belonging to another role.

---

# 👤 Ownership Security

The backend does not rely only on IDs supplied by the frontend.

For example, when a Recruiter creates a job:

```text
JWT
 ↓
Recruiter Email
 ↓
Find Recruiter
 ↓
Create Job
 ↓
Associate Job with Recruiter
```

Similarly, when a Job Seeker applies:

```text
JWT
 ↓
Job Seeker Email
 ↓
Find Applicant
 ↓
Create Application
 ↓
Associate Application with Applicant
```

This prevents users from manipulating another user's ID through frontend requests.

---

# 📦 Modules

## 1. User Management

Responsibilities:

* Registration
* Login
* Password hashing
* Role management
* JWT authentication

---

## 2. Job Management

Responsibilities:

* Create jobs
* Retrieve jobs
* Search jobs
* Update jobs
* Delete jobs
* Recruiter ownership

---

## 3. Application Management

Responsibilities:

* Submit applications
* Retrieve Job Seeker applications
* Retrieve Recruiter applications
* Retrieve job-specific applications
* Update application status
* Prevent duplicate applications
* Application ownership

---

## 4. Recruiter Dashboard

The Recruiter Dashboard provides:

* Recruiter job listings
* Job statistics
* Application information
* Job management actions
* Navigation to application management

---

## 5. Job Seeker Application Management

Job Seekers can access:

```text
My Applications
```

to track:

* Applied jobs
* Application information
* Current application status

---

# 🌐 API Overview

Base URL:

```text
http://localhost:8080/api
```

---

## User APIs

### Register

```http
POST /users
```

Access:

```text
Public
```

Purpose:

```text
Create a new user account.
```

---

### Login

```http
POST /users/login
```

Access:

```text
Public
```

Purpose:

```text
Authenticate user and generate JWT.
```

---

# 💼 Job APIs

### Get Jobs

```http
GET /jobs
```

Access:

```text
Public
```

---

### Get Job

```http
GET /jobs/{id}
```

Access:

```text
Public
```

---

### Search Jobs

```http
GET /jobs/search/**
```

Access:

```text
Public
```

---

### Create Job

```http
POST /jobs
```

Access:

```text
RECRUITER
```

---

### Update Job

```http
PUT /jobs/{id}
```

Access:

```text
RECRUITER
```

---

### Delete Job

```http
DELETE /jobs/{id}
```

Access:

```text
RECRUITER
```

---

### Recruiter's Jobs

```http
GET /jobs/recruiter/my-jobs
```

Access:

```text
RECRUITER
```

---

# 📝 Application APIs

### Apply for Job

```http
POST /applications
```

Access:

```text
JOB_SEEKER
```

---

### My Applications

```http
GET /applications/my-applications
```

Access:

```text
JOB_SEEKER
```

---

### Recruiter Applications

```http
GET /applications/my-recruiter-applications
```

Access:

```text
RECRUITER
```

---

### Job Applications

```http
GET /applications/job/{jobId}
```

Access:

```text
RECRUITER
```

---

### Get Application

```http
GET /applications/{id}
```

Access:

```text
Authenticated User
```

---

### Update Application Status

```http
PUT /applications/{id}/status
```

Access:

```text
RECRUITER
```

---

### Delete Application

```http
DELETE /applications/{id}
```

Access:

```text
JOB_SEEKER
```

---

# 🗄 Database

OJPMS uses **PostgreSQL** as its relational database.

The main application data is organized around:

```text
Users
   │
   ├────────────── Jobs
   │
   └────────────── Applications
                         │
                         └──── Jobs
```

### Main Entities

#### User

Contains information such as:

* ID
* Name
* Email
* Password
* Role

#### Job

Contains information such as:

* ID
* Title
* Company
* Location
* Description
* Salary
* Start Date
* End Date
* Status
* Recruiter

#### Job Application

Contains information such as:

* ID
* Applicant
* Job
* Application Date
* Application Status

---

# 📁 Frontend Structure

The frontend follows a component/page/service-oriented structure.

Example:

```text
src/
│
├── components/
│   ├── Navbar.jsx
│   ├── Sidebar.jsx
│   └── ...
│
├── pages/
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── Jobs.jsx
│   ├── JobDetails.jsx
│   ├── MyApplications.jsx
│   ├── RecruiterDashboard.jsx
│   ├── RecruiterApplications.jsx
│   ├── RecruiterAllApplications.jsx
│   ├── CreateJob.jsx
│   └── EditJob.jsx
│
├── services/
│   └── api.js
│
└── App.jsx
```

---

# 🔌 API Service

The frontend uses Axios for backend communication.

The API service is responsible for:

* Configuring the backend base URL
* Sending HTTP requests
* Automatically attaching JWT
* Handling unauthorized responses
* Redirecting users to login when authentication expires

---

# 📁 Backend Structure

The Spring Boot backend follows a layered architecture.

Typical structure:

```text
src/main/java/
│
├── controller/
│
├── service/
│
├── repository/
│
├── entity/
│
├── dto/
│
├── security/
│
└── ...
```

### Controller Layer

Handles HTTP requests and responses.

### Service Layer

Contains:

* Business logic
* Validation
* Ownership checks
* Authentication-based user identification

### Repository Layer

Handles database operations.

### Security Layer

Handles:

* JWT authentication
* Authentication filter
* Spring Security configuration
* Role-based authorization

---

# ⚙️ Project Setup

## Prerequisites

Install the following:

* Java 21
* Node.js
* npm
* PostgreSQL
* Maven
* Git

---

# 1. Clone the Repository

```bash
git clone <your-github-repository-url>
```

Navigate into the project directories.

---

# 2. Backend Configuration

Open the Spring Boot backend project.

Configure PostgreSQL database credentials in the application's configuration.

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ojpms
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

server.port=8080
```

Use your own PostgreSQL credentials.

---

# 3. Create PostgreSQL Database

Create the database:

```sql
CREATE DATABASE ojpms;
```

The Spring Boot application can then connect to this database according to the configured JPA/Hibernate settings.

---

# 4. Start Backend

From the backend project:

```bash
mvn spring-boot:run
```

Or run the Spring Boot main application from your IDE.

Backend:

```text
http://localhost:8080
```

API:

```text
http://localhost:8080/api
```

---

# 5. Frontend Configuration

Navigate to the React project.

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

The frontend normally runs at:

```text
http://localhost:5173
```

---

# 🔧 Environment Configuration

The frontend API service supports:

```text
VITE_API_URL
```

For local development, the application falls back to:

```text
http://localhost:8080/api
```

Example `.env`:

```env
VITE_API_URL=http://localhost:8080/api
```

Do not commit sensitive credentials or private environment files to GitHub.

---

# ▶️ Running the Complete Application

Start the components in this order:

```text
PostgreSQL
    ↓
Spring Boot Backend
    ↓
React Frontend
```

Then open:

```text
http://localhost:5173
```

---

# 🧪 Testing

The application was tested at multiple levels.

## Manual Testing

Testing included:

* Smoke Testing
* Sanity Testing
* Functional Testing
* Integration Testing
* System Testing
* Regression Testing
* UI Testing
* Security Testing
* End-to-End Testing

---

# 🔌 API Testing

REST APIs were tested for:

* Successful requests
* Invalid requests
* Authentication
* Authorization
* JWT validation
* Role restrictions
* Ownership restrictions
* Duplicate applications
* Business-rule validation
* Database integration
* Error handling

---

# 📊 Test Results

### Manual Testing

```text
Total Test Cases: 24
Passed:           24
Failed:           0
Blocked:          0
Pass Percentage:  100%
```

### API Testing

```text
Total API Test Cases: 37
Passed:               37
Failed:                0
Blocked:               0
Pass Percentage:       100%
```

---

# 🔄 End-to-End Testing

The complete workflow was successfully tested:

```text
Recruiter Login
      ↓
Create Job
      ↓
Job Seeker Login
      ↓
Browse Jobs
      ↓
View Job Details
      ↓
Apply for Job
      ↓
Recruiter Login
      ↓
View Application
      ↓
Update Application Status
      ↓
Job Seeker Login
      ↓
View Updated Application Status
```

Result:

```text
PASS ✅
```

---

# 🔐 Security Testing

Security testing verified:

### Authentication

* JWT generated after successful login
* JWT sent with protected requests
* Invalid credentials rejected
* Invalid JWT rejected

### Authorization

* Job Seekers cannot perform recruiter operations
* Recruiters cannot perform Job Seeker operations
* Protected APIs require authentication

### Ownership

* Recruiters cannot modify another recruiter's jobs
* Recruiters cannot delete another recruiter's jobs
* Job Seekers can access only their own applications

### Session

* Logout clears stored authentication information
* Unauthorized requests redirect the user to login

---

# 🎯 Project Highlights

Some important technical implementations in OJPMS include:

### 1. JWT Authentication

Used to securely authenticate users and identify the logged-in user on protected APIs.

### 2. Role-Based Authorization

Separate functionality is provided for:

```text
RECRUITER
JOB_SEEKER
```

### 3. Ownership Validation

Backend verifies that users own the resources they are trying to modify.

### 4. Secure Application Creation

The backend derives the applicant from the JWT instead of trusting an applicant ID sent by the frontend.

### 5. Duplicate Application Prevention

A Job Seeker cannot submit multiple applications for the same job.

### 6. React–Spring Boot Integration

React communicates with Spring Boot through REST APIs using Axios.

### 7. PostgreSQL Integration

Application data is persisted in PostgreSQL through the Spring Boot backend.

### 8. Application Status Tracking

Recruiters can update application status and Job Seekers can track the updated status.

---

# 🚀 Future Enhancements

The following features could be added in future versions:

* Resume upload
* Recruiter company profiles
* Job categories
* Advanced job filtering
* Email notifications
* Password reset
* Profile management
* Admin dashboard
* Recruiter verification
* Resume-based job matching
* Pagination
* Sorting
* Saved/bookmarked jobs
* Application withdrawal
* Interview scheduling
* Notification system
* Deployment using Docker
* Cloud deployment

---

# 📌 Project Status

```text
Development       ✅ Completed
Frontend           ✅ Completed
Backend            ✅ Completed
Database           ✅ Completed
JWT Security       ✅ Completed
API Integration    ✅ Completed
Manual Testing     ✅ Completed
API Testing        ✅ Completed
Integration Testing✅ Completed
E2E Testing        ✅ Completed
```

### Current Status

**Project Successfully Integrated and Tested**

---

# 👨‍💻 Author

**Ruturaj Narhe**

B.Sc. Computer Science Graduate

### Technical Areas

* Java
* Spring Boot
* REST APIs
* PostgreSQL
* React.js
* JavaScript
* HTML
* CSS
* JWT
* Git/GitHub

---

# ⭐ Conclusion

OJPMS is a full-stack Online Job Portal Management System that provides separate workflows for Job Seekers and Recruiters.

The application integrates:

```text
React.js
    +
Spring Boot REST APIs
    +
PostgreSQL
    +
JWT Authentication
    +
Role-Based Authorization
```

The complete application workflow has been manually tested, API tested, security tested and integration tested successfully.

**Final Testing Result: 100% test cases passed.**

