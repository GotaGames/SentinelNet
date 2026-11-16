package dao;

import model.Servidor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServidorDAO {

    public List<Servidor> listarTodos() {
        List<Servidor> lista = new ArrayList<>();
        String sql = "SELECT id_servidor, nome, ip_interno, nivel_criticidade, status_atual FROM servidor ORDER BY id_servidor";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Servidor(
                        rs.getInt("id_servidor"),
                        rs.getString("nome"),
                        rs.getString("ip_interno"),
                        rs.getInt("nivel_criticidade"),
                        rs.getString("status_atual")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void atualizarStatus(int idServidor, String novoStatus) {
        String sql = "UPDATE servidor SET status_atual = ? WHERE id_servidor = ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            stmt.setString(1, novoStatus);
            stmt.setInt(2, idServidor);
            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("UPDATE executado. Linhas afetadas: " + linhasAfetadas);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String estadoDoServidor(int idServidor) {
        String sql = "SELECT status_atual FROM servidor WHERE id_servidor = ?";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idServidor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("status_atual");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Ativo";
    }

    public int criticidadeDoServidor(int idServidor) {
        String sql = "SELECT nivel_criticidade FROM servidor WHERE id_servidor = ?";
        try (Connection conn = ConexaoPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idServidor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("nivel_criticidade");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 1;
    }

    public String nomeDoServidor(int idServidor) {
        String sql = "SELECT nome FROM servidor WHERE id_servidor = ?";
        try (Connection conn = ConexaoPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idServidor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("nome");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "";
    }
}