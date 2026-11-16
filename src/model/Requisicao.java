package model;

public class Requisicao {
    private int idRequisicao;
    private String clienteNome;
    private String servidorDestinoNome;
    private String acaoSolicitada;
    private String estadoRequisicao;
    private String dataHoraRequisicao;

    public Requisicao(int idRequisicao, String clienteNome, String servidorDestinoNome,
                      String acaoSolicitada, String estadoRequisicao, String dataHoraRequisicao) {
        this.idRequisicao = idRequisicao;
        this.clienteNome = clienteNome;
        this.servidorDestinoNome = servidorDestinoNome;
        this.acaoSolicitada = acaoSolicitada;
        this.estadoRequisicao = estadoRequisicao;
        this.dataHoraRequisicao = dataHoraRequisicao;
    }

    public int getIdRequisicao() { return idRequisicao; }
    public String getClienteNome() { return clienteNome; }
    public String getServidorDestinoNome() { return servidorDestinoNome; }
    public String getAcaoSolicitada() { return acaoSolicitada; }
    public String getEstadoRequisicao() { return estadoRequisicao; }
    public String getDataHoraRequisicao() { return dataHoraRequisicao; }
}