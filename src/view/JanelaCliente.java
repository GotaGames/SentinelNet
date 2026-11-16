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
        setTitle("Sentinel Net - Cliente");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 5, 5));

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

        // Carregar servidores DEPOIS de a janela estar construída
        SwingUtilities.invokeLater(this::carregarServidores);
    }

    private void carregarServidores() {
        comboDestino.removeAllItems();
        List<Servidor> servidores = servidorDAO.listarTodos();
        for (Servidor s : servidores) {
            comboDestino.addItem(s);
        }
    }

    private void enviarRequisicao() {
        Servidor destino = (Servidor) comboDestino.getSelectedItem();
        if (destino == null) {
            JOptionPane.showMessageDialog(this, "Seleciona um servidor de destino.");
            return;
        }
        int idCliente = comboCliente.getSelectedIndex() + 1; // assume IDs 1..4
        String acao = (String) comboAcao.getSelectedItem();
        requisicaoDAO.inserir(idCliente, destino.getIdServidor(), campoIpOrigem.getText(), campoIpExterno.getText(), acao);
        JOptionPane.showMessageDialog(this, "Requisição enviada com estado Pendente.");
    }

}

