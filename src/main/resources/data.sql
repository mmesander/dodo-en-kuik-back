-- Users
INSERT INTO users (username, password, email)
VALUES ('MMESANDER', '$2a$12$RfLgBdcAj/9o/XkMZNm.Zerka9oTm3WRp5nm5rkPg/G5mwVQftbzq', 'mark@test.nl');

-- Authorities
INSERT INTO authorities (username, authority)
VALUES ('MMESANDER', 'ROLE_ADMIN'),
       ('MMESANDER', 'ROLE_USER');