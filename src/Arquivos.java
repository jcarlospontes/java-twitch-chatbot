import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Arquivos {

private List<String> names = new ArrayList<String>();
private List<Filtro> listafiltro = new ArrayList<Filtro>();

private String nickBot = "";
private String token = "";
private String canal = "";

private Boolean namesHist = true;
private Boolean filtroHist = true;



public Arquivos() throws IOException{


    File nomesFile = new File("log/lognames.txt");
    File filtroFile = new File("log/logfiltro.txt");
    
    if(!nomesFile.exists() || !filtroFile.exists()){

        if(!nomesFile.exists()){
            nomesFile.createNewFile();
            namesHist = false;
        }
        if(!filtroFile.exists()){
            filtroFile.createNewFile();
            filtroHist = false;
        }
    }
    else{
        readNames("log/lognames.txt");
        readFiltro("log/logfiltro.txt");
        
    }
    readConfig("config/config.txt");

}

    //getters
    public String getNickBot(){
        return this.nickBot;
    }
    public String getToken(){
        return this.token;
    }
    public String getCanal(){
        return this.canal;
    }
    public List<String> getNames(){
        return this.names;
    }
    public List<Filtro> getListafiltro() {
        return listafiltro;
    }
    public Boolean getNameHist(){
        return this.namesHist;
    }
    public Boolean getFiltroHist() {
        return filtroHist;
    }

    //setter
    public void setConfig(String nickbot, String token, String canal, List<String> names,List<Filtro> listafiltro){
        this.nickBot = nickbot;
        this.token = token;
        this.canal = canal;
        this.names = names;
        this.listafiltro = listafiltro;
    }

    //le o arquivo de config
    private void readConfig(String localarquivo) throws IOException{
        FileReader freader = new FileReader(localarquivo);
        BufferedReader bufreader = new BufferedReader(freader);
        
        nickBot = bufreader.readLine();
        token = bufreader.readLine();
        canal = bufreader.readLine().toLowerCase();

        bufreader.close();
    }

    //le o arquivo de nomes
    private void readNames(String localarquivo) throws IOException{
        FileReader freader = new FileReader(localarquivo);
        BufferedReader bufreader = new BufferedReader(freader);
        String line;
        while((line = bufreader.readLine())!=null){
            this.names.add(line);
        }

        bufreader.close();
    }

    //le o arquivo de filtro
    
    private void readFiltro(String localarquivo) throws IOException{
        FileReader freader = new FileReader(localarquivo);
        BufferedReader bufreader = new BufferedReader(freader);
        String line;
        while((line = bufreader.readLine())!=null){
            if(!line.equals("")){
                
                this.listafiltro.add(new Filtro(line.split(";;")[0], line.split(";;")[1], line.split(";;")[2].equals("true")));
 
            }
        }

        bufreader.close();
    }

    //escreve as configuracoes no arquivo
    private void writeConfig(String localarquivo) throws IOException{
        //limpa o arquivo
        PrintWriter clean = new PrintWriter(localarquivo);
        clean.close();

        //abre o arquivo para escrita
        FileWriter fwriter = new FileWriter(localarquivo);
        BufferedWriter bufwriter = new BufferedWriter(fwriter);

        //escreve 3 linhas
        bufwriter.write(this.nickBot);
        bufwriter.newLine();
        bufwriter.write(this.token);
        bufwriter.newLine();
        bufwriter.write(this.canal);
        bufwriter.newLine();
        
        bufwriter.close();
    }

    //escreve nomes no arquivo
    private void writeNames(String localarquivo) throws IOException{
        //limpa o arquivo
        PrintWriter clean = new PrintWriter(localarquivo);
        clean.close();
        //abre para escrita
        FileWriter fwriter = new FileWriter(localarquivo);
        BufferedWriter bufwriter = new BufferedWriter(fwriter);
        //escreve nomes
        for(int x = 0; x<names.size();x++){
            bufwriter.write(names.get(x));
            bufwriter.newLine();
        }

        bufwriter.close();
    }

    //escreve arquivo filtro
    private void writeFiltro(String localarquivo) throws IOException{
        //limpa o arquivo
        PrintWriter clean = new PrintWriter(localarquivo);
        clean.close();
        //abre para escrita
        FileWriter fwriter = new FileWriter(localarquivo);
        BufferedWriter bufwriter = new BufferedWriter(fwriter);
        //escreve o filtro
        for(int x = 0; x<listafiltro.size();x++){
            if(listafiltro.get(x).getCompleta()){
                bufwriter.write(listafiltro.get(x).getKey()+";;"+listafiltro.get(x).getMensagem()+";;true");
            }
            else{
                bufwriter.write(listafiltro.get(x).getKey()+";;"+listafiltro.get(x).getMensagem()+";;false");
            }
            bufwriter.newLine();
        }

        bufwriter.close();
    }

    //salva os nomes e configs nos arquivos
    public void saveFiles() throws IOException{
        writeConfig("config/config.txt");
        writeNames("log/lognames.txt");
        writeFiltro("log/logfiltro.txt");
    }

    
}
