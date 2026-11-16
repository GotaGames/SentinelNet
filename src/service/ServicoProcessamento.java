package service;

import dao.*;
import model.Requisicao;
import model.Servidor;

import java.util.List;

public class ServicoProcessamento {

    private RequisicaoDAO requisicaoDAO = new RequisicaoDAO();
    private LogDAO logDAO = new LogDAO();
    private ServidorDAO servidorDAO = new ServidorDAO();
    private AlertaDAO alertaDAO = new AlertaDAO();
    private MotorRisco motorRisco = new MotorRisco();

    public String processar(Requisicao r) {
        int idServidor = encontrarIdServidorPorNome(r.getServidorDestinoNome());
        String estadoServidor = servidorDAO.estadoDoServidor(idServidor);

        // Servidor isolado: só o Dev pode aceder
        if (estadoServidor.equals("Isolado")) {
            String perfil = perfilDoCliente(r.getIdCliente());
            if (perfil.equals("Funcionario_Dev")) {
                requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Permitida");
                int idLog = logDAO.inserirLog(idServidor, r.getIdRequisicao(),
                        "AcessoDevServidorIsolado", 3, logDAO.ultimoLogDoServidor(idServidor));
                return "[ISOLADO-EXCEÇÃO] Dev autorizado em servidor isolado (risco 3)";
            } else {
                requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Bloqueada");
                int idLog = logDAO.inserirLog(idServidor, r.getIdRequisicao(),
                        "TentativaAcessoServidorIsolado", 10, logDAO.ultimoLogDoServidor(idServidor));
                String motivo = construirMotivo(r, 10, "CRÍTICO", 0, "Acesso a servidor isolado");
                alertaDAO.enviarAlerta(idLog, 1, "SMS", "Critico", motivo);
                alertaDAO.enviarAlerta(idLog, 2, "Email", "Critico", motivo);
                return "[ISOLADO] Acesso negado a " + r.getClienteNome() + " (risco 10)";
            }
        }

        // Verifica whitelist e calcula risco
        boolean naWhitelist = verificarWhitelist(r.getIdCliente(), idServidor);
        int risco = motorRisco.calcularRisco(r.getIdCliente(), idServidor, r.getAcaoSolicitada(), naWhitelist);

        String resultado;
        if (risco < 5) {
            requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Permitida");
            resultado = "PERMITIDA";
        } else {
            int tentativas = requisicaoDAO.contarTentativasRecentes(r.getIdCliente(), idServidor, 2);
            if (tentativas >= 5) {
                if (risco >= 8) {
                    requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Bloqueada");
                    servidorDAO.atualizarStatus(idServidor, "Isolado");
                    resultado = "BLOQUEADA + ISOLAMENTO (5 tentativas)";
                } else {
                    requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Bloqueada");
                    servidorDAO.atualizarStatus(idServidor, "Sob Suspeita");
                    resultado = "BLOQUEADA + SOB SUSPEITA (5 tentativas)";
                }
            } else {
                requisicaoDAO.atualizarEstado(r.getIdRequisicao(), "Bloqueada");
                resultado = "NÃO PERMITIDA (" + (tentativas + 1) + "/5 tentativas)";
            }
        }

        int idLog = logDAO.inserirLog(idServidor, r.getIdRequisicao(), resultado, risco, logDAO.ultimoLogDoServidor(idServidor));

        // Alertas com motivo detalhado
        if (risco >= 8) {
            String motivo = construirMotivo(r, risco, "CRÍTICO", 0, resultado);
            alertaDAO.enviarAlerta(idLog, 1, "SMS", "Critico", motivo);
            alertaDAO.enviarAlerta(idLog, 2, "Email", "Critico", motivo);
            alertaDAO.enviarAlerta(idLog, 1, "Painel_Web", "Critico", motivo);
        } else if (risco >= 5) {
            String motivo = construirMotivo(r, risco, "SUSPEITO", 0, resultado);
            alertaDAO.enviarAlerta(idLog, 1, "Painel_Web", "Suspeita", motivo);
        }

        return "[" + (risco >= 8 ? "CRÍTICO" : risco >= 5 ? "SUSPEITO" : "OK") + "] "
                + r.getAcaoSolicitada() + " -> " + r.getServidorDestinoNome() + " (risco " + risco + ")";
    }

    private String construirMotivo(Requisicao r, int risco, String nivel, int tentativas, String resultado) {
        return String.format(
                "ALERTA %s (risco %d)\n" +
                        "=====================================\n" +
                        "Resultado: %s\n" +
                        "Ação solicitada: %s\n" +
                        "Cliente: %s\n" +
                        "IP Origem: %s\n" +
                        "IP Externo: %s\n" +
                        "Servidor Destino: %s\n" +
                        "Hora da requisição: %s\n" +
                        "Tentativas consecutivas: %d/5\n" +
                        "Motivo: comportamento fora do padrão para o perfil deste cliente.",
                nivel, risco, resultado, r.getAcaoSolicitada(), r.getClienteNome(),
                r.getIpOrigem(), r.getIpExterno(), r.getServidorDestinoNome(),
                r.getDataHoraRequisicao(), tentativas
        );
    }

    private boolean verificarWhitelist(Integer idCliente, int idServidorDestino) {
        if (idCliente == null) return false;
        String sql = "SELECT COUNT(*) FROM conexao_permitida WHERE id_cliente = ? AND id_servidor_destino = ?";
        try (var conn = ConexaoPool.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idServidorDestino);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    private String perfilDoCliente(Integer idCliente) {
        if (idCliente == null) return "Servico";
        String sql = "SELECT c.tipo, c.nome FROM cliente c WHERE c.id_cliente = ?";
        try (var conn = ConexaoPool.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo");
                    String nome = rs.getString("nome");
                    if (tipo.equals("Funcionario") && nome.contains("Dev")) return "Funcionario_Dev";
                    if (tipo.equals("Funcionario") && nome.contains("Design")) return "Funcionario_Design";
                    if (tipo.equals("Fornecedor")) return "Fornecedor";
                    if (tipo.equals("Servico")) return "Servico";
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "Servico";
    }

    private int encontrarIdServidorPorNome(String nome) {
        List<Servidor> servidores = servidorDAO.listarTodos();
        for (Servidor s : servidores) if (s.getNome().equals(nome)) return s.getIdServidor();
        return 0;
    }
}