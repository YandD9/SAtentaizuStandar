import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private int size;
    private JTextField[][] kPPermainan;
    private JButton tombolPecahkan;
    private JButton tombolClear;
    private TentaizuSolver penyelesaian;

    public Main(int size) {
        this.size = size;
        this.kPPermainan = new JTextField[size][size];
        this.penyelesaian = new TentaizuSolver(size);

        setTitle("SUDOKUH");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelPapan = new JPanel(new GridLayout(size, size));
        // BUAT KOTA PERMAINAN
        for (int baris = 0; baris < size; baris++) {
            for (int kolom = 0; kolom < size; kolom++) {
                JTextField kotak = new JTextField(2);
                kotak.setHorizontalAlignment(JTextField.CENTER);
                kPPermainan[baris][kolom] = kotak;
                panelPapan.add(kotak);
            }
        }
        add(panelPapan, BorderLayout.CENTER);

        tombolPecahkan = new JButton("Selesaikan");
        tombolPecahkan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selesaikanTentaizu();
            }
        });

        tombolClear = new JButton("Bersihkan");
        tombolClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bersihkanPapan();
            }
        });

        JPanel panelTombol = new JPanel(new FlowLayout());
        panelTombol.add(tombolPecahkan);
        panelTombol.add(tombolClear);
        add(panelTombol, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void selesaikanTentaizu() {
        penyelesaian.reset(); // Reset sebelum ado solusi

        // Mengambi nilai untuk di isi ke papan permainan
        for (int baris = 0; baris < size; baris++) {
            for (int kolom = 0; kolom < size; kolom++) {
                String teks = kPPermainan[baris][kolom].getText();
                if (!teks.isEmpty()) {
                    int nilai = Integer.parseInt(teks);
                    penyelesaian.setNilaiPapan(baris, kolom, nilai);
                }
            }
        }

        if (penyelesaian.selesaikan()) {
            // INI SOLUSI
            int[][] solusi = penyelesaian.getSolusi();
            for (int baris = 0; baris < size; baris++) {
                for (int kolom = 0; kolom < size; kolom++) {
                    kPPermainan[baris][kolom].setText(String.valueOf(solusi[baris][kolom]));
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada solusi yang mungkin.", "Tentaizu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bersihkanPapan() {
        penyelesaian.reset(); // Reset kalau sudah menghapus solusi papan permainan

        for (int baris = 0; baris < size; baris++) {
            for (int kolom = 0; kolom < size; kolom++) {
                kPPermainan[baris][kolom].setText("");
            }
        }
    }

    public static void main(String[] args) {
        int size = 5; // Ukuran papan permainan
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main(size);
            }
        });
    }
}

class TentaizuSolver {
    private int size;
    private int[][] papan;
    private int[][] solusi;
    private boolean[][] barisTerpakai;
    private boolean[][] kolomTerpakai;

    public TentaizuSolver(int size) {
        this.size = size;
        this.papan = new int[size][size];
        this.solusi = new int[size][size];
        this.barisTerpakai = new boolean[size][size + 1];
        this.kolomTerpakai = new boolean[size][size + 1];
    }

    public void setNilaiPapan(int baris, int kolom, int nilai) {
        papan[baris][kolom] = nilai;
        barisTerpakai[baris][nilai] = true;
        kolomTerpakai[kolom][nilai] = true;
    }

    public boolean selesaikan() {
        return selesaikanSecaraRekursif(0, 0);
    }

    private boolean selesaikanSecaraRekursif(int baris, int kolom) {
        if (baris == size) {
            return true; // Base case: SELESAIII
        }

        if (kolom == size) {
            return selesaikanSecaraRekursif(baris + 1, 0); // Biar baris dak terisi dua kali
        }

        if (papan[baris][kolom] != 0) {
            return selesaikanSecaraRekursif(baris, kolom + 1); // Biar kolom dak terisi dua kali
        }

        for (int angka = 1; angka <= size; angka++) {
            if (isValidPlacement(baris, kolom, angka)) {
                solusi[baris][kolom] = angka;
                barisTerpakai[baris][angka] = true;
                kolomTerpakai[kolom][angka] = true;

                if (selesaikanSecaraRekursif(baris, kolom + 1)) {
                    return true;
                }

                solusi[baris][kolom] = 0;
                barisTerpakai[baris][angka] = false;
                kolomTerpakai[kolom][angka] = false;
            }
        }

        return false; // Tidak ada solusi yang mungkin pada posisi ini
    }

    private boolean isValidPlacement(int baris, int kolom, int angka) {
        // Cek aturan tidak ada angka yang berulang di baris dan kolom
        if (barisTerpakai[baris][angka] || kolomTerpakai[kolom][angka]) {
            return false;
        }

        return true; // Valid
    }

    public int[][] getSolusi() {
        return solusi;
    }

    public void reset() {
        this.papan = new int[size][size];
        this.solusi = new int[size][size];
        this.barisTerpakai = new boolean[size][size + 1];
        this.kolomTerpakai = new boolean[size][size + 1];
    }
}
