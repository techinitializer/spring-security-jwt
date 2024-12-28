-- Insert Roles (make sure these are unique)
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Insert Users (ensure usernames are unique)
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$/1hKqrGQkCNKzEQfiqflfuZ2G2QuNlwpHn3FoKhdV1Tv/tMmUQAOi'); -- Password: password (BCrypt)

-- Link users and roles (user_id and role_id must exist)
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1); -- User 'admin' has ROLE_ADMIN
