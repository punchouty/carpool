select * from _user;
select * from login_record;
delete from login_record where owner_id in (select id from _user where username like '%rajan@racloop.com%');
delete from _user_passwd_history where user_base_id in (select id from _user where username like '%rajan@racloop.com%');
delete from _role_users where user_base_id in (select id from _user where username like '%rajan@racloop.com%');
delete from _group_users where user_base_id in (select id from _user where username like '%rajan@racloop.com%');
delete from permission where user_id in (select id from _user where username like '%rajan@racloop.com%');
delete from profile_base where email like '%rajan@racloop.com%';
delete from _user where username like '%rajan@racloop.com%';
