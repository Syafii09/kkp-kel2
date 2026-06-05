ALTER TABLE transaksi
  ADD COLUMN id_user INT NULL AFTER id_anggota,
  ADD KEY fk_transaksi_user (id_user),
  ADD CONSTRAINT fk_transaksi_user
    FOREIGN KEY (id_user) REFERENCES users(id_user)
    ON DELETE SET NULL ON UPDATE CASCADE;
