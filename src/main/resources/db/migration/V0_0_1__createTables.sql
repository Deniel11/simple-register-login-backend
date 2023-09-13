DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    dateOfBirth VARCHAR(255),
    admin BIT NOT NULL,
    valid BIT NOT NULL,
    primary key (id)
)