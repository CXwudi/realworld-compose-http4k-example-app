-- V999__insert_test_data.sql
INSERT INTO users (email, username, password, bio, image)
VALUES
    ('john@example.com', 'john', '$2a$10$8BxPYyqz2.X5TKk2R6gGIeZ.Tkk2KmcC6yN1BxLLGCgMgMNEZpgjK', 'Hi, I''m John!', 'https://api.realworld.io/images/john.jpg'),
    ('jane@example.com', 'jane', '$2a$10$8BxPYyqz2.X5TKk2R6gGIeZ.Tkk2KmcC6yN1BxLLGCgMgMNEZpgjK', 'Hi, I''m Jane!', 'https://api.realworld.io/images/jane.jpg'),
    ('bob@example.com', 'bob', '$2a$10$8BxPYyqz2.X5TKk2R6gGIeZ.Tkk2KmcC6yN1BxLLGCgMgMNEZpgjK', NULL, NULL),
    ('alice@example.com', 'alice', '$2a$10$8BxPYyqz2.X5TKk2R6gGIeZ.Tkk2KmcC6yN1BxLLGCgMgMNEZpgjK', 'Software developer', 'https://api.realworld.io/images/alice.jpg'),
    ('test@test.com', 'test', '$2a$10$8BxPYyqz2.X5TKk2R6gGIeZ.Tkk2KmcC6yN1BxLLGCgMgMNEZpgjK', NULL, NULL);
