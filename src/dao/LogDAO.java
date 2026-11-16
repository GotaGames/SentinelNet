package dao;

import model.LogOperacao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class LogDAO {

    public List<LogOperacao> listarUltimos(int limite) {
        List<LogOperacao> lista = new ArrayList<>();
        String sql = "SELECT l.id_log, s.nome AS servidor, l.acao_executada, l.data_hora_evento, l.nivel_risco, l.id_log_pai " +
                "FROM log_operacao l JOIN servidor s ON l.id_servidor = s.id_servidor " +
                "ORDER BY l.id_log DESC LIMIT ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new LogOperacao(
                            rs.getInt("id_log"),
                            rs.getString("servidor"),
                            rs.getString("acao_executada"),
                            rs.getString("data_hora_evento"),
                            rs.getInt("nivel_risco"),
                            (Integer) rs.getObject("id_log_pai")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public Integer ultimoLogDoServidor(int idServidor) {
        String sql = "SELECT id_log FROM log_operacao WHERE id_servidor = ? ORDER BY id_log DESC LIMIT 1";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idServidor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("id_log");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int inserirLog(int idServidor, Integer idRequisicao, String acao, int nivelRisco, Integer idLogPai) {
        String sql = "INSERT INTO log_operacao (id_servidor, id_requisicao, acao_executada, nivel_risco, id_log_pai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, idServidor);
            if (idRequisicao != null) stmt.setInt(2, idRequisicao); else stmt.setNull(2, Types.INTEGER);
            stmt.setString(3, acao);
            stmt.setInt(4, nivelRisco);
            if (idLogPai != null) stmt.setInt(5, idLogPai); else stmt.setNull(5, Types.INTEGER);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }



}