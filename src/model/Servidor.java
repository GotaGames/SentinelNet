package model;

public class Servidor {
    private int idServidor;
    private String nome;
    private String ipInterno;
    private int nivelCriticidade;
    private String statusAtual;

    public Servidor(int idServidor, String nome, String ipInterno, int nivelCriticidade, String statusAtual) {
        this.idServidor = idServidor;
        this.nome = nome;
        this.ipInterno = ipInterno;
        this.nivelCriticidade = nivelCriticidade;
        this.statusAtual = statusAtual;
    }

    public int getIdServidor() { return idServidor; }
    public String getNome() { return nome; }
    public String getIpInterno() { return ipInterno; }
    public int getNivelCriticidade() { return nivelCriticidade; }
    public String getStatusAtual() { return statusAtual; }


    @Override
    public String toString() {
        return nome + " (" + ipInterno + ")";
    }

}