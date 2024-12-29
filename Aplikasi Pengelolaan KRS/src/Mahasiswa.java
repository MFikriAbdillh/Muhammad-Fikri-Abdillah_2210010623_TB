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
public class Mahasiswa extends javax.swing.JFrame {
    private Connection conn;

    /**
     * Creates new form Mahasiswa
     */
    public Mahasiswa() {
        initComponents();
        koneksi();
        loadData();
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
            DefaultTableModel model = (DefaultTableModel) tabelMahasiswa.getModel();
            model.setRowCount(0); // Kosongkan tabel
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM mahasiswa")) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("nim"),
                        rs.getString("nama"),
                        rs.getString("jurusan")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
            }
        }
        private void cariData() {
            String keyword = txtCari.getText().trim(); // Ambil teks dari field pencarian
            DefaultTableModel model = (DefaultTableModel) tabelMahasiswa.getModel();
            model.setRowCount(0); // Kosongkan tabel sebelum menampilkan hasil pencarian

            // SQL query untuk mencari berdasarkan NIM, Nama, atau Jurusan
            String sql = "SELECT * FROM Mahasiswa WHERE NIM LIKE ? OR Nama LIKE ? OR Jurusan LIKE ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                // Menggunakan parameter pencarian pada NIM, Nama, atau Jurusan
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");
                pstmt.setString(3, "%" + keyword + "%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("NIM"),
                            rs.getString("Nama"),
                            rs.getString("Jurusan")
                        });
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Pencarian gagal: " + e.getMessage());
            }
        }

       private void simpanData() {
            String nim = TxtNIM.getText().trim();  // Mengambil NIM dari field TxtNIM
            String nama = TxtMahasiswa.getText().trim();  // Mengambil nama dari field TxtMahasiswa
            String jurusan = TxtJurusan.getText().trim();  // Mengambil jurusan dari field TxtJurusan

            // Validasi input
            if (nim.isEmpty() || nama.isEmpty() || jurusan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Mahasiswa (NIM, Nama, Jurusan) VALUES (?, ?, ?)")) {
                pstmt.setString(1, nim);
                pstmt.setString(2, nama);
                pstmt.setString(3, jurusan);
                pstmt.executeUpdate();  // Eksekusi query untuk menyimpan data

                JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");

                // Memuat ulang data dari database ke tabel GUI
                loadData();

                // Kosongkan field input setelah data disimpan
                TxtNIM.setText("");
                TxtMahasiswa.setText("");
                TxtJurusan.setText("");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
            }
        }

        private void ubahData(){
            int selectedRow = tabelMahasiswa.getSelectedRow(); // Mengambil baris yang dipilih dari tabel
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih mahasiswa yang ingin diubah.");
                return;
            }

            String nim = TxtNIM.getText().trim();  // Mengambil NIM dari field TxtNIM
            String nama = TxtMahasiswa.getText().trim();  // Mengambil nama dari field TxtMahasiswa
            String jurusan = TxtJurusan.getText().trim();  // Mengambil jurusan dari field TxtJurusan
            String selectedNIM = tabelMahasiswa.getValueAt(selectedRow, 0).toString(); // Mengambil NIM dari tabel pada baris yang dipilih

            // Validasi input
            if (nim.isEmpty() || nama.isEmpty() || jurusan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE Mahasiswa SET NIM = ?, Nama = ?, Jurusan = ? WHERE NIM = ?")) {
                pstmt.setString(1, nim); // Set NIM baru
                pstmt.setString(2, nama); // Set nama baru
                pstmt.setString(3, jurusan); // Set jurusan baru
                pstmt.setString(4, selectedNIM); // Set NIM lama untuk identifikasi
                pstmt.executeUpdate(); // Eksekusi perintah UPDATE
                JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
                loadData(); // Muat ulang data untuk memperbarui tabel di GUI
                // Kosongkan field input setelah data disimpan
                TxtNIM.setText("");
                TxtMahasiswa.setText("");
                TxtJurusan.setText("");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
            }
        }
        private void hapusData(){
            int selectedRow = tabelMahasiswa.getSelectedRow(); // Mengambil baris yang dipilih dari tabel
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih mahasiswa yang ingin dihapus.");
                return;
            }

            // Mengambil NIM dari baris yang dipilih untuk dihapus
            String selectedNIM = tabelMahasiswa.getValueAt(selectedRow, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(
                    this, 
                    "Apakah Anda yakin ingin menghapus mahasiswa dengan NIM \"" + selectedNIM + "\"?", 
                    "Konfirmasi Hapus", 
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) { // Jika pengguna mengkonfirmasi penghapusan
                try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Mahasiswa WHERE NIM = ?")) {
                    pstmt.setString(1, selectedNIM); // Mengatur NIM sebagai parameter untuk query DELETE
                    pstmt.executeUpdate(); // Eksekusi perintah DELETE
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                    loadData(); // Muat ulang data untuk memperbarui tabel di GUI
                           
                    // Kosongkan field input setelah data disimpan
                    TxtNIM.setText("");
                    TxtMahasiswa.setText("");
                    TxtJurusan.setText("");
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
                }
            }
        }
        private void batal(){
            TxtNIM.setText("");  // Mengosongkan field TxtNIM
            TxtMahasiswa.setText("");  // Mengosongkan field TxtMahasiswa
            TxtJurusan.setText("");  // Mengosongkan field TxtJurusan
            txtCari.setText("");  // Mengosongkan field txtCari (jika ada)
            tabelMahasiswa.clearSelection();
        }
        private void cetak(){
                try {
                    String reportPath = "src/Report/laporanMahasiswa.jasper"; // Lokasi file laporan Jasper
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
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelMahasiswa = new javax.swing.JTable();
        BtnHapus = new javax.swing.JButton();
        BtnUbah = new javax.swing.JButton();
        BtnSimpan = new javax.swing.JButton();
        BtnBatal = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        TxtNIM = new javax.swing.JTextField();
        TxtMahasiswa = new javax.swing.JTextField();
        TxtJurusan = new javax.swing.JTextField();
        BtnCetak = new javax.swing.JButton();
        txtCari = new javax.swing.JTextField();
        BtnCari = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 204, 153));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("INPUT MAHASISWA");
        jPanel1.add(jLabel1);

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jButton1.setText("Kembali");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tabelMahasiswa.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        tabelMahasiswa.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NIM", "Nama Mahasiswa", "Jurusan"
            }
        ));
        tabelMahasiswa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelMahasiswaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelMahasiswa);

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
        jLabel3.setText("Nama Mahasiswa");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel4.setText("Jurusan");

        TxtNIM.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        TxtMahasiswa.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        TxtMahasiswa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtMahasiswaActionPerformed(evt);
            }
        });

        TxtJurusan.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        BtnCetak.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        BtnCetak.setText("Cetak");
        BtnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCetakActionPerformed(evt);
            }
        });

        txtCari.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        BtnCari.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        BtnCari.setText("Cari");
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(BtnSimpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BtnBatal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnUbah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BtnHapus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BtnCetak))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnCari)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(20, 20, 20))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(100, 100, 100)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TxtNIM)
                            .addComponent(TxtMahasiswa)
                            .addComponent(TxtJurusan))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(TxtNIM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(TxtMahasiswa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(TxtJurusan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnCari)
                    .addComponent(jButton1))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        cariData();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCetakActionPerformed
        cetak();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnCetakActionPerformed

    private void TxtMahasiswaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtMahasiswaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtMahasiswaActionPerformed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        batal();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        simpanData();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUbahActionPerformed
        ubahData();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnUbahActionPerformed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        hapusData();        // TODO add your handling code here:
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void tabelMahasiswaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelMahasiswaMouseClicked
        int selectedRow = tabelMahasiswa.getSelectedRow();  // Ambil baris yang dipilih
        if (selectedRow != -1) {  // Pastikan ada baris yang dipilih
            // Ambil data dari baris yang dipilih dan set ke field input
            String nim = tabelMahasiswa.getValueAt(selectedRow, 0).toString();  // NIM ada di kolom pertama (0)
            String nama = tabelMahasiswa.getValueAt(selectedRow, 1).toString();  // Nama ada di kolom kedua (1)
            String jurusan = tabelMahasiswa.getValueAt(selectedRow, 2).toString();  // Jurusan ada di kolom ketiga (2)

            // Set data yang diambil ke field input
            TxtNIM.setText(nim);
            TxtMahasiswa.setText(nama);
            TxtJurusan.setText(jurusan);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_tabelMahasiswaMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        new HalamanUtama().setVisible(true);  // Membuka frame aplikasi keuangan pribadi setelah login berhasil
        dispose(); // Menutup frame login
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(Mahasiswa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Mahasiswa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Mahasiswa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Mahasiswa.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Mahasiswa().setVisible(true);
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
    private javax.swing.JTextField TxtJurusan;
    private javax.swing.JTextField TxtMahasiswa;
    private javax.swing.JTextField TxtNIM;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelMahasiswa;
    private javax.swing.JTextField txtCari;
    // End of variables declaration//GEN-END:variables
}
