package view;

import dao.LogDAO;
import dao.ServidorDAO;
import model.LogOperacao;
import model.Servidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class JanelaAdmin extends JFrame {

    private DefaultTableModel modeloServidores;
    private DefaultTableModel modeloLogs;
    private ServidorDAO servidorDAO = new ServidorDAO();
    private LogDAO logDAO = new LogDAO();

    public JanelaAdmin() {
        setTitle("Sentinel Net - Painel do Administrador");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        modeloServidores = new DefaultTableModel(new String[]{"ID", "Nome", "IP Interno", "Criticidade", "Estado"}, 0);
        JTable tabelaServidores = new JTable(modeloServidores);
        JScrollPane scrollServidores = new JScrollPane(tabelaServidores);
        scrollServidores.setBorder(BorderFactory.createTitledBorder("Servidores"));

        modeloLogs = new DefaultTableModel(new String[]{"ID Log", "Servidor", "Ação", "Data/Hora", "Risco", "Log Pai"}, 0);
        JTable tabelaLogs = new JTable(modeloLogs);
        JScrollPane scrollLogs = new JScrollPane(tabelaLogs);
        scrollLogs.setBorder(BorderFactory.createTitledBorder("Últimos Logs"));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollServidores, scrollLogs);
        split.setResizeWeight(0.4);
        add(split, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton botaoIsolar = new JButton("Isolar Servidor Selecionado");
        botaoIsolar.addActionListener(e -> {
            int linha = tabelaServidores.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Seleciona um servidor na tabela.");
                return;
            }
            int idServidor = (int) modeloServidores.getValueAt(linha, 0);
            servidorDAO.atualizarStatus(idServidor, "Isolado");
            JOptionPane.showMessageDialog(this, "Servidor isolado.");
        });

        JButton botaoSuspeita = new JButton("Marcar Sob Suspeita");
        botaoSuspeita.addActionListener(e -> {
            int linha = tabelaServidores.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Seleciona um servidor na tabela.");
                return;
            }
            int idServidor = (int) modeloServidores.getValueAt(linha, 0);
            servidorDAO.atualizarStatus(idServidor, "Sob Suspeita");
            JOptionPane.showMessageDialog(this, "Servidor marcado como Sob Suspeita.");
        });

        JButton botaoAtivar = new JButton("Reativar Servidor");
        botaoAtivar.addActionListener(e -> {
            int linha = tabelaServidores.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Seleciona um servidor na tabela.");
                return;
            }
            int idServidor = (int) modeloServidores.getValueAt(linha, 0);
            servidorDAO.atualizarStatus(idServidor, "Ativo");
            JOptionPane.showMessageDialog(this, "Servidor reativado.");
        });

        painelBotoes.add(botaoIsolar);
        painelBotoes.add(botaoSuspeita);
        painelBotoes.add(botaoAtivar);
        add(painelBotoes, BorderLayout.SOUTH);

        Timer timer = new Timer(2000, e -> atualizarTabelas());
        timer.start();
        atualizarTabelas();
    }
    private void atualizarTabelas() {
        // Servidores
        modeloServidores.setRowCount(0);
        List<Servidor> servidores = servidorDAO.listarTodos();
        for (Servidor s : servidores) {
            modeloServidores.addRow(new Object[]{
                    s.getIdServidor(), s.getNome(), s.getIpInterno(), s.getNivelCriticidade(), s.getStatusAtual()
            });
        }

        // Logs
        modeloLogs.setRowCount(0);
        List<LogOperacao> logs = logDAO.listarUltimos(20);
        for (LogOperacao l : logs) {
            modeloLogs.addRow(new Object[]{
                    l.getIdLog(), l.getServidorNome(), l.getAcaoExecutada(),
                    l.getDataHoraEvento(), l.getNivelRisco(), l.getIdLogPai()
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JanelaAdmin().setVisible(true));
    }
}