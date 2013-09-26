CREATE DATABASE  IF NOT EXISTS `racloop` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `racloop`;
-- MySQL dump 10.13  Distrib 5.6.13, for Win32 (x86)
--
-- Host: 127.0.0.1    Database: racloop
-- ------------------------------------------------------
-- Server version	5.6.14

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
-- Table structure for table `rides`
--

DROP TABLE IF EXISTS `rides`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rides` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(100) NOT NULL,
  `travel_time` datetime DEFAULT NULL,
  `from_place` varchar(255) DEFAULT NULL,
  `from_state` varchar(255) DEFAULT NULL,
  `from_lattitude` double DEFAULT NULL,
  `from_longitude` double DEFAULT NULL,
  `to_place` varchar(255) DEFAULT NULL,
  `to_state` varchar(255) DEFAULT NULL,
  `to_latittude` double DEFAULT NULL,
  `to_longitude` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

/*
-- Query: SELECT * FROM racloop.rides
LIMIT 0, 1000

-- Date: 2013-09-25 01:10
*/
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (44,'Name 1','2013-09-25 13:30:00','India gate','Delhi',28.612857,77.231048,'Pinjore','Harayan',30.79899,76.917072);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (45,'Name 2','2013-09-25 14:00:00','Sohna','Haryana',28.247235,77.069815,'Anandpur Sahib','Punjab',31.237756,76.497446);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (46,'Name 3','2013-09-25 16:30:00','Dwarka Sector 10','Delhi',28.584635,77.058056,'IT Park Landmark','Chandigarh',30.734458,76.842644);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (47,'Name 4','2013-09-25 06:30:00','Jharoda Kalan','New Delhi',28.65075,76.946941,'Chandigarh Airport','Chandigarh',30.674091,76.789442);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (48,'Name 5','2013-09-25 07:00:00','Sector 4 Dwarka','New Delhi',28.600197,77.045692,'Sector 9 Chandigarh','Chandigarh',30.747774,76.794298);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (49,'Name 6','2013-09-25 11:45:00','Sector 60 Faridabad','Haryana',28.329844,77.319618,'Sector 8 Panchkula','Haryana',30.699796,76.84404);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (50,'Name 7','2013-09-25 11:00:00','Ballabgarh','Haryana',28.3302,77.31651,'City Centre DLFPhase - I Manimajra','Chandigarh',30.726859,76.846434);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (51,'Name 8','2013-09-25 07:45:00','Mangolpuri','New Delhi',28.690211,77.087875,'Punjab Engg College','Chandigarh',30.770159,76.785652);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (52,'Name 9','2013-09-25 15:00:00','Sector 49 Gurgaon','Haryana',28.4102,77.048057,'Derabassi','Punjab',30.590933,76.842815);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (53,'Name 10','2013-09-25 10:00:00','Crossings Golf Course Noida','Uttar Pradesh',28.62898,77.43333,'Sector 26 Chandigarh','Chandigarh',30.730493,76.810073);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (54,'Name 11','2013-09-25 07:15:00','Kanjhawala','New Delhi',28.728754,77.00256,'Sector 10 Chandigarh','Chandigarh',30.754191,76.789878);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (55,'Name 12','2013-09-25 08:30:00','Himmatpuri Trilokpuri','New Delhi',28.605604,77.304372,'Sector 18 Chandigarh','Chandigarh',30.7372,76.7872);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (56,'Name 13','2013-09-25 11:15:00','Faridabad Sector 37','Uttar Pradesh',28.480876,77.312158,'Civil Hospital Kharar - Landran Road','Punjab',30.752901,76.633666);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (57,'Name 14','2013-09-25 13:45:00','Sector 56 Gurgaon','Haryana',28.42267261,77.10313904,'Timber Trail Heights Solan','Himachal Pradesh',30.842995,76.981834);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (58,'Name 15','2013-09-25 06:00:00','Vasant Kunj','New Delhi',28.529186,77.15139,'New Secretariat','Chandigarh',30.738221,76.789173);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (59,'Name 16','2013-09-25 15:30:00','Sonepat','Haryana',28.943265,77.09233,'Ropar','Punjab',30.970397,76.52301);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (60,'Name 17','2013-09-25 15:45:00','Rai','Haryana',28.928843,77.096373,'Panchkula Heights','Haryana',30.657775,76.853822);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (61,'Name 18','2013-09-25 07:30:00','Khera Kalan','New Delhi',28.770123,77.115823,'Sector 12 Chandigarh','Chandigarh',30.767246,76.778248);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (62,'Name 19','2013-09-25 16:00:00','Raj Nagar Extension Ghaziabad','Uttar Pradesh',28.705645,77.433086,'Sector 34 chd','Chandigarh',30.72044,76.764129);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (63,'Name 20','2013-09-25 13:15:00','Sector 53 Gurgaon','Haryana',28.44730513,77.09648037,'Mashobra','Himachal Pradesh',31.129044,77.231005);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (64,'Name 21','2013-09-25 10:30:00','Sardar Patel Inter Collage Faridabad','Uttar Pradesh',25.374817,81.516359,'Mullanpur Chandigarh','Chandigarh',30.785239,76.756232);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (65,'Name 22','2013-09-25 08:15:00','Shalimar Bagh','New Delhi',28.713775,77.157398,'Sector 16 Chandigarh','Chandigarh',30.747479,76.778248);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (66,'Name 23','2013-09-25 12:00:00','Unitech Infopark NOIDA','Uttar Pradesh',28.498189,77.403389,'Sector 38-A',' Chandigarh',30.748659,76.747563);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (67,'Name 24','2013-09-25 08:45:00','Shakarpur','New Delhi',28.629093,77.282374,'Sector 47 Chandigarh','Chandigarh',30.695756,76.769493);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (68,'Name 25','2013-09-25 12:30:00','Army Institute of Management and Technology Greater Noida','Uttar Pradesh',28.442016,77.507637,'Vivek High School Sector 70 Mohali','Punjab',30.697933,76.711214);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (69,'Name 26','2013-09-25 10:15:00','Ramlila Maidanmore Raj Nagar Ghaziabad','Uttar Pradesh',28.680084,77.444834,'Manav Mangal Smart School Phase 10 Mohal','Punjab',30.689842,76.746313);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (70,'Name 27','2013-09-25 09:15:00','Gokulpuri','New Delhi',28.702182,77.282678,'Sector 29 Chandigarh','Chandigarh',30.712139,76.792882);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (71,'Name 28','2013-09-25 12:15:00','Pari Chowk Noida','Uttar Pradesh',28.464768,77.51107,'Sunny Enclave Sector125 Mohali','Punjab',30.742537,76.676195);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (72,'Name 29','2013-09-25 16:15:00','Connaught Place','Delhi',28.632784,77.219546,'Kurali','Punjab',30.835035,76.573406);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (73,'Name 30','2013-09-25 12:45:00','Hodal','Uttar Pradesh',27.8999168,77.3739284,'Bharari Rd Shankli Shimla','Himachal Pradesh',31.115194,77.176717);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (74,'Name 31','2013-09-25 06:15:00','Somesh Vihar','New Delhi',28.55829,77.014662,'Industrial Training Institute Sector 28','Chandigarh',30.71308,76.800267);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (75,'Name 32','2013-09-25 11:30:00','IMT Manesar','Haryana',28.378412,76.907017,'Mani Majra','Chandigarh',30.718153,76.833387);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (76,'Name 33','2013-09-25 09:00:00','Nand Nagri Terminal','New Delhi',28.691886,77.309951,'Sector 19 Chandigarh','Chandigarh',30.729368,76.792839);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (77,'Name 34','2013-09-25 14:45:00','Badshahpur','Haryana',28.432581,77.362369,'Nangal','Punjab',31.384563,76.380875);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (78,'Name 35','2013-09-25 09:45:00','Sector 106 Noida','Uttar Pradesh',28.535049,77.391125,'Sector 23 Chandigarh','Chandigarh',30.740287,76.766789);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (79,'Name 36','2013-09-25 06:45:00','Ujwa','New Delhi',28.563642,76.915191,'Sector 8 Chandigarh','Chandigarh',30.741061,76.798547);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (80,'Name 37','2013-09-25 14:30:00','Manesar','Haryana',28.35466,76.939978,'Baddi','Himachal Pradesh',30.96186,76.791145);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (81,'Name 38','2013-09-25 09:30:00','Bhajan Pura','New Delhi',28.703801,77.263881,'Sector 22 Chandigarh','Chandigarh',30.733794,76.771124);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (82,'Name 39','2013-09-25 08:00:00','Rohtak Rd A 1 Block Paschim Vihar','New Delhi',28.686371,77.098221,'Sector 14 Chandigarh','Chandigarh',30.761198,76.766832);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (83,'Name 40','2013-09-25 14:15:00','Unitech Infospace Dundahera Gurgaon','Haryana',28.50988,77.072028,'Sector 45 Chandigarh','Chandigarh',30.707822,76.75915);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (84,'Name 41','2013-09-25 13:00:00','South City I Sector 41Gurgaon','Haryana',28.45706777,77.06776491,'Sanjauli','Himachal Pradesh',31.104538,77.197445);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (85,'Name 42','2013-09-25 16:45:00','Paschim Vihar','Delhi',28.667847,77.093454,'Pinjore Graden','Haryana',30.793313,76.911651);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (86,'Name 43','2013-09-25 10:45:00','Sector 25 Faridabad','Uttar Pradesh',28.336946,77.305885,'Ranbaxy Laboratories Limited Mohali Sas Nagar','Chandigarh',30.72626,76.712732);
INSERT INTO `rides` (`id`,`user_name`,`travel_time`,`from_place`,`from_state`,`from_lattitude`,`from_longitude`,`to_place`,`to_state`,`to_latittude`,`to_longitude`) VALUES (87,'Name 44','2013-09-25 15:15:00','Narela','Delhi',28.850838,77.096702,'Ambala',' Haryana',30.381613,76.776382);
