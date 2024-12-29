import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Pongo
 */
public class KRS extends javax.swing.JFrame {
 private Connection conn;
    /**
     * Creates new form KRS
     */
    public KRS() {
        initComponents();
        koneksi();
        loadData();
         populateNIMComboBox();
        populateKodeMKComboBox();
    }
    private void koneksi() {
    try {
        conn = koneksiDB.getConnection();
        if (conn != null) {
            System.out.println("Koneksi ke database berhasil.");
        } else {
            JOptionPane.showMessageDialog(this, "Koneksi ke database gagal.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Koneksi gagal: " + e.getMessage());
    }
}

private void loadData() {
    // Inisialisasi model tabel dengan kolom ID disertakan
    DefaultTableModel model = new DefaultTableModel(
        new Object[][]{}, // Data awal kosong
        new String[]{"ID", "NIM", "Kode MK", "Nama", "Semester"} // Header tabel, ID akan disembunyikan
    );
    tabelKRS.setModel(model); // Set model ke JTable

    // Query untuk mengambil data
    String query = "SELECT krs.ID, krs.NIM, krs.KodeMK, mahasiswa.Nama, krs.Semester " +
                   "FROM krs JOIN mahasiswa ON krs.NIM = mahasiswa.NIM";

    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            // Tambahkan data ke model tabel
            model.addRow(new Object[]{
                rs.getInt("ID"),          // ID, tetap ditambahkan tapi akan disembunyikan
                rs.getString("NIM"),      // NIM
                rs.getString("KodeMK"),   // Kode Mata Kuliah
                rs.getString("Nama"),     // Nama Mahasiswa
                rs.getString("Semester")  // Semester
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
    }

    // Sembunyikan kolom ID di JTable
    tabelKRS.getColumnModel().getColumn(0).setMinWidth(0);  // Menyembunyikan kolom pertama (ID)
    tabelKRS.getColumnModel().getColumn(0).setMaxWidth(0);  // Menyembunyikan kolom pertama (ID)
    tabelKRS.getColumnModel().getColumn(0).setWidth(0);     // Menyembunyikan kolom pertama (ID)
}



private void populateNIMComboBox() {
    cbNIM.removeAllItems(); // Hapus item sebelumnya
    cbNIM.addItem("-- Pilih NIM --"); // Tambahkan placeholder
    String queryNIM = "SELECT NIM FROM Mahasiswa";
    try (Connection conn = koneksiDB.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(queryNIM)) {
        while (rs.next()) {
            cbNIM.addItem(rs.getString("NIM"));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}

private void populateKodeMKComboBox() {
    cbKodeMK.removeAllItems(); // Hapus item sebelumnya
    cbKodeMK.addItem("-- Pilih KodeMK --"); // Tambahkan placeholder
    String queryKodeMK = "SELECT KodeMK FROM MataKuliah"; // Query untuk mengambil data
    try (Connection conn = koneksiDB.getConnection(); // Mendapatkan koneksi
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(queryKodeMK)) {
        while (rs.next()) {
            cbKodeMK.addItem(rs.getString("KodeMK")); // Tambahkan setiap KodeMK ke ComboBox
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); // Menampilkan error
        e.printStackTrace(); // Log error ke konsol untuk debugging
    }
}

 private void cariData() {
    String keyword = TxtCari.getText().trim(); // Ambil teks dari field pencarian
    DefaultTableModel model = (DefaultTableModel) tabelKRS.getModel();
    model.setRowCount(0); // Kosongkan tabel sebelum menampilkan hasil pencarian

    // Perbarui query untuk mencari berdasarkan NIM, KodeMK, Semester, atau Nama
     // Query SQL untuk mencari berdasarkan NIM, KodeMK, Semester, atau Nama
    String sql = "SELECT krs.ID, krs.NIM, krs.KodeMK, krs.Semester, mahasiswa.Nama " +
                 "FROM krs " +
                 "JOIN mahasiswa ON krs.NIM = mahasiswa.NIM " +
                 "WHERE krs.NIM LIKE ? OR krs.KodeMK LIKE ? OR krs.Semester LIKE ? OR mahasiswa.Nama LIKE ?";


    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
        // Set parameter untuk pencarian
        pstmt.setString(1, "%" + keyword + "%");
        pstmt.setString(2, "%" + keyword + "%");
        pstmt.setString(3, "%" + keyword + "%");
        pstmt.setString(4, "%" + keyword + "%");

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Tambahkan data ke tabel, termasuk kolom ID yang tetap tersembunyi
                model.addRow(new Object[]{
                    rs.getInt("ID"),           // ID tetap ada untuk operasi selanjutnya
                    rs.getString("NIM"),
                    rs.getString("KodeMK"),
                    rs.getString("Nama"),
                    rs.getString("Semester")
                });
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Pencarian gagal: " + e.getMessage());
    }

    // Sembunyikan kolom ID di tabel (sama seperti di `loadData`)
    tabelKRS.getColumnModel().getColumn(0).setMinWidth(0);
    tabelKRS.getColumnModel().getColumn(0).setMaxWidth(0);
    tabelKRS.getColumnModel().getColumn(0).setWidth(0);
}




private void simpanData() {
    String nim = cbNIM.getSelectedItem().toString();
    String kodeMk = cbKodeMK.getSelectedItem().toString();
    String semester = txtSemester.getText().trim();

    if (nim.isEmpty() || kodeMk.isEmpty() || semester.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
        return;
    }

    try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO krs (NIM, KodeMK, Semester) VALUES (?, ?, ?)")) {
        pstmt.setString(1, nim);
        pstmt.setString(2, kodeMk);
        pstmt.setString(3, semester);
        pstmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
        loadData();

        cbNIM.setSelectedIndex(0);
        cbKodeMK.setSelectedIndex(0);
        txtSemester.setText("");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
    }
}

private void ubahData() {
    int selectedRow = tabelKRS.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah.");
        return;
    }

    // Ambil ID dari kolom pertama tabel (yang tersembunyi)
    String id = tabelKRS.getValueAt(selectedRow, 0).toString();
    String nim = cbNIM.getSelectedItem().toString();
    String kodeMk = cbKodeMK.getSelectedItem().toString();
    String semester = txtSemester.getText().trim();

    // Debugging: Print nilai yang diambil
    System.out.println("ID: " + id);
    System.out.println("NIM: " + nim);
    System.out.println("KodeMK: " + kodeMk);
    System.out.println("Semester: " + semester);

    // Validasi input
    if (nim.isEmpty() || kodeMk.isEmpty() || semester.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
        return;
    }

    // Update data di database berdasarkan ID
    try (Connection conn = koneksiDB.getConnection(); // Pastikan ada koneksi
         PreparedStatement pstmt = conn.prepareStatement(
                 "UPDATE krs SET NIM = ?, KodeMK = ?, Semester = ? WHERE ID = ?")) {
        pstmt.setString(1, nim);
        pstmt.setString(2, kodeMk);
        pstmt.setString(3, semester);
        pstmt.setInt(4, Integer.parseInt(id)); // Konversi ID ke Integer

        // Eksekusi query update dan cek jumlah baris yang diupdate
        int rowsUpdated = pstmt.executeUpdate();

        // Debugging: Cek jumlah baris yang diupdate
        System.out.println("Rows updated: " + rowsUpdated);

        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
            loadData();  // Reload data untuk memperbarui tampilan
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada data yang diubah. Pastikan data yang dipilih benar.");
        }

        // Clear fields setelah update
        cbNIM.setSelectedIndex(0);
        cbKodeMK.setSelectedIndex(0);
        txtSemester.setText("");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
        e.printStackTrace();  // Print stack trace untuk debugging
    }
}

private void hapusData() {
    int selectedRow = tabelKRS.getSelectedRow(); // Mengambil baris yang dipilih dari tabel
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus.");
        return;
    }

    // Mengambil ID dari baris yang dipilih untuk dihapus
    String selectedIdString = tabelKRS.getValueAt(selectedRow, 0).toString(); // Ambil ID dari tabel
    int selectedId = Integer.parseInt(selectedIdString); // Mengonversi string menjadi integer

    // Debugging: Tampilkan ID yang akan dihapus
    System.out.println("ID yang akan dihapus: " + selectedId);

    int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda yakin ingin menghapus data dengan ID \"" + selectedId + "\"?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) { // Jika pengguna mengkonfirmasi penghapusan
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM KRS WHERE ID = ?")) {
            pstmt.setInt(1, selectedId);  // Set ID yang akan dihapus
            int affectedRows = pstmt.executeUpdate();  // Mengeksekusi perintah DELETE dan mendapatkan jumlah baris yang terpengaruh

            // Debugging: Tampilkan jumlah baris yang terpengaruh
            System.out.println("Jumlah baris yang terhapus: " + affectedRows);

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                loadData();  // Muat ulang data untuk memperbarui tabel di GUI
            } else {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan atau gagal dihapus.");
            }

            // Kosongkan ComboBox dan field input setelah data dihapus
            cbNIM.setSelectedIndex(0);  // Reset ComboBox NIM ke indeks pertama
            cbKodeMK.setSelectedIndex(0);  // Reset ComboBox KodeMK ke indeks pertama
            txtSemester.setText("");  // Kosongkan field Semester

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            e.printStackTrace();  // Debugging: cek error yang terjadi
        }
    }
}
private void batal() {
    cbNIM.setSelectedIndex(0);
    cbKodeMK.setSelectedIndex(0);
    txtSemester.setText("");
    tabelKRS.clearSelection();
}

private void cetak(){
                try {
                    String reportPath = "src/Report/laporanKRS.jasper"; // Lokasi file laporan Jasper
                    Connection conn = koneksiDB.getConnection(); // Metode untuk mendapatkan koneksi database

                    HashMap<String, Object> parameters = new HashMap<>(); // Membuat parameter untuk laporan
                   
                    JasperPrint print = JasperFillManager.fillReport(reportPath, parameters, conn); // Mengisi laporan Jasper dengan data
                    JasperViewer viewer = new JasperViewer(print, false); // Membuat viewer untuk menampilkan laporan
                    viewer.setVisible(true); // Menampilkan viewer laporan
                } catch (Exception e)    {
                    JOptionPane.showMessageDialog(this, "Kesalahan saat menampilkan laporan : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
        }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TxtCari = new javax.swing.JTextField();
        BtnCari = new javax.swing.JButton();
        BtnBatal = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        BtnCetak = new javax.swing.JButton();
        txtSemester = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelKRS = new javax.swing.JTable();
        BtnHapus = new javax.swing.JButton();
        BtnUbah = new javax.swing.JButton();
        BtnSimpan = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        cbNIM = new javax.swing.JComboBox<>();
        cbKodeMK = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 204, 153));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("INPUT KRS");
        jPanel1.add(jLabel1);

        TxtCari.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        BtnCari.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        BtnCari.setText("Cari");
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });

        BtnBatal.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        BtnBatal.setText("Batal");
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setText("NIM");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel3.setText("Kode MK");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel4.setText("Semester");

        BtnCetak.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        BtnCetak.setText("Cetak");
        BtnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCetakActionPerformed(evt);
            }
        });

        txtSemester.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        tabelKRS.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        tabelKRS.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NIM", "Kode MK", "Nama", "Semester"
            }
        ));
        tabelKRS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelKRSMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelKRS);

        BtnHapus.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        BtnHapus.setText("Hapus");
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });

        BtnUbah.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        BtnUbah.setText("Ubah");
        BtnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUbahActionPerformed(evt);
            }
        });

        BtnSimpan.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        BtnSimpan.setText("Simpan");
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton2.setText("Kembali");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        cbNIM.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        cbKodeMK.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(20, 20, 20)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(82, 82, 82)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtSemester)
                            .addComponent(cbNIM, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbKodeMK, 0, 191, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BtnSimpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BtnBatal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnUbah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BtnHapus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BtnCetak))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(TxtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnCari)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbNIM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbKodeMK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnHapus)
                    .addComponent(BtnUbah)
                    .addComponent(BtnBatal)
                    .addComponent(BtnSimpan)
                    .addComponent(BtnCetak))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TxtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnCari)
                    .addComponent(jButton2))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        new HalamanUtama().setVisible(true);  // Membuka frame aplikasi keuangan pribadi setelah login berhasil
        dispose(); // Menutup frame login
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        simpanData();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        batal();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUbahActionPerformed
        ubahData();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnUbahActionPerformed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        hapusData();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCetakActionPerformed
        cetak();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnCetakActionPerformed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        cariData();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnCariActionPerformed

    private void tabelKRSMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelKRSMouseClicked
          int selectedRow = tabelKRS.getSelectedRow(); // Ambil baris yang dipilih di tabel
    if (selectedRow != -1) { // Pastikan ada baris yang dipilih
        // Ambil data dari baris yang dipilih
        String id = tabelKRS.getValueAt(selectedRow, 0).toString();         // Ambil ID
        String nim = tabelKRS.getValueAt(selectedRow, 1).toString();        // Ambil NIM
        String kodeMk = tabelKRS.getValueAt(selectedRow, 2).toString();     // Ambil Kode MK
        String semester = tabelKRS.getValueAt(selectedRow, 4).toString();   // Ambil Semester

        // Debugging: Menampilkan data di konsol
        System.out.println("Baris yang dipilih: " + selectedRow);
        System.out.println("ID: " + id);
        System.out.println("NIM: " + nim);
        System.out.println("Kode MK: " + kodeMk);
        System.out.println("Semester: " + semester);

        // Set data yang diambil ke field input
        cbNIM.setSelectedItem(nim);        // Set NIM ke ComboBox cbNIM
        cbKodeMK.setSelectedItem(kodeMk);  // Set Kode MK ke ComboBox cbKodeMK
        txtSemester.setText(semester);     // Set Semester ke TextField txtSemester
    }

    }//GEN-LAST:event_tabelKRSMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(KRS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(KRS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(KRS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KRS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new KRS().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnBatal;
    private javax.swing.JButton BtnCari;
    private javax.swing.JButton BtnCetak;
    private javax.swing.JButton BtnHapus;
    private javax.swing.JButton BtnSimpan;
    private javax.swing.JButton BtnUbah;
    private javax.swing.JTextField TxtCari;
    private javax.swing.JComboBox<String> cbKodeMK;
    private javax.swing.JComboBox<String> cbNIM;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelKRS;
    private javax.swing.JTextField txtSemester;
    // End of variables declaration//GEN-END:variables
}
