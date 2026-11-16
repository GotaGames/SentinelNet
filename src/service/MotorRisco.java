package service;

import dao.ConexaoPool;
import dao.ServidorDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalTime;

public class MotorRisco {

    private ServidorDAO servidorDAO = new ServidorDAO();

    public int calcularRisco(int idCliente, int idServidorDestino, String acao, boolean origemNaWhitelist) {
        String perfil = perfilDoCliente(idCliente);
        int criticidade = servidorDAO.criticidadeDoServidor(idServidorDestino);
        String nomeServidor = servidorDAO.nomeDoServidor(idServidorDestino);

        // Regras por perfil
        int riscoBase = switch (perfil) {
            case "Funcionario_Dev" -> regrasDev(nomeServidor, acao, criticidade);
            case "Funcionario_Design" -> regrasDesign(nomeServidor, acao, criticidade);
            case "Fornecedor" -> regrasFornecedor(nomeServidor, acao);
            case "Servico" -> regrasClienteNormal(nomeServidor, acao);
            default -> 8;
        };

        // Agravamentos (só se a base já não for crítica)
        if (riscoBase < 8) {
            LocalTime agora = LocalTime.now();
            if (agora.isBefore(LocalTime.of(7, 0)) || agora.isAfter(LocalTime.of(21, 0))) {
                riscoBase += 2; // fora do horário laboral
            }
            if (!origemNaWhitelist) {
                riscoBase += 3; // origem não autorizada
            }
        }

        return Math.min(riscoBase, 10);
    }

    private int regrasDev(String servidor, String acao, int criticidade) {
        // Dev pode tudo em qualquer servidor
        return 1;
    }

    private int regrasDesign(String servidor, String acao, int criticidade) {
        if (acao.equals("AlterarPrivilegio")) return 8;
        if (servidor.contains("Testes") || servidor.contains("Ficheiros") || servidor.contains("BD")) return 2;
        return 3;
    }

    private int regrasFornecedor(String servidor, String acao) {
        if (!servidor.contains("Fornecedor")) return 9; // só pode aceder ao servidor de fornecedor
        if (acao.equals("DownloadMassivo") || acao.equals("AlterarPrivilegio")) return 8;
        return 2;
    }

    private int regrasClienteNormal(String servidor, String acao) {
        if (!servidor.contains("Producao")) return 9; // só pode aceder ao servidor de produção
        if (acao.equals("Login") || acao.equals("ExecutarScript")) return 2;
        return 7; // outras ações são suspeitas
    }

    private String perfilDoCliente(int idCliente) {
        String sql = "SELECT tipo FROM cliente WHERE id_cliente = ?";
        try (Connection conn = ConexaoPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo");
                    String nome = nomeDoCliente(idCliente);
                    if (tipo.equals("Funcionario") && nome.contains("Dev")) return "Funcionario_Dev";
                    if (tipo.equals("Funcionario") && nome.contains("Design")) return "Funcionario_Design";
                    if (tipo.equals("Fornecedor")) return "Fornecedor";
                    if (tipo.equals("Servico")) return "Servico";
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "Servico";
    }

    private String nomeDoCliente(int idCliente) {
        String sql = "SELECT nome FROM cliente WHERE id_cliente = ?";
        try (Connection conn = ConexaoPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("nome");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "";
    }
}