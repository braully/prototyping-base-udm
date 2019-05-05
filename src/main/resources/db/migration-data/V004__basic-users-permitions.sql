insert into security.user_login(id, active, user_name, password_type, password) values 
(1, true, 'admin','BCryptPasswordEncoder', '$2a$10$EbTxuBEAg88pq0jfRu2F8.egYmWGnQMsP9hdinExSYN/NByIxuV56'),
(2, true, 'manager', 'BCryptPasswordEncoder', '$2a$10$NcBH8iqa6I5R3EMMSG9lO.U8aNq7aW4Azi330ONo.YMWYQnHinh2m'),
(3, true, 'user', 'BCryptPasswordEncoder', '$2a$10$paDrFJFhSYA.5z382NAdue9SyYAEaOcID/qMC.bxdpGw6BDc7Kd3C');


insert into security.user_login_role(fk_user_login, fk_roles) values 
(1,1), (2,2), (3,3);
