INSERT INTO PUBLIC.BOOKS ( TITLE, ISBN, PUBLISH_YEAR, EDITION, SYNOPSIS, TOTAL_PAGES )
VALUES (
        'Lord of the Rings',
        '9780261102439',
        1992,
        'HarperCollins',
        'Continuing the story of The Hobbit',
        1198
        );

INSERT INTO PUBLIC.BOOKS (TITLE, ISBN, PUBLISH_YEAR, EDITION, SYNOPSIS, TOTAL_PAGES)
VALUES (
        'Harry Potter and the Sorcerer'' Stone',
        '9780590353427',
        1998,
        'Scholastic',
        'Harry Potter has never been the star of a Quidditch team, scoring points while riding a broom far above the ground.',
        320
       );

INSERT INTO PUBLIC.AUTHORS (ID, NAME, BIRTH_DATE, NATIONALITY, BIOGRAPHY)
VALUES (
        'fb1c26ad-a614-49b9-916e-c49aeeef28b7',
        'J. R. R. Tolkien',
        '1892-01-03',
        'South African',
        'https://en.wikipedia.org/wiki/J._R._R._Tolkien#Biography'
       );

INSERT INTO PUBLIC.AUTHORS (ID, NAME, BIRTH_DATE, NATIONALITY, BIOGRAPHY)
VALUES (
        '4be84d9a-dff1-4e6a-81e5-cd824029d5ee',
        'J. K. Rowling',
        '1965-07-31',
        'British',
        'https://en.wikipedia.org/wiki/J._K._Rowling'
       );

INSERT INTO PUBLIC.BOOK_AUTHOR (BOOK_ID, AUTHOR_ID)
VALUES (
        (SELECT id FROM books WHERE isbn = '9780261102439'),
        'fb1c26ad-a614-49b9-916e-c49aeeef28b7'
       );

INSERT INTO PUBLIC.BOOK_AUTHOR (BOOK_ID, AUTHOR_ID)
VALUES (
        (SELECT id FROM books WHERE isbn = '9780590353427'),
        '4be84d9a-dff1-4e6a-81e5-cd824029d5ee'
       );

INSERT INTO PUBLIC.USERS (ID, NAME, EMAIL, PHONE, PASSWORD)
VALUES (
        '0b592ed4-6efe-49de-bb9b-8413ef564812',
        'John Doe',
        'john.doe@email.com',
        '123456789',
        '$2a$12$AFbCuWlS4bRzkkKFWxL.weaxLHrPq7uQSW02Bn7PcWYH.yMNlbqi2'
       );

INSERT INTO PUBLIC.RESERVATIONS (BOOK_ID, USER_ID, EXPECTED_DEVOLUTION_DATE, DEVOLUTION_DATE, OBSERVATIONS)
VALUES (
        (SELECT id FROM books WHERE isbn = '9780261102439'),
        '0b592ed4-6efe-49de-bb9b-8413ef564812',
        '2026-06-15',
        null,
        null
       );

INSERT INTO PUBLIC.USER_ROLES (ROLE_ID, USER_ID)
VALUES (
        (SELECT id FROM roles WHERE name = 'ROLE_USER'),
        '0b592ed4-6efe-49de-bb9b-8413ef564812'
       );