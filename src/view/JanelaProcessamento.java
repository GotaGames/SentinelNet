package view;

import dao.*;
import model.Requisicao;
import model.Servidor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JanelaProcessamento extends JFrame {

    private JTextArea areaLog;
    private RequisicaoDAO requisicaoDAO = new RequisicaoDAO();
    private LogDAO logDAO = new LogDAO();
    private ServidorDAO servidorDAO = new ServidorDAO();

    public JanelaProcessamento() {
        setTitle("Sentinel Net - Processamento");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        areaLog = new JTextArea();
        areaLog.setEditable(false);
        add(new JScrollPane(areaLog), BorderLayout.CENTER);

        Timer timer = new Timer(3000, e -> processarPendentes());
        timer.start();
    }

    private void processarPendentes() {
        List<Requisicao> pendentes = requisicaoDAO.listarPendentes();
        for (Requisicao r : pendentes) {
            // Lógica simples: ações de risco em servidores de criticidade alta são bloqueadas
            boolean bloquear = r.getAcaoSolicitada().equals("DownloadMassivo") || r.getAcaoSolicitada().equals("AlterarPrivilegio");

            if (bloquear) {
                requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Bloqueada");
                int idServidor = encontrarIdServidorPorNome(r.getServidorDestinoNome());
                logDAO.inserirLog(idServidor, r.getIdRequisicao(), r.getAcaoSolicitada(), 9, null);
                areaLog.append("BLOQUEADA: " + r.getAcaoSolicitada() + " em " + r.getServidorDestinoNome() + "\n");
            } else {
                requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Permitida");
                int idServidor = encontrarIdServidorPorNome(r.getServidorDestinoNome());
                logDAO.inserirLog(idServidor, r.getIdRequisicao(), r.getAcaoSolicitada(), 2, null);
                areaLog.append("PERMITIDA: " + r.getAcaoSolicitada() + " em " + r.getServidorDestinoNome() + "\n");
            }
        }
    }

    private int encontrarIdServidorPorNome(String nome) {
        List<Servidor> servidores = servidorDAO.listarTodos();
        for (Servidor s : servidores) {
            if (s.getNome().equals(nome)) return s.getIdServidor();
        }
        return 0;
    }
}