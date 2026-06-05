# 🏦 Sistem Koperasi Simpan Pinjam
### KKP Kelompok 2

Aplikasi desktop berbasis Java Swing untuk manajemen koperasi simpan pinjam, mencakup pengelolaan anggota, simpanan, pinjaman, angsuran, transaksi, dan laporan keuangan.

---

## 👥 Tim Pengembang

| Nama | Username GitHub |
|------|----------------|
| Syafii | [@Syafii09](https://github.com/Syafii09) |
| Sabil | [@sblrrsdi3](https://github.com/sblrrsdi3) |
| Rangga | [@koperasiRAS](https://github.com/koperasiRAS) |
| Feri | [@FeryK8](https://github.com/FeryK8) |
| Beni | [@BeniAkbarSuparman](https://github.com/BeniAkbarSuparman) |
| Farid | [@muhammadfarid2](https://github.com/muhammadfarid2) |
| Julfi | [@julfialfiyansyah](https://github.com/julfialfiyansyah) |

---

## 🖥️ Fitur Aplikasi

- 🔐 **Login** — Autentikasi pengguna
- 🏠 **Dashboard** — Ringkasan data koperasi
- 👥 **Manajemen Anggota** — CRUD data anggota
- 🏛️ **Manajemen Simpanan** — Kelola simpanan anggota
- 🏛️ **Manajemen Pinjaman** — Kelola pinjaman anggota
- 💰 **Angsuran** — Pencatatan angsuran pinjaman
- 💲 **Transaksi** — Riwayat transaksi keuangan
- 📊 **Laporan** — Cetak dan export laporan
- 🛠️ **Pengaturan** — Konfigurasi aplikasi

---

## 🛠️ Teknologi

- **Bahasa:** Java
- **GUI:** Java Swing (NetBeans GUI Builder)
- **Database:** MySQL
- **IDE:** Apache NetBeans 25
- **Library:** JCalendar (JDateChooser)

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
- Import file `database/koperasi_raya_abadi.sql`

### 3. Konfigurasi Koneksi Database
Buka file `src/koneksi_database.java` dan sesuaikan:
```java
String host = "localhost";
String db   = "koperasi_raya_abadi";
String user = "root";
String pass = ""; // sesuaikan password MySQL kamu
```

### 4. Tambahkan Library JCalendar
- Download `jcalendar-1.4.jar`
- Di NetBeans: klik kanan project → **Properties** → **Libraries** → **Add JAR/Folder**
- Pilih file `jcalendar-1.4.jar`

### 5. Build dan Jalankan
- Klik kanan project → **Clean and Build**
- Tekan **F6** atau klik **Run**

---

## 📁 Struktur Project

```
kkp-kel2/
├── src/
│   ├── Tampilan/          # File .java dan .form (GUI)
│   │   ├── Login.java
│   │   ├── Menu.java
│   │   ├── DashboardPanel.java
│   │   ├── Management_Anggota.java
│   │   ├── manajemen_simpanan.java
│   │   ├── MANAJEMEN_PINJAMAN.java
│   │   ├── Angsuran.java
│   │   ├── Transaksi.java
│   │   ├── Laporan.java
│   │   └── Pengaturan.java
│   └── koneksi_database.java
├── database/
│   └── koperasi_raya_abadi.sql
├── lib/
│   └── jcalendar-1.4.jar
└── README.md
```

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
- MySQL Server
- Apache NetBeans IDE 25
- Library JCalendar 1.4

---

## run apache & mysql
- net start Apache2.4    
- net start mysql  
- net stop mysql
- net stop Apache2.4

> Dibuat untuk keperluan KKP (Kuliah Kerja Praktik) — 2026
