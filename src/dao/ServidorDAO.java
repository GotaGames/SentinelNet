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
            stmt.setString(1, novoStatus);
            stmt.setInt(2, idServidor);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}