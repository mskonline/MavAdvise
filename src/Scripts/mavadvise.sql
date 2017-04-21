-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: mavadvise
-- ------------------------------------------------------
-- Server version	5.7.17-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointments`
--

CREATE DATABASE mavadvise;
USE mavadvise;

CREATE USER 'mavuser'@'%' IDENTIFIED BY 'GoMavericks123$';
GRANT SELECT,INSERT,UPDATE,DELETE ON mavadvise.* TO 'mavuser'@'%';
FLUSH PRIVILEGES; 

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `appointments` (
  `appointment_id` int(11) NOT NULL AUTO_INCREMENT,
  `net_id` varchar(30) DEFAULT NULL,
  `session_id` int(10) NOT NULL,
  `date` date DEFAULT NULL,
  `slot_number` int(11) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sessions`
--

DROP TABLE IF EXISTS `sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sessions` (
  `session_id` int(10) NOT NULL AUTO_INCREMENT,
  `net_id` varchar(30) NOT NULL,
  `date` date DEFAULT NULL,
  `starttime` time DEFAULT NULL,
  `endtime` time DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `no_of_slots` int(11) DEFAULT NULL,
  `slot_counter` int(11) DEFAULT '0',
  `location` varchar(250) DEFAULT NULL,
  `comment` text,
  PRIMARY KEY (`session_id`),
  UNIQUE KEY `session_id_UNIQUE` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `net_id` varchar(30) CHARACTER SET latin1 NOT NULL,
  `uta_id` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `firstname` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `lastname` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `roletype` varchar(10) CHARACTER SET latin1 DEFAULT NULL,
  `branch` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `email` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `password` varchar(100) CHARACTER SET latin1 DEFAULT NULL,
  `security_question_id` int(11) NOT NULL,
  `security_answer` varchar(1000) CHARACTER SET latin1 DEFAULT NULL,
  PRIMARY KEY (`net_id`),
  UNIQUE KEY `net_id_UNIQUE` (`net_id`),
  UNIQUE KEY `uta_id_UNIQUE` (`uta_id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='MavAdvise users table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `announcements`
--

CREATE TABLE `announcements` (
  `net_id` VARCHAR(30) NULL,
  `message` TEXT NULL,
  `date` DATE NULL,
  `priority` INT NULL,
  `title` VARCHAR(50) NULL);

-- Data inserts

INSERT INTO `users` (`net_id`, `uta_id`, `firstname`, `lastname`, `roletype`, `branch`, `email`, `password`, `security_question_id`, `security_answer`) VALUES
('bxs1234', '1001231234', 'Bob', 'Smith', 'Advisor', 'CSE', 'bob.smith@mail.com', 'A665A45920422F9D417E4867EFDC4FB8A04A1F3FFF1FA07E998E86F7F7A27AE3', 1, 'Dallas'),
('wxs1234', '1001231235', 'Will', 'Smith', 'Student', 'CSE', 'will.smith@mail.com', 'A665A45920422F9D417E4867EFDC4FB8A04A1F3FFF1FA07E998E86F7F7A27AE3', 1, 'Dallas');
