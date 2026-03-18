CREATE DATABASE IF NOT EXISTS ipos_pu;
USE ipos_pu;

CREATE TABLE CommercialApplicant (
                                     ApplicationID INT AUTO_INCREMENT PRIMARY KEY,
                                     CompanyName VARCHAR(255),
                                     CompanyRegistrationNumber VARCHAR(20),
                                     BusinessType VARCHAR(50),
                                     Email VARCHAR(255),
                                     ApplicationDate DATE,
                                     ApplicationStatus VARCHAR(50),
                                     NotificationPreference VARCHAR(50),
                                     CompanyAddress VARCHAR(255)
);

CREATE TABLE CompanyDirector (
                                 DirectorID INT AUTO_INCREMENT PRIMARY KEY,
                                 ApplicationID INT,
                                 FirstName VARCHAR(100),
                                 LastName VARCHAR(100),
                                 PhoneNumber VARCHAR(20),
                                 FOREIGN KEY (ApplicationID) REFERENCES CommercialApplicant(ApplicationID)
);

CREATE TABLE NonCommercialMember (
                                     MemberID INT AUTO_INCREMENT PRIMARY KEY,
                                     Email VARCHAR(255),
                                     Password VARCHAR(255),
                                     MustChangePassword BOOLEAN,
                                     TotalOrders INT,
                                     CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);