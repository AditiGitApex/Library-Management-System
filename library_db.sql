-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 31, 2024 at 11:03 AM
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
-- Database: `library_db`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `AuthenticateUser` (IN `input_username` VARCHAR(255), IN `input_password` VARCHAR(255), OUT `user_id` INT, OUT `is_admin` BOOLEAN)   BEGIN
    DECLARE user_exists INT DEFAULT 0;

    -- Check if user exists
    SELECT COUNT(*)
    INTO user_exists
    FROM users
    WHERE username = input_username;

    -- If the user exists, check the password
    IF user_exists = 1 THEN
        SELECT id, isAdmin
        INTO user_id, is_admin
        FROM users
        WHERE username = input_username AND password = input_password;

        -- If the user_id is still NULL, the password was incorrect
        IF user_id IS NULL THEN
            SET user_id = -1; -- Indicate failure
            SET is_admin = FALSE;
        END IF;
    ELSE
        -- If the user does not exist
        SET user_id = -1; -- Indicate failure
        SET is_admin = FALSE;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `IssueBook` (IN `userId` INT, IN `bookId` INT)   BEGIN
    INSERT INTO Transactions (userId, bookId, issueDate)
    VALUES(userId, bookId, CURRENT_DATE);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `RegisterUser` (IN `p_username` VARCHAR(255), IN `p_password` VARCHAR(255), IN `p_isAdmin` BOOLEAN)   BEGIN
    DECLARE usernameCount INT;

    -- Check if the username already exists
    SELECT COUNT(*) INTO usernameCount
    FROM Users
    WHERE username = p_username;

    IF usernameCount > 0 THEN
        -- Username already exists, signal an error
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: Username already exists.';

    ELSE
        -- Username does not exist, proceed with insertion
        INSERT INTO Users (name, password, isAdmin)
        VALUES (p_username, p_password, p_isAdmin);
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `ReturnBook` (IN `p_userId` INT, IN `p_bookId` INT)   BEGIN
    UPDATE Transactions
    SET returnDate = CURRENT_DATE, isReturned = TRUE
    WHERE userId = p_userId AND bookId = p_bookId AND isReturned = FALSE;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `books`
--

CREATE TABLE `books` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `genre` varchar(255) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `price` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `books`
--

INSERT INTO `books` (`id`, `title`, `author`, `genre`, `quantity`, `price`) VALUES
(1, 'The Great Gatsby', 'F. Scott Fitzgerald', 'Classic', 10, 15),
(2, '1984', 'George Orwell', 'Dystopian', 8, 13),
(3, 'To Kill a Mockingbird', 'Harper Lee', 'Classic', 5, NULL),
(4, 'The Catcher in the Rye', 'J.D. Salinger', 'Classic', 7, 14),
(5, 'The Hobbit', 'J.R.R. Tolkien', 'Fantasy', 12, 18);

--
-- Triggers `books`
--
DELIMITER $$
CREATE TRIGGER `BeforeIssueBook` BEFORE UPDATE ON `books` FOR EACH ROW BEGIN
    IF NEW.quantity < 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Book quantity cannot be negative';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `id` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `bookId` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `reason` varchar(255) NOT NULL,
  `paymentDate` date DEFAULT curdate()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `id` int(11) NOT NULL,
  `userId` int(11) DEFAULT NULL,
  `bookId` int(11) DEFAULT NULL,
  `issueDate` date DEFAULT NULL,
  `returnDate` date DEFAULT NULL,
  `isReturned` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`id`, `userId`, `bookId`, `issueDate`, `returnDate`, `isReturned`) VALUES
(2, 3, 5, '2024-08-31', NULL, 0),
(3, 9, 5, '2024-08-31', NULL, 0);

--
-- Triggers `transactions`
--
DELIMITER $$
CREATE TRIGGER `AfterReturnBook` AFTER UPDATE ON `transactions` FOR EACH ROW BEGIN
    IF NEW.isReturned = TRUE THEN
        UPDATE Books SET quantity = quantity + 1 WHERE id = NEW.bookId;
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `isAdmin` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `isAdmin`) VALUES
(1, 'aditi', '011005', 1),
(2, 'sarvesh', '203121', 0),
(3, 'rajvi', '123456', 0),
(9, 'ankush', '123456', 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `books`
--
ALTER TABLE `books`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `userId` (`userId`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `userId` (`userId`),
  ADD KEY `bookId` (`bookId`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `books`
--
ALTER TABLE `books`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=202;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`id`);

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `transactions_ibfk_2` FOREIGN KEY (`bookId`) REFERENCES `books` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
