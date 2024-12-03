-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: web
-- ------------------------------------------------------
-- Server version	8.0.34

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
-- Table structure for table `book_history`
--

DROP TABLE IF EXISTS `book_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `book_id` int DEFAULT NULL,
  `action` enum('create','update','delete') NOT NULL,
  `field_name` varchar(255) DEFAULT NULL,
  `old_value` text,
  `new_value` text,
  `user_id` int DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `book_id` (`book_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `book_history_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  CONSTRAINT `book_history_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_history`
--

LOCK TABLES `book_history` WRITE;
/*!40000 ALTER TABLE `book_history` DISABLE KEYS */;
INSERT INTO `book_history` VALUES (1,1,'create',NULL,NULL,'Title: Clean Code, Author: Robert C. Martin',1,'2024-12-01 15:35:51'),(2,2,'create',NULL,NULL,'Title: The Pragmatic Programmer, Author: Andrew Hunt, David Thomas',1,'2024-12-01 15:35:51'),(3,3,'update','price','35.00','40.00',2,'2024-12-01 15:35:51'),(4,4,'delete',NULL,'Title: Introduction to Algorithms, Author: Thomas H. Cormen',NULL,2,'2024-12-01 15:35:51');
/*!40000 ALTER TABLE `book_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_shelves`
--

DROP TABLE IF EXISTS `book_shelves`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_shelves` (
  `book_id` int NOT NULL,
  `shelf_id` int NOT NULL,
  PRIMARY KEY (`book_id`,`shelf_id`),
  KEY `shelf_id` (`shelf_id`),
  CONSTRAINT `book_shelves_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`),
  CONSTRAINT `book_shelves_ibfk_2` FOREIGN KEY (`shelf_id`) REFERENCES `shelves` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_shelves`
--

LOCK TABLES `book_shelves` WRITE;
/*!40000 ALTER TABLE `book_shelves` DISABLE KEYS */;
INSERT INTO `book_shelves` VALUES (1,1),(2,1),(4,2),(3,3);
/*!40000 ALTER TABLE `book_shelves` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `category` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `publisher` varchar(255) NOT NULL,
  `publish_year` int NOT NULL,
  `description` text,
  `image_link` varchar(255) NOT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `books_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES (1,'Clean Code','Robert C. Martin','Programming',10,25.99,'2024-12-01 15:35:51','2024-12-01 15:35:51','Prentice Hall',2008,'A Handbook of Agile Software Craftsmanship','https://example.com/clean_code.jpg',1),(2,'The Pragmatic Programmer','Andrew Hunt, David Thomas','Programming',15,30.50,'2024-12-01 15:35:51','2024-12-01 15:35:51','Addison-Wesley',1999,'Your Journey to Mastery','https://example.com/pragmatic_programmer.jpg',1),(3,'Design Patterns','Erich Gamma','Programming',8,40.00,'2024-12-01 15:35:51','2024-12-01 15:35:51','Addison-Wesley',1994,'Elements of Reusable Object-Oriented Software','https://example.com/design_patterns.jpg',2),(4,'Introduction to Algorithms','Thomas H. Cormen','Algorithms',5,80.00,'2024-12-01 15:35:51','2024-12-01 15:35:51','MIT Press',2009,'Comprehensive guide to algorithms','https://example.com/algorithms.jpg',2);
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `otp`
--

DROP TABLE IF EXISTS `otp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otp` (
  `username` varchar(255) NOT NULL,
  `otp_code` varchar(6) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `expiration_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `otp`
--

LOCK TABLES `otp` WRITE;
/*!40000 ALTER TABLE `otp` DISABLE KEYS */;
/*!40000 ALTER TABLE `otp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shelves`
--

DROP TABLE IF EXISTS `shelves`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shelves` (
  `id` int NOT NULL AUTO_INCREMENT,
  `shelf_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shelves`
--

LOCK TABLES `shelves` WRITE;
/*!40000 ALTER TABLE `shelves` DISABLE KEYS */;
INSERT INTO `shelves` VALUES (1,'Programming Shelf'),(2,'Algorithms Shelf'),(3,'Design Shelf');
/*!40000 ALTER TABLE `shelves` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `salt` varchar(255) NOT NULL,
  `role` enum('manager','staff') NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','khaipham182@gmail.com','d411f1744f61390ffbda6da5db5eb6cb7c7824b80d6cdfa613308e48585146c2','95df826f11d961b3e34bb2bed79a5ddf','manager','2024-12-01 15:35:51'),(2,'staff1','123@gmail.com','9df6ed93bde74504f6bfdcdc6ee99461a9992696a4dbaef0aff14ed77a9e5985','adcbe3918485d28004b98e99730d9131','staff','2024-12-01 15:35:51'),(3,'staff2','123@gmail.com','hashed_password3','random_salt3','staff','2024-12-01 15:35:51');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-03 18:11:12
