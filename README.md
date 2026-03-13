# RevWorkforce — Human Resource Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)
![Angular](https://img.shields.io/badge/Angular-21-red?style=for-the-badge&logo=angular)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=for-the-badge&logo=jsonwebtokens)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-85EA2D?style=for-the-badge&logo=swagger)

**A full-stack Human Resource Management System** built with Spring Boot 3 and Angular 21, featuring role-based access control for Admin, Manager, and Employee roles.

</div>

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Default Credentials](#-default-credentials)
- [API Reference](#-api-reference)
- [Role-Based Access](#-role-based-access)

---

## ✨ Features

### 👑 Admin
- Full employee lifecycle — Add, Edit, Deactivate, Activate, Delete
- Bulk employee and department onboarding via single API
- Department & Designation management with bulk support
- Leave type configuration, approval & rejection
- Holiday calendar — single & bulk add
- Company-wide announcements
- System-wide performance goals overview
- Reports — Employee Excel export & individual PDF reports
- Audit logs — every system action tracked with user + timestamp
- Dashboard with live stats (total employees, departments, leaves, goals)

### 👔 Manager
- Team member management & detailed profile view
- Leave application approve / reject with comments
- Team leave calendar & leave balance overview per member
- Goal assignment with priority (HIGH / MEDIUM / LOW)
- Performance review feedback with manager rating (1–5)
- Team-scoped announcements
- Dashboard — team size, pending leaves, goal completion
- Notification center

### 👤 Employee
- Personal profile update
- Leave application — apply, view history, cancel
- Leave balance overview by leave type
- Goal tracking with progress percentage updates
- Self performance review submission with self-rating
- Manager feedback view after review completion
- Company announcements & holiday calendar
- Employee directory with search
- Personal QR code & downloadable PDF report
- Notification center — read / unread management

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend Language | Java 17 |
| Backend Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (jjwt) |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8.0 |
| Frontend | Angular 21 (Standalone Components) |
| UI Library | Angular Material 21 (MDC) |
| API Documentation | Swagger / OpenAPI 3 (springdoc-openapi) |
| Build Tool | Maven |
| PDF Reports | iText / ReportService |
| Excel Reports | Apache POI (ExcelGenerator) |
| QR Code | ZXing (QRCodeService) |

---

## 📁 Project Structure

### Backend

```
backend/
└── src/
    ├── main/
    │   ├── java/com/revworkforce/
    │   │   ├── RevWorkforceApplication.java
    │   │   │
    │   │   ├── config/
    │   │   │   ├── CorsConfig.java
    │   │   │   ├── DataInitializer.java
    │   │   │   ├── PasswordConfig.java
    │   │   │   ├── SecurityConfig.java
    │   │   │   └── SwaggerConfig.java
    │   │   │
    │   │   ├── controller/
    │   │   │   ├── AdminController.java
    │   │   │   ├── AnnouncementController.java
    │   │   │   ├── AuditLogController.java
    │   │   │   ├── AuthController.java
    │   │   │   ├── DashboardController.java
    │   │   │   ├── DepartmentController.java
    │   │   │   ├── DesignationController.java
    │   │   │   ├── EmployeeController.java
    │   │   │   ├── HolidayController.java
    │   │   │   ├── LeaveManagementController.java
    │   │   │   ├── ManagerController.java
    │   │   │   ├── NotificationController.java
    │   │   │   ├── PerformanceController.java
    │   │   │   ├── QRCodeController.java
    │   │   │   ├── ReportController.java
    │   │   │   └── UserController.java
    │   │   │
    │   │   ├── dto/
    │   │   │   ├── AuthRequest.java
    │   │   │   ├── AuthResponse.java
    │   │   │   ├── EmployeeGoalDTO.java
    │   │   │   ├── HolidayDTO.java
    │   │   │   ├── LeaveApplicationDTO.java
    │   │   │   ├── LeaveBalanceDTO.java
    │   │   │   ├── ManagerFeedbackDTO.java
    │   │   │   ├── NotificationDTO.java
    │   │   │   ├── PerformanceReviewDTO.java
    │   │   │   └── UserDTO.java
    │   │   │
    │   │   ├── entity/
    │   │   │   ├── Announcement.java
    │   │   │   ├── AuditLog.java
    │   │   │   ├── Department.java
    │   │   │   ├── Designation.java
    │   │   │   ├── EmployeeGoal.java          # Enum: GoalStatus, Priority
    │   │   │   ├── Holiday.java
    │   │   │   ├── LeaveApplication.java      # Enum: LeaveStatus
    │   │   │   ├── LeaveBalance.java
    │   │   │   ├── LeaveRequest.java
    │   │   │   ├── LeaveType.java
    │   │   │   ├── ManagerFeedback.java
    │   │   │   ├── Notification.java          # Enum: NotificationType
    │   │   │   ├── PerformanceReview.java     # Enum: ReviewStatus
    │   │   │   └── User.java                  # Enum: UserRole (ADMIN/MANAGER/EMPLOYEE)
    │   │   │
    │   │   ├── exception/
    │   │   │   └── GlobalExceptionHandler.java
    │   │   │
    │   │   ├── repository/
    │   │   │   ├── AnnouncementRepository.java
    │   │   │   ├── AuditLogRepository.java
    │   │   │   ├── DepartmentRepository.java
    │   │   │   ├── DesignationRepository.java
    │   │   │   ├── EmployeeGoalRepository.java
    │   │   │   ├── HolidayRepository.java
    │   │   │   ├── LeaveApplicationRepository.java
    │   │   │   ├── LeaveBalanceRepository.java
    │   │   │   ├── LeaveRequestRepository.java
    │   │   │   ├── LeaveTypeRepository.java
    │   │   │   ├── ManagerFeedbackRepository.java
    │   │   │   ├── NotificationRepository.java
    │   │   │   ├── PerformanceReviewRepository.java
    │   │   │   └── UserRepository.java
    │   │   │
    │   │   ├── security/
    │   │   │   ├── CustomUserDetailsService.java
    │   │   │   ├── JwtAuthenticationFilter.java
    │   │   │   └── JwtTokenProvider.java
    │   │   │
    │   │   ├── service/
    │   │   │   ├── AnnouncementService.java
    │   │   │   ├── AuditLogService.java
    │   │   │   ├── AuthService.java
    │   │   │   ├── DepartmentService.java
    │   │   │   ├── DesignationService.java
    │   │   │   ├── EmployeeGoalService.java
    │   │   │   ├── HolidayService.java
    │   │   │   ├── LeaveApplicationService.java
    │   │   │   ├── LeaveBalanceService.java
    │   │   │   ├── LeaveTypeService.java
    │   │   │   ├── NotificationService.java
    │   │   │   ├── PerformanceReviewService.java
    │   │   │   ├── QRCodeService.java
    │   │   │   ├── ReportService.java
    │   │   │   └── UserService.java
    │   │   │
    │   │   └── util/
    │   │       ├── ApiResponse.java
    │   │       ├── DateUtils.java
    │   │       └── ExcelGenerator.java
    │   │
    │   └── resources/
    │       └── application.properties
    │
    └── test/
        └── java/com/revworkforce/service/
            ├── LeaveApplicationServiceTest.java
            ├── PerformanceReviewServiceTest.java
            └── UserServiceTest.java
```

---

### Frontend

```
frontend/
└── src/
    ├── index.html
    ├── main.ts
    ├── styles.scss
    │
    ├── environments/
    │   └── environment.ts
    │
    └── app/
        ├── app.ts
        ├── app.routes.ts
        ├── app.config.ts
        │
        ├── core/
        │   ├── constants/
        │   │   └── api.constants.ts
        │   ├── guards/
        │   │   └── auth.guard.ts
        │   ├── interceptors/
        │   │   ├── auth.interceptor.ts
        │   │   └── response.interceptor.ts
        │   ├── models/
        │   │   └── models.ts
        │   └── services/
        │       ├── admin.service.ts
        │       ├── auth.service.ts
        │       ├── employee.service.ts
        │       ├── leave.service.ts
        │       └── manager.service.ts
        │
        └── features/
            ├── auth/
            │   ├── login/
            │   │   └── login.component.ts
            │   └── unauthorized/
            │       └── unauthorized.component.ts
            │
            ├── admin/                            # 12 pages
            │   ├── layout/admin-layout.component.ts
            │   ├── dashboard/admin-dashboard.component.ts
            │   ├── employees/employees.component.ts
            │   ├── departments/departments.component.ts
            │   ├── designations/designations.component.ts
            │   ├── leaves/admin-leaves.component.ts
            │   ├── holidays/holidays.component.ts
            │   ├── announcements/admin-announcements.component.ts
            │   ├── goals/admin-goals.component.ts
            │   ├── left-employees/left-employees.component.ts
            │   ├── profile/admin-profile.component.ts
            │   └── view-profile/admin-view-profile.component.ts
            │
            ├── manager/                          # 9 pages
            │   ├── layout/manager-layout.component.ts
            │   ├── dashboard/manager-dashboard.component.ts
            │   ├── team/team.component.ts
            │   ├── view-employee/manager-view-employee.component.ts
            │   ├── leaves/manager-leaves.component.ts
            │   ├── goals/manager-goals.component.ts
            │   ├── performance-reviews/manager-reviews.component.ts
            │   ├── announcements/manager-announcements.component.ts
            │   ├── holidays/manager-holidays.component.ts
            │   └── profile/manager-profile.component.ts
            │
            └── employee/                         # 8 pages
                ├── layout/employee-layout.component.ts
                ├── dashboard/employee-dashboard.component.ts
                ├── profile/employee-profile.component.ts
                ├── leaves/employee-leaves.component.ts
                ├── goals/employee-goals.component.ts
                ├── performance-reviews/employee-reviews.component.ts
                ├── announcements/employee-announcements.component.ts
                ├── directory/employee-directory.component.ts
                └── notifications/employee-notifications.component.ts
```

---

## 🚀 Getting Started

### Prerequisites

| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.8+ |
| Node.js | 20+ |
| Angular CLI | 21 |
| MySQL | 8.0+ |

---

### Backend Setup

**1. Clone the repository**
```bash
git clone https://github.com/your-username/revworkforce.git
cd revworkforce/backend
```

**2. Create MySQL database**
```sql
CREATE DATABASE revworkinghrm;
```

**3. Update `application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/revworkinghrm
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

jwt.secret=your_jwt_secret_minimum_32_characters_here
jwt.expiration=86400000
```

> ⚠️ Never commit real passwords or JWT secrets to Git. Use environment variables in production.

**4. Run**
```bash
mvn spring-boot:run
```

| URL | Description |
|-----|-------------|
| `http://localhost:8080/rev` | Base API URL |
| `http://localhost:8080/rev/swagger-ui/index.html` | Swagger UI |
| `http://localhost:8080/rev/v3/api-docs` | OpenAPI JSON |

---

### Frontend Setup

**1. Install dependencies**
```bash
cd revworkforce/frontend
npm install
```

**2. Update environment**
```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/rev'
};
```

**3. Run**
```bash
ng serve
```

App runs at: **`http://localhost:4200`**

---

## 🔑 Default Credentials

| Role | Email | Password |
|------|-------|----------|
| **Admin** | sysadmin@revworkforce.com | Admin@123 |
| **Manager** | arjun@revworkforce.com | Manager@123 |
| **Employee** | aman@revworkforce.com | Employee@123 |

> 💡 Default password for new employees: `FirstName@123`

---

## 📡 API Reference

### Standard Response Format
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {},
  "status": "success",
  "timestamp": 1710000000000
}
```

All protected endpoints require:
```
Authorization: Bearer <jwt_token>
```

---

### 🔐 Auth

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/login` | Login & get JWT |

```json
{ "email": "sysadmin@revworkforce.com", "password": "Admin@123" }
```

---

### 👑 Admin — `/api/admin`

#### Employees
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/employees` | All employees |
| GET | `/api/admin/employees/search?query=` | Search |
| POST | `/api/admin/employees` | Add single |
| POST | `/api/admin/employees/bulk` | Bulk add |
| PUT | `/api/admin/employees/{id}` | Update |
| PUT | `/api/admin/employees/{id}/deactivate` | Deactivate |
| PUT | `/api/admin/employees/{id}/activate` | Activate |
| PUT | `/api/admin/employees/{empId}/assign-manager/{mgId}` | Assign manager |
| DELETE | `/api/admin/employees/{id}` | Delete |

#### Departments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/departments` | All |
| GET | `/api/admin/departments/{id}` | By ID |
| POST | `/api/admin/departments` | Create |
| POST | `/api/admin/departments/bulk` | Bulk create |
| PUT | `/api/admin/departments/{id}` | Update |
| DELETE | `/api/admin/departments/{id}` | Delete |

#### Designations
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/designations` | All |
| GET | `/api/admin/designations/{id}` | By ID |
| POST | `/api/admin/designations` | Create |
| POST | `/api/admin/designations/bulk` | Bulk create |
| PUT | `/api/admin/designations/{id}` | Update |
| DELETE | `/api/admin/designations/{id}` | Delete |

#### Leaves
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/leaves/all` | All applications |
| GET | `/api/admin/leaves/types` | Leave types |
| GET | `/api/admin/leaves/utilization` | Utilization stats |
| POST | `/api/admin/leaves/types/bulk-create` | Bulk create types |
| PUT | `/api/admin/leaves/types/{id}` | Update type |
| PUT | `/api/admin/leaves/applications/{id}/approve` | Approve |
| PUT | `/api/admin/leaves/applications/{id}/reject` | Reject |
| DELETE | `/api/admin/leaves/types/{id}` | Delete type |

#### Holidays
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/holidays/all` | All holidays |
| GET | `/api/admin/holidays/{id}` | By ID |
| POST | `/api/admin/holidays/add` | Add single |
| POST | `/api/admin/holidays/bulk-add` | Bulk add |
| PUT | `/api/admin/holidays/{id}` | Update |
| DELETE | `/api/admin/holidays/{id}` | Delete |

#### Announcements
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/announcements` | All (paginated) |
| GET | `/api/announcements/{id}` | By ID |
| POST | `/api/admin/announcements` | Create |
| PUT | `/api/announcements/{id}` | Update |
| DELETE | `/api/announcements/{id}` | Delete |

#### Dashboard, Goals & Reports
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/dashboard/stats` | Full dashboard stats |
| GET | `/api/admin/dashboard/main/stats` | Quick numbers |
| GET | `/api/admin/goals` | All goals |
| GET | `/api/admin/reports/employees/excel` | Excel export |
| GET | `/api/admin/reports/employee/{id}/pdf` | PDF report |
| GET | `/api/admin/audit-logs` | All audit logs (paginated) |
| GET | `/api/admin/audit-logs/user/{userId}` | User audit logs |

---

### 👔 Manager — `/api/manager`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/manager/dashboard/stats` | Dashboard |
| GET | `/api/manager/profile` | My profile |
| PUT | `/api/manager/profile` | Update profile |
| GET | `/api/manager/team-members` | Team list |
| GET | `/api/manager/team-members/{id}` | Member detail |
| GET | `/api/manager/leaves/applications` | Team leaves |
| GET | `/api/manager/leaves/applications/{id}` | Leave detail |
| GET | `/api/manager/leaves/team-calendar` | Leave calendar |
| GET | `/api/manager/leaves/team-balance` | Leave balance |
| PUT | `/api/manager/leaves/applications/{id}/approve` | Approve |
| PUT | `/api/manager/leaves/applications/{id}/reject` | Reject |
| GET | `/api/manager/goals` | Team goals |
| GET | `/api/manager/goals/{id}` | Goal detail |
| POST | `/api/manager/goals` | Assign goal |
| PUT | `/api/manager/goals/{id}/comments` | Add comment |
| GET | `/api/manager/performance-reviews` | Team reviews |
| GET | `/api/manager/performance-reviews/{id}` | Review detail |
| GET | `/api/manager/performance-reviews/{id}/feedback` | Feedback |
| POST | `/api/manager/performance-reviews/{id}/feedback` | Submit feedback |
| POST | `/api/manager/announcements` | Team announcement |
| GET | `/api/manager/notifications` | Notifications |

---

### 👤 Employee — `/api/employee`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/employee/dashboard` | Dashboard |
| GET | `/api/employee/profile` | My profile |
| PUT | `/api/employee/profile` | Update profile |
| GET | `/api/employee/manager` | Reporting manager |
| GET | `/api/employee/leaves/balance` | Leave balance |
| GET | `/api/employee/leaves/balance/{typeId}` | Balance by type |
| GET | `/api/employee/leaves/applications` | My applications |
| GET | `/api/employee/leaves/applications/{id}` | Detail |
| POST | `/api/employee/leaves/apply` | Apply for leave |
| DELETE | `/api/employee/leaves/applications/{id}/cancel` | Cancel |
| GET | `/api/employee/goals` | My goals |
| GET | `/api/employee/goals/{id}` | Goal detail |
| POST | `/api/employee/goals` | Create goal |
| PUT | `/api/employee/goals/{id}/progress` | Update progress |
| GET | `/api/employee/performance-reviews` | My reviews |
| POST | `/api/employee/performance-reviews` | Submit self review |
| GET | `/api/employee/performance-reviews/{id}/feedback` | Manager feedback |
| GET | `/api/employee/announcements` | Announcements |
| GET | `/api/employee/announcements/{id}` | Detail |
| GET | `/api/employee/holidays` | Holiday calendar |
| GET | `/api/employee/directory` | Directory |
| GET | `/api/employee/directory/search?search=` | Search |
| GET | `/api/employee/notifications` | Notifications |
| GET | `/api/employee/notifications/unread-count` | Unread count |
| PUT | `/api/employee/notifications/{id}/read` | Mark read |
| PUT | `/api/employee/notifications/all/read` | Mark all read |
| GET | `/api/employee/qrcode` | QR code |
| GET | `/api/employee/report/download` | PDF report |

---

## 🔒 Role-Based Access

| Feature | Admin | Manager | Employee |
|---------|:-----:|:-------:|:--------:|
| Employee CRUD | ✅ | ❌ | ❌ |
| Department / Designation Management | ✅ | ❌ | ❌ |
| Holiday Management | ✅ | ❌ | ❌ |
| Leave Type Management | ✅ | ❌ | ❌ |
| Approve / Reject Leaves | ✅ | ✅ | ❌ |
| Apply for Leave | ❌ | ❌ | ✅ |
| Assign Goals to Team | ❌ | ✅ | ❌ |
| Create Personal Goals | ❌ | ❌ | ✅ |
| Submit Performance Self Review | ❌ | ❌ | ✅ |
| Give Manager Feedback & Rating | ❌ | ✅ | ❌ |
| Create Announcements | ✅ | ✅ (team) | ❌ |
| View Announcements | ✅ | ✅ | ✅ |
| Employee Directory | ❌ | ❌ | ✅ |
| Excel / PDF Reports | ✅ | ❌ | ✅ (own) |
| Audit Logs | ✅ | ❌ | ❌ |
| QR Code | ✅ (all) | ❌ | ✅ (own) |

---

## 🗄 Database Schema

```
users                — Employee/user data (role: ADMIN / MANAGER / EMPLOYEE)
departments          — Company departments
designations         — Job titles/designations
leave_types          — Leave categories (Casual, Sick, Paid, etc.)
leave_balances       — Per-user quota and used days
leave_applications   — Leave requests (PENDING / APPROVED / REJECTED)
leave_requests       — Extended leave request entity
holidays             — Holiday calendar (optional / mandatory)
announcements        — Company and team announcements
employee_goals       — Goals with priority & completion status
performance_reviews  — Self reviews (DRAFT / SUBMITTED / COMPLETED)
manager_feedback     — Manager ratings and written feedback
notifications        — In-app notification system
audit_logs           — Full system activity trail (who did what, when)
```

---

## ⚙️ Environment Config

### Backend — `application.properties`
```properties
spring.application.name=revworkinghrm
server.port=8080
server.servlet.context-path=/rev

spring.datasource.url=jdbc:mysql://localhost:3306/revworkinghrm
spring.datasource.username=root
spring.datasource.password=YOUR_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

jwt.secret=your_jwt_secret_minimum_32_characters_here
jwt.expiration=86400000

springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

### Frontend — `environment.ts`
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/rev'
};
```

---

## 👨‍💻 Author

Built with ❤️ using **Spring Boot 3** + **Angular 21**

---

## 📄 License

This project is licensed under the **MIT License**.
