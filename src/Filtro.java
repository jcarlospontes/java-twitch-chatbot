public class Filtro {

    private String mensagem;
    private String key;
    private Boolean completa;

    public Filtro(String key, String mensagem, Boolean completa){
        this.mensagem = mensagem;
        this.key = key;
        this.completa = completa;
    }

    public String getKey() {
        return key;
    }
    public String getMensagem() {
        return mensagem;
    }
    public Boolean getCompleta() {
        return completa;
    }
    public void setCompleta(Boolean completa) {
        this.completa = completa;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

}
