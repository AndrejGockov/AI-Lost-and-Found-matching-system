INSERT INTO auth_user (id, alias, name, email, role, active)
VALUES
    ('student', NULL, 'Student', 'student@finki.ukim.mk', 'STUDENT', TRUE),
    ('professor', NULL, 'Professor', 'professor@finki.ukim.mk', 'PROFESSOR', TRUE)
ON CONFLICT (id) DO NOTHING;

-- Insert professor record for the professor user
INSERT INTO professor (id, title, ordering_rank, office_name, active, email, name)
VALUES ('professor', 'PROFESSOR', 1, NULL, TRUE, 'professor@finki.ukim.mk', 'Professor')
ON CONFLICT (id) DO NOTHING;