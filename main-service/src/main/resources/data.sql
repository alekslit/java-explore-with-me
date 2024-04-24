-- тестовые данные для postman тестов (EWM feature_comments):

-- userId = 1:
INSERT INTO users (name, email)
VALUES('user1_test', 'user1@test.com');

-- userId = 2:
INSERT INTO users (name, email)
VALUES('user2_test', 'user2@test.com');

-- userId = 3:
INSERT INTO users (name, email)
VALUES('user3_test', 'user3@test.com');

-- userId = 4:
INSERT INTO users (name, email)
VALUES('user4_test', 'user4@test.com');

-- catId = 1:
INSERT INTO categories (name)
VALUES('category1_test');

-- eventId = 1:
INSERT INTO events (annotation, category_id, confirmed_requests, creation_date, description, event_date, user_id,
                   location_lat, location_lon, paid, participant_limit, published_date, request_moderation,
                   event_status, title, views, available, comments_count)
VALUES('event_annotation1_test', 1, 0, '2024-01-01 00:00:00', 'event_description1_test', '2024-02-01 00:00:00', 1,
       10.10, 20.20, false, 0, '2024-01-05 00:00:00', false, 'PUBLISHED', 'title1_test', 0, true, 0);

-- eventId = 2 (неопубликованное событие):
INSERT INTO events (annotation, category_id, confirmed_requests, creation_date, description, event_date, user_id,
                   location_lat, location_lon, paid, participant_limit, request_moderation,
                   event_status, title, views, available, comments_count)
VALUES('event_annotation2_test', 1, 0, '2024-01-01 00:00:00', 'event_description2_test', '2024-03-01 00:00:00', 2,
       11.10, 21.20, false, 0, false, 'PENDING', 'title2_test', 0, true, 0);

-- eventId = 3:
INSERT INTO events (annotation, category_id, confirmed_requests, creation_date, description, event_date, user_id,
                   location_lat, location_lon, paid, participant_limit, published_date, request_moderation,
                   event_status, title, views, available, comments_count)
VALUES('event_annotation3_test', 1, 0, '2024-01-15 00:00:00', 'event_description3_test', '2024-02-05 00:00:00', 3,
       12.10, 22.20, false, 0, '2024-01-20 00:00:00', false, 'PUBLISHED', 'title3_test', 0, true, 0);

-- eventId = 4:
INSERT INTO events (annotation, category_id, confirmed_requests, creation_date, description, event_date, user_id,
                   location_lat, location_lon, paid, participant_limit, published_date, request_moderation,
                   event_status, title, views, available, comments_count)
VALUES('event_annotation4_test', 1, 0, '2024-01-16 00:00:00', 'event_description4_test', '2024-02-10 00:00:00', 4,
       13.10, 23.20, false, 0, '2024-01-21 00:00:00', false, 'PUBLISHED', 'title4_test', 0, true, 0);

-- commentId = 1 (старый комментарий, прошло больше 24 часов с момента создания):
INSERT INTO comments (text, user_id, event_id, creation_date)
VALUES('text_comment4', 1, 1, '2024-01-10 00:00:00');