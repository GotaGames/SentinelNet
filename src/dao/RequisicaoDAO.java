package dao;

import model.Requisicao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequisicaoDAO {

    public void inserir(int idCliente, int idServidorDestino, String ipOrigem, String ipExterno, String acao) {
        String sql = "INSERT INTO requisicao (id_cliente, id_servidor_destino, ip_origem_requisicao, ip_emissao_externo, acao_solicitada, estado_requisicao) " +
                "VALUES (?, ?, ?, ?, ?, 'Pendente')";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idServidorDestino);
            stmt.setString(3, ipOrigem);
            stmt.setString(4, ipExterno);
            stmt.setString(5, acao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Requisicao> listarPendentes() {
        List<Requisicao> lista = new ArrayList<>();
        String sql = "SELECT r.id_requisicao, r.id_cliente, c.nome AS cliente, s.nome AS destino, " +
                "r.acao_solicitada, r.estado_requisicao, r.data_hora_requisicao, " +
                "r.ip_origem_requisicao, r.ip_emissao_externo " +
                "FROM requisicao r " +
                "LEFT JOIN cliente c ON r.id_cliente = c.id_cliente " +
                "JOIN servidor s ON r.id_servidor_destino = s.id_servidor " +
                "WHERE r.estado_requisicao = 'Pendente' ORDER BY r.id_requisicao";
        try (Connection conn = ConexaoPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Requisicao(
                        rs.getInt("id_requisicao"),
                        (Integer) rs.getObject("id_cliente"),
                        rs.getString("cliente"),
                        rs.getString("destino"),
                        rs.getString("acao_solicitada"),
                        rs.getString("estado_requisicao"),
                        rs.getString("data_hora_requisicao"),
                        rs.getString("ip_origem_requisicao"),
                        rs.getString("ip_emissao_externo")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    public void atualizarEstado(int idRequisicao, String novoEstado) {
        String sql = "UPDATE requisicao SET estado_requisicao = ? WHERE id_requisicao = ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoEstado);
            stmt.setInt(2, idRequisicao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int contarTentativasRecentes(int idCliente, int idServidorDestino, int minutos) {
        String sql = "SELECT COUNT(*) FROM requisicao " +
                "WHERE id_cliente = ? AND id_servidor_destino = ? " +
                "AND estado_requisicao IN ('Bloqueada', 'Pendente') " +
                "AND data_hora_requisicao >= NOW() - INTERVAL ? MINUTE";
        try (Connection conn = ConexaoPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idServidorDestino);
            stmt.setInt(3, minutos);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}