package view;

import dao.RequisicaoDAO;
import dao.ServidorDAO;
import model.Servidor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JanelaCliente extends JFrame {

    private JComboBox<String> comboCliente;
    private JComboBox<Servidor> comboDestino;
    private JComboBox<String> comboAcao;
    private JTextField campoIpOrigem, campoIpExterno;
    private JTextArea historico;
    private RequisicaoDAO requisicaoDAO = new RequisicaoDAO();
    private ServidorDAO servidorDAO = new ServidorDAO();

    public JanelaCliente() {
        setTitle("Sentinel Net - Terminal de Cliente");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Nova Requisição"));
        form.add(new JLabel("Cliente:"));
        comboCliente = new JComboBox<>(new String[]{"Joao Dev", "Maria Design", "Fornecedor ACME", "Cliente API"});
        form.add(comboCliente);
        form.add(new JLabel("Servidor Destino:"));
        comboDestino = new JComboBox<>();
        form.add(comboDestino);
        form.add(new JLabel("Ação:"));
        comboAcao = new JComboBox<>(new String[]{"Login", "ExecutarScript", "DownloadMassivo", "AlterarPrivilegio"});
        form.add(comboAcao);
        form.add(new JLabel("IP Origem:"));
        campoIpOrigem = new JTextField("10.0.10.100");
        form.add(campoIpOrigem);
        form.add(new JLabel("IP Externo:"));
        campoIpExterno = new JTextField("203.0.113.55");
        form.add(campoIpExterno);
        add(form, BorderLayout.NORTH);

        historico = new JTextArea();
        historico.setEditable(false);
        historico.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollHist = new JScrollPane(historico);
        scrollHist.setBorder(BorderFactory.createTitledBorder("Histórico da Sessão"));
        add(scrollHist, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new GridLayout(3, 1, 5, 5));
        JButton btnEnviar = new JButton("Enviar Requisição");
        btnEnviar.addActionListener(e -> enviar());
        JButton btnAtualizar = new JButton("Recarregar Servidores");
        btnAtualizar.addActionListener(e -> carregarServidores());
        JButton btnAtaque = new JButton("SIMULAR ATAQUE AUTOMÁTICO");
        btnAtaque.setBackground(new Color(180, 0, 0));
        btnAtaque.setForeground(Color.WHITE);
        btnAtaque.addActionListener(e -> simularAtaque());
        botoes.add(btnEnviar);
        botoes.add(btnAtualizar);
        botoes.add(btnAtaque);
        add(botoes, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(this::carregarServidores);
    }

    private void carregarServidores() {
        comboDestino.removeAllItems();
        for (Servidor s : servidorDAO.listarTodos()) comboDestino.addItem(s);
    }

    private void enviar() {
        Servidor destino = (Servidor) comboDestino.getSelectedItem();
        if (destino == null) return;
        int idCliente = comboCliente.getSelectedIndex() + 1;
        String acao = (String) comboAcao.getSelectedItem();
        requisicaoDAO.inserir(idCliente, destino.getIdServidor(), campoIpOrigem.getText(), campoIpExterno.getText(), acao);
        historico.append(">> " + acao + " -> " + destino.getNome() + " [Pendente]\n");
    }

    private void simularAtaque() {
        String[] acoes = {"Login", "ExecutarScript", "DownloadMassivo"};
        Timer timer = new Timer(1500, null);
        final int[] index = {0};
        timer.addActionListener(e -> {
            if (index[0] >= acoes.length) { timer.stop(); return; }
            Servidor destino = (Servidor) comboDestino.getSelectedItem();
            if (destino == null) return;
            requisicaoDAO.inserir(3, destino.getIdServidor(), "10.0.40.50", "203.0.113.55", acoes[index[0]]);
            historico.append(">> [ATAQUE] " + acoes[index[0]] + " -> " + destino.getNome() + "\n");
            index[0]++;
        });
        timer.start();
    }
}