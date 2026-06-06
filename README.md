# 🏦 Sistem Koperasi Simpan Pinjam
### Pemrograman Visual kelompok 2

Aplikasi desktop berbasis Java Swing untuk manajemen koperasi simpan pinjam **Koperasi Raya Abadi Saudara**, mencakup pengelolaan anggota, simpanan, pinjaman, angsuran, transaksi, dashboard, laporan keuangan, pengaturan sistem, serta hak akses pengguna.

---

## 👥 Tim Pengembang

| Nama | Username GitHub |
|------|----------------|
| Syafii Muhammad Arif | [@Syafii09](https://github.com/Syafii09) |
| Sabilarrusdi | [@sblrrsdi3](https://github.com/sblrrsdi3) |
| Rangga Danuarta | [@koperasiRAS](https://github.com/koperasiRAS) |
| Ferry Kadafi | [@FeryK8](https://github.com/FeryK8) |
| Beni Akbar Suparman | [@BeniAkbarSuparman](https://github.com/BeniAkbarSuparman) |
| Muhammad Alfaridzi | [@muhammadfarid2](https://github.com/muhammadfarid2) |
| Julfi Alfiansyah | [@julfialfiyansyah](https://github.com/julfialfiyansyah) |

---

## 🖥️ Fitur Aplikasi

- 🔐 **Login & Sign Up** — Autentikasi pengguna, role user, remember login, dan logout
- 🏠 **Dashboard Admin** — Ringkasan data koperasi dan grafik transaksi
- 👤 **Dashboard Anggota** — Informasi profil, simpanan, pinjaman, angsuran, dan riwayat anggota
- 👥 **Manajemen Anggota** — Tambah, edit, hapus, detail, pencarian, cetak, dan export data anggota
- 🏛️ **Manajemen Simpanan** — Kelola simpanan anggota berdasarkan jenis simpanan
- 🏦 **Manajemen Pinjaman** — Kelola pengajuan pinjaman, bunga, tenor, dan status pinjaman
- 💰 **Angsuran** — Pencatatan pembayaran angsuran pinjaman anggota
- 💲 **Transaksi** — Riwayat transaksi keuangan koperasi
- 📊 **Laporan** — Preview, cetak, dan export laporan menggunakan JasperReport
- 🛠️ **Pengaturan** — Konfigurasi sistem, mata uang, bunga, simpanan wajib, user, dan hak akses
- 🌗 **Tema Tampilan** — FlatLaf macOS Light dan macOS Dark
- 📄 **Footer Informasi** — Privacy Policy dan Terms of Service

---

## 🛠️ Teknologi

- **Bahasa:** Java
- **GUI:** Java Swing dengan NetBeans GUI Builder
- **Database:** MySQL / MariaDB
- **IDE:** Apache NetBeans 25
- **Look and Feel:** FlatLaf 3.7.1
- **Report:** JasperReports 5.5.0
- **Chart:** JFreeChart
- **Date Picker:** JCalendar
- **PDF Export:** iText 2.1.7

---

## ⚙️ Cara Instalasi

### 1. Clone Repository
```bash
git clone https://github.com/Syafii09/kkp-kel2.git
cd kkp-kel2
```

### 2. Import Database
- Buka **phpMyAdmin** atau **MySQL Workbench**
- Buat database baru bernama `koperasi_raya_abadi`
- Import file:

```text
database/koperasi_raya_abadi.sql
```

### 3. Konfigurasi Koneksi Database
Buka file:

```text
src/koneksi/Koneksi.java
```

Sesuaikan konfigurasi database:

```java
String host = "localhost";
String db   = "koperasi_raya_abadi";
String user = "root";
String pass = ""; // sesuaikan password MySQL
```

### 4. Pastikan Library Tersedia
Library aplikasi berada di folder:

```text
lib/
```

Library penting:

- `FlatLaf-3.7.1.jar`
- `jcalendar-1.4.jar`
- `jfreechart-1.5.4.jar`
- `jasperreports-5.5.0.jar`
- `itext-2.1.7.jar`
- `commons-beanutils-1.8.2.jar`
- `commons-collections-3.2.1.jar`
- `commons-digester-2.1.jar`
- `commons-logging-1.1.jar`
- `groovy-all-2.0.1.jar`
- `servlet-api-2.4.jar`

Jika ada library yang belum terbaca di NetBeans:
- Klik kanan project → **Properties**
- Pilih **Libraries**
- Klik **Add JAR/Folder**
- Pilih file `.jar` dari folder `lib/`

### 5. Build dan Jalankan
- Buka project di Apache NetBeans
- Klik kanan project → **Clean and Build**
- Jalankan file utama:

```text
src/app/Main.java
```

---

## 📁 Struktur Project

```text
kkp-kel2/
├── src/
│   ├── app/
│   │   └── Main.java
│   ├── koneksi/
│   │   └── Koneksi.java
│   ├── dao/
│   │   ├── AnggotaDAO.java
│   │   ├── AngsuranDAO.java
│   │   ├── DashboardAnggotaDAO.java
│   │   ├── DashboardDAO.java
│   │   ├── DatabaseMigrationDAO.java
│   │   ├── JasperReportDAO.java
│   │   ├── LaporanDAO.java
│   │   ├── PengaturanDAO.java
│   │   ├── PinjamanDAO.java
│   │   ├── SimpananDAO.java
│   │   ├── TransaksiDAO.java
│   │   └── UserDAO.java
│   ├── utils/
│   │   └── SesiLogin.java
│   ├── view/
│   │   ├── Login.java
│   │   ├── Dashboard.java
│   │   ├── DashboardAnggota.java
│   │   ├── DashboardPanel.java
│   │   ├── ManajemenAnggota.java
│   │   ├── ManajemenSimpanan.java
│   │   ├── ManajemenPinjaman.java
│   │   ├── Angsuran.java
│   │   ├── Transaksi.java
│   │   ├── Laporan.java
│   │   ├── Pengaturan.java
│   │   ├── FormPengisian.java
│   │   ├── FormPengisianUser.java
│   │   └── FormLengkapiDataDiri.java
│   └── resources/
│       ├── icons/
│       ├── images/
│       └── reports/
├── database/
│   ├── koperasi_raya_abadi.sql
│   └── migrations/
├── lib/
├── nbproject/
├── build.xml
└── README.md
```

---

## 🗄️ Database

Database utama:

```text
koperasi_raya_abadi
```

Tabel utama:

- `anggota`
- `users`
- `groups`
- `simpanan`
- `jenis_simpanan`
- `pinjaman`
- `angsuran`
- `transaksi`
- `aktivitas`
- `pengaturan_koperasi`

---

## 📊 Laporan JasperReport

File laporan berada di:

```text
src/resources/reports/
```

Report yang tersedia:

- `ReportAnggota.jrxml`
- `ReportSimpanan.jrxml`
- `ReportPinjaman.jrxml`
- `ReportAngsuran.jrxml`
- `ReportTransaksi.jrxml`
- `ReportSHU.jrxml`

Logo report dipanggil melalui resource:

```text
/resources/images/Logo_Image.png
```

Parameter umum:

```text
TANGGAL_AWAL  java.sql.Date
TANGGAL_AKHIR java.sql.Date
LOGO_IMAGE    java.net.URL
```

Parameter `LOGO_IMAGE` dikirim otomatis melalui `JasperReportDAO`.

---

## 🔄 Alur Kerja Git (Untuk Anggota Tim)

Sebelum mulai coding, selalu jalankan:

```bash
git pull origin main
```

Setelah selesai coding:

```bash
git add .
git commit -m "deskripsi perubahan - nama"
git push
```

---

## 📋 Requirement

- Java JDK 17 atau lebih baru
- MySQL Server / MariaDB
- Apache NetBeans IDE 25
- Library pada folder `lib/`
- Database `koperasi_raya_abadi`

---

## ▶️ Run Apache & MySQL

```powershell
net start Apache2.4
net start mysql
net stop mysql
net stop Apache2.4
```

---

## 📝 Catatan Pengembangan

- File `.form` NetBeans tetap digunakan agar GUI Builder tetap berjalan.
- Query database dipusatkan di package `dao`.
- Package `view` berisi tampilan GUI dan event handler.
- Resource gambar, icon, dan report dipanggil melalui `getResource()` agar tidak bergantung pada path absolut komputer.
- Entry point aplikasi berada di `src/app/Main.java`.

---

> Dibuat untuk keperluan KKP (Kuliah Kerja Praktik) — 2026
