export interface User {
  id: number;
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  role: 'ADMIN' | 'MANAGER' | 'EMPLOYEE';
  active: string;
  phoneNumber?: string;
  address?: string;
  salary?: number;
  joiningDate?: string;
  department?: Department;
  designation?: Designation;
  manager?: User;
  // flat fields from backend
  departmentId?: number;
  departmentName?: string;
  designationId?: number;
  designationName?: string;
  managerId?: number;
  managerName?: string;
}

export interface Department {
  id: number;
  name: string;
  description?: string;
}

export interface Designation {
  id: number;
  name: string;
  description?: string;
}

export interface LeaveType {
  id: number;
  name: string;
  description?: string;
  defaultQuota: number;
  isActive: boolean;
}

export interface LeaveApplication {
  id: number;
  employee?: User;
  leaveType?: LeaveType;
  startDate: string;
  endDate: string;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';
  appliedDate?: string;
  approvedBy?: User;
  comment?: string;
  // flat fields from backend
  userId?: number;
  employeeName?: string;
  leaveTypeId?: number;
  leaveTypeName?: string;
  approverName?: string;
  approvalComment?: string;
  numberOfDays?: number;
}

export interface LeaveBalance {
  leaveType: LeaveType;
  totalQuota: number;
  usedDays: number;
  remainingDays: number;
}

export interface Holiday {
  id: number;
  name: string;
  description?: string;
  holidayDate: string;
  isOptional: boolean;
}

export interface PerformanceReview {
  id: number;
  employee?: User;
  keyDeliverables?: string;
  accomplishments?: string;
  areasOfImprovement?: string;
  employeeSelfRating?: number;
  status: 'DRAFT' | 'SUBMITTED' | 'UNDER_REVIEW' | 'COMPLETED';
  createdAt?: string;
  updatedAt?: string;
  managerFeedback?: ManagerFeedback;
}

export interface ManagerFeedback {
  id: number;
  feedback: string;
  managerRating: number;
  manager?: User;
  createdAt?: string;
}

export interface Goal {
  id: number;
  employee?: User;
  goalDescription: string;
  deadline: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  progressPercentage?: number;
  progressComment?: string;
  managerComment?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Announcement {
  id: number;
  title: string;
  content: string;
  isActive: boolean;
  createdBy?: User;
  createdAt?: string;
}

export interface Notification {
  id: number;
  message: string;
  isRead: boolean;
  createdAt?: string;
}

export interface DashboardStats {
  totalEmployees?: number;
  totalDepts?: number;
  leaveStats?: any;
  goalStats?: any;
  activeAnnouncements?: number;
  teamSize?: number;
  pendingLeaveRequests?: number;
  pendingLeaves?: number;
  approvedLeaves?: number;
  rejectedLeaves?: number;
  pendingReviews?: number;
  leaveBalance?: LeaveBalance[];
}

export interface AuthResponse {
  token: string;
  role: string;
  userId: number;
  email: string;
}

export interface ApiResponse<T> {
  data?: T;
  message?: string;
  success?: boolean;
}