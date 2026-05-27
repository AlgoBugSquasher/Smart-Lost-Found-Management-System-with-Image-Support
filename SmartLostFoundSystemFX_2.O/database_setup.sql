-- MySQL Database Setup Script for Smart Lost & Found System
-- Run this script in MySQL Workbench or via command line

-- Create the database
CREATE DATABASE IF NOT EXISTS lostfound;
USE lostfound;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert a default admin user (password: admin123)
INSERT INTO users (name, email, password, role) 
VALUES ('Administrator', 'admin@system.com', 'admin123', 'ADMIN');

-- Insert a sample regular user for testing
INSERT INTO users (name, email, password, role)
VALUES ('Test User', 'user@test.com', 'user', 'USER');

-- Verify the setup
SELECT * FROM users;

-- Create items table for storing lost/found items
CREATE TABLE IF NOT EXISTS items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(200) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    date DATE,
    type VARCHAR(20) NOT NULL, -- 'Lost' or 'Found'
    image_path VARCHAR(500),
    user_id INT NULL,
    user_email VARCHAR(100) NOT NULL, -- Foreign key using user's email
    status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'FOUND', 'CLAIMED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_email) REFERENCES users(email) ON DELETE CASCADE
);

-- Add user_email column to existing items table (run this if table exists)
ALTER TABLE items 
    ADD COLUMN IF NOT EXISTS user_email VARCHAR(100) NOT NULL DEFAULT 'user@test.com';