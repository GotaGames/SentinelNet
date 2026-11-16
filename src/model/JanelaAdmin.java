package view;

import dao.*;
import model.LogOperacao;
import model.Servidor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class JanelaAdmin extends JFrame {

    private DefaultTableModel modeloServidores;
    private DefaultTableModel modeloLogs;
    private DefaultTableModel modeloAlertas;
    private JTable tabelaServidores;
    private ServidorDAO servidorDAO = new ServidorDAO();
    private LogDAO logDAO = new LogDAO();
    private AlertaDAO alertaDAO = new AlertaDAO();
    private int idServidorSelecionado = -1;

    public JanelaAdmin() {
        setTitle("Sentinel Net - Painel do Administrador");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane abas = new JTabbedPane();

        // Aba Servidores
        modeloServidores = new DefaultTableModel(new String[]{"ID", "Nome", "IP Interno", "Criticidade", "Estado"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaServidores = new JTable(modeloServidores);
        tabelaServidores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPopupMenu menuContexto = new JPopupMenu();
        JMenuItem itemIsolar = new JMenuItem("Isolar Servidor");
        JMenuItem itemSuspeita = new JMenuItem("Marcar Sob Suspeita");
        JMenuItem itemAtivar = new JMenuItem("Reativar Servidor");

        itemIsolar.addActionListener(e -> alterarEstado("Isolado"));
        itemSuspeita.addActionListener(e -> alterarEstado("Sob Suspeita"));
        itemAtivar.addActionListener(e -> alterarEstado("Ativo"));

        menuContexto.add(itemIsolar);
        menuContexto.add(itemSuspeita);
        menuContexto.add(itemAtivar);

        tabelaServidores.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int linha = tabelaServidores.rowAtPoint(e.getPoint());
                    if (linha >= 0) {
                        tabelaServidores.setRowSelectionInterval(linha, linha);
                        idServidorSelecionado = (int) modeloServidores.getValueAt(linha, 0);
                        menuContexto.show(tabelaServidores, e.getX(), e.getY());
                    }
                }
            }
        });

        abas.addTab("Servidores", new JScrollPane(tabelaServidores));

        // Aba Logs
        modeloLogs = new DefaultTableModel(new String[]{"ID", "Servidor", "Ação", "Data/Hora", "Risco", "Log Pai"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabelaLogs = new JTable(modeloLogs);
        tabelaLogs.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                int risco = (int) t.getValueAt(row, 4);
                if (risco >= 8) c.setForeground(Color.RED);
                else if (risco >= 5) c.setForeground(Color.ORANGE);
                else c.setForeground(Color.BLACK);
                return c;
            }
        });
        abas.addTab("Logs", new JScrollPane(tabelaLogs));

        // Aba Alertas
        modeloAlertas = new DefaultTableModel(new String[]{"ID", "Log", "Admin", "Meio", "Tipo", "Data"}, 0);
        JTable tabelaAlertas = new JTable(modeloAlertas);
        abas.addTab("Alertas", new JScrollPane(tabelaAlertas));

        add(abas, BorderLayout.CENTER);

        Timer timer = new Timer(2000, e -> atualizarTabelas());
        timer.start();
        atualizarTabelas();
    }

    private void alterarEstado(String novoEstado) {
        if (idServidorSelecionado == -1) return;
        servidorDAO.atualizarStatus(idServidorSelecionado, novoEstado);
        atualizarTabelas(); // atualiza imediatamente, sem esperar pelo Timer
    }

    private void atualizarTabelas() {
        modeloServidores.setRowCount(0);
        for (Servidor s : servidorDAO.listarTodos()) {
            modeloServidores.addRow(new Object[]{s.getIdServidor(), s.getNome(), s.getIpInterno(), s.getNivelCriticidade(), s.getStatusAtual()});
        }

        modeloLogs.setRowCount(0);
        for (LogOperacao l : logDAO.listarUltimos(30)) {
            modeloLogs.addRow(new Object[]{l.getIdLog(), l.getServidorNome(), l.getAcaoExecutada(), l.getDataHoraEvento(), l.getNivelRisco(), l.getIdLogPai()});
        }

        modeloAlertas.setRowCount(0);
        for (Object[] a : alertaDAO.listarTodos()) {
            modeloAlertas.addRow(a);
        }
    }
}