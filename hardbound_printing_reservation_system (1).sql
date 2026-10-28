-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 12, 2026 at 06:47 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hardbound_printing_reservation_system`
--

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `reservation_ID` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `path_file_name` varchar(500) DEFAULT NULL,
  `service` varchar(100) NOT NULL,
  `quality` varchar(100) DEFAULT NULL,
  `paper_size` varchar(50) DEFAULT NULL,
  `paper_type` varchar(100) DEFAULT NULL,
  `color` varchar(50) DEFAULT NULL,
  `binding` varchar(100) DEFAULT NULL,
  `orientation` varchar(50) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `selected_date` date NOT NULL,
  `selected_time` varchar(50) DEFAULT NULL,
  `additional_info` text DEFAULT NULL,
  `total` decimal(10,2) NOT NULL DEFAULT 0.00,
  `payment_method` enum('Cash','GCash','Maya') NOT NULL,
  `referencee_number` varchar(100) DEFAULT 'N/A',
  `status_reservation` enum('Pending','Approved','Rejected','Claimed') NOT NULL DEFAULT 'Pending',
  `payment_status` enum('Unpaid','Partially Paid','Fully Paid') NOT NULL DEFAULT 'Unpaid',
  `printing_status` enum('Not Started','Printing','Ready to claim','Printed') NOT NULL DEFAULT 'Not Started',
  `admin_note_rejection` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `completed_date` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reservations`
--

INSERT INTO `reservations` (`reservation_ID`, `user_id`, `file_name`, `path_file_name`, `service`, `quality`, `paper_size`, `paper_type`, `color`, `binding`, `orientation`, `quantity`, `selected_date`, `selected_time`, `additional_info`, `total`, `payment_method`, `referencee_number`, `status_reservation`, `payment_status`, `printing_status`, `admin_note_rejection`, `created_at`, `completed_date`) VALUES
(7, 1, '34', 'b62f4b14-e6a0-426d-867c-cf9383644107.jpg', 'Printing', 'Eco', 'A4', 'Bond Paper', 'Colored', 'None', 'Landscape', 2, '2026-10-12', '08:00 AM', 'No additional information', 12.00, 'Maya', 'ddd', 'Rejected', 'Partially Paid', 'Not Started', NULL, '2026-10-12 10:29:48', NULL),
(9, 1, 'hello', 'chgpt01.png', 'Binding', 'Normal', 'A4', 'Bond Paper', 'Colored', 'Thermal Binding', 'Landscape', 12, '2026-10-12', '08:00 AM', 'paprint po sir nito', 1272.00, 'Maya', '89897777666777', 'Claimed', 'Fully Paid', 'Printed', NULL, '2026-10-12 15:32:20', '2026-10-13 00:13:59'),
(10, 1, 'ss', 'ChatGPT Image Sep 30, 2026, 06_37_50 PM.png', 'Printing', 'Normal', 'A4', 'Glossy Paper', 'Mono', 'None', 'Landscape', 2, '2026-10-13', '08:00 AM', 'eee', 26.00, 'Cash', 'N/A', 'Rejected', 'Unpaid', 'Not Started', 'You haven\'t paid your initial down payment.', '2026-10-12 16:13:38', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_users`
--

CREATE TABLE `tbl_users` (
  `user_id` int(11) NOT NULL,
  `f_name` varchar(50) NOT NULL,
  `l_name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_users`
--

INSERT INTO `tbl_users` (`user_id`, `f_name`, `l_name`, `email`, `phone_number`, `password`, `created_at`) VALUES
(1, 'mahiro', 'Guingab', 'mahiro@gmail.com', '09070756415', 'mahiro123', '2026-10-10 13:33:21'),
(2, 'mahiro', 'oyama', 'mahirochan@gmail.com', '09070756415', 'mahiro123', '2026-10-11 06:05:52');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`reservation_ID`);

--
-- Indexes for table `tbl_users`
--
ALTER TABLE `tbl_users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `reservation_ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `tbl_users`
--
ALTER TABLE `tbl_users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
