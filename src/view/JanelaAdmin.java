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
    private DefaultTableModel modeloCadeia;
    private JTable tabelaServidores;
    private JTabbedPane abas;
    private ServidorDAO servidorDAO = new ServidorDAO();
    private LogDAO logDAO = new LogDAO();
    private AlertaDAO alertaDAO = new AlertaDAO();
    private int idServidorSelecionado = -1;

    public JanelaAdmin() {
        setTitle("Sentinel Net - Painel do Administrador");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        abas = new JTabbedPane();

        // ===== ABA SERVIDORES =====
        modeloServidores = new DefaultTableModel(new String[]{"ID", "Nome", "IP Interno", "Criticidade", "Estado"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaServidores = new JTable(modeloServidores);
        tabelaServidores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaServidores.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String estado = (String) t.getValueAt(row, 4);
                if (!sel) {
                    if (estado.equals("Isolado")) c.setBackground(new Color(255, 200, 200));
                    else if (estado.equals("Sob Suspeita")) c.setBackground(new Color(255, 240, 200));
                    else c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        JPopupMenu menu = new JPopupMenu();
        JMenuItem mIsolar = new JMenuItem("Isolar Servidor");
        JMenuItem mSuspeita = new JMenuItem("Marcar Sob Suspeita");
        JMenuItem mAtivar = new JMenuItem("Reativar Servidor");
        mIsolar.addActionListener(e -> alterarEstado("Isolado"));
        mSuspeita.addActionListener(e -> alterarEstado("Sob Suspeita"));
        mAtivar.addActionListener(e -> alterarEstado("Ativo"));
        menu.add(mIsolar);
        menu.add(mSuspeita);
        menu.add(mAtivar);

        tabelaServidores.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int linha = tabelaServidores.rowAtPoint(e.getPoint());
                    if (linha >= 0) {
                        tabelaServidores.setRowSelectionInterval(linha, linha);
                        idServidorSelecionado = (int) modeloServidores.getValueAt(linha, 0);
                        menu.show(tabelaServidores, e.getX(), e.getY());
                    }
                }
            }
        });
        abas.addTab("Servidores", new JScrollPane(tabelaServidores));

        // ===== ABA LOGS =====
        modeloLogs = new DefaultTableModel(new String[]{"ID", "Servidor", "Ação", "Data/Hora", "Risco", "Log Pai"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabelaLogs = new JTable(modeloLogs);
        tabelaLogs.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                int risco = (int) t.getValueAt(row, 4);
                if (!sel) {
                    if (risco >= 8) c.setForeground(Color.RED);
                    else if (risco >= 5) c.setForeground(new Color(200, 120, 0));
                    else c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        tabelaLogs.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int linha = tabelaLogs.getSelectedRow();
                    if (linha >= 0) {
                        int idLog = (int) modeloLogs.getValueAt(linha, 0);
                        carregarCadeia(idLog);
                        abas.setSelectedIndex(3);
                    }
                }
            }
        });
        abas.addTab("Logs", new JScrollPane(tabelaLogs));

        // ===== ABA ALERTAS =====
        modeloAlertas = new DefaultTableModel(new String[]{"ID", "Log", "Admin", "Meio", "Tipo", "Motivo", "Data"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabelaAlertas = new JTable(modeloAlertas);
        tabelaAlertas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int linha = tabelaAlertas.getSelectedRow();
                    if (linha >= 0) {
                        String motivo = (String) modeloAlertas.getValueAt(linha, 5);
                        int idLog = (int) modeloAlertas.getValueAt(linha, 1);

                        JTextArea area = new JTextArea(motivo != null ? motivo : "Sem detalhe disponível.");
                        area.setEditable(false);
                        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                        JOptionPane.showMessageDialog(null, new JScrollPane(area), "Detalhe do Alerta", JOptionPane.INFORMATION_MESSAGE);

                        carregarCadeia(idLog);
                        abas.setSelectedIndex(3);
                    }
                }
            }
        });
        abas.addTab("Alertas", new JScrollPane(tabelaAlertas));

        // ===== ABA CADEIA FORENSE =====
        modeloCadeia = new DefaultTableModel(new String[]{"Profundidade", "ID Log", "Servidor", "Ação", "Risco", "Log Pai"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabelaCadeia = new JTable(modeloCadeia);
        tabelaCadeia.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    if (row == 0) c.setBackground(new Color(255, 200, 200)); // evento mais recente
                    else if (row == t.getRowCount() - 1) c.setBackground(new Color(200, 255, 200)); // Paciente Zero
                    else c.setBackground(Color.WHITE);
                }
                return c;
            }
        });
        abas.addTab("Cadeia Forense", new JScrollPane(tabelaCadeia));

        add(abas, BorderLayout.CENTER);

        Timer timer = new Timer(2000, e -> atualizar());
        timer.start();
        atualizar();
    }

    private void alterarEstado(String novo) {
        if (idServidorSelecionado == -1) return;
        servidorDAO.atualizarStatus(idServidorSelecionado, novo);
        atualizar();
    }

    private void carregarCadeia(int idLog) {
        modeloCadeia.setRowCount(0);
        String sql = "WITH RECURSIVE cadeia AS (" +
                "SELECT id_log, id_servidor, acao_executada, nivel_risco, id_log_pai, 1 AS prof " +
                "FROM log_operacao WHERE id_log = ? " +
                "UNION ALL " +
                "SELECT l.id_log, l.id_servidor, l.acao_executada, l.nivel_risco, l.id_log_pai, c.prof + 1 " +
                "FROM log_operacao l JOIN cadeia c ON l.id_log = c.id_log_pai) " +
                "SELECT c.prof, c.id_log, s.nome AS servidor, c.acao_executada, c.nivel_risco, c.id_log_pai " +
                "FROM cadeia c JOIN servidor s ON c.id_servidor = s.id_servidor ORDER BY c.prof DESC";
        try (var conn = ConexaoPool.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLog);
            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    modeloCadeia.addRow(new Object[]{
                            rs.getInt("prof"), rs.getInt("id_log"), rs.getString("servidor"),
                            rs.getString("acao_executada"), rs.getInt("nivel_risco"), rs.getObject("id_log_pai")
                    });
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void atualizar() {
        modeloServidores.setRowCount(0);
        for (Servidor s : servidorDAO.listarTodos())
            modeloServidores.addRow(new Object[]{s.getIdServidor(), s.getNome(), s.getIpInterno(), s.getNivelCriticidade(), s.getStatusAtual()});

        modeloLogs.setRowCount(0);
        for (LogOperacao l : logDAO.listarUltimos(30))
            modeloLogs.addRow(new Object[]{l.getIdLog(), l.getServidorNome(), l.getAcaoExecutada(), l.getDataHoraEvento(), l.getNivelRisco(), l.getIdLogPai()});

        modeloAlertas.setRowCount(0);
        for (Object[] a : alertaDAO.listarTodos()) modeloAlertas.addRow(a);
    }
}