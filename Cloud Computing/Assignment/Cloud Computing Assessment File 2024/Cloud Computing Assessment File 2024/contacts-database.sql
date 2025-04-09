-- Dumping database structure for contacts database
DROP DATABASE IF EXISTS `contact`;
CREATE DATABASE IF NOT EXISTS `contact`;
USE `contact`;

-- Dumping structure for table todo_list
DROP TABLE IF EXISTS `contacts`;
CREATE TABLE contacts (id int NOT NULL AUTO_INCREMENT, name varchar(255), email varchar(255), PRIMARY KEY (id));
INSERT INTO contacts (name, email)  VALUES ("Mohammed Kaleem", "m.kaleem@mmu.ac.uk");
INSERT INTO contacts (name, email)  VALUES ("John Wick", "mr_wick@continental.com");
INSERT INTO contacts (name, email) VALUES ("James Bond", "bond007@mi5.co.uk");
INSERT INTO contacts (name, email) VALUES ("Shaikha AlJameela", "drshaikha@gmail.com");
INSERT INTO contacts (name, email) VALUES ("Keeley Crockett", "keeley@yahoo.com");
INSERT INTO contacts (name, email) VALUES ("James O' Shea", "jim@outlook.com");

-- Dumping structure for table dvddb.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `apikey` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- Dumping data for table dvddb.users: ~2 rows (approximately)
INSERT INTO `users` (`id`, `username`, `password`, `apikey`) VALUES
	(1, 'kaleem', 'pa55word', '5h4ikH4');
	