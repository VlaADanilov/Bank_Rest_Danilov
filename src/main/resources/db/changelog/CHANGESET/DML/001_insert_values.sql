INSERT INTO users(id, first_name, last_name, patronymic_name,
       username, password, role)
VALUES ('f6faf60e-5f25-4fa0-a912-2b288ddd4a44', 'Danilov', 'Vladislav', 'Alekseevich',
       'test', '$2a$10$j.DfDZdEVif2bcpJASgRf.UsDtCJ1TBbMBuabwh8HIj4y0Yge3rpK', 'USER'),
       ('b941d894-4020-44d2-a5b1-c243d8fdeb2f', 'Ivanov', 'Ivan', 'Ivanovich',
       '1234', '$2a$10$j.DfDZdEVif2bcpJASgRf.UsDtCJ1TBbMBuabwh8HIj4y0Yge3rpK', 'USER'),
       ('9c1b1209-485c-4137-bb1b-463c1ce94e10', 'Adminov', 'Admin', 'Ivanovich',
       'admin', '$2a$10$j.DfDZdEVif2bcpJASgRf.UsDtCJ1TBbMBuabwh8HIj4y0Yge3rpK', 'ADMIN');

INSERT INTO card(id, balance, card_number, expiry_date,
                 status, user_id)
VALUES ('d342922a-4edd-4906-8858-3ac3c136ea18', 1000,'1234567812345678',
        '27-08-2025', 'ACTIVE', 'f6faf60e-5f25-4fa0-a912-2b288ddd4a44'
        ),
       ('1af06f47-1505-4259-8a19-1e4175adf232', 1200,'1234567812345679',
        '28-08-2025', 'ACTIVE', 'f6faf60e-5f25-4fa0-a912-2b288ddd4a44'
       ),
       ('02d8a2a6-81f8-4c58-9f8b-d854032ad061', 1200,'1234567812345688',
        '28-08-2025', 'BLOCKED', 'f6faf60e-5f25-4fa0-a912-2b288ddd4a44'
       ),
       ('0d3f7ac1-a9a8-4e0b-b53a-0251e1325bed', 1200,'1234567812345618',
        '23-07-2025', 'EXPIRED', 'f6faf60e-5f25-4fa0-a912-2b288ddd4a44'
       ),
       ('5f441056-d5c0-4d97-bac6-d5c1275628cc', 1000,'1234567812345600',
        '27-08-2025', 'ACTIVE', 'b941d894-4020-44d2-a5b1-c243d8fdeb2f'
       ),
       ('893970c6-efb0-4307-8ef3-d1bd804cc8d0', 1200,'1234567812345999',
        '28-08-2025', 'ACTIVE', 'b941d894-4020-44d2-a5b1-c243d8fdeb2f'
       );

INSERT INTO request_to_block(id, card_id)
VALUES ('a0ad2efa-52c0-48ea-a91c-b7803d9df1d2', '1af06f47-1505-4259-8a19-1e4175adf232')
