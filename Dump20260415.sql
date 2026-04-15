-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: new_user
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `connections`
--

DROP TABLE IF EXISTS `connections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `connections` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_email` varchar(100) DEFAULT NULL,
  `connected_email` varchar(100) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'pending',
  `distance_km` decimal(8,2) DEFAULT NULL,
  `request_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_connection` (`user_email`,`connected_email`),
  KEY `connected_email` (`connected_email`),
  CONSTRAINT `connections_ibfk_1` FOREIGN KEY (`user_email`) REFERENCES `userlist` (`Email`),
  CONSTRAINT `connections_ibfk_2` FOREIGN KEY (`connected_email`) REFERENCES `userlist` (`Email`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `connections`
--

LOCK TABLES `connections` WRITE;
/*!40000 ALTER TABLE `connections` DISABLE KEYS */;
INSERT INTO `connections` VALUES (1,'ross@gmail.com','Shaikh221@gmail.com','accepted',NULL,'2026-04-08 17:48:52'),(2,'Shaikh221@gmail.com','ross@gmail.com','pending',NULL,'2026-04-08 18:17:03'),(11,'shetty@gmail.com','ross@gmail.com','pending',NULL,'2026-04-08 20:36:58'),(12,'shetty@gmail.com','Shaikh221@gmail.com','accepted',NULL,'2026-04-08 20:37:01');
/*!40000 ALTER TABLE `connections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_connections`
--

DROP TABLE IF EXISTS `event_connections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_connections` (
  `connection_id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `admin_id` int NOT NULL,
  `event_name` varchar(255) DEFAULT NULL,
  `status` varchar(50) DEFAULT 'pending',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`connection_id`),
  KEY `fk_sender` (`sender_id`),
  KEY `fk_admin` (`admin_id`),
  CONSTRAINT `fk_admin` FOREIGN KEY (`admin_id`) REFERENCES `userlist` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_sender` FOREIGN KEY (`sender_id`) REFERENCES `userlist` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_connections`
--

LOCK TABLES `event_connections` WRITE;
/*!40000 ALTER TABLE `event_connections` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_connections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `eventslist`
--

DROP TABLE IF EXISTS `eventslist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `eventslist` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `eventDate` varchar(30) DEFAULT NULL,
  `eventTime` varchar(30) DEFAULT NULL,
  `sports` varchar(30) DEFAULT NULL,
  `level` varchar(30) DEFAULT NULL,
  `description` text,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `pincode` varchar(10) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `user_city` varchar(30) DEFAULT NULL,
  `user_level` varchar(30) DEFAULT NULL,
  `userId` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `eventslist`
--

LOCK TABLES `eventslist` WRITE;
/*!40000 ALTER TABLE `eventslist` DISABLE KEYS */;
INSERT INTO `eventslist` VALUES (1,'GPL','2026-04-17','16:41','Cricket','Advanced','good','Gokuldham Socity','Mumbai','400011',NULL,NULL,NULL,NULL),(3,'gpl2','2026-04-15','00:00','Badminton','Advanced','with price 1000000',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'Gpl3','2026-04-18','02:04','Basketball','Beginner','good','MG Road','Mumbai','400001',NULL,NULL,NULL,NULL),(5,'GPL league','2026-04-09','04:39','Cricket','Beginner','good sport','Gokuldham society, mg road','Mumbai','400101',NULL,NULL,NULL,NULL),(6,'Salvi league','2026-04-09','04:56','Football','Intermediate','','Indraprastha','Kandivali','100123','Atharva Salvi',NULL,'Beginner',NULL),(7,'gpl','2026-04-11','07:19','Cricket','Beginner','good','saw','mumbai','400101','Atharva Salvi',NULL,'Beginner',NULL),(8,'Indian Premiere Leager','2026-04-09','05:27','Cricket','Advanced','Its about game, its about power','Wankhade, Bandra','Mumbai','400101','Atharva Salvi',NULL,'Beginner',NULL),(9,'Gpl4','2026-04-18','','Cricket','','good good','MG Road','Mumbai','400001','Ayaan Shaikh','Thane','Beginner',NULL),(10,'Gpl8','2026-04-18','','Cricket','','hhghhhg','MG Road bhayander','Mumbai','400001','Ayaan Shaikh','Thane','Advanced',NULL),(11,'Japan Football League','2026-04-15','19:59','Football','Advanced','konichiwa','Tokyo Dome','Tokyo','100101','Atharva Salvi',NULL,'Advanced',1),(13,'Francis Football League','2026-04-17','12:17','Football','Beginner','francis time','St francis ground','mumbai','400011','Ayan Shetty',NULL,'Beginner',8);
/*!40000 ALTER TABLE `eventslist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `messageId` int NOT NULL AUTO_INCREMENT,
  `eventId` int DEFAULT NULL,
  `comment` varchar(500) DEFAULT NULL,
  `senderName` varchar(50) DEFAULT NULL,
  `time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`messageId`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,1,'Hey','Guest','2026-04-13 15:50:12'),(2,1,'I hope you are doing fine','Guest','2026-04-13 15:52:35'),(3,1,'Yeah I am','Atharva Salvi','2026-04-13 15:56:52'),(4,4,'Chote Chote Kadam','Atharva Salvi','2026-04-13 16:17:44'),(5,1,'hey','Atharva Salvi','2026-04-14 08:29:02'),(6,1,'Hey are there any seats available','Jay Rao','2026-04-15 15:59:04'),(7,1,'Yes there are','Atharva Salvi','2026-04-15 16:00:24'),(8,1,'thanks','Jay Rao','2026-04-15 16:00:33'),(9,1,'welcome','Atharva Salvi','2026-04-15 16:00:40'),(10,11,'Would love to join','Jay Rao','2026-04-15 16:01:36'),(11,11,'sure','Atharva Salvi','2026-04-15 16:01:47');
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userlist`
--

DROP TABLE IF EXISTS `userlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userlist` (
  `id` int NOT NULL AUTO_INCREMENT,
  `Fname` varchar(50) DEFAULT NULL,
  `Email` varchar(50) DEFAULT NULL,
  `Password` varchar(50) DEFAULT NULL,
  `role` varchar(15) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `pincode` varchar(6) DEFAULT NULL,
  `level` varchar(50) DEFAULT 'Beginner',
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `last_location_update` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Email` (`Email`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userlist`
--

LOCK TABLES `userlist` WRITE;
/*!40000 ALTER TABLE `userlist` DISABLE KEYS */;
INSERT INTO `userlist` VALUES (1,'Atharva Salvi','Salvi360@gmail.com','Salvi360@','Player',NULL,NULL,NULL,'Advanced',NULL,NULL,'2026-04-08 16:54:11'),(2,'John Doe','john@gmail.com','123456789','Player',NULL,NULL,NULL,'Beginner',NULL,NULL,'2026-04-08 16:54:11'),(7,'Jay Rao','rao@gmail.com','rao12345','Player',NULL,NULL,NULL,'Beginner',NULL,NULL,'2026-04-08 16:54:11'),(8,'Ayan Shetty','shetty@gmail.com','12345678','Player',NULL,NULL,NULL,'Beginner',19.30123655,72.85048855,'2026-04-08 16:54:11'),(9,'Shraddha','salvi78@gmail.com','12345678','Coach',NULL,NULL,NULL,'Beginner',NULL,NULL,'2026-04-08 16:54:11'),(10,'Ayan Sheikh','Sheikh@gmail.com','12345678','Player',NULL,NULL,NULL,'Beginner',NULL,NULL,'2026-04-08 16:54:11'),(11,'Ayan Shaikh','Shaikh@gmail.com','12345678','Player',NULL,NULL,NULL,'Beginner',NULL,NULL,'2026-04-08 16:54:11'),(12,'Ayaan Shaikh','Shaikh121@gmail.com','12345678','Player',NULL,NULL,NULL,'Beginner',NULL,NULL,'2026-04-08 16:54:11'),(13,'Ayaan Shaikh','Shaikh221@gmail.com','12345678','Player','dipti apt, room 707','Thane','401101','Advanced',19.24356300,72.85630425,'2026-04-08 18:21:47'),(14,'Bob ross','ross@gmail.com','12345678','Player','A-Indraprastha, lokhandwala','Mumbai','400101','Intermediate',19.30126159,72.85050208,'2026-04-08 17:48:38');
/*!40000 ALTER TABLE `userlist` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-15 22:06:01
