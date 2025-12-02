-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 02, 2025 at 03:25 PM
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
-- Database: `db_recipe`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `nama`) VALUES
(1, 'Breakfast'),
(2, 'Lunch'),
(3, 'Dinner'),
(4, 'Snack'),
(5, 'Dessert'),
(6, 'Beverage'),
(7, 'Appetizer');

-- --------------------------------------------------------

--
-- Table structure for table `comments`
--

CREATE TABLE `comments` (
  `id` int(11) NOT NULL,
  `recipe_id` int(11) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `user_name` varchar(100) NOT NULL,
  `rating` float NOT NULL,
  `comment_text` text NOT NULL,
  `like_count` int(11) DEFAULT 0,
  `created_at` datetime DEFAULT current_timestamp(),
  `dislike_count` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `comments`
--

INSERT INTO `comments` (`id`, `recipe_id`, `parent_id`, `user_id`, `user_name`, `rating`, `comment_text`, `like_count`, `created_at`, `dislike_count`) VALUES
(15, 1, 0, NULL, 'Sandy', 5, 'yooo', 3, '2025-11-27 11:18:58', 0),
(16, 1, 15, NULL, 'Sandy', 5, 'kelass', 2, '2025-11-27 11:19:07', 0),
(17, 1, 0, NULL, 'Sandy', 5, 'kelasss', 0, '2025-11-27 11:20:30', 0),
(18, 6, 0, NULL, 'Sandy', 5, 'tes', 0, '2025-12-02 09:40:16', 0),
(22, 4, 0, 4, 'd', 5, 'tes', 2, '2025-12-02 09:53:15', 0),
(23, 4, 0, 2, 'b', 5, 'b', 0, '2025-12-02 09:54:22', 0),
(24, 4, 22, 2, 'b', 5, 'tes', 0, '2025-12-02 09:54:27', 0),
(25, 4, 0, 3, 'c', 3, 'c', 0, '2025-12-02 10:01:09', 0),
(26, 4, 23, 3, 'c', 4, 'asd', 0, '2025-12-02 10:01:17', 0),
(27, 4, 0, 3, 'c', 1, 'asd', 0, '2025-12-02 10:01:57', 0),
(28, 4, 0, 1, 'a', 5, 'qweqweqwe', 0, '2025-12-02 10:04:42', 0);

-- --------------------------------------------------------

--
-- Table structure for table `favorites`
--

CREATE TABLE `favorites` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `recipe_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `favorites`
--

INSERT INTO `favorites` (`id`, `user_id`, `recipe_id`, `created_at`) VALUES
(4, 1, 3, '2025-12-01 13:42:01'),
(11, 2, 6, '2025-12-01 14:55:57'),
(18, 1, 8, '2025-12-01 16:51:28'),
(21, 3, 4, '2025-12-02 03:03:37'),
(26, 1, 11, '2025-12-02 14:16:50');

-- --------------------------------------------------------

--
-- Table structure for table `recipes`
--

CREATE TABLE `recipes` (
  `id` int(11) NOT NULL,
  `nama_resep` varchar(150) NOT NULL,
  `gambar` varchar(255) DEFAULT NULL,
  `bahan` text NOT NULL,
  `instruksi` text NOT NULL,
  `posted_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `porsi` varchar(50) NOT NULL,
  `durasi` varchar(50) NOT NULL,
  `kategori` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`kategori`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `recipes`
--

INSERT INTO `recipes` (`id`, `nama_resep`, `gambar`, `bahan`, `instruksi`, `posted_by`, `created_at`, `porsi`, `durasi`, `kategori`) VALUES
(1, 'Tahu campur', 'tahu_campur.jpg', '1. 500 gram daging sengkel / sandung lamur\r\n2. 500 gram thetelan / koyor\r\n3. 1 ruas jahe\r\n4. 1 ruas lengkuas\r\n5. 1 batang serai\r\n6. 3 daun salam\r\n7. 3 lembar daun jeruk\r\n\r\nBumbu Kuah : haluskan\r\n1. 10 siung bawang merah\r\n2. 10 siung bawang putih\r\n3. 5 buah kemiri\r\n4. 1 sdm ketumbar bubuk\r\n5. 2 ruas jari kunyit\r\n6. 2 buah kencur\r\n7. 1 ruas besar Jahe\r\n8. 1 jempol lengkuas\r\n\r\nKrenyem Singkong :\r\n1. 500 gram singkong parut\r\n2. 1/4 kelapa parut\r\n3. 2 sdm bumbu halus\r\n\r\nPelengkap lain :\r\n1. 10 buah Tahu Goreng kecil\r\n2. 100 gram Kecambah\r\n3. 1 bonggol Selada keriting\r\n4. bila suka Mie kuning\r\n5. secukupnya Lontong\r\n6. Kerupuk\r\n\r\nSambal Petis :\r\n1. 3 siung bawang putih iris dan goreng\r\n2. 15 buah cabe rawit\r\n3. 2 sdm petis udang\r\n4. 100 ml air', '1. Siapkan bahan bahan. rebus daging dan tetelan/ koyor dengan jahe, dll nya hingga empuk.\r\n2. Siapkan bumbu” haluskan dan tumis hingga harum. \r\n3. Buat krenyem / perkedel singkok, campur singkong parut, kelapa parut dan 2 sdm bumbu, beri garam bila perlu. Bisa juga menggunakan bumbu racik ayam.\r\n4. Bulatkan seperi perkedel, buat ukuran 1 porsi 1 perkedel, bisa langsung digoreng atau kukus dulu bila tdk langsung dikonsumsi. \r\n5. Masak rebusan daging dengan bumbu halus yg sdh ditumis, koreksi rasa. Potong daging kecil / sesuai selera.. ini dagingnya beda dengan yang saya rebus awal, ada drama gosong sampai kebakar panciku karena kutinggal keluar dan lupa.\r\n6. Goreng tahu, masak sambal petisnya. dan siapkan bahan bahan lainnya. Lontong, Mie, Kecambah dan Selada. \r\n7. Tata dipiring, beri potongan daging dan kuah yang mendidih.. yummy.. yummy..', 1, '2025-11-28 07:53:14', '4-6 porsi', '60 menit', '[\r\n    \"main\",\"indonesian\",\"east_javanese\",\"soup\",\r\n    \"Lunch\",\"Dinner\"\r\n]'),
(3, 'Tahu tek', 'tahu_tek.jpg', '1. 2 buah tahu putih\r\n2. 3 buah Lontong\r\n3. 1 buah Kentang\r\n4. 1 genggam Tauge\r\n5. 3 butir Telur ayam\r\n6. 1 buah Timun\r\n7. secukupnya kerupuk\r\n8. secukupnya Minyak goreng\r\n9. sdm Kacang tanah goreng\r\n10. 1 siung Bawang putih\r\n11. 3 buah Cabe rawit (sesuaikan selera)\r\n12. 1 sdm Petis udang\r\n13. 2 sdm Kecap Manis Indofood\r\n14. 100 ml Air matang\r\n15. 1/2 sdt Garam\r\n16. secukupnya garam dan gula\r\n17. secukupnya Bawang goreng dan irisan seledri', '1. Siapkan semua bahan......cuci bersih dan tahu potong dadu.\r\n2. Ulek kacang, bawang putih juga rawit(aslinya bawang putih mentah cm kalo saya digoreng sebentar) garam dan gula,kalo sudah halus tambahkan bumbu petis.\r\n3. Beri air sedikit demi sedikit tambahkan juga Kecap Manis Indofood aduk rata koreksi rasa!!!\r\n4. Rebus sebentar toge yang sudah dicuci bersih kemudian tiriskan, kocok telur dan sejumput garam kocok lepas.\r\n5. Masukkan potongan tahu ke dalam kocokan telur, kemudian goreng hingga matang. kentang potong dadu lalu goreng hingga matang juga!!!\r\n6. Di piring potong lontong, tambahkan toge,tahu, kentang, timun, dan tata di piring.\r\n7. Siram dengan bumbu petis kacang, taburi bawang goreng dan irisan seledri.\r\n8. Tambahkan kerupuk di atasnya, sajikan', 2, '2025-11-28 12:20:10', '3 porsi', '20 menit', '[\r\n    \"snack\",\"street_food\",\"indonesian\",\"east_javanese\",\r\n    \"Snack\",\"Lunch\"\r\n]'),
(4, 'Steak', 'steak.jpg', '1. 1 kg ayam filet dada & filet paha\r\n2. 300 g kentang\r\n3. 200 g buncis\r\n\r\nBahan marinasi ayam 🌿\r\n\r\n1. 7 sdm saos tiram\r\n2. 3 sdm saos sambal\r\n3. 1 sdt kaldu jamur\r\n4. Sejumput garam\r\n5. 1 sdm kecap manis\r\n6. 1 butir Telur\r\n\r\nBahan saos steak 🧅\r\n1. 250 g saos sapi lada hitam\r\n2. 3 sdm saos sambal\r\n3. 1 buah bawang bombay\r\n4. Gula pasir\r\n5. Air\r\n\r\nPelengkap :\r\nWortel, buncis, kentang', '1. Cuci bersih daging ayam, lumurin dengan jeruk lemon, diamkan 15 menit cuci kembali tiriskan, iris filet agak tipis, jgn terlalu tebal supaya matang luar dalamnya dan tusuk2 dengan garpu. \r\n2. Masukkan semua bumbu marinasi ayam aduk merata.\r\n3. Susun potongan filet ayam berbumbu marinasi dalam wadah dan masukkan ke dalam kulkas suapay meresap.\r\n4. Saos steak : Panaskan minyak dan tumis bawang putih dan bawang bombay hingga harum lalu tambahkan saos lada hitam, saos sambal, sejumput gula pasir aduk merata, masak sebentar saja.\r\n5. Siapkan teflon atau pemanggang olesin permukaannya dengan margarin/ minyak, ambil beberapa potong daging ayam filet (sesuai kebutuhan) dan panggang dengan api sedang sambil dibolak balik hingga berwarna kecoklatan/ matang.\r\n6. Susun daging ayam yang sudah matang di atas piring saji siram dengan saos yang tadi kita masak, tambahkan buncis, kentang, wortel.', 3, '2025-11-28 12:25:08', '4-5 porsi', '60-75 menit', '[\r\n    \"main\",\"western\",\"grill\",\"protein\",\r\n    \"Lunch\",\"Dinner\"\r\n]'),
(5, 'Rawon', 'rawon.jpg', 'Bahan Utama:\r\n1. 500 gram tetelan daging (daging sandung lamur). Aku rebus dengan metode 5.30.7\r\n2. 1,5 liter air untuk merebus daging\r\n3. 1 bungkus Bumbu Jadi Rawon\r\n4. 2 sdm minyak untuk menumis\r\n\r\nBumbu Cemplung:\r\n1. 1 buah bawang bombay\r\n2. 2 batang serai digeprek\r\n3. 1 jempol lengkuas, digeprek\r\n4. 5 lembar daun jeruk\r\n\r\nBumbu Penyedap:\r\n1. 1 sachet kaldu daging\r\n2. 1 sdt penyedap jamur\r\n3. 2 sdt garam\r\n4. 1/2 sdt gula\r\n5. 1/2 sdt Ketumbar bubuk\r\n\r\nBahan Pelengkap:\r\n1. Bawang Goreng\r\n2. Toge pendek\r\n3. Telur asin\r\n4. Sambal Matang', '1. Siapkan daging yang sudah direbus dengan metode 5.30.7. Sebelum direbus, daging sudah dipotong-potong kotak-kotak. siapkan bahan-bahan bumbu.\r\n2. Siapkan bumbu cemplungnya. Rajang Bawang bombay. Lalu geprek Lengkuas dan Sereh. Sisihkan. panaskan minyak lalu tumis bawang Bombay sampai layu. lalu masukkan bumbu Jadi Rawon. Tambahkan air dan aduk sampai rata.\r\n3. Masukkan tumisan Bumbu ke dalam Rebusan Daging. Lalu masukkan semua bahan Bumbu Cemplug. Lalu masukkan Air Asam.\r\n4. Masak Sampai mendidih dan masukkan semua bahan Bumbu Penyedap. Masak kembali sampai mendidih. Setelah itu lanjutkan masak dengan api kecil sekitar 10 menit agar bumbunya meresap. Terkahir lakukan test rasa, koreksi rasa jika diperlukan.\r\n5. Sambil menunggu matang, siapkan bahan-bahan pelengkap. Setelah semuanya siap. Rawon siap disajikan.', 4, '2025-11-28 12:36:12', '5 porsi', '120 menit', '[\r\n    \"main\",\"indonesian\",\"east_javanese\",\"soup\",\"beef\",\r\n    \"Lunch\",\"Dinner\"\r\n]'),
(6, 'Nasi kebuli', 'nasi_kebuli.jpg', '1. 3 butir Kapulaga\r\n2. 1/2 sdt Bubuk kari\r\n3. 1/2 sdt Bubuk kunyit\r\n4. 1/2 sdt Merica bubuk\r\n5. 1 sdt Ketumbar bubuk\r\n6. 1/2 sdt Jinten bubuk\r\n7. 1/4 butir, parut Biji pala\r\n8. 1 batang kecil Kayu manis\r\n9. 3 butir Cengkeh\r\n10. 250 gr Beras basmati\r\n11. 400 ml Air\r\n12. 2 sdm Minyak goreng\r\n13. secukupnya Penyedap rasa sapi\r\n14. 1 buah Bawang bombay\r\n15. 3 siung Bawang putih\r\n16. 1 sachet (65 ml) Santan kara\r\n17. 250 gr Daging sapi', '1. Tumis bawang putih dan bawang bombay dengan minyak hingga harum.\r\n2. Masukkan daging sapi, aduk hingga berubah warna.\r\n3. Tambahkan semua rempah kebuli, santan, air, dan penyedap rasa sapi. Masak hingga mendidih.\r\n4. Siram beras basmati mentah dengan kuah dan daging ke dalam ricecooker.\r\n5. Masak di ricecooker hingga nasi matang.\r\n6. Setelah matang, ambil dagingnya dan panggang di airfryer 10 menit suhu 200°C.', 5, '2025-11-28 12:36:12', '2 porsi', '60 menit', '[\r\n    \"main\",\"rice\",\"middle_eastern\",\"spiced\",\r\n    \"Lunch\",\"Dinner\"\r\n]'),
(7, 'Nasi goreng', 'nasi_goreng.jpg', '1. 2 cup nasi\r\n2. 4 siung bawang merah iris\r\n3. 3 siung Bawang putih\r\n4. 1 buah tomat\r\n5. 1 sdt Garam\r\n6. secukupnya Kecap', '1. Haluskan tomat, bawang putih, garam\r\n2. Panaskan minyak\r\n3. Goreng irisan bawang merah\r\n4. Masukkan bumbu tomat dll yg sudah dihaluskan\r\n5. Masukkan nasi\r\n6. Masukkan kecap\r\n7. Aduk merata\r\n8. Jangan lupa telor dadar + bawang pre + garam digoreng dulu', 6, '2025-11-28 12:36:12', '4 porsi', '15 menit', '[\r\n    \"main\",\"rice\",\"indonesian\",\"quick_meal\",\"street_food\",\r\n    \"Breakfast\",\"Lunch\",\"Dinner\"\r\n]'),
(8, 'Nasi campur', 'nasi_campur.jpg', '1. 200 gr nasi\r\n2. 100 gr beef slice\r\n3. 70 gr bayam\r\n4. 50 gr tauge\r\n5. 50 gr jamur kancing\r\n6. 40 gr wortel\r\n\r\nBumbu untuk daging :\r\n1. 1 siung bawang putih\r\n2. 1/2 sdm saus tiram\r\n3. 1/4 sdt gula pasir\r\n4. Sejumput garam dan lada\r\n\r\nSaus Gochujang :\r\n1. 2 sdm gochujang\r\n2. 2 sdm air\r\n3. 1 siung bawang putih\r\n4. 1 sdt minyak wijen\r\n5. 1/2 sdt cuka\r\n6. 1/2 sdt kecap asin\r\n7. Sejumput gula\r\n\r\nPelengkap :\r\n1. 1 pcs telur ceplok\r\n2. 1/2 sdt wijen sangrai', '1. Masak wortel dan tauge dengan sedikit air dan sejumput garam hingga matang.\r\n2. Masak jamur kancing dengan sedikit air dan sejumput garam hingga matang.\r\n3. Masak bayam dengan sedikit air dan sejumput garam hingga matang.\r\n4. Untuk daging, tumis bawang putih hingga harum, masukkan daging dan semua bumbu. Masak sebentar hingga matang.\r\n5. Siapkan nasi yang sudah dicetak, tata semua sayur dan daging yang sudah dimasak. Tambahkan telur ceplok.\r\n6. Taburi wijen. Untuk saus gochujang tinggal dicampur saja semua bahannya di mangkok, jangan lupa koreksi rasa. Siram saus gochujang di atas nasi campur.\r\n7. Bibimbab/nasi campur korea siap disantap ', 7, '2025-11-28 12:36:12', '1-2 porsi', '20 menit', '[\r\n    \"main\",\"rice\",\"indonesian\",\"mixed\",\r\n    \"Lunch\",\"Dinner\"\r\n]'),
(9, 'Karage', 'karage.jpg', '1. 1 ekor Fillet paha ayam\r\n2. secukupnya Soyu\r\n3. secukupnya Ponzu\r\n4. secukupnya Garam\r\n5. 1 siung Jahe\r\n6. secukupnya Gula\r\n7. secukupnya Air putih\r\n8. secukupnya Tepung kentang\r\n9. secukupnya Tepung maizena\r\n10. secukupnya air perasan lemon\r\n11. secukupnua Bawang putih', '1. Rendam potongan fillet ayam dengan air garam selama 1 jam. \r\n2. Iris kecil kecil sesuai selera ayamnya. \r\n3. Taruh ke wadah mangkok untuk diberi campuran bumbu. \r\n4. Taru parutan jahe, garam, air gula, air lemon, ponzu, soyu, parutan bawang putih tunggu 5 menit bejek2. \r\n5.Baluri adonan ayam dengan sedikit tepung maizena\r\n6. Baluri ayam denhan tepung kentang\r\n7. Panaskan minyak\r\n8. Goreng dengan suhu rendah selama 4 menit. \r\n9. Goreng dengan suhu tinggi hingga coklat\r\n10. Selesai, tiriskan\r\n11. Bisa beri toping mayonaise, irisan kubis yang diberi lemon', 8, '2025-11-28 12:36:12', '2 porsi', '90 menit', '[\r\n    \"snack\",\"japanese\",\"fried\",\"protein\",\r\n    \"Snack\",\"Lunch\"\r\n]'),
(10, 'Dimsum', 'dimsum.jpg', '1. 250 gr daging ayam fillet (cincang halus)\r\n2. 100 gr udang kupas (cincang halus)\r\n3. 2 siung bawang putih (haluskan)\r\n4. 1 batang daun bawang (iris halus)\r\n5. 1 sdm saus tiram\r\n6. 1 sdm minyak wijen\r\n7. 1 sdm kecap asin\r\n8. 1 sdm tepung maizena\r\n9. 1/2 sdt garam\r\n10. 1/2 sdt merica\r\n11. kulit pangsit/dimsum secukupnya (belii ya ini)\r\n12. 1 wortel potong (diiris tipis2)', '1. Dalam mangkuk, campurkan ayam cincang, udang cincang, bawang putih, daun bawang, dan semua bumbu hingga merata.\r\n2. Bungkus dimsum nya dan Ambil 1 lembar kulit pangsit, beri 1 sendok teh adonan di tengahnya. Bentuk seperti mangkuk kecil, rapatkan pinggirnya.\r\n3. Diatas adona dimsun beri potongan wortel\r\n4. Kukus dehh, Susun dimsum dalam kukusan byang sudah dialasi baking paper/tanpa alas juga bisa kok\r\n5. Kukus selama 20 menit.\r\n6. Dimsum siap disajikan dengan saus sambal atau saus dimsum!\r\n7. Happy cooking', 9, '2025-11-28 12:36:12', '4 porsi', '30 menit', '[\r\n    \"snack\",\"chinese\",\"steamed\",\r\n    \"Snack\",\"Lunch\"\r\n]'),
(11, 'Batagor', 'batagor.jpg', '1. 10 sdm tepung tapioka\r\n2. 4 sdm tepung terigu\r\n3. 1 butir telur ayam\r\n4. 4 siung bawang putih (uleg)\r\n5. 1 batang daun bawang (iris)\r\n6. 1/2 sdt baking powder\r\n7. Secukupnya merica bubuk\r\n8. 1/4 sdt garam\r\n9. 1/4 sdt kaldu bubuk\r\n10. 100 ml air panas\r\n11. Secukupnya Kulit pangsit\r\n12. Secukupnya bumbu kacang', '1. Campur tepung, bawang putih, dan daun bawang dan bumbu tabur lainnya, aduk. Masukkan telur, aduk lagi. Lalu tuang air panas, aduk cepat.\r\n2. Ambil kulit pangsit, potong jadi 4 persegi kecil. Ambil 1 potongan kulit, beri isian. Goreng hingga matang kecoklatan dengan api kecil saja.\r\n3. Sajikan bersama bumbu kacang homemade. ', 10, '2025-11-28 12:58:05', '2-4 porsi', '30 menit', '[\r\n    \"snack\",\"street_food\",\"indonesian\",\"fried\",\r\n    \"Snack\"\r\n]');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `password`, `created_at`) VALUES
(1, 'a', 'a', '$2y$10$1rPA2QgCwPoVNREN.39gme2esYbqZteu4sv81z5q//3ihcpWBP4Ki', '2025-11-27 04:13:30'),
(2, 'b', 'b', '$2y$10$p/6MHyPhSq849WkKoZfdK.iuldCfYjQOl4BLz1sm5GbN6nHMvtcG6', '2025-11-27 04:14:27'),
(3, 'c', 'c', '$2y$10$8GrLZ.FcNgTssLvbLSl.QuRkpcuI9l2gUbHKX4KX18O2MiQLY3Vgm', '2025-12-01 03:02:26'),
(4, 'd', 'd', '$2y$10$SKc.CAr33OSHZBORZKl3COLDjtWT/8MwcyCb9PsU./W8GYmar9aBG', '2025-12-01 17:10:12');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `favorites`
--
ALTER TABLE `favorites`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `comments`
--
ALTER TABLE `comments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT for table `favorites`
--
ALTER TABLE `favorites`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
