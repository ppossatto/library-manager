INSERT INTO USERS (ID, NAME, EMAIL, PHONE, PASSWORD, STATUS)
VALUES (
           '0b592ed4-6efe-49de-bb9b-8413ef564812',
           'John Doe',
           'john.doe@email.com',
           '123456789',
           '$2a$12$AFbCuWlS4bRzkkKFWxL.weaxLHrPq7uQSW02Bn7PcWYH.yMNlbqi2',
           'active'
       );

INSERT INTO ROLES (NAME) VALUES ('ROLE_USER');

INSERT INTO USER_ROLES (ROLE_ID, USER_ID)
VALUES (
           (SELECT id FROM roles WHERE name = 'ROLE_USER'),
           '0b592ed4-6efe-49de-bb9b-8413ef564812'
       );