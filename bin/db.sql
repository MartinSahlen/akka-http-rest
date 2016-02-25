CREATE ROLE akka LOGIN;
CREATE USER akkalogin;
ALTER ROLE akkalogin WITH PASSWORD 'akka';
CREATE DATABASE akkablog OWNER akkalogin;
