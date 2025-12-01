-- Generated on: 2025-12-01T16:52:13.673439100

-- Solution for Question 2 (even regNo)
SELECT e.employee_id, e.name
FROM employees e
WHERE e.salary > (SELECT AVG(salary) FROM employees);