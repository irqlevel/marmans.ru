CREATE TABLE IF NOT EXISTS Sessions(value VARCHAR(255) PRIMARY KEY NOT NULL, uid BIGINT UNIQUE NOT NULL, csrfToken VARCHAR(255) UNIQUE NOT NULL, expires BIGINT NOT NULL);
CREATE TABLE IF NOT EXISTS Users(uid BIGINT PRIMARY KEY NOT NULL, name VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL UNIQUE, hashp VARCHAR(255) NOT NULL);
CREATE TABLE IF NOT EXISTS Posts(postId BIGINT PRIMARY KEY NOT NULL, uid BIGINT, title VARCHAR(255) NOT NULL, content TEXT NOT NULL, creationTime BIGINT);
