-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: study_hub_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `CustomerID` int NOT NULL AUTO_INCREMENT,
  `StudentID` varchar(30) DEFAULT NULL,
  `GovID` varchar(50) DEFAULT NULL,
  `IDType` varchar(50) DEFAULT NULL,
  `FirstName` varchar(100) NOT NULL,
  `LastName` varchar(100) NOT NULL,
  `Email` varchar(150) NOT NULL,
  `MobileNumber` varchar(20) DEFAULT NULL,
  `University` varchar(150) DEFAULT NULL,
  `StreetAddress` varchar(255) DEFAULT NULL,
  `City` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`CustomerID`),
  UNIQUE KEY `Email` (`Email`),
  UNIQUE KEY `StudentID` (`StudentID`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'20260001','GOV10001','Passport','Alex','Santos','alex.santos@example.com','09170000001','DLSU','123 Taft Avenue','Manila'),(2,'20260002','GOV10002','National ID','Jamie','Reyes','jamie.reyes@example.com','09170000002','DLSU','45 Ayala Boulevard','Manila'),(3,'20260003','GOV10003','Passport','Morgan','Freeman','morganfreeman@example.com','09170000003','UST','78 Espana Boulevard','Manila'),(4,'20260004','GOV10004','National ID','Taylor','Swift','tayloyswift@example.com','09170000004','UPD','90 Katipunan Avenue','Quezon City'),(5,'20260005','GOV10005','Passport','Michael','Jordarn','michaeljordan@example.com','09170000005','ADM','12 Katipunan Avenue','Quezon City'),(6,'20260006','GOV10006','National ID','Cassandra','Cage','cassycage@MK.com','09170000006','DLSU','56 Buendia Avenue','Makati'),(7,'20260007','GOV10007','Passport','Princess','Peach','peachycrown@mario.com','09170000007','FEU','34 Morayta Street','Manila'),(8,'20260008','GOV10008','National ID','Seto','Kaiba','skaiba@kaibacorp.com','091700000008','DLSU','67 Shaw Boulevard','Mandaluyong'),(9,'20260009','GOV10009','Passport','Yugi','Moto','yugimoto@ygo.com','09170000009','NU','89 EDSA','Quezon City'),(10,'20260010','GOV10010','National ID','Megan','Fox','mfox@of.com','09190000010','Mapua','23 Chino Roces Avenue','Makati'),(11,'20269999',NULL,NULL,'Joey','Wheeler','joeywheels@ygo.com','09987654321','NU',NULL,NULL);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expense`
--

DROP TABLE IF EXISTS `expense`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `expense` (
  `ExpenseID` int NOT NULL AUTO_INCREMENT,
  `CustomerID` int DEFAULT NULL,
  `BranchID` int NOT NULL,
  `TransactionType` enum('Income','Expense') NOT NULL,
  `BookingID` int DEFAULT NULL,
  `ExpenseType` varchar(100) NOT NULL,
  `ExpenseDate` date NOT NULL,
  `Amount` decimal(10,2) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ExpenseID`),
  UNIQUE KEY `uq_expense_booking` (`BookingID`),
  KEY `fk_expense_branch` (`BranchID`),
  KEY `fk_expense_customer` (`CustomerID`),
  CONSTRAINT `fk_expense_booking` FOREIGN KEY (`BookingID`) REFERENCES `reservation` (`BookingID`),
  CONSTRAINT `fk_expense_branch` FOREIGN KEY (`BranchID`) REFERENCES `hubbranch` (`BranchID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_expense_customer` FOREIGN KEY (`CustomerID`) REFERENCES `customer` (`CustomerID`),
  CONSTRAINT `chk_expense_amount` CHECK ((`Amount` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expense`
--

LOCK TABLES `expense` WRITE;
/*!40000 ALTER TABLE `expense` DISABLE KEYS */;
INSERT INTO `expense` VALUES (1,1,1,'Expense',NULL,'Food','2026-11-03',200.00,'Updated test expense'),(2,2,2,'Income',2,'Reservation Payment','2026-10-21',900.00,'Payment for a meeting room reservation'),(3,3,3,'Income',3,'Reservation Payment','2026-10-22',160.00,'Payment for a two-hour study space reservation'),(4,4,1,'Income',4,'Reservation Payment','2026-10-23',150.00,'Payment for a two-hour study space reservation'),(5,5,3,'Income',5,'Reservation Payment','2026-10-24',800.00,'Payment for a meeting room reservation'),(6,6,2,'Income',6,'Reservation Payment','2026-10-25',180.00,'Payment for a three-hour study space reservation'),(7,7,4,'Income',7,'Reservation Payment','2026-10-26',110.00,'Payment for a pending reservation'),(8,8,5,'Income',8,'Reservation Payment','2026-10-27',140.00,'Payment for a study space reservation'),(9,9,6,'Income',9,'Reservation Payment','2026-10-28',1500.00,'Payment for a meeting room reservation'),(10,10,7,'Income',10,'Reservation Payment','2026-10-29',100.00,'Payment for a study space reservation'),(11,NULL,1,'Expense',NULL,'Food Restocking','2026-11-04',500.00,'Test expense through ExpenseService'),(12,1,1,'Income',NULL,'Food and Drinks','2026-11-04',250.00,'Customer purchase'),(13,1,1,'Income',1,'Reservation Payment','2026-11-04',100.00,'Payment for Booking ID 1'),(14,1,1,'Income',13,'Reservation Payment','2026-11-04',130.00,'Valid branch assignment test');
/*!40000 ALTER TABLE `expense` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expense_backup`
--

DROP TABLE IF EXISTS `expense_backup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `expense_backup` (
  `ExpenseID` int NOT NULL DEFAULT '0',
  `CustomerID` int NOT NULL,
  `BranchID` int NOT NULL,
  `BookingID` int DEFAULT NULL,
  `ExpenseType` varchar(100) NOT NULL,
  `ExpenseDate` date NOT NULL,
  `Amount` decimal(10,2) NOT NULL,
  `Description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expense_backup`
--

LOCK TABLES `expense_backup` WRITE;
/*!40000 ALTER TABLE `expense_backup` DISABLE KEYS */;
INSERT INTO `expense_backup` VALUES (1,1,1,NULL,'Food','2026-11-03',200.00,'Updated test expense'),(2,2,2,2,'Reservation Payment','2026-10-21',900.00,'Payment for a meeting room reservation'),(3,3,3,3,'Reservation Payment','2026-10-22',160.00,'Payment for a two-hour study space reservation'),(4,4,1,4,'Reservation Payment','2026-10-23',150.00,'Payment for a two-hour study space reservation'),(5,5,3,5,'Reservation Payment','2026-10-24',800.00,'Payment for a meeting room reservation'),(6,6,2,6,'Reservation Payment','2026-10-25',180.00,'Payment for a three-hour study space reservation'),(7,7,4,7,'Reservation Payment','2026-10-26',110.00,'Payment for a pending reservation'),(8,8,5,8,'Reservation Payment','2026-10-27',140.00,'Payment for a study space reservation'),(9,9,6,9,'Reservation Payment','2026-10-28',1500.00,'Payment for a meeting room reservation'),(10,10,7,10,'Reservation Payment','2026-10-29',100.00,'Payment for a study space reservation');
/*!40000 ALTER TABLE `expense_backup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hubbranch`
--

DROP TABLE IF EXISTS `hubbranch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hubbranch` (
  `BranchID` int NOT NULL AUTO_INCREMENT,
  `BranchName` varchar(100) NOT NULL,
  `Address` varchar(255) NOT NULL,
  `City` varchar(100) NOT NULL,
  `ContactNumber` varchar(20) DEFAULT NULL,
  `OperatingHoursOpen` time NOT NULL,
  `OperatingHoursClose` time NOT NULL,
  PRIMARY KEY (`BranchID`),
  UNIQUE KEY `uq_branch_name` (`BranchName`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hubbranch`
--

LOCK TABLES `hubbranch` WRITE;
/*!40000 ALTER TABLE `hubbranch` DISABLE KEYS */;
INSERT INTO `hubbranch` VALUES (1,'Taft Branch','123 Taft Avenue','Manila','09171234567','08:00:00','22:00:00'),(2,'Makati Branch','45 Ayala Avenue','Makati','09181234567','07:00:00','23:00:00'),(3,'Quezon City Branch','88 Katipunan Avenue','Quezon City','09191234567','08:00:00','22:00:00'),(4,'Ortigal Branch','200 Ortigas Avenue','Pasig','09201234567','07:00:00','23:00:00'),(5,'Alabang Branch','15 Alabang Town Center','Muntinlupa','09211234567','08:00:00','22:00:00'),(6,'BGC Branch','30 Bonifacio High Street','Taguig','09221234567','07:00:00','23:00:00'),(7,'Espana Branch','56 Espana Boulevard','Manila','09231234567','08:00:00','22:00:00'),(8,'Pasay Branch','78 Taft Avenue Extension','Pasay','09241234567','08:00:00','21:00:00'),(9,'Marikina Branch','12 J.P. Rizal Street','Marikina','09251234567','08:00:00','22:00:00'),(10,'Las Pinas Branch','90 Alabang-Zapote Road','Las Pinas','09261234567','08:00:00','22:00:00');
/*!40000 ALTER TABLE `hubbranch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `BookingID` int NOT NULL AUTO_INCREMENT,
  `CustomerID` int NOT NULL,
  `SpaceID` int NOT NULL,
  `BookingDate` date NOT NULL,
  `StartTime` time NOT NULL,
  `EndTime` time NOT NULL,
  `BookingStatus` enum('Pending','Confirmed','CheckedIN','Completed','Cancelled') NOT NULL DEFAULT 'Pending',
  `HourlyRateAtBooking` decimal(10,2) NOT NULL,
  `TotalPrice` decimal(10,2) NOT NULL,
  `CreateAt` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`BookingID`),
  KEY `fk_reservation_customer` (`CustomerID`),
  KEY `fk_reservation_space` (`SpaceID`),
  CONSTRAINT `fk_reservation_customer` FOREIGN KEY (`CustomerID`) REFERENCES `customer` (`CustomerID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_reservation_space` FOREIGN KEY (`SpaceID`) REFERENCES `studyspace` (`SpaceID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_reservation_price` CHECK (((`HourlyRateAtBooking` >= 0) and (`TotalPrice` >= 0))),
  CONSTRAINT `chk_reservation_time` CHECK ((`EndTime` > `StartTime`))
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (1,1,1,'2026-10-20','08:00:00','10:00:00','Completed',50.00,100.00,'2026-10-30 11:08:51'),(2,2,3,'2026-10-21','09:00:00','12:00:00','Completed',300.00,900.00,'2026-10-30 11:08:51'),(3,3,5,'2026-10-22','13:00:00','15:00:00','Completed',80.00,160.00,'2026-10-30 11:08:51'),(4,4,2,'2026-10-23','10:00:00','12:00:00','Completed',75.00,150.00,'2026-10-30 11:08:51'),(5,5,6,'2026-10-24','14:00:00','16:00:00','Confirmed',400.00,800.00,'2026-10-30 11:08:51'),(6,6,4,'2026-10-25','08:00:00','11:00:00','Confirmed',60.00,180.00,'2026-10-30 11:08:51'),(7,7,7,'2026-10-26','15:00:00','17:00:00','Pending',55.00,110.00,'2026-10-30 11:08:51'),(8,8,8,'2026-10-27','09:00:00','11:00:00','Confirmed',70.00,140.00,'2026-10-30 11:08:51'),(9,9,9,'2026-10-28','12:00:00','15:00:00','Pending',500.00,1500.00,'2026-10-30 11:08:51'),(10,10,10,'2026-10-29','16:00:00','18:00:00','Confirmed',50.00,100.00,'2026-10-30 11:08:51'),(13,1,1,'2026-11-05','09:00:00','11:00:00','Completed',65.00,130.00,'2026-10-31 15:02:45');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studyspace`
--

DROP TABLE IF EXISTS `studyspace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `studyspace` (
  `SpaceID` int NOT NULL AUTO_INCREMENT,
  `BranchID` int NOT NULL,
  `SpaceType` enum('Desk','Meeting Room','Cubicle') NOT NULL,
  `Capacity` int NOT NULL,
  `HourlyRate` decimal(10,2) NOT NULL,
  `DiscountPercent` decimal(5,2) NOT NULL DEFAULT '0.00',
  `Amenities` text,
  `SpaceStatus` enum('Available','Occupied','Under Maintenance') NOT NULL DEFAULT 'Available',
  PRIMARY KEY (`SpaceID`),
  KEY `fk_space_branch` (`BranchID`),
  CONSTRAINT `fk_space_branch` FOREIGN KEY (`BranchID`) REFERENCES `hubbranch` (`BranchID`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `chk_discount` CHECK ((`DiscountPercent` between 0 and 100)),
  CONSTRAINT `chk_hourly_rate` CHECK ((`HourlyRate` >= 0)),
  CONSTRAINT `chk_space_capacity` CHECK ((`Capacity` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studyspace`
--

LOCK TABLES `studyspace` WRITE;
/*!40000 ALTER TABLE `studyspace` DISABLE KEYS */;
INSERT INTO `studyspace` VALUES (1,1,'Desk',1,65.00,0.00,'Power Outlet, Wifi','Available'),(2,1,'Cubicle',1,75.00,5.00,'Power Outlet, Wifi, Lamp','Available'),(3,2,'Meeting Room',6,300.00,10.00,'TV, Whiteboard, Wifi','Available'),(4,2,'Desk',1,60.00,0.00,'Power Outlet, Wifi','Available'),(5,3,'Cubicle',1,80.00,5.00,'Power Outlet, Wifi, Lamp','Occupied'),(6,3,'Meeting Room',8,400.00,10.00,'TV, Whiteboard, Wifi','Available'),(7,4,'Desk',1,55.00,0.00,'Power Outlet, Wifi','Available'),(8,5,'Cubicle',1,70.00,0.00,'Power Outlet, Wifi, Lamp','Under Maintenance'),(9,6,'Meeting Room',10,500.00,15.00,'TV, Whiteboard, Wifi, Projector','Available'),(10,7,'Desk',1,50.00,0.00,'Power Outlet, Wifi','Available');
/*!40000 ALTER TABLE `studyspace` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-14 10:06:43
