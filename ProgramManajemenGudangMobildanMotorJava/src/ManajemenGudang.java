//import library
import java.sql.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

// Interface untuk operasi
interface operasi {
    void buat(); // Fungsi buat
    void cari(); // Fungsi cari
    void update(); // Fungsi update 
    void keluar(); // Fungsi keluar 
}

// Superclass Kendaraan
class Kendaraan {
    protected String tipe;
    protected String id;
    protected String merk;
    protected String nama;
    protected int ccMesin;
    protected int jumlahSilinder;
    protected String tipeKopling;
    protected double harga;

    // Constructor (Digunakan untuk inisialisasi objek Kendaraan)
    public Kendaraan(String tipe, String id, String merk, String nama, int ccMesin, int jumlahSilinder, String tipeKopling, double harga) {
        this.tipe = tipe;
        this.id = id;
        this.merk = merk;
        this.nama = nama;
        this.ccMesin = ccMesin;
        this.jumlahSilinder = jumlahSilinder;
        this.tipeKopling = tipeKopling;
        this.harga = harga;
    }

    // Method untuk menampilkan informasi kendaraan
    public void tampilkanInfo() {
        DecimalFormat df = new DecimalFormat("#,###.00"); 
        String hargaFormatted = df.format(harga);
        
        System.out.println("Tipe: " + tipe);
        System.out.println("ID: " + id);
        System.out.println("Merk: " + merk);
        System.out.println("Nama: " + nama);
        System.out.println("CC Mesin: " + ccMesin);
        System.out.println("Jumlah Silinder: " + jumlahSilinder);
        System.out.println("Tipe Kopling: " + tipeKopling);
        System.out.println("Harga: Rp " + hargaFormatted);
    }
}

// Subclass Mobil (Inheritance dari Kendaraan)
class Mobil extends Kendaraan {

    public Mobil(String tipe, String id, String merk, String nama, int ccMesin, int jumlahSilinder, String tipeKopling, double harga) {
        super(tipe, id, merk, nama, ccMesin, jumlahSilinder, tipeKopling, harga); // Memanggil constructor superclass
    }

}

// Subclass Motor (Inheritance dari Kendaraan)
class Motor extends Kendaraan {
    public Motor(String tipe, String id, String merk, String nama, int ccMesin, int jumlahSilinder, String tipeKopling, double harga) {
        super(tipe, id, merk, nama, ccMesin, jumlahSilinder, tipeKopling, harga); // Memanggil constructor superclass
    }
}

// Class utama
public class ManajemenGudang implements operasi {

   // Koneksi ke database
   private Connection koneksi;  // Menggunakan JDBC untuk koneksi database

   private List<Kendaraan> daftarKendaraan = new ArrayList<>(); // Menggunakan Collection Framework (ArrayList)

   public ManajemenGudang() {
       try {
           // Memuat driver MySQL
           Class.forName("com.mysql.cj.jdbc.Driver");

           // Inisialisasi koneksi database (menggunakan XAMPP)
           koneksi = DriverManager.getConnection("jdbc:mysql://localhost:3306/gudang", "root", "");
           System.out.println("Koneksi database berhasil!");
       } catch (ClassNotFoundException e) {
           System.out.println("Driver MySQL tidak ditemukan: " + e.getMessage()); // Exception handling
       } catch (SQLException e) {
           System.out.println("Koneksi database gagal: " + e.getMessage()); // Exception handling
       }
   }

   public Connection getKoneksi() {
       return koneksi;
   }

    public boolean loginAdmin() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Masukkan username: ");
            String username = scanner.nextLine();
            System.out.print("Masukkan password: ");
            String password = scanner.nextLine();

            String query = "SELECT * FROM admin WHERE username = ? AND password = ?"; // Query SQL untuk login (JDBC)
            PreparedStatement ps = koneksi.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // Percabangan (if-else)
                // Generate CAPTCHA
                String captcha = generateCaptcha(6); // Panjang CAPTCHA 6 karakter // Perulangan (digunakan dalam generateCaptcha untuk membangun string)
                System.out.println("CAPTCHA: " + captcha);
                System.out.print("Masukkan CAPTCHA: ");
                String inputCaptcha = scanner.nextLine();
            
                // Validasi CAPTCHA (tidak case-sensitive)
                if (captcha.equalsIgnoreCase(inputCaptcha)) { // Percabangan dan manipulasi String
                    System.out.println("Login berhasil! Selamat datang, " + username + "!");
                    return true;
                } else {
                    System.out.println("CAPTCHA salah. Login gagal.");
                    return false;
                }
            } else {
                System.out.println("Username atau password salah. Login gagal.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error saat login: " + e.getMessage()); // Exception handling
            return false;
        }
    }

    public void registerAdmin() {
        try {
            // Membuat objek Scanner untuk input pengguna
            Scanner scanner = new Scanner(System.in);
            System.out.print("Masukkan username baru: ");
            String username = scanner.nextLine(); // Manipulasi String
            System.out.print("Masukkan password baru: ");
            String password = scanner.nextLine(); // Manipulasi String

            // Menggunakan JDBC untuk operasi create pada database
            String query = "INSERT INTO admin (username, password) VALUES (?, ?)";
            PreparedStatement ps = koneksi.prepareStatement(query); // JDBC
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate(); // Operasi Create
            System.out.println("Admin berhasil didaftarkan.");
        } catch (SQLException e) {
             // Exception handling untuk SQL
            System.out.println("Error saat mendaftarkan admin: " + e.getMessage());
        }
    }

    public String generateCaptcha(int length) {
        // Menggunakan perulangan untuk membentuk CAPTCHA
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // Manipulasi String
        StringBuilder captcha = new StringBuilder();
        Random random = new Random(); 
    
        for (int i = 0; i < length; i++) { // Perulangan
            int index = random.nextInt(characters.length());
            captcha.append(characters.charAt(index)); // Manipulasi String

        }
        return captcha.toString();
    }    


    @Override
    public void buat() {
        try {
            // Membuat objek Scanner untuk input pengguna
            Scanner scanner = new Scanner(System.in);
            System.out.print("Masukkan tipe kendaraan (Mobil/Motor): ");
            String tipe = scanner.nextLine(); // Manipulasi String
            System.out.print("Masukkan ID kendaraan: ");
            String id = scanner.nextLine(); // Manipulasi String
            System.out.print("Masukkan merk kendaraan: ");
            String merk = scanner.nextLine(); // Manipulasi String
            System.out.print("Masukkan nama kendaraan: ");
            String nama = scanner.nextLine(); // Manipulasi String
            System.out.print("Masukkan CC mesin kendaraan: ");
            int ccMesin = scanner.nextInt(); // Perhitungan matematika
            System.out.print("Masukkan jumlah silinder kendaraan: ");
            int jumlahSilinder = scanner.nextInt(); // Perhitungan matematika
            scanner.nextLine(); // Membersihkan buffer
            System.out.print("Masukkan tipe kopling (Manual/Otomatis): ");
            String tipeKopling = scanner.nextLine(); // Manipulasi String
            System.out.print("Masukkan harga kendaraan: ");
            double harga = scanner.nextDouble(); // Perhitungan matematika

            // Menggunakan inheritance (superclass: Kendaraan, subclass: Mobil/Motor)
            Kendaraan kendaraan;
            if (tipe.equalsIgnoreCase("Mobil")) { // Percabangan
                kendaraan = new Mobil(tipe, id, merk, nama, ccMesin, jumlahSilinder, tipeKopling, harga);
            } else {
                kendaraan = new Motor(tipe, id, merk, nama, ccMesin, jumlahSilinder, tipeKopling, harga);
            }
            daftarKendaraan.add(kendaraan); // Collection framework: ArrayList

            // Menggunakan JDBC untuk operasi create pada database
            String query = "INSERT INTO kendaraan (tipe, id, merk, nama, ccMesin, jumlah_silinder, tipe_kopling, harga) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = koneksi.prepareStatement(query); // JDBC
            ps.setString(1, tipe);
            ps.setString(2, id);
            ps.setString(3, merk);
            ps.setString(4, nama);
           
            ps.setInt(5, ccMesin);
            ps.setInt(6, jumlahSilinder);
            ps.setString(7, tipeKopling);
            ps.setDouble(8, harga);
            ps.executeUpdate();
            System.out.println("Data berhasil ditambahkan.");

             // Operasi Create tambahan untuk mencatat riwayat dengan manipulasi Date
            query = "INSERT INTO riwayat (id_kendaraan, aksi, tanggal) VALUES (?, ?, ?)";
            ps = koneksi.prepareStatement(query);
            ps.setString(1, id);
            ps.setString(2, "Masuk");
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis())); // Manipulasi Date
            ps.executeUpdate();

        } catch (SQLException e) {
            // Exception handling untuk SQL
            System.out.println("Error saat menambahkan data: " + e.getMessage());
        }
    }

    @Override
public void cari() {
    try {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Lihat Data Kendaraan ===");
        System.out.println("1. Cari kendaraan berdasarkan ID");
        System.out.println("2. Lihat kendaraan berdasarkan tipe");
        System.out.println("3. Lihat kendaraan berdasarkan merk");
        System.out.println("4. Lihat semua kendaraan terurut");
        System.out.print("Pilih opsi: ");
        int pilihan = scanner.nextInt();
        scanner.nextLine(); // Membersihkan buffer

        String query;
        List<Kendaraan> hasilPencarian = new ArrayList<>(); // Menggunakan Collection Framework (ArrayList)
        PreparedStatement ps;

        switch (pilihan) {
            case 1:
                System.out.print("Masukkan ID kendaraan: ");
                String id = scanner.nextLine();
                query = """
                    SELECT k.*
                    FROM kendaraan k
                    JOIN (
                        SELECT id_kendaraan, MAX(CASE WHEN aksi = 'Update' THEN tanggal ELSE NULL END) AS last_update,
                               MAX(tanggal) AS last_action
                        FROM riwayat
                        GROUP BY id_kendaraan
                    ) r ON k.id = r.id_kendaraan
                    WHERE k.id = ?
                    AND (r.last_update IS NOT NULL OR r.last_action IS NOT NULL)
                    ORDER BY r.last_update DESC, r.last_action DESC
                    LIMIT 1
                """;
                ps = koneksi.prepareStatement(query); // JDBC untuk query dengan parameter
                ps.setString(1, id);
                break;
        
                case 2:
                System.out.print("Masukkan tipe kendaraan (Mobil/Motor): ");
                String tipe = scanner.nextLine();
                query = """
                    SELECT k.*
                    FROM kendaraan k
                    JOIN (
                        SELECT id_kendaraan, 
                               MAX(CASE WHEN aksi = 'Update' THEN tanggal ELSE NULL END) AS last_update,
                               MAX(tanggal) AS last_action
                        FROM riwayat
                        WHERE id_kendaraan NOT IN (
                            SELECT id_kendaraan
                            FROM riwayat
                            WHERE aksi = 'Keluar'
                        )
                        GROUP BY id_kendaraan
                    ) r ON k.id = r.id_kendaraan
                    WHERE k.tipe = ?
                    ORDER BY r.last_update DESC, r.last_action DESC
                """;
                ps = koneksi.prepareStatement(query);
                ps.setString(1, tipe);
                break;
            
        
            case 3:
                System.out.print("Masukkan merk kendaraan: ");
                String merk = scanner.nextLine();
                query = """
                    SELECT k.*
                    FROM kendaraan k
                    JOIN (
                        SELECT id_kendaraan, MAX(CASE WHEN aksi = 'Update' THEN tanggal ELSE NULL END) AS last_update,
                               MAX(tanggal) AS last_action
                        FROM riwayat
                        GROUP BY id_kendaraan
                    ) r ON k.id = r.id_kendaraan
                    WHERE k.merk = ?
                    ORDER BY r.last_update DESC, r.last_action DESC
                """;
                ps = koneksi.prepareStatement(query);
                ps.setString(1, merk);
                break;
        
                case 4:
    query = """
        SELECT k.*
        FROM kendaraan k
        JOIN (
            SELECT id_kendaraan, 
                   MAX(CASE WHEN aksi = 'Update' THEN tanggal ELSE NULL END) AS last_update,
                   MAX(tanggal) AS last_action
            FROM riwayat
            WHERE id_kendaraan NOT IN (
                SELECT id_kendaraan
                FROM riwayat
                WHERE aksi = 'Keluar'
            )
            GROUP BY id_kendaraan
        ) r ON k.id = r.id_kendaraan
        ORDER BY 
            CASE WHEN k.tipe = 'Mobil' THEN 1 ELSE 2 END, -- Prioritaskan Mobil
            r.last_update DESC, -- Urutkan berdasarkan update terbaru
            r.last_action DESC  -- Urutkan berdasarkan aksi terakhir
    """;
    ps = koneksi.prepareStatement(query);
    break;

        
            default:
                System.out.println("Pilihan tidak valid.");
                return;
        }                

        ResultSet rs = ps.executeQuery(); // Menjalankan query untuk mendapatkan hasil

        while (rs.next()) { // Perulangan untuk membaca hasil query
            String tipe = rs.getString("tipe");
            String id = rs.getString("id");
            String merk = rs.getString("merk");
            String nama = rs.getString("nama");
            int ccMesin = rs.getInt("ccMesin");
            int jumlahSilinder = rs.getInt("jumlah_silinder");
            String tipeKopling = rs.getString("tipe_kopling");
            double harga = rs.getDouble("harga");

            if (tipe.equalsIgnoreCase("Mobil")) { // Percabangan berdasarkan tipe
                hasilPencarian.add(new Mobil(tipe, id, merk, nama, ccMesin, jumlahSilinder, tipeKopling, harga));
            } else {
                hasilPencarian.add(new Motor(tipe, id, merk, nama, ccMesin, jumlahSilinder, tipeKopling, harga));
            }
        }

        // Menampilkan hasil dari ArrayList (Collection Framework)
        for (Kendaraan kendaraan : hasilPencarian) {
            System.out.println("----------------------------");
            kendaraan.tampilkanInfo(); // Pemanggilan method pada objek
            System.out.println("----------------------------");
        }

    } catch (SQLException e) {
        System.out.println("Error saat membaca data: " + e.getMessage()); // Exception handling
    }
}


    @Override
    public void update() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Masukkan ID kendaraan yang ingin diupdate: ");
            String id = scanner.nextLine();
            System.out.print("Masukkan tipe kendaraan baru (Mobil/Motor): ");
            String tipe = scanner.nextLine();
            System.out.print("Masukkan merk kendaraan baru: ");
            String merk = scanner.nextLine();
            System.out.print("Masukkan nama kendaraan baru: ");
            String nama = scanner.nextLine();
            System.out.print("Masukkan CC mesin kendaraan baru: ");
            int ccMesin = scanner.nextInt();
            System.out.print("Masukkan jumlah silinder kendaraan baru: ");
            int jumlahSilinder = scanner.nextInt();
            scanner.nextLine(); // Membersihkan buffer
            System.out.print("Masukkan tipe kopling baru (Manual/Otomatis): ");
            String tipeKopling = scanner.nextLine();
            System.out.print("Masukkan harga kendaraan baru: ");
            double harga = scanner.nextDouble();

            String query = "UPDATE kendaraan SET tipe = ?, merk = ?, nama = ?, harga = ?, jumlah_silinder = ?, tipe_kopling = ?, harga = ? WHERE id = ?";
            PreparedStatement ps = koneksi.prepareStatement(query); // Menggunakan JDBC untuk query UPDATE
            ps.setString(1, tipe);
            ps.setString(2, merk);
            ps.setString(3, nama);
            ps.setInt(4, ccMesin);
            ps.setInt(5, jumlahSilinder);
            ps.setString(6, tipeKopling);
            ps.setDouble(7, harga);
            ps.setString(8, id);
            ps.executeUpdate();

            System.out.println("Data berhasil diperbarui.");

            // Catat ke riwayat
            query = "INSERT INTO riwayat (id_kendaraan, aksi, tanggal) VALUES (?, ?, ?)";
            ps = koneksi.prepareStatement(query);
            ps.setString(1, id);
            ps.setString(2, "Update");
            ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error saat memperbarui data: " + e.getMessage()); // Exception handling
        }
    }

    @Override
public void keluar() {
    try {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan ID kendaraan yang ingin dihapus: ");
        String id = scanner.nextLine();

        // Ubah aksi menjadi "Keluar" di tabel riwayat
        String query = "INSERT INTO riwayat (id_kendaraan, aksi, tanggal) VALUES (?, ?, ?)";
        PreparedStatement ps = koneksi.prepareStatement(query); // Menggunakan JDBC untuk query INSERT
        ps.setString(1, id);
        ps.setString(2, "Keluar");
        ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
        ps.executeUpdate();

        System.out.println("Data kendaraan berhasil diperbarui menjadi keluar.");
    } catch (SQLException e) {
        System.out.println("Error saat memperbarui data: " + e.getMessage()); // Exception handling
    }
}

public void hitungJumlahKendaraan() {
    try {
        String query = """
            SELECT 
                SUM(CASE WHEN k.tipe = 'Mobil' THEN 1 ELSE 0 END) AS jumlah_mobil,
                SUM(CASE WHEN k.tipe = 'Motor' THEN 1 ELSE 0 END) AS jumlah_motor,
                COUNT(*) AS total_kendaraan
            FROM kendaraan k
            WHERE k.id NOT IN (
                SELECT id_kendaraan
                FROM riwayat
                WHERE aksi = 'Keluar'
            );
        """;

        PreparedStatement ps = koneksi.prepareStatement(query); // Menyiapkan query
        ResultSet rs = ps.executeQuery(); // Mengeksekusi query

        if (rs.next()) { // Membaca hasil query
            int jumlahMobil = rs.getInt("jumlah_mobil");
            int jumlahMotor = rs.getInt("jumlah_motor");
            int totalKendaraan = rs.getInt("total_kendaraan");

            System.out.println("Jumlah mobil yang masih ada di gudang: " + jumlahMobil);
            System.out.println("Jumlah motor yang masih ada di gudang: " + jumlahMotor);
            System.out.println("Total kendaraan yang masih ada di gudang: " + totalKendaraan);
        } else {
            System.out.println("Tidak ada kendaraan dalam gudang.");
        }
    } catch (SQLException e) {
        System.out.println("Error saat menghitung jumlah kendaraan: " + e.getMessage()); // Penanganan exception
    }
}


   // Hitung total aset kendaraan
public void hitungTotalAset() {
    try {
        // Query untuk menghitung total aset mobil, motor, dan keseluruhan
        String query = """
            SELECT 
                SUM(CASE WHEN k.tipe = 'Mobil' THEN k.harga ELSE 0 END) AS total_aset_mobil,
                SUM(CASE WHEN k.tipe = 'Motor' THEN k.harga ELSE 0 END) AS total_aset_motor,
                SUM(k.harga) AS total_aset
            FROM kendaraan k
            WHERE k.id NOT IN (
                SELECT id_kendaraan
                FROM riwayat
                WHERE aksi = 'Keluar'
            );
        """;

        PreparedStatement ps = koneksi.prepareStatement(query); // Menyiapkan query
        ResultSet rs = ps.executeQuery(); // Mengeksekusi query

        if (rs.next()) { // Membaca hasil query
            double totalAsetMobil = rs.getDouble("total_aset_mobil");
            double totalAsetMotor = rs.getDouble("total_aset_motor");
            double totalAset = rs.getDouble("total_aset");

            // Format hasil dengan DecimalFormat
            DecimalFormat df = new DecimalFormat("#,###.00");
            String formattedAsetMobil = df.format(totalAsetMobil);
            String formattedAsetMotor = df.format(totalAsetMotor);
            String formattedTotalAset = df.format(totalAset);

            // Tampilkan hasil
            System.out.println("Total aset mobil di gudang: Rp " + formattedAsetMobil);
            System.out.println("Total aset motor di gudang: Rp " + formattedAsetMotor);
            System.out.println("Total keseluruhan aset kendaraan di gudang: Rp " + formattedTotalAset);
        } else {
            System.out.println("Tidak ada data kendaraan.");
        }
    } catch (SQLException e) {
        System.out.println("Error saat menghitung total aset: " + e.getMessage());
    }
}

    

    public void lihatRiwayat() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("=== Lihat Riwayat Kendaraan ===");
            System.out.println("1. Riwayat kendaraan masuk");
            System.out.println("2. Riwayat kendaraan keluar");
            System.out.println("3. Semua Riwayat kendaraan");
            System.out.println("4. Hapus seluruh riwayat kendaraan");
            System.out.print("Pilih opsi: ");
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // Membersihkan buffer
    
            String query;
    
            if (pilihan == 1) {
                query = "SELECT * FROM riwayat WHERE aksi = 'Masuk' ORDER BY tanggal DESC";
                PreparedStatement ps = koneksi.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
            
                System.out.println("\n--- Riwayat Kendaraan Masuk ---");
                if (!rs.isBeforeFirst()) { // Cek apakah ResultSet kosong
                    System.out.println("Riwayat kosong");
                } else {
                    while (rs.next()) {
                        System.out.println("ID Kendaraan: " + rs.getString("id_kendaraan"));
                        System.out.println("Aksi: " + rs.getString("aksi"));
                        System.out.println("Tanggal: " + rs.getDate("tanggal"));
                        System.out.println("------------------------");
                    }
                }
            } else if (pilihan == 2) {
                query = "SELECT * FROM riwayat WHERE aksi = 'Keluar' ORDER BY tanggal DESC";
                PreparedStatement ps = koneksi.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                System.out.println("\n--- Riwayat Kendaraan Keluar ---");
                if (!rs.isBeforeFirst()) { // Cek apakah ResultSet kosong
                    System.out.println("Riwayat kosong");
                } else {
                    while (rs.next()) {
                        System.out.println("ID Kendaraan: " + rs.getString("id_kendaraan"));
                        System.out.println("Aksi: " + rs.getString("aksi"));
                        System.out.println("Tanggal: " + rs.getDate("tanggal"));
                        System.out.println("------------------------");
                    }
                }
            } else if (pilihan == 3) {
                query = "SELECT * FROM riwayat ORDER BY tanggal DESC";
                PreparedStatement ps = koneksi.prepareStatement(query);
                ResultSet rs = ps.executeQuery();

                System.out.println("\n--- Semua Riwayat Kendaraan ---");
                if (!rs.isBeforeFirst()) { // Cek apakah ResultSet kosong
                    System.out.println("Riwayat kosong");
                } else {
                    while (rs.next()) {
                        System.out.println("ID Kendaraan: " + rs.getString("id_kendaraan"));
                        System.out.println("Aksi: " + rs.getString("aksi"));
                        System.out.println("Tanggal: " + rs.getDate("tanggal"));
                        System.out.println("------------------------");
                    }
                }
            } else if (pilihan == 4) {
                System.out.print("Apakah Anda yakin ingin menghapus seluruh riwayat? (ya/tidak): ");
                String konfirmasi = scanner.nextLine();
                if (konfirmasi.equalsIgnoreCase("ya")) {
                    query = "DELETE FROM riwayat";  // Menggunakan JDBC untuk query DELETE
                    PreparedStatement ps = koneksi.prepareStatement(query);
                    int rowsDeleted = ps.executeUpdate();
                    System.out.println(rowsDeleted + " baris riwayat berhasil dihapus.");
                } else {
                    System.out.println("Penghapusan riwayat dibatalkan.");
                }
            } else {
                System.out.println("Pilihan tidak valid.");
            }
    
        } catch (SQLException e) {
            System.out.println("Error saat membaca atau menghapus riwayat: " + e.getMessage());
        }
    }    
    

    public static void main(String[] args) {
        // Membuat instance ManajemenGudang untuk menguji koneksi
        ManajemenGudang gudang = new ManajemenGudang();
        boolean isRunning = true;
    
        if (gudang.getKoneksi() != null) {
            System.out.println("Koneksi ke database berhasil digunakan!");
        } else {
            System.out.println("Koneksi ke database gagal.");
        }
    
        Scanner scanner = new Scanner(System.in);
    
        while (isRunning) {
            System.out.println("\n=== Sistem Manajemen Gudang ===");
            System.out.println("1. Login Admin");
            System.out.println("2. Register Admin");
            System.out.println("3. Keluar");
            System.out.print("Pilih menu: ");
            int menu = scanner.nextInt();
            scanner.nextLine(); // Membersihkan buffer
    
            switch (menu) {
                case 1:
                    if (gudang.loginAdmin()) {
                        while (true) {
                            System.out.println("\n=== Menu Utama ===");
                            System.out.println("1. Tambah Data Kendaraan");
                            System.out.println("2. Lihat Data Kendaraan");
                            System.out.println("3. Perbarui Data Kendaraan");
                            System.out.println("4. Keluarkan Kendaraan");
                            System.out.println("5. Lihat Riwayat");
                            System.out.println("6. Jumlah Kendaraan dalam Gudang");
                            System.out.println("7. Total Aset");
                            System.out.println("8. Keluar");
                            System.out.print("Pilih menu: ");
                            int pilihan = scanner.nextInt();
                            scanner.nextLine(); // Membersihkan buffer
    
                            switch (pilihan) {
                                case 1:
                                    gudang.buat();
                                    break;
                                case 2:
                                    gudang.cari();
                                    break;
                                case 3:
                                    gudang.update();
                                    break;
                                case 4:
                                    gudang.keluar();
                                    break;
                                case 5:
                                    gudang.lihatRiwayat();
                                    break;
                                case 6:
                                    gudang.hitungJumlahKendaraan();
                                    break;
                                case 7:
                                    gudang.hitungTotalAset();
                                    break;        
                                case 8:
                                    System.out.println("Keluar dari sistem admin. Kembali ke menu utama.");
                                    break;
                                default:
                                    System.out.println("Pilihan tidak valid.");
                            }
    
                            // Jika pengguna memilih keluar, kembali ke menu utama
                            if (pilihan == 8) {
                                break;
                            }
                        }
                    }
                    break;
    
                case 2:
                    gudang.registerAdmin();
                    break;
    
                case 3:
                    System.out.println("Terima kasih telah menggunakan aplikasi!");
                    isRunning = false; // Menghentikan perulangan utama
                    break;
    
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
    }
}
