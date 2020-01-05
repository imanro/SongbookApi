INSERT INTO user(id, email, first_name, last_name, create_time, update_time) VALUES(1, 'manro@localhost.com', 'Roman', 'Manro', NOW(), NOW());

INSERT INTO song(id, author,copyright,create_time,update_time,title) VALUES(1, 'Billy Funk', '&copy; 1991 Integrity''s Praise! Music/BMI', NOW(), NOW(), 'Let There Be Joy');
INSERT INTO song(id, author,copyright,create_time,update_time,title) VALUES(2, 'As I Praise And Worship You', '&copy; 1989 Integrity''s Hosanna! Music/ASCAP', NOW(), NOW(), 'As I Praise And Worship You');

INSERT INTO song_content(type,song_id,user_id,content, is_favorite) VALUES('HEADER', 1, 1, 'И в дом Господень', 1);
INSERT INTO song_content(type,song_id,user_id,content, is_favorite) VALUES('HEADER', 2, 1, 'Как прославлю', 1);