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
    private JTextField campoIpOrigem;
    private JTextField campoIpExterno;
    private RequisicaoDAO requisicaoDAO = new RequisicaoDAO();
    private ServidorDAO servidorDAO = new ServidorDAO();

    public JanelaCliente() {
        setTitle("Sentinel Net - Terminal de Cliente");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2, 5, 5));

        add(new JLabel("Cliente:"));
        comboCliente = new JComboBox<>(new String[]{"Joao Dev", "Maria Design", "Fornecedor ACME", "Cliente API"});
        add(comboCliente);

        add(new JLabel("Servidor Destino:"));
        comboDestino = new JComboBox<>();
        add(comboDestino);

        add(new JLabel("Ação:"));
        comboAcao = new JComboBox<>(new String[]{"Login", "ExecutarScript", "DownloadMassivo", "AlterarPrivilegio"});
        add(comboAcao);

        add(new JLabel("IP Origem:"));
        campoIpOrigem = new JTextField("10.0.10.100");
        add(campoIpOrigem);

        add(new JLabel("IP Externo:"));
        campoIpExterno = new JTextField("203.0.113.55");
        add(campoIpExterno);

        JButton botaoEnviar = new JButton("Enviar Requisição");
        botaoEnviar.addActionListener(e -> enviarRequisicao());
        add(new JLabel());
        add(botaoEnviar);

        JButton botaoAtualizar = new JButton("Recarregar Servidores");
        botaoAtualizar.addActionListener(e -> carregarServidores());
        add(new JLabel());
        add(botaoAtualizar);

        JButton botaoAtaque = new JButton("SIMULAR ATAQUE AUTOMÁTICO");
        botaoAtaque.setBackground(Color.RED);
        botaoAtaque.setForeground(Color.WHITE);
        botaoAtaque.addActionListener(e -> simularAtaque());
        add(new JLabel());
        add(botaoAtaque);

        SwingUtilities.invokeLater(this::carregarServidores);
    }

    private void carregarServidores() {
        comboDestino.removeAllItems();
        for (Servidor s : servidorDAO.listarTodos()) comboDestino.addItem(s);
    }

    private void enviarRequisicao() {
        Servidor destino = (Servidor) comboDestino.getSelectedItem();
        if (destino == null) return;
        int idCliente = comboCliente.getSelectedIndex() + 1;
        requisicaoDAO.inserir(idCliente, destino.getIdServidor(), campoIpOrigem.getText(), campoIpExterno.getText(), (String) comboAcao.getSelectedItem());
        JOptionPane.showMessageDialog(this, "Requisição enviada (Pendente).");
    }

    private void simularAtaque() {
        String[] acoes = {"Login", "ExecutarScript", "DownloadMassivo"};
        Timer timer = new Timer(1500, null);
        final int[] index = {0};
        timer.addActionListener(e -> {
            if (index[0] >= acoes.length) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "Ataque simulado concluído. Verifica o painel do Admin.");
                return;
            }
            Servidor destino = (Servidor) comboDestino.getSelectedItem();
            if (destino == null) return;
            requisicaoDAO.inserir(3, destino.getIdServidor(), "10.0.40.50", "203.0.113.55", acoes[index[0]]);
            index[0]++;
        });
        timer.start();
    }
}