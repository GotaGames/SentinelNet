package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertaDAO {

    public void enviarAlerta(int idLog, int idAdmin, String meioEnvio, String tipoAlerta, String motivo) {
        String sql = "INSERT INTO alerta_enviado (id_log, id_admin, meio_envio, tipo_alerta, motivo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLog);
            stmt.setInt(2, idAdmin);
            stmt.setString(3, meioEnvio);
            stmt.setString(4, tipoAlerta);
            stmt.setString(5, motivo);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Object[]> listarTodos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT a.id_alerta, a.id_log, COALESCE(ad.nome, 'Desconhecido') AS admin, " +
                "a.meio_envio, a.tipo_alerta, a.motivo, a.data_hora_alerta " +
                "FROM alerta_enviado a " +
                "LEFT JOIN administrador ad ON a.id_admin = ad.id_admin " +
                "ORDER BY a.id_alerta DESC LIMIT 50";
        try (Connection conn = ConexaoPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("id_alerta"),
                        rs.getInt("id_log"),
                        rs.getString("admin"),
                        rs.getString("meio_envio"),
                        rs.getString("tipo_alerta"),
                        rs.getString("motivo"),
                        rs.getString("data_hora_alerta")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}