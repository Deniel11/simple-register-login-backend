DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    dateOfBirth VARCHAR(255),
    admin BIT NOT NULL,
    verified BIT NOT NULL,
    enabled BIT NOT NULL,
    verification_token VARCHAR(255),
    forgot_password_token VARCHAR(255),
    forgot_password_request_time BIGINT,
    primary key (id)
)