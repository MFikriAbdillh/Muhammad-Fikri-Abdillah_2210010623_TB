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
public class MataKuliah extends javax.swing.JFrame {
     private Connection conn;
    /**
     * Creates new form MataKuliah
     */
    public MataKuliah() {
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
            DefaultTableModel model = (DefaultTableModel) tabelMataKuliah.getModel();
            model.setRowCount(0); // Kosongkan tabel
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM matakuliah")) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("kodemk"),
                        rs.getString("namamk"),
                        rs.getString("sks")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
            }
        }
        private void cariData() {
            String keyword = TxtCari.getText().trim(); // Ambil teks dari field pencarian
            DefaultTableModel model = (DefaultTableModel) tabelMataKuliah.getModel();
            model.setRowCount(0); // Kosongkan tabel sebelum menampilkan hasil pencarian

       
            String sql = "SELECT * FROM matakuliah WHERE KodeMK LIKE ? OR NamaMK LIKE ? OR SKS LIKE ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
 
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");
                pstmt.setString(3, "%" + keyword + "%");

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getString("KodeMK"),
                            rs.getString("NamaMK"),
                            rs.getString("SKS")
                        });
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Pencarian gagal: " + e.getMessage());
            }
        }

         private void simpanData() {
            String kodeMk = txtKodeMk.getText().trim();  
            String namaMk = txtNamaMk.getText().trim();  
            String sks = txtSks.getText().trim();  

            // Validasi input
            if (kodeMk.isEmpty() || namaMk.isEmpty() || sks.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO matakuliah (KodeMK, NamaMK, SKS) VALUES (?, ?, ?)")) {
                pstmt.setString(1, kodeMk);
                pstmt.setString(2, namaMk);
                pstmt.setString(3, sks);
                pstmt.executeUpdate();  // Eksekusi query untuk menyimpan data

                JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");

                // Memuat ulang data dari database ke tabel GUI
                loadData();

                // Kosongkan field input setelah data disimpan
                txtKodeMk.setText("");
                txtNamaMk.setText("");
                txtSks.setText("");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
            }
        }

        private void ubahData(){
            int selectedRow = tabelMataKuliah.getSelectedRow(); // Mengambil baris yang dipilih dari tabel
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Pilih Mata kuliah yang ingin diubah.");
                return;
            }

            String kodeMk = txtKodeMk.getText().trim();  
            String namaMk = txtNamaMk.getText().trim();  
            String sks = txtSks.getText().trim();       
            String selectedKodeMk = tabelMataKuliah.getValueAt(selectedRow, 0).toString(); 

            // Validasi input
            if (kodeMk.isEmpty() || namaMk.isEmpty() || sks.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mohon lengkapi semua data.");
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE matakuliah SET KodeMK = ?, NamaMK = ?, SKS = ? WHERE KodeMK = ?")) {
                pstmt.setString(1, kodeMk); 
                pstmt.setString(2, namaMk);
                pstmt.setString(3, sks); 
                pstmt.setString(4, selectedKodeMk); 
                pstmt.executeUpdate(); // Eksekusi perintah UPDATE
                JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
                loadData(); // Muat ulang data untuk memperbarui tabel di GUI
                // Kosongkan field input setelah data disimpan
                txtKodeMk.setText("");
                txtNamaMk.setText("");
                txtSks.setText("");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Gagal mengubah data: " + e.getMessage());
            }
        }
        private void hapusData() {
    int selectedRow = tabelMataKuliah.getSelectedRow(); // Mengambil baris yang dipilih dari tabel
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih Mata kuliah yang ingin dihapus.");
        return;
    }

    // Mengambil KodeMK dari baris yang dipilih untuk dihapus
    String selectedKodeMk = tabelMataKuliah.getValueAt(selectedRow, 0).toString();
    System.out.println("KodeMK yang akan dihapus: " + selectedKodeMk);  // Debugging

    int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda yakin ingin menghapus matakuliah dengan KodeMK \"" + selectedKodeMk + "\"?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) { // Jika pengguna mengkonfirmasi penghapusan
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM matakuliah WHERE KodeMK = ?")) {
            pstmt.setString(1, selectedKodeMk); 

            // Debugging: Tampilkan query yang akan dieksekusi
            System.out.println("Query yang akan dieksekusi: " + pstmt.toString());

            int rowsAffected = pstmt.executeUpdate();  // Mengecek berapa baris yang terpengaruh
            System.out.println("Rows affected: " + rowsAffected);  // Debugging

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                loadData(); // Muat ulang data untuk memperbarui tabel di GUI
            } else {
                JOptionPane.showMessageDialog(this, "Data dengan KodeMK \"" + selectedKodeMk + "\" tidak ditemukan.");
            }

            // Kosongkan field input setelah data disimpan
            txtKodeMk.setText("");
            txtNamaMk.setText("");
            txtSks.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            e.printStackTrace();  // Debugging: cek error yang terjadi
        }
    }
}

        private void batal(){
            txtKodeMk.setText("");
            txtNamaMk.setText("");
            txtSks.setText("");  
            TxtCari.setText("");  // Mengosongkan field txtCari (jika ada)
            tabelMataKuliah.clearSelection();
        }
        private void cetak(){
                try {
                    String reportPath = "src/Report/laporanMataKuliah.jasper"; // Lokasi file laporan Jasper
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
        txtKodeMk = new javax.swing.JTextField();
        txtNamaMk = new javax.swing.JTextField();
        txtSks = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelMataKuliah = new javax.swing.JTable();
        BtnHapus = new javax.swing.JButton();
        BtnUbah = new javax.swing.JButton();
        BtnSimpan = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 204, 153));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("INPUT MATA KULIAH");
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
        jLabel2.setText("Kode MK");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel3.setText("Nama Mk");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel4.setText("SKS");

        BtnCetak.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        BtnCetak.setText("Cetak");
        BtnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCetakActionPerformed(evt);
            }
        });

        txtKodeMk.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        txtNamaMk.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        txtNamaMk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaMkActionPerformed(evt);
            }
        });

        txtSks.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        tabelMataKuliah.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        tabelMataKuliah.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode MK", "Nama MK", "SKS"
            }
        ));
        tabelMataKuliah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelMataKuliahMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabelMataKuliah);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                            .addComponent(txtKodeMk, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(20, 20, 20)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtSks)
                                .addComponent(txtNamaMk, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))))
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
                        .addComponent(TxtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(txtKodeMk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNamaMk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtSks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNamaMkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaMkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaMkActionPerformed

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

    private void tabelMataKuliahMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelMataKuliahMouseClicked
    int selectedRow = tabelMataKuliah.getSelectedRow();  // Ambil baris yang dipilih
        if (selectedRow != -1) {  // Pastikan ada baris yang dipilih
            // Ambil data dari baris yang dipilih dan set ke field input
            String kodeMk = tabelMataKuliah.getValueAt(selectedRow, 0).toString();  
            String namaMk = tabelMataKuliah.getValueAt(selectedRow, 1).toString();  
            String sks = tabelMataKuliah.getValueAt(selectedRow, 2).toString();  

            // Set data yang diambil ke field input
            txtKodeMk.setText(kodeMk);
            txtNamaMk.setText(namaMk);
            txtSks.setText(sks);
        }         // TODO add your handling code here:
    }//GEN-LAST:event_tabelMataKuliahMouseClicked

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
            java.util.logging.Logger.getLogger(MataKuliah.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MataKuliah.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MataKuliah.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MataKuliah.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MataKuliah().setVisible(true);
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
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelMataKuliah;
    private javax.swing.JTextField txtKodeMk;
    private javax.swing.JTextField txtNamaMk;
    private javax.swing.JTextField txtSks;
    // End of variables declaration//GEN-END:variables
}
