package model;

public class Requisicao {
    private int idRequisicao;
    private Integer idCliente;
    private String clienteNome;
    private String servidorDestinoNome;
    private String acaoSolicitada;
    private String estadoRequisicao;
    private String dataHoraRequisicao;
    private String ipOrigem;
    private String ipExterno;

    public Requisicao(int idRequisicao, Integer idCliente, String clienteNome,
                      String servidorDestinoNome, String acaoSolicitada,
                      String estadoRequisicao, String dataHoraRequisicao,
                      String ipOrigem, String ipExterno) {
        this.idRequisicao = idRequisicao;
        this.idCliente = idCliente;
        this.clienteNome = clienteNome;
        this.servidorDestinoNome = servidorDestinoNome;
        this.acaoSolicitada = acaoSolicitada;
        this.estadoRequisicao = estadoRequisicao;
        this.dataHoraRequisicao = dataHoraRequisicao;
        this.ipOrigem = ipOrigem;
        this.ipExterno = ipExterno;
    }

    public int getIdRequisicao() { return idRequisicao; }
    public Integer getIdCliente() { return idCliente; }
    public String getClienteNome() { return clienteNome; }
    public String getServidorDestinoNome() { return servidorDestinoNome; }
    public String getAcaoSolicitada() { return acaoSolicitada; }
    public String getEstadoRequisicao() { return estadoRequisicao; }
    public String getDataHoraRequisicao() { return dataHoraRequisicao; }
    public String getIpOrigem() { return ipOrigem; }
    public String getIpExterno() { return ipExterno; }
}