package view;

import dao.RequisicaoDAO;
import model.Requisicao;
import service.ServicoProcessamento;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class JanelaProcessamento extends JFrame {

    private JTextPane consola;
    private StyledDocument doc;
    private JLabel lblContadores;
    private RequisicaoDAO requisicaoDAO = new RequisicaoDAO();
    private ServicoProcessamento servico = new ServicoProcessamento();
    private int total = 0, permitidas = 0, bloqueadas = 0, isolamentos = 0, alertas = 0;

    public JanelaProcessamento() {
        setTitle("Sentinel Net - Motor de Processamento");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        lblContadores = new JLabel("Processadas: 0 | Permitidas: 0 | Bloqueadas: 0 | Isolamentos: 0 | Alertas: 0");
        lblContadores.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblContadores.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(lblContadores, BorderLayout.NORTH);

        consola = new JTextPane();
        consola.setEditable(false);
        consola.setBackground(new Color(15, 15, 15));
        consola.setForeground(Color.WHITE);
        consola.setFont(new Font("Monospaced", Font.PLAIN, 12));
        doc = consola.getStyledDocument();
        add(new JScrollPane(consola), BorderLayout.CENTER);

        Timer timer = new Timer(2000, e -> processarPendentes());
        timer.start();

        escrever("[SISTEMA] Motor Sentinel Net iniciado.\n", Color.GREEN);
    }

    private void escrever(String texto, Color cor) {
        try {
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, cor);
            doc.insertString(doc.getLength(), texto, attrs);
            consola.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) { e.printStackTrace(); }
    }

    private void processarPendentes() {
        List<Requisicao> pendentes = requisicaoDAO.listarPendentes();
        for (Requisicao r : pendentes) {
            String resultado = servico.processar(r);
            total++;
            if (resultado.contains("PERMITIDA")) permitidas++;
            else if (resultado.contains("ISOLADO") || resultado.contains("ISOLAMENTO")) { bloqueadas++; isolamentos++; alertas += 3; escrever(resultado + "\n", Color.RED); }
            else if (resultado.contains("SUSPEITO")) { bloqueadas++; alertas++; escrever(resultado + "\n", Color.ORANGE); }
            else escrever(resultado + "\n", Color.GREEN);
        }
        lblContadores.setText("Processadas: " + total + " | Permitidas: " + permitidas +
                " | Bloqueadas: " + bloqueadas + " | Isolamentos: " + isolamentos + " | Alertas: " + alertas);
    }
}