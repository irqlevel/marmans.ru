CREATE TABLE IF NOT EXISTS Sessions(value VARCHAR(255) PRIMARY KEY NOT NULL, uid BIGINT UNIQUE NOT NULL, csrfToken VARCHAR(255) UNIQUE NOT NULL, expires BIGINT NOT NULL);
CREATE TABLE IF NOT EXISTS Users(uid BIGINT PRIMARY KEY NOT NULL, name VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL UNIQUE, hashp VARCHAR(255) NOT NULL, avatarId BIGINT NOT NULL,
thumbnailId BIGINT NOT NULL);
CREATE TABLE IF NOT EXISTS Posts(postId BIGINT PRIMARY KEY NOT NULL, uid BIGINT, title VARCHAR(255) NOT NULL, content TEXT NOT NULL, creationTime BIGINT, imageId BIGINT NOT NULL, active INTEGER NOT NULL, youtubeLinkId VARCHAR(20));
CREATE TABLE IF NOT EXISTS Comments(commentId BIGINT PRIMARY KEY NOT NULL, uid BIGINT NOT NULL, parentId BIGINT NOT NULL, postId BIGINT NOT NULL, content TEXT NOT NULL, creationTime BIGINT NOT NULL, imageId BIGINT NOT NULL);
CREATE TABLE IF NOT EXISTS Images(imageId BIGINT PRIMARY KEY NOT NULL, uid BIGINT NOT NULL, title VARCHAR(255) NOT NULL,  content TEXT NOT NULL, creationTime BIGINT NOT NULL, url TEXT NOT NULL, s3bucket VARCHAR(255) NOT NULL, s3key VARCHAR(255) NOT NULL, width BIGINT NOT NULL, height BIGINT NOT NULL, fileBaseName VARCHAR(255) NOT NULL, fileExtension VARCHAR(255) NOT NULL, fileSize BIGINT NOT NULL, fileName VARCHAR(255) NOT NULL, fileType VARCHAR(255));
