# FoodRescue — Web-Based Food Redistribution & Rescue Platform

> **Project-Based Learning (PBL) \| B.Tech Computer Science
> Engineering**  
> Arya College of Engineering & IT, Rajasthan Technical University  
> Project Guide: **Er. Ram Babu Buri**, Department of CSE

FoodRescue is a web-based platform designed to connect food providers,
NGOs, and volunteers to coordinate the redistribution of surplus food.
Providers can publish food listings, NGOs can discover and claim
eligible listings, and volunteers can support pickup and delivery
workflows where enabled.

## Project Links

- **Live Project:** [FoodRescue Web Application](https://food-rescue-ftd5.onrender.com/index.html)
- **Project Documentation:** [FoodRescue Documentation Folder on Google Drive](https://drive.google.com/drive/folders/1XAjb3Eem2pYDR0IasrbvKweW1qBEzNfd?usp=sharing)
- **GitHub Repository:** [Food-Rescue](https://github.com/rajeevvv018/Food-Rescue)

> The live website and documentation links are provided for demonstration and project review. Availability of the live application may depend on the hosting service.

## Table of Contents

- [Project Overview](#project-overview)
- [Objectives](#objectives)
- [Key Features](#key-features)
- [User Roles](#user-roles)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Database Overview](#database-overview)
- [Local Setup](#local-setup)
- [Configuration](#configuration)
- [Build and Deployment](#build-and-deployment)
- [Testing Checklist](#testing-checklist)
- [Team Contributions](#team-contributions)
- [Security Notes](#security-notes)
- [Known Limitations and Verification
  Notes](#known-limitations-and-verification-notes)
- [Future Enhancements](#future-enhancements)

## Project Overview

Food waste and food insecurity can exist at the same time. FoodRescue
aims to improve coordination between organizations with surplus food and
NGOs that can redistribute it. It provides role-based dashboards and a
central application workflow for food listings, claims, notifications,
and pickup/delivery status tracking.

**Project workflow:**

``` text
Food Provider
     |
     v
Create surplus-food listing
     |
     v
FoodRescue Backend + MySQL
     |
     v
NGO discovers and claims eligible food
     |
     v
Provider reviews claim (where enabled)
     |
     v
Volunteer pickup / delivery (where enabled)
     |
     v
Status updates and notifications
```

The software supports coordination and record keeping. It does not
independently guarantee food safety or that a physical delivery has
occurred; these require appropriate real-world checks.

## Objectives

- Reduce avoidable surplus-food waste through better coordination.
- Provide a digital way to publish and discover available food.
- Manage NGO food claims and their statuses.
- Support volunteer pickup and delivery tracking.
- Keep application data in a relational database.
- Provide separate interfaces and access rules for Providers, NGOs, and
  Volunteers.
- Deploy the web application for demonstration and evaluation.

## Key Features

### Authentication and Access Control

- User registration and login.
- Password hashing and server-side session handling.
- Role-based access restrictions for protected routes.
- Logout and session invalidation.

### Provider

- Create food listings with details such as name, description, quantity,
  unit, food type, preparation time, expiry time, and pickup address.
- View and manage listings.
- Review pending NGO claims and approve/reject them where supported by
  the configured workflow.

### NGO

- Browse eligible available food.
- Submit claims for food listings.
- View claim details and status.

### Volunteer

- Support pickup/delivery workflows and status updates where enabled in
  the deployed version.

### Notifications and Public Information

- Retrieve relevant in-app notifications where implemented.
- Display public homepage statistics or available-food information
  through public endpoints where enabled.

## User Roles

| Role                               | Main responsibilities                                              |
|------------------------------------|--------------------------------------------------------------------|
| Provider                           | Publish surplus food and coordinate handover                       |
| NGO                                | Discover available listings and submit claims                      |
| Volunteer                          | Support assigned pickup/delivery activities where enabled          |
| Authorized operator / project team | Configure, deploy, maintain, test, and demonstrate the application |

## Technology Stack

| Layer                             | Technologies                                                              |
|-----------------------------------|---------------------------------------------------------------------------|
| Frontend                          | HTML5, CSS3, JavaScript                                                   |
| Backend                           | Java, Jakarta Servlets                                                    |
| Architecture                      | MVC-style separation: Controller, Service, DAO, Model, Utility            |
| Database                          | MySQL, accessed through JDBC                                              |
| Build                             | Apache Maven, WAR packaging                                               |
| Application server                | Apache Tomcat 11                                                          |
| Frontend hosting                  | Netlify                                                                   |
| Backend hosting                   | Render (Docker/Tomcat deployment)                                         |
| Intended managed database hosting | Aiven MySQL; verify connectivity and migration for the current deployment |

## Architecture

``` text
Browser
  |
  | HTML / CSS / JavaScript
  v
Frontend (Netlify or local web root)
  |
  | HTTP requests / JSON responses
  v
Java Servlet Controllers (Tomcat)
  |
  v
Service Layer (business rules)
  |
  v
DAO Layer (JDBC / parameterized SQL)
  |
  v
MySQL Database
```

### Backend request flow

1.  The frontend submits an HTTP request to a configured backend
    endpoint.
2.  A Servlet/controller receives the request.
3.  The Service layer applies business rules and validation.
4.  The DAO layer executes database operations through JDBC.
5.  The backend returns a response to the frontend.
6.  The frontend displays the result or an appropriate error state.

## Project Structure

The exact structure can change as the project evolves. The main backend
and web resources are organized approximately as follows:

``` text
Food-Rescue/
├── pom.xml
├── Dockerfile
├── src/
│   └── main/
│       ├── java/
│       │   └── com/foodrescue/
│       │       ├── controller/       # Servlet controllers / endpoints
│       │       ├── dao/              # Database access
│       │       ├── model/            # Domain models
│       │       ├── service/          # Business logic
│       │       └── util/             # Shared utilities, DB connection
│       └── webapp/
│           ├── index.html
│           ├── login.html
│           ├── register.html
│           ├── provider/
│           ├── ngo/
│           ├── volunteer/
│           └── assets/
│               ├── css/
│               └── js/
└── README.md
```

> This is a high-level guide, not a guaranteed exhaustive file listing.
> Refer to the current repository for the authoritative structure.

## Database Overview

The project uses MySQL and JDBC. Core logical tables include:

| Table           | Purpose                         |
|-----------------|---------------------------------|
| `users`         | User accounts and roles         |
| `food_listings` | Surplus-food listing details    |
| `food_claims`   | NGO claims and claim statuses   |
| `pickups`       | Pickup/delivery tracking        |
| `notifications` | User-facing event notifications |

Actual columns, constraints, and relationships should be taken from the
current database schema. Do not run destructive schema scripts against a
database containing needed data.

## Local Setup

### Prerequisites

Install and configure:

- JDK compatible with the project configuration.
- Apache Maven 3.9.x or another compatible Maven version.
- MySQL Server 8.x.
- Apache Tomcat 11.
- Git.
- A modern web browser.

Check your installed versions:

``` powershell
java -version
mvn -version
git --version
```

Check MySQL service on Windows:

``` powershell
Get-Service MySQL*
```

### 1. Clone the repository

``` bash
git clone https://github.com/rajeevvv018/Food-Rescue.git
cd Food-Rescue
```

### 2. Configure MySQL

1.  Start the MySQL service.
2.  Create or verify the project database using the schema/configuration
    provided in the repository.
3.  Configure a database user with only the permissions the application
    needs.
4.  Do not put real passwords in source files or commit them to GitHub.

The application has used the database name `foodrescue` in local
development. Confirm the current `DBConnection` configuration and schema
before importing or changing data.

### 3. Configure environment variables

The database connection supports environment-based configuration. Set
values for the environment in which the application runs:

``` text
FOODRESCUE_DB_URL
FOODRESCUE_DB_USER
FOODRESCUE_DB_PASSWORD
```

Example URL shape (replace with your own local settings):

``` text
jdbc:mysql://localhost:3306/foodrescue
```

For cloud deployments, use the database provider’s host, port, database
name, SSL requirements, and credentials. Do not commit `.env` files or
passwords. Use host-managed environment variables/secrets.

### 4. Build the application

Run from the repository root:

``` bash
mvn clean package
```

If you need to build while skipping automated tests (for example, when
no tests are configured), use:

``` bash
mvn clean package -DskipTests
```

The WAR package is expected at:

``` text
target/FoodRescue.war
```

A successful build confirms compilation and packaging; it does **not**
prove that every runtime workflow works.

### 5. Run with Tomcat

1.  Build the WAR.
2.  Copy `target/FoodRescue.war` into Tomcat’s `webapps` directory.
3.  Start Tomcat using its platform-specific startup script.
4.  Open:

``` text
http://localhost:8080/FoodRescue/
```

If Tomcat is configured with a different context path or port, use the
actual configured URL.

## Configuration

### Frontend backend URL

The frontend uses a configuration file at:

``` text
src/main/webapp/assets/js/config.js
```

Set the backend base URL to the correct deployed backend origin for
production, or the local backend origin for development. An empty base
URL is only appropriate when the frontend and backend are served from
compatible origins and paths.

### CORS and sessions

- Configure the backend to allow only the intended frontend origin(s).
- If cross-origin requests use cookies/sessions, configure credentials
  and cookie attributes consistently.
- Never use wildcard origins with credentialed requests.
- Verify browser network requests and backend logs when an API call
  fails.

## Build and Deployment

### Frontend — Netlify

The static frontend is configured for Netlify. Confirm the publish
directory and any frontend configuration values before redeploying. The
frontend must point to the correct backend API URL.

### Backend — Render

The backend has been deployed to Render using a Tomcat/Docker-based
setup.

Live website: - https://food-rescue-ftd5.onrender.com/index.html

Free hosting plans may spin down after inactivity, so the first request
can take longer. Deployment success alone does not establish that every
API endpoint is connected to the intended cloud database.

### Database — Aiven MySQL

Aiven MySQL has been prepared as the intended managed database target.
Before considering cloud data migration complete, verify:

1.  The backend can authenticate to the database using SSL settings
    required by the provider.
2.  The expected schema exists.
3.  Required records have been migrated intentionally and safely.
4.  Creating or updating a listing persists after page reload.
5.  No database credentials appear in logs or source control.

**Do not upload database dumps, local backups, `.env` files, passwords,
access tokens, or private user data to this repository.**

## Testing Checklist

Use this checklist before a final demonstration. Mark a test as passed
only after actually running it.

- [ ] Homepage opens in the target environment.
- [ ] Registration works and duplicate email is handled correctly.
- [ ] Login and logout work.
- [ ] Protected routes reject unauthenticated users.
- [ ] Role-based access prevents unauthorized operations.
- [ ] Provider can create a valid food listing.
- [ ] Invalid or missing required fields are rejected.
- [ ] Listings persist after reload.
- [ ] NGO can view eligible listings and submit a claim.
- [ ] Provider claim approval/rejection works where enabled.
- [ ] Notifications show the expected status updates where implemented.
- [ ] Volunteer pickup/delivery status updates work where enabled.
- [ ] Cloud database connectivity and migration are verified.
- [ ] Browser console and server logs contain no exposed secrets.

## Team Contributions

| Team member      | Assigned contribution                                                                                     |
|------------------|-----------------------------------------------------------------------------------------------------------|
| **Rajeev Kumar** | Backend development, MySQL/JDBC database work, Servlet/API integration, build and deployment coordination |
| **Riteek Kumar** | Frontend development, dashboard UI, food form, claims interface, and presentation (PPT)                   |
| **Raunak Kumar** | UI/UX and project report/documentation support, diagrams, screenshots, and presentation assistance        |
| **Ritu Raj**     | Debugging support, issue reproduction, testing, regression checks, and verification of fixes              |

Update this table if the final team allocation differs from the work
actually completed.

## Security Notes

- Never commit passwords, API keys, database URLs containing
  credentials, or session secrets.
- Keep secrets in environment variables or the hosting provider’s secret
  manager.
- Use password hashing; never store plaintext passwords.
- Use parameterized SQL statements.
- Validate input on the server, even when the frontend also validates
  it.
- Enforce authorization in backend endpoints, not only by hiding
  frontend controls.
- Use HTTPS for public deployments.
- Back up the database before migration or schema changes and verify the
  backup before relying on it.

## Known Limitations and Verification Notes

- The public website and login flow have been reported as working, but
  this does not independently confirm all backend workflows.
- Cloud database connectivity, schema/data migration, and persistence
  must be verified against the currently deployed backend.
- Free hosting may sleep or have cold starts.
- Notifications, public statistics, and volunteer workflows should be
  described as fully implemented only after testing the corresponding
  endpoints and user journeys.
- The current repository is the source of truth for exact endpoint
  names, schema details, and deployment configuration.

## Future Enhancements

- End-to-end automated tests for Provider → NGO → Volunteer workflows.
- Better monitoring and structured error reporting.
- Clearer claim and pickup status history.
- Improved accessibility and mobile usability.
- Documented backup/restore and deployment procedures.
- Additional reporting and verified impact statistics.

## Academic Project

**FoodRescue — Web-Based Food Redistribution & Rescue Platform**  
Arya College of Engineering & IT · Rajasthan Technical University  
Project Guide: Er. Ram Babu Buri, Department of CSE

------------------------------------------------------------------------

If you find an issue, please document the steps to reproduce it, the
expected behavior, and the actual behavior so the team can investigate
it.

## Team Member-Wise Weekly Work Updates (Weeks 1–10)

> **Mentor review note:** This section is a consolidated weekly summary
> based on the team’s assigned roles, the project history available
> while preparing this README, and the weekly reports already visible in
> the PBL portal. It is a draft for review—not independent proof that
> every activity below was completed in that exact week. Before
> submission, each member should confirm the entries against Git
> commits, screenshots, issue logs, reports, and actual work dates.
> Change or remove any entry that does not match the evidence.

### Week-by-Week Summary

| Week        | Rajeev Kumar — Backend, Database & Deployment                                                                                                                                                                                                                  | Riteek Kumar — Frontend & PPT                                                                                               | Raunak Kumar — UI/UX & Report Support                                                                     | Ritu Raj — Debugging & Testing                                                                                                                  |
|-------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| **Week 1**  | Set up the Java/Maven/MySQL/Tomcat project foundation, organized the initial MVC-style packages, and generated the initial WAR build, as reflected in the submitted Week 1 report.                                                                             | Reviewed the project requirements and planned the frontend pages and role-based dashboard structure.                        | Reviewed project scope and began organizing report sections, diagrams, and presentation requirements.     | Reviewed the proposed workflows and prepared an initial list of areas to verify during testing.                                                 |
| **Week 2**  | Continued database/JDBC configuration and backend development; worked on registration/login, session and role checks, and Provider/NGO workflow endpoints, as described in the submitted Week 2 report.                                                        | Built/refined frontend pages and dashboard interactions; worked on forms and integration points for the backend APIs.       | Worked on UI/UX planning, report structure, architecture/workflow diagrams, and presentation material.    | Reviewed the user workflows and documented reproducible issues or test scenarios as they arose.                                                 |
| **Week 3**  | Improved Provider, NGO, and Volunteer workflows; addressed pickup status and notification integration issues; worked on replacing homepage dummy statistics with database-backed data; deployed an updated build, as described in the submitted Week 3 report. | Refined dashboards and listing/claim interfaces and coordinated display of backend-provided data.                           | Updated project documentation and presentation materials to reflect the implemented modules and workflow. | Supported issue reproduction and verification of fixes across the available workflows; record actual test outcomes before marking tests passed. |
| **Week 4**  | Reviewed JDBC connectivity, database relationships, and the MVC separation between Servlet/controller, Service, DAO, Model, and Utility layers.                                                                                                                | Refined forms, layout, navigation, and role-specific dashboard UI.                                                          | Updated report sections, architecture diagrams, and implementation documentation.                         | Checked application startup, navigation, form behavior, and recorded issues found during team testing.                                          |
| **Week 5**  | Worked on authentication, registration validation, password hashing, session handling, and role-based authorization.                                                                                                                                           | Connected frontend login/registration and dashboard behavior to the backend endpoints where available.                      | Organized screenshots and documentation for authentication and role-specific modules.                     | Tested login/registration scenarios and recorded invalid-input, session, or access-control issues where observed.                               |
| **Week 6**  | Worked on food-listing CRUD operations, server-side validation, and persistence through DAO/Service layers.                                                                                                                                                    | Refined the Add Food form, listing display, required-field validation, and Provider dashboard interactions.                 | Documented food-listing requirements and prepared supporting screenshots/diagrams.                        | Checked listing creation, validation messages, and update/cancel behavior; recorded actual pass/fail results.                                   |
| **Week 7**  | Worked on claim-related APIs and Provider/NGO integration, including claim retrieval and status handling where implemented.                                                                                                                                    | Integrated claim data into dashboard tables and refined loading, empty, error, and status display states.                   | Updated the project report and workflow documentation for listing and claim modules.                      | Reproduced claim integration issues and retested fixes with the team where the relevant environment was available.                              |
| **Week 8**  | Reviewed notification handling, pickup status logic, CORS, environment-based configuration, and backend security settings.                                                                                                                                     | Refined notification/claim UI, dashboard feedback, and theme or usability improvements as included in the current frontend. | Updated implementation notes, diagrams, and presentation material for refined features.                   | Checked role-specific access, claim-status consistency, notifications, and regression scenarios where implemented.                              |
| **Week 9**  | Built the Maven WAR and reviewed Tomcat/Docker/Render deployment configuration and environment-variable requirements.                                                                                                                                          | Checked frontend configuration and the deployed site’s connection settings for the backend API.                             | Prepared deployment screenshots, final report sections, and presentation flow.                            | Helped identify deployment/integration issues and recorded which live workflows still required verification.                                    |
| **Week 10** | Reviewed the Render deployment and cloud configuration; the live website and login were reported as working. Cloud database migration and complete end-to-end verification still require separate confirmation.                                                | Prepared the frontend for the live demonstration and checked the visible website and login flow.                            | Consolidated project documentation, screenshots, report, and presentation material for final review.      | Prepared final smoke/regression checks and documented any unresolved database, claim, notification, or pickup/delivery tests.                   |

### Weekly Evidence and Reporting Rules

For a formal mentor review, attach or reference evidence wherever
possible:

- **Rajeev:** relevant Git commits, Maven build output, backend logs,
  database schema/configuration, and deployment logs.
- **Riteek:** frontend commits, screenshots of pages/forms, API
  integration changes, and the PPT.
- **Raunak:** report versions, diagrams, UI/UX artifacts, screenshots,
  and presentation updates.
- **Ritu Raj:** issue/reproduction notes, test cases, before/after
  screenshots, and actual retest outcomes.

Do not report a feature as “completed” or a test as “passed” solely
because the website opens or login succeeds. Record cloud database
connectivity, data persistence, claim transitions, notifications, and
pickup/delivery workflows only after testing them in the relevant
environment.
