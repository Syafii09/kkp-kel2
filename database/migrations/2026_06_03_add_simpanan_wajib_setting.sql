ALTER TABLE pengaturan_koperasi
  ADD COLUMN simpanan_wajib decimal(15,2) NOT NULL DEFAULT 0.00 AFTER persen_bunga;
