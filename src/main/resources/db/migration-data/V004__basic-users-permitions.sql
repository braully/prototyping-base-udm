insert into security.user_login(id, active, user_name, password_type, password) values 
(1, true, 'admin','SHA-512', 'x61Ey612Kl2gpFL56FT9weDnpSo4AV8j8+qx2AuTHdRyY036xxzTTrw10Wq3+4qQyB+XURPWx1ONxp3Y3pB37A=='),
(2, true, 'manager', 'SHA-512', 'X8LKbwhZGfL3dibx4oD6ucyStO3J7cU6xu7j9yxcUI6GnunWepbWOYbRTBwrgsNf9fMUlL6oMQFUJPWclv/2ZA=='),
(3, true, 'user', 'SHA-512', 'sUNhQEwHj/1UnAPbRDw/7eLz5TTXP3j3cwHtl9SkNqn9nbBe6LMlwK02Q4tD/shRDCBPwcHtsh0JQcAOniwc4g==');


insert into security.user_login_role(fk_user_login, fk_roles) values 
(1,1), (2,2), (3,3);
