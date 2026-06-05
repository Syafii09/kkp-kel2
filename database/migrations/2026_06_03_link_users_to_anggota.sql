ALTER TABLE users
  ADD COLUMN id_anggota int(11) DEFAULT NULL AFTER id_group,
  ADD KEY fk_users_anggota (id_anggota),
  ADD CONSTRAINT fk_users_anggota
    FOREIGN KEY (id_anggota) REFERENCES anggota (id_anggota)
    ON DELETE SET NULL ON UPDATE CASCADE;
