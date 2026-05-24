CREATE DATABASE IF NOT EXISTS koperasi_raya_abadi
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE koperasi_raya_abadi;

CREATE TABLE groups (
  id_group INT AUTO_INCREMENT PRIMARY KEY,
  nama_group VARCHAR(50) NOT NULL UNIQUE,
  deskripsi VARCHAR(255) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE users (
  id_user INT AUTO_INCREMENT PRIMARY KEY,
  id_group INT NOT NULL,
  nama VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  status ENUM('Aktif', 'Nonaktif') NOT NULL DEFAULT 'Aktif',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_users_group
    FOREIGN KEY (id_group) REFERENCES groups(id_group)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE anggota (
  id_anggota INT AUTO_INCREMENT PRIMARY KEY,
  no_anggota VARCHAR(30) NOT NULL UNIQUE,
  nik VARCHAR(30) NULL UNIQUE,
  nama VARCHAR(100) NOT NULL,
  tempat_lahir VARCHAR(80) NULL,
  tanggal_lahir DATE NULL,
  jenis_kelamin ENUM('Laki-laki', 'Perempuan') NULL,
  alamat TEXT NULL,
  kota_kabupaten VARCHAR(100) NULL,
  no_hp VARCHAR(20) NULL,
  pekerjaan VARCHAR(100) NULL,
  tanggal_daftar DATE NOT NULL,
  status ENUM('Calon', 'Aktif', 'Nonaktif') NOT NULL DEFAULT 'Aktif',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE jenis_simpanan (
  id_jenis_simpanan INT AUTO_INCREMENT PRIMARY KEY,
  nama_jenis VARCHAR(50) NOT NULL UNIQUE,
  keterangan VARCHAR(255) NULL
) ENGINE=InnoDB;

CREATE TABLE simpanan (
  id_simpanan INT AUTO_INCREMENT PRIMARY KEY,
  id_anggota INT NOT NULL,
  id_jenis_simpanan INT NOT NULL,
  tanggal DATE NOT NULL,
  nominal DECIMAL(15,2) NOT NULL,
  saldo DECIMAL(15,2) NOT NULL DEFAULT 0,
  keterangan VARCHAR(255) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_simpanan_anggota
    FOREIGN KEY (id_anggota) REFERENCES anggota(id_anggota)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT fk_simpanan_jenis
    FOREIGN KEY (id_jenis_simpanan) REFERENCES jenis_simpanan(id_jenis_simpanan)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE pinjaman (
  id_pinjaman INT AUTO_INCREMENT PRIMARY KEY,
  no_pinjaman VARCHAR(30) NOT NULL UNIQUE,
  id_anggota INT NOT NULL,
  tanggal_pinjaman DATE NOT NULL,
  jumlah_pinjaman DECIMAL(15,2) NOT NULL,
  tenor_bulan INT NOT NULL,
  bunga_persen DECIMAL(5,2) NOT NULL DEFAULT 0,
  angsuran_per_bulan DECIMAL(15,2) NOT NULL,
  tujuan VARCHAR(255) NULL,
  status ENUM('Pengajuan', 'Aktif', 'Lunas', 'Ditolak') NOT NULL DEFAULT 'Pengajuan',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_pinjaman_anggota
    FOREIGN KEY (id_anggota) REFERENCES anggota(id_anggota)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE angsuran (
  id_angsuran INT AUTO_INCREMENT PRIMARY KEY,
  id_pinjaman INT NOT NULL,
  angsuran_ke INT NOT NULL,
  tanggal_jatuh_tempo DATE NULL,
  tanggal_bayar DATE NOT NULL,
  jumlah_bayar DECIMAL(15,2) NOT NULL,
  status ENUM('Belum Bayar', 'Dibayar', 'Terlambat') NOT NULL DEFAULT 'Dibayar',
  keterangan VARCHAR(255) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_angsuran_pinjaman
    FOREIGN KEY (id_pinjaman) REFERENCES pinjaman(id_pinjaman)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT uq_angsuran_pinjaman_ke UNIQUE (id_pinjaman, angsuran_ke)
) ENGINE=InnoDB;

CREATE TABLE transaksi (
  id_transaksi INT AUTO_INCREMENT PRIMARY KEY,
  tanggal DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  jenis_transaksi ENUM('Simpanan', 'Pinjaman', 'Angsuran', 'Lainnya') NOT NULL,
  id_anggota INT NULL,
  referensi_tabel VARCHAR(50) NULL,
  referensi_id INT NULL,
  debet DECIMAL(15,2) NOT NULL DEFAULT 0,
  kredit DECIMAL(15,2) NOT NULL DEFAULT 0,
  keterangan VARCHAR(255) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_transaksi_anggota
    FOREIGN KEY (id_anggota) REFERENCES anggota(id_anggota)
    ON UPDATE CASCADE
    ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE pengaturan_koperasi (
  id_pengaturan INT AUTO_INCREMENT PRIMARY KEY,
  nama_koperasi VARCHAR(150) NOT NULL,
  alamat TEXT NULL,
  no_telepon VARCHAR(30) NULL,
  email VARCHAR(100) NULL,
  mata_uang VARCHAR(30) NOT NULL DEFAULT 'Rupiah(Rp)',
  persen_bunga DECIMAL(5,2) NOT NULL DEFAULT 0,
  tahun_buku YEAR NOT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE hak_akses (
  id_hak_akses INT AUTO_INCREMENT PRIMARY KEY,
  id_group INT NOT NULL,
  nama_menu VARCHAR(50) NOT NULL,
  boleh_lihat TINYINT(1) NOT NULL DEFAULT 0,
  boleh_tambah TINYINT(1) NOT NULL DEFAULT 0,
  boleh_edit TINYINT(1) NOT NULL DEFAULT 0,
  boleh_hapus TINYINT(1) NOT NULL DEFAULT 0,
  boleh_cetak TINYINT(1) NOT NULL DEFAULT 0,
  CONSTRAINT fk_hak_akses_group
    FOREIGN KEY (id_group) REFERENCES groups(id_group)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT uq_hak_akses_group_menu UNIQUE (id_group, nama_menu)
) ENGINE=InnoDB;

CREATE TABLE aktivitas (
  id_aktivitas INT AUTO_INCREMENT PRIMARY KEY,
  waktu DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  id_user INT NULL,
  aktivitas VARCHAR(150) NOT NULL,
  keterangan VARCHAR(255) NULL,
  CONSTRAINT fk_aktivitas_user
    FOREIGN KEY (id_user) REFERENCES users(id_user)
    ON UPDATE CASCADE
    ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE VIEW v_total_simpanan_anggota AS
SELECT
  a.id_anggota,
  a.no_anggota,
  a.nama,
  COALESCE(SUM(s.nominal), 0) AS total_simpanan
FROM anggota a
LEFT JOIN simpanan s ON s.id_anggota = a.id_anggota
GROUP BY a.id_anggota, a.no_anggota, a.nama;

CREATE VIEW v_total_pinjaman_anggota AS
SELECT
  a.id_anggota,
  a.no_anggota,
  a.nama,
  COALESCE(SUM(CASE WHEN p.status = 'Aktif' THEN p.jumlah_pinjaman ELSE 0 END), 0) AS total_pinjaman_aktif
FROM anggota a
LEFT JOIN pinjaman p ON p.id_anggota = a.id_anggota
GROUP BY a.id_anggota, a.no_anggota, a.nama;

CREATE VIEW v_sisa_pinjaman AS
SELECT
  p.id_pinjaman,
  p.no_pinjaman,
  p.id_anggota,
  p.jumlah_pinjaman,
  COALESCE(SUM(ang.jumlah_bayar), 0) AS total_dibayar,
  p.jumlah_pinjaman - COALESCE(SUM(ang.jumlah_bayar), 0) AS sisa_pinjaman
FROM pinjaman p
LEFT JOIN angsuran ang ON ang.id_pinjaman = p.id_pinjaman
GROUP BY p.id_pinjaman, p.no_pinjaman, p.id_anggota, p.jumlah_pinjaman;

INSERT INTO groups (nama_group, deskripsi) VALUES
('Super Admin', 'Akses penuh ke seluruh menu'),
('Pengguna', 'Akses operasional terbatas');

INSERT INTO users (id_group, nama, email, password_hash, status) VALUES
(1, 'tes', 'tes@example.com', SHA2('123456', 256), 'Aktif');

INSERT INTO jenis_simpanan (nama_jenis, keterangan) VALUES
('Simpanan Pokok', 'Simpanan awal anggota'),
('Simpanan Wajib', 'Simpanan rutin anggota'),
('Simpanan Sukarela', 'Simpanan tambahan anggota');

INSERT INTO pengaturan_koperasi (
  nama_koperasi,
  alamat,
  no_telepon,
  email,
  mata_uang,
  persen_bunga,
  tahun_buku
) VALUES (
  'Koperasi Raya Abadi Saudara',
  NULL,
  NULL,
  NULL,
  'Rupiah(Rp)',
  0,
  YEAR(CURDATE())
);

INSERT INTO hak_akses (id_group, nama_menu, boleh_lihat, boleh_tambah, boleh_edit, boleh_hapus, boleh_cetak) VALUES
(1, 'Dashboard', 1, 1, 1, 1, 1),
(1, 'Manajemen Anggota', 1, 1, 1, 1, 1),
(1, 'Manajemen Pinjaman', 1, 1, 1, 1, 1),
(1, 'Manajemen Simpanan', 1, 1, 1, 1, 1),
(1, 'Angsuran', 1, 1, 1, 1, 1),
(1, 'Transaksi', 1, 1, 1, 1, 1),
(1, 'Laporan', 1, 1, 1, 1, 1),
(1, 'Pengaturan', 1, 1, 1, 1, 1),
(2, 'Dashboard', 1, 0, 0, 0, 0),
(2, 'Manajemen Anggota', 1, 1, 1, 0, 1),
(2, 'Manajemen Pinjaman', 1, 1, 1, 0, 1),
(2, 'Manajemen Simpanan', 1, 1, 1, 0, 1),
(2, 'Angsuran', 1, 1, 1, 0, 1),
(2, 'Transaksi', 1, 0, 0, 0, 1),
(2, 'Laporan', 1, 0, 0, 0, 1),
(2, 'Pengaturan', 0, 0, 0, 0, 0);
