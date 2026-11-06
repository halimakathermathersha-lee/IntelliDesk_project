# IntelliDesk – Full-stack issue management starter

A beginner-oriented React + Spring Boot + MySQL project. Frontend and backend are independent folders. The current implementation provides JWT login, role-aware ticket visibility, rule-based ticket classification, workload-aware assignment, progress updates, ticket lifecycle actions, notifications and scheduled SLA checks.

> **Important honest scope note:** The rule-based classifier is a fallback, not genuine AI. The uploaded attachment is currently recorded as metadata only; file bytes are not stored or served. The current admin interface includes workload, SLA and history views, but full admin editing (skills, availability, reassignment, category/priority override and user management) still needs to be completed before production use. Review the security notes before exposing this app to a network.

## Technology
- Frontend: React, Vite, JavaScript, React Router, Tailwind CSS, Fetch API
- Backend: Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security, JWT, Maven
- Database: MySQL `intellidesk`

## Folder layout
- `frontend/` React application
- `backend/` Spring Boot REST API
- `database/schema.sql` database tables
- `README.md` this guide

## 1. Database setup
1. Start MySQL.
2. Open MySQL Workbench or the MySQL terminal.
3. Run the contents of `database/schema.sql`.
4. Ensure the schema name is `intellidesk`.

Set your own database username/password as environment variables rather than editing credentials into source:
- Windows PowerShell:
  ```powershell
  $env:DB_USERNAME="root"
  $env:DB_PASSWORD="your-local-password"
  $env:JWT_SECRET="use-a-long-random-secret-at-least-32-characters"
  ```
- macOS/Linux:
  ```bash
  export DB_USERNAME=root
  export DB_PASSWORD='your-local-password'
  export JWT_SECRET='use-a-long-random-secret-at-least-32-characters'
  ```

`application.properties` defaults to `root` with an empty password for a local practice database. Change it through environment variables for your own machine. Never commit real secrets.

## 2. Start the backend
Requirements: Java 17+ and Maven (or use the included Maven wrapper if you add one).

```bash
cd backend
mvn spring-boot:run
```
API base URL: `http://localhost:8080`

Hibernate uses `ddl-auto=validate`, so the SQL schema should be imported before startup. If you see a schema-validation error, compare `database/schema.sql` with the entity mappings and ensure you ran the current schema.

## 3. Start the frontend
In another terminal:

```bash
cd frontend
npm install
cp .env.example .env
npm run dev
```

On Windows, copy `.env.example` to `.env` manually if `cp` is unavailable. Open `http://localhost:5173`.

## Authentication and roles
- Register at `/register`; public registration supports Employee (`USER`) and Worker (`WORKER`) only.
- Admin registration is deliberately blocked. Create an admin through a controlled database/admin bootstrap procedure, never by exposing a public admin registration form.
- Passwords are hashed with BCrypt.
- JWT tokens are sent in the `Authorization: Bearer ...` header.
- For beginner convenience, the token is stored in `localStorage`. This is vulnerable to token theft if an XSS vulnerability exists; production applications commonly prefer secure, HttpOnly cookies and additional hardening.
- Backend access checks are required even if the React UI hides a page. Employees can only access their own tickets. Workers can only access tickets assigned to them. Admin-only endpoints check the authenticated role.

## Automatic classification
When an employee submits only a description, `ClassificationService` looks for simple keywords and infers category, title, priority, severity, required role and skills. For example, payroll/salary/payment words suggest an application ticket; network/Wi-Fi/VPN words suggest network support. Phrases such as “production down” or “security breach” raise the priority to CRITICAL. This is transparent keyword logic, not a machine-learning model or an external AI API.

The service is separated from ticket persistence so a real provider integration can later be added. Any external AI result must be validated against allowed categories and priority values before saving. No AI API key is needed for the current fallback.

## Automatic worker assignment
`AssignmentService` checks each worker:
1. Is the worker marked available?
2. Is their current active ticket count below `maxWorkload`?
3. Does their job role match the required role, or are they a General Support fallback?
4. How many of the required skills match?

Among eligible workers, the service scores skill gaps most heavily and then considers workload ratio. The lowest score wins. If nobody is eligible, the ticket stays OPEN and admins receive a notification. The admin reassignment/skills editing UI is not fully implemented in this starter.

New worker accounts default to `General Support`, available, maximum workload 5. Set up worker job roles and skills through controlled database changes for now.

## Ticket lifecycle
- Employee creates ticket: `OPEN` if unassigned or `ASSIGNED` if a suitable worker was found.
- Worker adds an update: assigned/open ticket becomes `IN_PROGRESS`.
- Worker resolves with resolution details: `RESOLVED`.
- Employee confirms: `CLOSED`.
- Employee can reopen a resolved/closed issue.

Ticket history is recorded for creation, progress updates, resolution, closure, reopening and escalation.

## SLA
Default targets are CRITICAL 2h, HIGH 4h, MEDIUM 8h, LOW 24h. These are configurable business rules, not universal standards. The scheduled task checks every 60 seconds and marks overdue, non-closed, non-resolved tickets as breached. A future enhancement could add warning notifications before the deadline.

## Main API endpoints
| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register employee/worker |
| POST | `/api/auth/login` | Login and receive JWT |
| GET | `/api/users/me` | Current profile |
| GET | `/api/tickets` | Role-filtered ticket list |
| POST | `/api/tickets` | Create ticket from description + optional attachment metadata |
| GET | `/api/tickets/{id}` | Read permitted ticket |
| POST | `/api/tickets/{id}/updates` | Worker/admin progress update |
| POST | `/api/tickets/{id}/resolve` | Worker/admin resolution |
| POST | `/api/tickets/{id}/confirm` | Employee confirms resolution |
| POST | `/api/tickets/{id}/reopen` | Employee reopens resolved/closed issue |
| POST | `/api/tickets/{id}/escalate` | Admin escalation |
| GET | `/api/notifications` | Current user's notifications |
| GET | `/api/admin/workers` | Admin worker list/workload basics |
| GET | `/api/admin/sla` | Admin SLA view |
| GET | `/api/admin/history` | Admin ticket history |

## Common errors
- **Connection refused to MySQL:** Start MySQL and verify port 3306.
- **Access denied for user:** Set `DB_USERNAME` and `DB_PASSWORD`.
- **Schema validation failed:** Run the current `database/schema.sql` against `intellidesk`.
- **CORS error:** Vite should run at `http://localhost:5173`; adjust `SecurityConfig` if using another origin.
- **401 response:** Log in again; JWT may be missing or expired.
- **No worker assigned:** Create worker accounts and configure their role/skills/availability; the ticket remains OPEN if no eligible worker exists.

## Beginner Tanglish quick guide
- `controller` na frontend request receive panra entry point.
- `service` na business logic irukkura place. Ticket classify pannradhu, worker assign pannradhu inga nadakkum.
- `repository` na database-la save/find panra layer.
- `entity` na Java object ↔ MySQL table mapping.
- `useState` form input / page data maintain pannum; `useEffect` page load aagumbodhu API call pannum.
- `fetch()` frontend-lendhu backend API-ku request anuppum.
- `JWT` login success aana token; backend token validate pannitu user role check pannum.
- First MySQL start pannunga, next backend run pannunga, appuram frontend run pannunga.

## Verification status
The archive contains source/configuration files only. This generation environment did not run Maven against your local MySQL database or install npm dependencies, so do not treat this as a verified production build. Run `mvn test` and `npm run build` locally and address any environment-specific errors before real company use.
