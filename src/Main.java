import view.JanelaAdmin;
import view.JanelaCliente;
import view.JanelaProcessamento;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JanelaAdmin admin = new JanelaAdmin();
            JanelaCliente cliente = new JanelaCliente();
            JanelaProcessamento processamento = new JanelaProcessamento();

            admin.setVisible(true);
            cliente.setVisible(true);
            processamento.setVisible(true);
        });
    }
}