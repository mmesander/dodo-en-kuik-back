-- Users
INSERT INTO users (username, password, email)
VALUES ('mmesander', '$2a$12$RfLgBdcAj/9o/XkMZNm.Zerka9oTm3WRp5nm5rkPg/G5mwVQftbzq', 'mark@test.nl'),

-- Authorities
INSERT INTO authorities (username, authority)
VALUES ('mmesander', 'ROLE_ADMIN'),
       ('mmesander', 'ROLE_USER');