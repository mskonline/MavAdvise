-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 29, 2017 at 03:30 AM
-- Server version: 10.1.21-MariaDB
-- PHP Version: 5.6.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `mavadvise`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `net_id` varchar(30) NOT NULL,
  `uta_id` varchar(45) DEFAULT NULL,
  `firstname` varchar(100) DEFAULT NULL,
  `lastname` varchar(100) DEFAULT NULL,
  `roletype` varchar(10) DEFAULT NULL,
  `branch` varchar(45) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `security_question_id` int(11) NOT NULL,
  `security_answer` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='MavAdvise users table';

--
-- Table structure for table `session`
--

CREATE TABLE `session` (
  `net_id` varchar(30) NOT NULL,
  `starttime` time DEFAULT NULL,
  `endtime` time DEFAULT NULL,
  `Date` date DEFAULT NULL,
  `session_id` varchar(45) NOT NULL,
  `no_of_students` int(11) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`session_id`),
  UNIQUE KEY `session_id_UNIQUE` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

CREATE TABLE `appointments` (
  `appointment_id` int(11) NOT NULL,
  `net_id` varchar(30) DEFAULT NULL,
  `session_id` varchar(45) NOT NULL,
  `date` date DEFAULT NULL,
  `slot_number` int(11) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`net_id`, `uta_id`, `firstname`, `lastname`, `roletype* `branch`, `email`, `password`, `security_question`, `security_answer`) VALUES
('123', '123', 'abc', 'efg', 'STUDENT', 'CS', 'abc@gmail.co', '123', 'q', 'a');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`net_id`),
  ADD UNIQUE KEY `net_id_UNIQUE` (`net_id`),
  ADD UNIQUE KEY `uta_id_UNIQUE` (`uta_id`),
  ADD UNIQUE KEY `email_UNIQUE` (`email`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;



