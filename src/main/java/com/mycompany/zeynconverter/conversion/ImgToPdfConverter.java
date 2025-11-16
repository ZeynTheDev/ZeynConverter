/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.zeynconverter.conversion;

import com.mycompany.zeynconverter.MainFrame;
import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

/**
 *
 * @author Zeyn
 */
public class ImgToPdfConverter extends javax.swing.SwingWorker<String, Integer> {

    private List<File> filesToConvert;
    private String outputPath;
    private MainFrame mainFrame;
    private JProgressBar progressBar;
    private boolean isLossless;

    // 1. Konstruktor: Terima data yang dibutuhkan dari thread utama
    public ImgToPdfConverter (List<File> files, String path,
            MainFrame frame, JProgressBar bar, boolean isLossless) {
        // Buat salinan list agar aman dari perubahan
        this.filesToConvert = new ArrayList<>(files); 
        this.outputPath = path;
        this.mainFrame = frame;
        this.progressBar = bar;
        this.isLossless = isLossless;
    }

    // 2. doInBackground(): Ini adalah "MESIN"
    // Berjalan di BACKGROUND THREAD.
    // JANGAN sentuh GUI (progressBar, dll) dari sini!
    @Override
    protected String doInBackground() throws Exception {
        try (PDDocument doc = new PDDocument()) {
            int totalFiles = filesToConvert.size();
            
            for (int i = 0; i < totalFiles; i++) {
                File imageFile = filesToConvert.get(i);
                
                // --- Logika PDFBox Anda ---
                BufferedImage bImage = ImageIO.read(imageFile);
                
                if(bImage == null){
                    throw new IOException("Failed to read image (plugin error): " + imageFile.getName());
                }
                
                PDImageXObject pdImage;
                if(this.isLossless) {
                    pdImage = LosslessFactory.createFromImage(doc, bImage);
                } else {
                    pdImage = JPEGFactory.createFromImage(doc, bImage, 0.90f);
                }
                PDRectangle pageSize = new PDRectangle(pdImage.getWidth(), pdImage.getHeight());
                PDPage page = new PDPage(pageSize);
                doc.addPage(page);
                
                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                    contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
                }
                // --- Selesai logika PDFBox ---
                
                // 3. KIRIM PROGRESS: Hitung persentase dan kirim ke GUI
                int progress = (int) (((double) (i + 1) / totalFiles) * 100);
                publish(progress); // Ini akan memicu method process()
            }
            
            doc.save(outputPath);
            
            // 4. KIRIM HASIL: Kirim pesan sukses
            return "Conversion Success!\nPDF saved in: " + outputPath;
            
        } catch (Exception e) {
            e.printStackTrace();
            // 4. KIRIM HASIL: Kirim pesan error
            return "Error: An error occured during conversion:\n" + e.getMessage();
        }
    }

    // 5. process(): Ini untuk UPDATE GUI
    // Berjalan di GUI THREAD. Aman sentuh GUI.
    // Dipanggil setiap kali 'publish()' dieksekusi.
    @Override
    protected void process(List<Integer> chunks) {
        // Ambil progress TERBARU yang dikirim
        int latestProgress = chunks.get(chunks.size() - 1);
        this.progressBar.setValue(latestProgress); // Update JProgressBar
    }

    // 6. done(): Ini untuk PEMBERSIHAN
    // Berjalan di GUI THREAD.
    // Dipanggil SETELAH doInBackground() selesai (baik sukses atau error).
    @Override
    protected void done() {
        try {
            // 7. Ambil HASIL dari doInBackground()
            String result = get(); // 'get()' mengambil return value
            
            // 8. Tampilkan hasil (sukses atau error)
            if (result.startsWith("Error:")) {
                JOptionPane.showMessageDialog(mainFrame, result, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Object[] options = {"Open File", "Close"};

            // 2. Tampilkan dialog interaktif
            int choice = JOptionPane.showOptionDialog(
                    mainFrame,
                    result, // "Conversion Success!..."
                    "Success",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, // (no custom icon)
                    options, // Teks tombol kustom kita
                    options[0]); // Pilihan default (tombol "Open File")

            // 3. Check user selection
            if (choice == 0) { // 0 is the first btton ("Open File")
                try {
                    // 'this.outputPath' was contains full path
                    Desktop.getDesktop().open(new File(this.outputPath)); 
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    // show error if couldn't open file
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Could not open file: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            }
            
        } catch (Exception e) {
            // catch error from SwingWorker itself
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, 
                    "Internal error on worker: " + e.getMessage(), 
                    "Worker Error", 
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // 9. BERSIH-BERSIH: APAPUN YANG TERJADI...
            // Aktifkan lagi semua tombol
            mainFrame.setUIEnabled(true);
            
            // Reset progress bar
            progressBar.setValue(0);
        }
    }
}
