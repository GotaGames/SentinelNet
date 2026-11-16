package view;

import dao.*;
import model.Requisicao;
import model.Servidor;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class JanelaProcessamento extends JFrame {

    private JTextPane consola;
    private StyledDocument doc;
    private RequisicaoDAO requisicaoDAO = new RequisicaoDAO();
    private LogDAO logDAO = new LogDAO();
    private ServidorDAO servidorDAO = new ServidorDAO();
    private AlertaDAO alertaDAO = new AlertaDAO();

    public JanelaProcessamento() {
        setTitle("Sentinel Net - Motor de Processamento");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        consola = new JTextPane();
        consola.setEditable(false);
        consola.setBackground(new Color(20, 20, 20));
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
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void processarPendentes() {
        List<Requisicao> pendentes = requisicaoDAO.listarPendentes();
        for (Requisicao r : pendentes) {
            int idServidor = encontrarIdServidorPorNome(r.getServidorDestinoNome());
            String estadoServidor = servidorDAO.estadoDoServidor(idServidor);

            int risco;
            String resultado;

            if (estadoServidor.equals("Isolado")) {
                risco = 10;
                resultado = "BLOQUEADA (Servidor Isolado)";
                requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Bloqueada");
                escrever("[ISOLADO] " + r.getAcaoSolicitada() + " -> " + r.getServidorDestinoNome() + " (risco 10)\n", Color.RED);
            } else if (estadoServidor.equals("Sob Suspeita")) {
                risco = 8;
                resultado = "BLOQUEADA (Servidor Sob Suspeita)";
                requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Bloqueada");
                escrever("[SUSPEITO] " + r.getAcaoSolicitada() + " -> " + r.getServidorDestinoNome() + " (risco 8)\n", Color.ORANGE);
            } else if (r.getAcaoSolicitada().equals("DownloadMassivo") || r.getAcaoSolicitada().equals("AlterarPrivilegio")) {
                risco = 9;
                resultado = "BLOQUEADA (Ação Crítica)";
                requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Bloqueada");
                escrever("[CRÍTICO] " + r.getAcaoSolicitada() + " -> " + r.getServidorDestinoNome() + " (risco 9)\n", Color.RED);
            } else {
                risco = 2;
                resultado = "PERMITIDA";
                requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Permitida");
                escrever("[OK] " + r.getAcaoSolicitada() + " -> " + r.getServidorDestinoNome() + " (risco 2)\n", Color.GREEN);
            }

            Integer idLogPai = logDAO.ultimoLogDoServidor(idServidor);
            int idLogGerado = logDAO.inserirLog(idServidor, r.getIdRequisicao(), resultado, risco, idLogPai);

            if (risco >= 8) {
                alertaDAO.enviarAlerta(idLogGerado, 1, "SMS", "Critico");
                alertaDAO.enviarAlerta(idLogGerado, 2, "Email", "Critico");
                alertaDAO.enviarAlerta(idLogGerado, 1, "Painel_Web", "Critico");
                escrever("   >> ALERTA CRÍTICO enviado a Ana Silva (SMS) e Bruno Costa (Email)\n", Color.MAGENTA);
            } else if (risco >= 5) {
                alertaDAO.enviarAlerta(idLogGerado, 1, "Painel_Web", "Suspeita");
                escrever("   >> ALERTA DE SUSPEITA registado no painel\n", Color.YELLOW);
            }
        }
    }

    private int encontrarIdServidorPorNome(String nome) {
        List<Servidor> servidores = servidorDAO.listarTodos();
        for (Servidor s : servidores) if (s.getNome().equals(nome)) return s.getIdServidor();
        return 0;
    }
}