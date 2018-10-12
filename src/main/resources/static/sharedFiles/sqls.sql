CREATE TABLE matsim.users
(save_url text,
temp_url text,
save_block_number int,
user_level int,
allowed_block_list text,
user_email text,
user_password text ,
other text,
user_login_time timestamp ,
user_name text,
user_register_time timestamp,
user_id int auto_increment primary key);

CREATE TABLE matsim.saved_workspace
(
    save_name text ,
    other text ,
    save_time timestamp NOT NULL,
    user_id integer NOT NULL,
    save_content text ,
    save_info text ,
    save_id int auto_increment primary key
);
set session sql_mode='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';


java -jar matsim-0.0.1-SNAPSHOT.jar > log.file 2>&1 &
