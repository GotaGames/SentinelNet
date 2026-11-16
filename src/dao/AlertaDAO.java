package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertaDAO {

    public void enviarAlerta(int idLog, int idAdmin, String meioEnvio, String tipoAlerta) {
        String sql = "INSERT INTO alerta_enviado (id_log, id_admin, meio_envio, tipo_alerta) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idLog);
            stmt.setInt(2, idAdmin);
            stmt.setString(3, meioEnvio);
            stmt.setString(4, tipoAlerta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object[]> listarTodos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT a.id_alerta, a.id_log, ad.nome AS admin, a.meio_envio, a.tipo_alerta, a.data_hora_alerta " +
                "FROM alerta_enviado a JOIN administrador ad ON a.id_admin = ad.id_admin " +
                "ORDER BY a.id_alerta DESC LIMIT 50";
        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("id_alerta"),
                        rs.getInt("id_log"),
                        rs.getString("admin"),
                        rs.getString("meio_envio"),
                        rs.getString("tipo_alerta"),
                        rs.getString("data_hora_alerta")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}