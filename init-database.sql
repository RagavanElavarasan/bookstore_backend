-- Clean up duplicate roles and ensure proper data structure
USE online_bookstore;


-- Insert unique roles
INSERT INTO roles (name) VALUES ('ROLE_CUSTOMER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Verify the roles
SELECT * FROM roles;
