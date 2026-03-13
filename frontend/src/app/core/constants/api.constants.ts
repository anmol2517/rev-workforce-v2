export const API_ROUTES = {
  AUTH: {
    LOGIN: '/auth/login'
  },
  ADMIN: {
    DASHBOARD: '/api/admin/dashboard/stats',
    EMPLOYEES: '/api/admin/employees',
    EMPLOYEE_BY_ID: (id: number) => `/api/admin/employees/${id}`,
    EMPLOYEE_SEARCH: '/api/admin/employees/search',
    EMPLOYEE_ACTIVATE: (id: number) => `/api/admin/employees/${id}/activate`,
    EMPLOYEE_DEACTIVATE: (id: number) => `/api/admin/employees/${id}/deactivate`,
    EMPLOYEE_ASSIGN_MANAGER: (empId: number, managerId: number) => `/api/admin/employees/${empId}/assign-manager/${managerId}`,
    DEPARTMENTS: '/api/admin/departments',
    DEPARTMENT_BY_ID: (id: number) => `/api/admin/departments/${id}`,
    DESIGNATIONS: '/api/admin/designations',
    DESIGNATION_BY_ID: (id: number) => `/api/admin/designations/${id}`,
    LEAVES_ALL: '/api/admin/leaves/all',
    LEAVES_TYPES: '/api/admin/leaves/types',
    LEAVE_TYPE_BY_ID: (id: number) => `/api/admin/leaves/types/${id}`,
    LEAVES_UTILIZATION: '/api/admin/leaves/utilization',
    LEAVE_APPROVE: (id: number) => `/api/admin/leaves/applications/${id}/approve`,
    LEAVE_REJECT: (id: number) => `/api/admin/leaves/applications/${id}/reject`,
    HOLIDAYS: '/api/admin/holidays/all',
    HOLIDAY_BY_ID: (id: number) => `/api/admin/holidays/${id}`,
    HOLIDAY_ADD: '/api/admin/holidays/add',
    HOLIDAY_BULK_ADD: '/api/admin/holidays/bulk-add',
    ANNOUNCEMENTS: '/api/admin/announcements',
    REPORTS_EXCEL: '/api/admin/reports/employees/excel',
    REPORT_PDF: (id: number) => `/api/admin/reports/employee/${id}/pdf`,
    LEAVES_TYPES_BULK: '/api/admin/leaves/types/bulk-create'
  },
  LEAVES: {
    TYPES: '/api/leaves/types',
    TYPES_ACTIVE: '/api/leaves/types/active',
    BALANCE: (userId: number) => `/api/leaves/balance/${userId}`,
    APPLICATIONS: (userId: number) => `/api/leaves/applications/${userId}`,
    PENDING_TEAM: '/api/leaves/applications/pending/team',
    HOLIDAYS: '/api/leaves/holidays',
    APPLY: '/api/leaves/apply',
    BALANCE_ASSIGN: '/api/leaves/balance/assign',
    BALANCE_ADJUST: '/api/leaves/balance/adjust',
    BULK_ASSIGN: '/api/leaves/bulk-assign',
    APPROVE: (id: number) => `/api/leaves/${id}/approve`,
    REJECT: (id: number) => `/api/leaves/${id}/reject`,
    CANCEL: (id: number) => `/api/leaves/${id}/cancel`
  },
  MANAGER: {
    PROFILE: '/api/manager/profile',
    DASHBOARD: '/api/manager/dashboard/stats',
    TEAM: '/api/manager/team-members',
    TEAM_MEMBER: (id: number) => `/api/manager/team-members/${id}`,
    LEAVES: '/api/manager/leaves/applications',
    LEAVE_BY_ID: (id: number) => `/api/manager/leaves/applications/${id}`,
    LEAVE_APPROVE: (id: number) => `/api/manager/leaves/applications/${id}/approve`,
    LEAVE_REJECT: (id: number) => `/api/manager/leaves/applications/${id}/reject`,
    TEAM_CALENDAR: '/api/manager/leaves/team-calendar',
    TEAM_BALANCE: '/api/manager/leaves/team-balance',
    REVIEWS: '/api/manager/performance-reviews',
    REVIEW_BY_ID: (id: number) => `/api/manager/performance-reviews/${id}`,
    REVIEW_FEEDBACK: (id: number) => `/api/manager/performance-reviews/${id}/feedback`,
    GOALS: '/api/manager/goals',
    GOAL_BY_ID: (id: number) => `/api/manager/goals/${id}`,
    GOAL_COMMENT: (id: number) => `/api/manager/goals/${id}/comments`,
    NOTIFICATIONS: '/api/manager/notifications',
    ANNOUNCEMENTS: '/api/manager/announcements'
  },
  EMPLOYEE: {
    DASHBOARD: '/api/employee/dashboard',
    PROFILE: '/api/employee/profile',
    MANAGER: '/api/employee/manager',
    LEAVE_BALANCE: '/api/employee/leaves/balance',
    LEAVE_APPLICATIONS: '/api/employee/leaves/applications',
    LEAVE_BY_ID: (id: number) => `/api/employee/leaves/applications/${id}`,
    LEAVE_APPLY: '/api/employee/leaves/apply',
    LEAVE_CANCEL: (id: number) => `/api/employee/leaves/applications/${id}/cancel`,
    HOLIDAYS: '/api/employee/holidays',
    REVIEWS: '/api/employee/performance-reviews',
    REVIEW_BY_ID: (id: number) => `/api/employee/performance-reviews/${id}`,
    REVIEW_FEEDBACK: (id: number) => `/api/employee/performance-reviews/${id}/feedback`,
    GOALS: '/api/employee/goals',
    GOAL_BY_ID: (id: number) => `/api/employee/goals/${id}`,
    GOAL_PROGRESS: (id: number) => `/api/employee/goals/${id}/progress`,
    ANNOUNCEMENTS: '/api/employee/announcements',
    ANNOUNCEMENT_BY_ID: (id: number) => `/api/employee/announcements/${id}`,
    DIRECTORY: '/api/employee/directory',
    DIRECTORY_SEARCH: '/api/employee/directory/search',
    NOTIFICATIONS: '/api/employee/notifications',
    NOTIFICATIONS_UNREAD: '/api/employee/notifications/unread-count',
    NOTIFICATION_READ: (id: number) => `/api/employee/notifications/${id}/read`,
    NOTIFICATIONS_ALL_READ: '/api/employee/notifications/all/read',
    QRCODE: '/api/employee/qrcode',
    REPORT: '/api/employee/report/download'
  }
};

export const ROLES = {
  ADMIN: 'ADMIN',
  MANAGER: 'MANAGER',
  EMPLOYEE: 'EMPLOYEE'
} as const;

export const STORAGE_KEYS = {
  TOKEN: 'rw_token',
  USER: 'rw_user'
} as const;