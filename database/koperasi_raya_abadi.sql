-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 24, 2026 at 05:13 AM
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
-- Database: `koperasi_raya_abadi`
--
CREATE DATABASE IF NOT EXISTS `koperasi_raya_abadi`;
USE `koperasi_raya_abadi`;
-- --------------------------------------------------------

--
-- Table structure for table `aktivitas`
--

CREATE TABLE `aktivitas` (
  `id_aktivitas` int(11) NOT NULL,
  `waktu` datetime NOT NULL DEFAULT current_timestamp(),
  `id_user` int(11) DEFAULT NULL,
  `aktivitas` varchar(150) NOT NULL,
  `keterangan` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `anggota`
--

CREATE TABLE `anggota` (
  `id_anggota` int(11) NOT NULL,
  `no_anggota` varchar(30) NOT NULL,
  `nik` varchar(30) DEFAULT NULL,
  `nama` varchar(100) NOT NULL,
  `tempat_lahir` varchar(80) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `jenis_kelamin` enum('Laki-laki','Perempuan') DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `kota_kabupaten` varchar(100) DEFAULT NULL,
  `no_hp` varchar(20) DEFAULT NULL,
  `divisi` varchar(100) DEFAULT NULL,
  `tanggal_daftar` date NOT NULL,
  `status` enum('Calon','Aktif','Nonaktif') NOT NULL DEFAULT 'Aktif',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `anggota`
--

INSERT INTO `anggota` (`id_anggota`, `no_anggota`, `nik`, `nama`, `tempat_lahir`, `tanggal_lahir`, `jenis_kelamin`, `alamat`, `kota_kabupaten`, `no_hp`, `divisi`, `tanggal_daftar`, `status`, `created_at`, `updated_at`) VALUES
(1, 'A_001', NULL, 'sabilarrusdi', 'jakarta', '2004-06-24', 'Laki-laki', 'perumahan bukit waringin', 'Bogor', '05280788110', 'pengacara', '2026-05-24', 'Aktif', '2026-05-23 23:31:22', NULL),
(2, 'A_002', NULL, 'fulan', 'depok', '2000-05-02', 'Laki-laki', 'cilodong depok', 'depok', '082283201093', 'fullstack developer', '2026-05-23', 'Aktif', '2026-05-23 23:48:00', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `angsuran`
--

CREATE TABLE `angsuran` (
  `id_angsuran` int(11) NOT NULL,
  `id_pinjaman` int(11) NOT NULL,
  `angsuran_ke` int(11) NOT NULL,
  `tanggal_jatuh_tempo` date DEFAULT NULL,
  `tanggal_bayar` date NOT NULL,
  `jumlah_bayar` decimal(15,2) NOT NULL,
  `status` enum('Belum Bayar','Dibayar','Terlambat') NOT NULL DEFAULT 'Dibayar',
  `keterangan` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE `groups` (
  `id_group` int(11) NOT NULL,
  `nama_group` varchar(50) NOT NULL,
  `deskripsi` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `groups`
--

INSERT INTO `groups` (`id_group`, `nama_group`, `deskripsi`, `created_at`) VALUES
(1, 'Super Admin', 'Akses penuh ke seluruh menu', '2026-05-23 22:57:00'),
(2, 'Pengguna', 'Akses operasional terbatas', '2026-05-23 22:57:00'),
(3, 'Admin', 'Akses administrasi dan pengelolaan data', '2026-06-03 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `jenis_simpanan`
--

CREATE TABLE `jenis_simpanan` (
  `id_jenis_simpanan` int(11) NOT NULL,
  `nama_jenis` varchar(50) NOT NULL,
  `keterangan` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `jenis_simpanan`
--

INSERT INTO `jenis_simpanan` (`id_jenis_simpanan`, `nama_jenis`, `keterangan`) VALUES
(1, 'Simpanan Pokok', 'Simpanan awal anggota'),
(2, 'Simpanan Wajib', 'Simpanan rutin anggota'),
(3, 'Simpanan Sukarela', 'Simpanan tambahan anggota');

-- --------------------------------------------------------

--
-- Table structure for table `pengaturan_koperasi`
--

CREATE TABLE `pengaturan_koperasi` (
  `id_pengaturan` int(11) NOT NULL,
  `nama_koperasi` varchar(150) NOT NULL,
  `alamat` text DEFAULT NULL,
  `no_telepon` varchar(30) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `mata_uang` varchar(30) NOT NULL DEFAULT 'Rupiah(Rp)',
  `persen_bunga` decimal(5,2) NOT NULL DEFAULT 0.00,
  `simpanan_wajib` decimal(15,2) NOT NULL DEFAULT 0.00,
  `tahun_buku` year(4) NOT NULL,
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `pengaturan_koperasi`
--

INSERT INTO `pengaturan_koperasi` (`id_pengaturan`, `nama_koperasi`, `alamat`, `no_telepon`, `email`, `mata_uang`, `persen_bunga`, `simpanan_wajib`, `tahun_buku`, `updated_at`) VALUES
(1, 'Koperasi Raya Abadi Saudara', NULL, NULL, NULL, 'Rupiah(Rp)', 0.00, 0.00, '2026', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `pinjaman`
--

CREATE TABLE `pinjaman` (
  `id_pinjaman` int(11) NOT NULL,
  `no_pinjaman` varchar(30) NOT NULL,
  `id_anggota` int(11) NOT NULL,
  `tanggal_pinjaman` date NOT NULL,
  `jumlah_pinjaman` decimal(15,2) NOT NULL,
  `tenor_bulan` int(11) NOT NULL,
  `bunga_persen` decimal(5,2) NOT NULL DEFAULT 0.00,
  `angsuran_per_bulan` decimal(15,2) NOT NULL,
  `tujuan` varchar(255) DEFAULT NULL,
  `status` enum('Pengajuan','Aktif','Lunas','Ditolak') NOT NULL DEFAULT 'Pengajuan',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `agunan`
--

CREATE TABLE `agunan` (
  `id_agunan` int(11) NOT NULL,
  `id_pinjaman` int(11) NOT NULL,
  `jenis_agunan` varchar(100) NOT NULL,
  `nilai_taksir` decimal(15,2) NOT NULL DEFAULT 0.00,
  `tanggal` date NOT NULL,
  `status` varchar(30) NOT NULL DEFAULT 'Aktif',
  `deskripsi` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `simpanan`
--

CREATE TABLE `simpanan` (
  `id_simpanan` int(11) NOT NULL,
  `id_anggota` int(11) NOT NULL,
  `id_jenis_simpanan` int(11) NOT NULL,
  `tanggal` date NOT NULL,
  `nominal` decimal(15,2) NOT NULL,
  `saldo` decimal(15,2) NOT NULL DEFAULT 0.00,
  `keterangan` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id_transaksi` int(11) NOT NULL,
  `tanggal` datetime NOT NULL DEFAULT current_timestamp(),
  `jenis_transaksi` enum('Simpanan','Pinjaman','Angsuran','Lainnya') NOT NULL,
  `id_anggota` int(11) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL,
  `referensi_tabel` varchar(50) DEFAULT NULL,
  `referensi_id` int(11) DEFAULT NULL,
  `debet` decimal(15,2) NOT NULL DEFAULT 0.00,
  `kredit` decimal(15,2) NOT NULL DEFAULT 0.00,
  `keterangan` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id_user` int(11) NOT NULL,
  `id_group` int(11) NOT NULL,
  `id_anggota` int(11) DEFAULT NULL,
  `nama` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `jabatan` varchar(100) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `status` enum('Aktif','Nonaktif') NOT NULL DEFAULT 'Aktif',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id_user`, `id_group`, `id_anggota`, `nama`, `email`, `jabatan`, `password_hash`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, NULL, 'admin', 'admin@klp2.com', 'Ketua Koperasi', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Aktif', '2026-05-23 22:57:00', NULL),
(2, 2, NULL, 'user', 'user@example.com', 'Staff', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 'Aktif', '2026-05-24 03:01:32', NULL);

-- --------------------------------------------------------

--
-- Stand-in structure for view `v_sisa_pinjaman`
-- (See below for the actual view)
--
CREATE TABLE `v_sisa_pinjaman` (
`id_pinjaman` int(11)
,`no_pinjaman` varchar(30)
,`id_anggota` int(11)
,`jumlah_pinjaman` decimal(15,2)
,`total_dibayar` decimal(37,2)
,`sisa_pinjaman` decimal(38,2)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `v_total_pinjaman_anggota`
-- (See below for the actual view)
--
CREATE TABLE `v_total_pinjaman_anggota` (
`id_anggota` int(11)
,`no_anggota` varchar(30)
,`nama` varchar(100)
,`total_pinjaman_aktif` decimal(37,2)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `v_total_simpanan_anggota`
-- (See below for the actual view)
--
CREATE TABLE `v_total_simpanan_anggota` (
`id_anggota` int(11)
,`no_anggota` varchar(30)
,`nama` varchar(100)
,`total_simpanan` decimal(37,2)
);

-- --------------------------------------------------------

--
-- Structure for view `v_sisa_pinjaman`
--
DROP TABLE IF EXISTS `v_sisa_pinjaman`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_sisa_pinjaman`  AS SELECT `p`.`id_pinjaman` AS `id_pinjaman`, `p`.`no_pinjaman` AS `no_pinjaman`, `p`.`id_anggota` AS `id_anggota`, `p`.`jumlah_pinjaman` AS `jumlah_pinjaman`, coalesce(sum(`ang`.`jumlah_bayar`),0) AS `total_dibayar`, `p`.`jumlah_pinjaman`- coalesce(sum(`ang`.`jumlah_bayar`),0) AS `sisa_pinjaman` FROM (`pinjaman` `p` left join `angsuran` `ang` on(`ang`.`id_pinjaman` = `p`.`id_pinjaman`)) GROUP BY `p`.`id_pinjaman`, `p`.`no_pinjaman`, `p`.`id_anggota`, `p`.`jumlah_pinjaman` ;

-- --------------------------------------------------------

--
-- Structure for view `v_total_pinjaman_anggota`
--
DROP TABLE IF EXISTS `v_total_pinjaman_anggota`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_total_pinjaman_anggota`  AS SELECT `a`.`id_anggota` AS `id_anggota`, `a`.`no_anggota` AS `no_anggota`, `a`.`nama` AS `nama`, coalesce(sum(case when `p`.`status` = 'Aktif' then `p`.`jumlah_pinjaman` else 0 end),0) AS `total_pinjaman_aktif` FROM (`anggota` `a` left join `pinjaman` `p` on(`p`.`id_anggota` = `a`.`id_anggota`)) GROUP BY `a`.`id_anggota`, `a`.`no_anggota`, `a`.`nama` ;

-- --------------------------------------------------------

--
-- Structure for view `v_total_simpanan_anggota`
--
DROP TABLE IF EXISTS `v_total_simpanan_anggota`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_total_simpanan_anggota`  AS SELECT `a`.`id_anggota` AS `id_anggota`, `a`.`no_anggota` AS `no_anggota`, `a`.`nama` AS `nama`, coalesce(sum(`s`.`nominal`),0) AS `total_simpanan` FROM (`anggota` `a` left join `simpanan` `s` on(`s`.`id_anggota` = `a`.`id_anggota`)) GROUP BY `a`.`id_anggota`, `a`.`no_anggota`, `a`.`nama` ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `aktivitas`
--
ALTER TABLE `aktivitas`
  ADD PRIMARY KEY (`id_aktivitas`),
  ADD KEY `fk_aktivitas_user` (`id_user`);

--
-- Indexes for table `anggota`
--
ALTER TABLE `anggota`
  ADD PRIMARY KEY (`id_anggota`),
  ADD UNIQUE KEY `no_anggota` (`no_anggota`),
  ADD UNIQUE KEY `nik` (`nik`);

--
-- Indexes for table `angsuran`
--
ALTER TABLE `angsuran`
  ADD PRIMARY KEY (`id_angsuran`),
  ADD UNIQUE KEY `uq_angsuran_pinjaman_ke` (`id_pinjaman`,`angsuran_ke`);

--
-- Indexes for table `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`id_group`),
  ADD UNIQUE KEY `nama_group` (`nama_group`);

--
-- Indexes for table `jenis_simpanan`
--
ALTER TABLE `jenis_simpanan`
  ADD PRIMARY KEY (`id_jenis_simpanan`),
  ADD UNIQUE KEY `nama_jenis` (`nama_jenis`);

--
-- Indexes for table `pengaturan_koperasi`
--
ALTER TABLE `pengaturan_koperasi`
  ADD PRIMARY KEY (`id_pengaturan`);

--
-- Indexes for table `pinjaman`
--
ALTER TABLE `pinjaman`
  ADD PRIMARY KEY (`id_pinjaman`),
  ADD UNIQUE KEY `no_pinjaman` (`no_pinjaman`),
  ADD KEY `fk_pinjaman_anggota` (`id_anggota`);

--
-- Indexes for table `agunan`
--
ALTER TABLE `agunan`
  ADD PRIMARY KEY (`id_agunan`),
  ADD KEY `fk_agunan_pinjaman` (`id_pinjaman`);

--
-- Indexes for table `simpanan`
--
ALTER TABLE `simpanan`
  ADD PRIMARY KEY (`id_simpanan`),
  ADD KEY `fk_simpanan_anggota` (`id_anggota`),
  ADD KEY `fk_simpanan_jenis` (`id_jenis_simpanan`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id_transaksi`),
  ADD KEY `fk_transaksi_anggota` (`id_anggota`),
  ADD KEY `fk_transaksi_user` (`id_user`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `fk_users_group` (`id_group`),
  ADD KEY `fk_users_anggota` (`id_anggota`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `aktivitas`
--
ALTER TABLE `aktivitas`
  MODIFY `id_aktivitas` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `anggota`
--
ALTER TABLE `anggota`
  MODIFY `id_anggota` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `angsuran`
--
ALTER TABLE `angsuran`
  MODIFY `id_angsuran` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `groups`
--
ALTER TABLE `groups`
  MODIFY `id_group` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `jenis_simpanan`
--
ALTER TABLE `jenis_simpanan`
  MODIFY `id_jenis_simpanan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `pengaturan_koperasi`
--
ALTER TABLE `pengaturan_koperasi`
  MODIFY `id_pengaturan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `pinjaman`
--
ALTER TABLE `pinjaman`
  MODIFY `id_pinjaman` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `agunan`
--
ALTER TABLE `agunan`
  MODIFY `id_agunan` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `simpanan`
--
ALTER TABLE `simpanan`
  MODIFY `id_simpanan` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id_transaksi` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `aktivitas`
--
ALTER TABLE `aktivitas`
  ADD CONSTRAINT `fk_aktivitas_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `angsuran`
--
ALTER TABLE `angsuran`
  ADD CONSTRAINT `fk_angsuran_pinjaman` FOREIGN KEY (`id_pinjaman`) REFERENCES `pinjaman` (`id_pinjaman`) ON UPDATE CASCADE;

--
-- Constraints for table `pinjaman`
--
ALTER TABLE `pinjaman`
  ADD CONSTRAINT `fk_pinjaman_anggota` FOREIGN KEY (`id_anggota`) REFERENCES `anggota` (`id_anggota`) ON UPDATE CASCADE;

--
-- Constraints for table `agunan`
--
ALTER TABLE `agunan`
  ADD CONSTRAINT `fk_agunan_pinjaman` FOREIGN KEY (`id_pinjaman`) REFERENCES `pinjaman` (`id_pinjaman`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `simpanan`
--
ALTER TABLE `simpanan`
  ADD CONSTRAINT `fk_simpanan_anggota` FOREIGN KEY (`id_anggota`) REFERENCES `anggota` (`id_anggota`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_simpanan_jenis` FOREIGN KEY (`id_jenis_simpanan`) REFERENCES `jenis_simpanan` (`id_jenis_simpanan`) ON UPDATE CASCADE;

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `fk_transaksi_anggota` FOREIGN KEY (`id_anggota`) REFERENCES `anggota` (`id_anggota`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_transaksi_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `fk_users_anggota` FOREIGN KEY (`id_anggota`) REFERENCES `anggota` (`id_anggota`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_users_group` FOREIGN KEY (`id_group`) REFERENCES `groups` (`id_group`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
