CREATE TABLE challenge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(4000) NOT NULL,
    period NUMBER(3) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(10) NOT NULL,
    owner_id INT NOT NULL,
    sent_notification BOOLEAN,
    created_by VARCHAR(50),
    created_at DATETIME,
    last_modified_by VARCHAR(50),
    last_modified_at DATETIME
);

CREATE TABLE daily (
    id INT AUTO_INCREMENT PRIMARY KEY,
    challenge_id INT NOT NULL,
    owner_id INT NOT NULL,
    days NUMBER(3) NOT NULL,
    completed_at BOOLEAN,
    comment VARCHAR(2048),
    created_by VARCHAR(50),
    created_at DATETIME,
    last_modified_by VARCHAR(50),
    last_modified_at DATETIME
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    refresh_token VARCHAR(2048),
    activated_at BOOLEAN,
    created_by VARCHAR(50),
    created_at DATETIME,
    last_modified_by VARCHAR(50),
    last_modified_at DATETIME
);