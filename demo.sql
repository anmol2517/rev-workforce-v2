CREATE DATABASE revworkinghrm;
use revworkinghrm;
SELECT * FROM users WHERE role = 'ADMIN';
SELECT * FROM users WHERE role = 'Manager';
SELECT * FROM users WHERE role = 'Employee';

DESCRIBE users;

SHOW TABLES;

select * from users;
SELECT * FROM departments;
SELECT * FROM designations;
select * from holidays;
SELECT * FROM announcements ORDER BY created_at DESC;
SELECT employee_id, first_name, last_name, email, active FROM users WHERE active = 'Working';
SELECT active, COUNT(*) as total_count FROM users GROUP BY active;