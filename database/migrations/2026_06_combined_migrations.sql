USE `koperasi_raya_abadi`;

DELIMITER $$

DROP PROCEDURE IF EXISTS `migrate_koperasi_2026_06` $$

CREATE PROCEDURE `migrate_koperasi_2026_06`()
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'anggota'
          AND COLUMN_NAME = 'pekerjaan'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'anggota'
          AND COLUMN_NAME = 'divisi'
    ) THEN
        ALTER TABLE anggota
            CHANGE COLUMN pekerjaan divisi varchar(100) DEFAULT NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'pengaturan_koperasi'
          AND COLUMN_NAME = 'simpanan_wajib'
    ) THEN
        ALTER TABLE pengaturan_koperasi
            ADD COLUMN simpanan_wajib decimal(15,2) NOT NULL DEFAULT 0.00 AFTER persen_bunga;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'transaksi'
          AND COLUMN_NAME = 'id_user'
    ) THEN
        ALTER TABLE transaksi
            ADD COLUMN id_user int(11) DEFAULT NULL AFTER id_anggota;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'transaksi'
          AND INDEX_NAME = 'fk_transaksi_user'
    ) THEN
        ALTER TABLE transaksi
            ADD KEY fk_transaksi_user (id_user);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE()
          AND TABLE_NAME = 'transaksi'
          AND CONSTRAINT_NAME = 'fk_transaksi_user'
    ) THEN
        ALTER TABLE transaksi
            ADD CONSTRAINT fk_transaksi_user
            FOREIGN KEY (id_user) REFERENCES users(id_user)
            ON DELETE SET NULL ON UPDATE CASCADE;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'users'
          AND COLUMN_NAME = 'id_anggota'
    ) THEN
        ALTER TABLE users
            ADD COLUMN id_anggota int(11) DEFAULT NULL AFTER id_group;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'users'
          AND INDEX_NAME = 'fk_users_anggota'
    ) THEN
        ALTER TABLE users
            ADD KEY fk_users_anggota (id_anggota);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE()
          AND TABLE_NAME = 'users'
          AND CONSTRAINT_NAME = 'fk_users_anggota'
    ) THEN
        ALTER TABLE users
            ADD CONSTRAINT fk_users_anggota
            FOREIGN KEY (id_anggota) REFERENCES anggota(id_anggota)
            ON DELETE SET NULL ON UPDATE CASCADE;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'users'
          AND COLUMN_NAME = 'jabatan'
    ) THEN
        ALTER TABLE users
            ADD COLUMN jabatan varchar(100) DEFAULT NULL AFTER email;
    END IF;

    CREATE TABLE IF NOT EXISTS agunan (
        id_agunan int(11) NOT NULL AUTO_INCREMENT,
        id_pinjaman int(11) NOT NULL,
        jenis_agunan varchar(100) NOT NULL,
        nilai_taksir decimal(15,2) NOT NULL DEFAULT 0.00,
        tanggal date NOT NULL,
        status varchar(30) NOT NULL DEFAULT 'Aktif',
        deskripsi text DEFAULT NULL,
        created_at timestamp NOT NULL DEFAULT current_timestamp(),
        updated_at timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
        PRIMARY KEY (id_agunan),
        KEY fk_agunan_pinjaman (id_pinjaman)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE()
          AND TABLE_NAME = 'agunan'
          AND CONSTRAINT_NAME = 'fk_agunan_pinjaman'
    ) THEN
        ALTER TABLE agunan
            ADD CONSTRAINT fk_agunan_pinjaman
            FOREIGN KEY (id_pinjaman) REFERENCES pinjaman(id_pinjaman)
            ON DELETE CASCADE ON UPDATE CASCADE;
    END IF;

    INSERT IGNORE INTO groups (nama_group, deskripsi)
    VALUES ('Admin', 'Akses administrasi dan pengelolaan data');

    DROP TABLE IF EXISTS hak_akses;
END $$

CALL `migrate_koperasi_2026_06`() $$

DROP PROCEDURE IF EXISTS `migrate_koperasi_2026_06` $$

DELIMITER ;
