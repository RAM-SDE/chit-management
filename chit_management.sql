-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: chit_management
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chit_enrollments`
--

DROP TABLE IF EXISTS `chit_enrollments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chit_enrollments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` char(36) NOT NULL,
  `enrolled_at` datetime(6) DEFAULT NULL,
  `status` enum('ACTIVE','COMPLETED','WITHDRAWN') NOT NULL,
  `chit_plan_id` bigint(20) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `enrolled_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKra3ay9ssf43gipio8puljtpva` (`customer_id`,`chit_plan_id`),
  UNIQUE KEY `UK65yni1skagt84xm5ayknl2uhy` (`uuid`),
  KEY `FK7x7agidns6m1ahjvknno85ybv` (`chit_plan_id`),
  KEY `FKda11xtn7osdigtmw6lv9q47fs` (`enrolled_by`),
  CONSTRAINT `FK7x7agidns6m1ahjvknno85ybv` FOREIGN KEY (`chit_plan_id`) REFERENCES `chit_plans` (`id`),
  CONSTRAINT `FKda11xtn7osdigtmw6lv9q47fs` FOREIGN KEY (`enrolled_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrfa33pw4lo9dxx93q0vfe0qkb` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chit_enrollments`
--

LOCK TABLES `chit_enrollments` WRITE;
/*!40000 ALTER TABLE `chit_enrollments` DISABLE KEYS */;
INSERT INTO `chit_enrollments` VALUES (1,'5220f766-2e9b-48cf-919a-05330e874e9b','2026-05-23 09:23:28.000000','ACTIVE',1,1,1);
/*!40000 ALTER TABLE `chit_enrollments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chit_plans`
--

DROP TABLE IF EXISTS `chit_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chit_plans` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` char(36) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `duration_months` int(11) NOT NULL,
  `end_date` date NOT NULL,
  `monthly_amount` decimal(12,2) NOT NULL,
  `plan_name` varchar(100) NOT NULL,
  `start_date` date NOT NULL,
  `status` enum('ACTIVE','CANCELLED','COMPLETED') NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `total_members` int(11) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK865418lvvjbb47s1kx2cxbeby` (`uuid`),
  KEY `FKf63xpg6duv1y43lf4p6jua7iw` (`created_by`),
  CONSTRAINT `FKf63xpg6duv1y43lf4p6jua7iw` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chit_plans`
--

LOCK TABLES `chit_plans` WRITE;
/*!40000 ALTER TABLE `chit_plans` DISABLE KEYS */;
INSERT INTO `chit_plans` VALUES (1,'c9d03a28-fdf7-450d-9f66-b36112406f24','2026-05-22 12:10:42.000000',20,'2028-03-01',25000.00,'Gold Savings Plan','2026-07-01','ACTIVE',500000.00,20,'2026-05-22 17:22:02.000000',1,1);
/*!40000 ALTER TABLE `chit_plans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` char(36) NOT NULL,
  `aadhar_no` varchar(20) DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `address` text DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKenfyfum1en20int6gevojxlln` (`uuid`),
  UNIQUE KEY `UKm3iom37efaxd5eucmxjqqcbe9` (`phone`),
  KEY `FK1nxl2d7l8l355kw6264jpx9st` (`created_by`),
  CONSTRAINT `FK1nxl2d7l8l355kw6264jpx9st` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'0b7c9f51-9534-4865-920d-896c17dbbb94','','','srivilliputhur','2026-05-22 08:57:22.000000','ram96@gmail.com','ram','9876543210','2026-05-22 09:23:16.000000',1);
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount_paid` decimal(12,2) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `due_amount` decimal(12,2) NOT NULL,
  `month_number` int(11) NOT NULL,
  `payment_date` datetime(6) DEFAULT NULL,
  `payment_mode` enum('BANK_TRANSFER','CASH','CHEQUE','UPI') NOT NULL,
  `receipt_no` varchar(50) DEFAULT NULL,
  `remarks` text DEFAULT NULL,
  `status` enum('PAID','PARTIAL','PENDING') NOT NULL,
  `collected_by` bigint(20) DEFAULT NULL,
  `enrollment_id` bigint(20) NOT NULL,
  `uuid` char(36) NOT NULL,
  `carry_forward` decimal(12,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKhm20xjvnkxv1u1fr5hcfa2upd` (`uuid`),
  KEY `FKll6si0jovk9ppl7qwyikpvnua` (`collected_by`),
  KEY `FKl282otmgst5hofa5u9jfq95in` (`enrollment_id`),
  CONSTRAINT `FKl282otmgst5hofa5u9jfq95in` FOREIGN KEY (`enrollment_id`) REFERENCES `chit_enrollments` (`id`),
  CONSTRAINT `FKll6si0jovk9ppl7qwyikpvnua` FOREIGN KEY (`collected_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (3,25000.00,'2026-05-23 09:25:08.000000',25000.00,1,'2026-05-23 09:25:08.000000','UPI','RCP-2026-0001','','PAID',1,1,'4561003b-0efc-4ac5-b360-ee9e8d1c07a5',0.00),(4,25000.00,'2026-05-23 09:25:08.000000',25000.00,2,'2026-05-23 09:25:08.000000','UPI','RCP-2026-0001','','PAID',1,1,'0855aa59-b051-4089-9dc4-b73fe5e577b9',0.00),(5,25000.00,'2026-05-23 11:08:07.000000',25000.00,3,'2026-05-23 11:08:07.000000','CHEQUE','RCP-2026-0002','','PAID',3,1,'4de1093a-5424-4dc3-98e9-3e6527e29c36',0.00),(6,25000.00,'2026-05-23 11:12:17.000000',25000.00,4,'2026-05-23 11:12:17.000000','CHEQUE','RCP-2026-0003','','PAID',3,1,'05635c6c-9177-43b9-9afc-ebf471f7ebc0',0.00);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` char(36) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `role_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKbdys1vaxs0jqndxmixeragus8` (`uuid`),
  UNIQUE KEY `UK716hgxp60ym1lifrdgp67xt5k` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'fb545175-54f5-11f1-b2ba-b882f296b80c','Administrator','','ROLE_ADMIN'),(2,'fb54c1d2-54f5-11f1-b2ba-b882f296b80c','Collection Agent','','ROLE_AGENT');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_roles` (
  `user_id` bigint(20) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,1),(2,1),(3,2);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` char(36) NOT NULL,
  `email` varchar(100) NOT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `password` varchar(255) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `gender` enum('FEMALE','MALE','OTHER') DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6km2m9i3vjuy36rnvkgj1l61s` (`uuid`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'c1767071-5329-11f1-ab20-c3f892f20540','ram@gmail.com',1,'$2a$10$wGMtTm0JBFjEEx0Ftt7AF.aBvis7nO5oA3RpwXU0goik6VGvgs8na','Srivilliputhur','MALE','Ram','9360392848'),(2,'fb5a0138-54f5-11f1-b2ba-b882f296b80c','admin@chit.com',1,'$2y$10$.wlIReWW9ygd9ogZofHmRe6haOy5UL1BeKpGZDuwf0m6968c3nzwS','','MALE','admin','8765432190'),(3,'fb5a3a87-54f5-11f1-b2ba-b882f296b80c','user@chit.com',1,'$2y$10$tub7L/w4EcZUi.kANjbvHeGzmCuirVyFyTWOW3LdbUGmpnPLWvYOK',NULL,'MALE','agent',NULL);
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

-- Dump completed on 2026-05-23 11:30:37
