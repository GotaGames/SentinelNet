package model;

public class LogOperacao {
    private int idLog;
    private String servidorNome;
    private String acaoExecutada;
    private String dataHoraEvento;
    private int nivelRisco;
    private Integer idLogPai;

    public LogOperacao(int idLog, String servidorNome, String acaoExecutada, String dataHoraEvento, int nivelRisco, Integer idLogPai) {
        this.idLog = idLog;
        this.servidorNome = servidorNome;
        this.acaoExecutada = acaoExecutada;
        this.dataHoraEvento = dataHoraEvento;
        this.nivelRisco = nivelRisco;
        this.idLogPai = idLogPai;
    }

    public int getIdLog() { return idLog; }
    public String getServidorNome() { return servidorNome; }
    public String getAcaoExecutada() { return acaoExecutada; }
    public String getDataHoraEvento() { return dataHoraEvento; }
    public int getNivelRisco() { return nivelRisco; }
    public Integer getIdLogPai() { return idLogPai; }
}