import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;


public class ChatController extends Thread implements Initializable{

    String nickbot = "";
    String token = "";
    String canal = "";

    //notifica
    Notifica notif = new Notifica(nickbot,"images/icon.png");

    List<String> nomes = new ArrayList<String>();
    List<Filtro> listaFiltro = new ArrayList<Filtro>();

    Boolean ligado = true;

    String mensagem = "";
    String msgchat = "";
    String nick = "";
    String tratada = "";
    String TWITCH_HOST = "irc.chat.twitch.tv";
    int TWITCH_PORT = 6667;
    PrintWriter prt;
    InputStreamReader in;
    BufferedReader bf;

    //socket
    Socket socket;

    //arquivo
    Arquivos filebot;

    //tempo
    LocalTime now;

    @FXML
    private TextArea textChat;

    @FXML
    private TextField typeChat;

    @FXML
    private Button btnChat;


    //ativa quando digitamos qualquer coisa no textfield.
    @FXML
    void apertaEnter(KeyEvent event) throws IOException {
        if(event.getCode().toString().equals("ENTER")){
            escreveChat();
        }

    }


    //ativa quando clicamos no botao enviar.
    @FXML
    void onClickChat(ActionEvent event) throws IOException {
        escreveChat();
        typeChat.requestFocus();

    }

    //responsavel por fazer scrolldown na area de chat.
    void scrollText(){
        textChat.setScrollLeft(0);
    }

    //envia a mensagem pro chat do streamer e mostra na tela do programa
    void escreveChat() throws IOException{
        if (!isEmpty(typeChat)){

                mensagem = ""+typeChat.getText();

                if(mensagem.equals("close")){
                    typeChat.setText("");
                    closeBot();
                }

                textChat.appendText("\n"+nickbot+": "+mensagem);

                prt.println("PRIVMSG #"+ canal+" :"+mensagem);
                prt.flush();

                typeChat.setText("");
                scrollText();


        }
    }

    //recebe uma string e envia pro chat do streamer a mensagem
    public void printChat(String mensagem){
        prt.println("PRIVMSG #"+ canal+" :"+mensagem);
        prt.flush();
        textChat.appendText("\n"+nickbot+": "+mensagem);
        scrollText();
    }



    //verifica se um textfield esta vazio
    boolean isEmpty(TextField texto){
        return (texto.getText().equals(""));
    }

    //verifica se um textarea esta vazio
    boolean isEmpty(TextArea texto){
        return (texto.getText().equals(""));
    }

    public void setNickBot(String nickbot){
        this.nickbot = nickbot;
    }
    public void setToken(String token){
        this.token = token;
    }
    public void setCanal(String canal){
        this.canal = canal;
    }

    //cria ou verifica arquivo de nomes do bot e config
    void leArquivos() throws IOException{
        filebot = new Arquivos();
        this.canal = filebot.getCanal();
        this.token = filebot.getToken();
        this.nickbot = filebot.getNickBot();

        if(filebot.getNameHist()){
            this.nomes = filebot.getNames();
        }
        if(filebot.getFiltroHist()){
            this.listaFiltro = filebot.getListafiltro();
        }
    }

    void conectaSocket() throws UnknownHostException, IOException{
        //conecta ao irc da twitch atraves do socket
        socket = new Socket(TWITCH_HOST, TWITCH_PORT);
        //variavel para conseguir enviar mensagem ao servidor.
        prt = new PrintWriter(socket.getOutputStream());

        in = new InputStreamReader(socket.getInputStream());
        bf = new BufferedReader(in);

        prt.println("PASS "+ token);
        prt.println("NICK "+ nickbot);
        prt.println("USER "+ nickbot+" 0 * "+nickbot);
        prt.println("JOIN #"+ canal);
        prt.flush();
    }

    void trataMensagem(String msgchat){
        if(msgchat.indexOf("PRIVMSG") !=-1){
            nick = "";
            tratada = "";
            nick = msgchat.split(":")[1].split("!")[0];
            tratada = msgchat.split(":")[2];

            System.out.println(nick + ": "+tratada);
            textChat.appendText("\n"+nick+": "+tratada);
            scrollText();

            //verifica o nome das pessoas no chat
            Boolean repetido = false;
            for(int x = 0; x<nomes.size();x++){
                if(nomes.get(x).equals(nick)){
                    repetido = true;
                    break;
                }
            }
            if(!repetido){
                nomes.add(nick);
                now = LocalTime.now();
                String tempo = ""+now.toString().split(":")[0];
                if(Integer.parseInt(tempo) < 6){
                    printChat("Boa madrugada "+ nick+ ", aproveite a live! <3");
                }
                else if(Integer.parseInt(tempo) <= 12){
                    printChat("Bom dia "+ nick+ ", aproveite a live! <3");
                }
                else if(Integer.parseInt(tempo) <= 18){
                    printChat("Boa tarde "+ nick+ ", aproveite a live! <3");
                }
                else{
                    printChat("Boa noite "+ nick+ ", aproveite a live! <3");
                }
            }

            //acessa a lista de filtro de mensagens
            for(int x = 0; x<listaFiltro.size();x++){
                if(listaFiltro.get(x).getCompleta()){
                    if(listaFiltro.get(x).getKey().equals(tratada)){
                        printChat(listaFiltro.get(x).getMensagem());
                    }
                }
                else{
                    if(tratada.indexOf(listaFiltro.get(x).getKey()) !=-1){
                        printChat(listaFiltro.get(x).getMensagem());
                    }
                }
            }
        }
    }

    public void closeBot() throws IOException{
        prt.println("PART #"+ canal);
        prt.println("QUIT");
        prt.flush();

        filebot.setConfig(this.nickbot, this.token, this.canal, this.nomes,this.listaFiltro);
        filebot.saveFiles();

        ligado = false;


        socket.close();
        notif.closeMsg();

        System.out.println("Conexão encerrada!");

    }

    public void lendoChat(){
        new Thread(){
            @Override
            public void run(){
                try{
                    System.out.println("abriu o run");
                    while(ligado){
                        msgchat = bf.readLine();
        
                        if(msgchat.startsWith("PING")){
                            prt.write("PONG "+TWITCH_HOST+"\r\n");
                            prt.flush();
                        }
                        trataMensagem(msgchat);
                        
        
                    }
                    
                }catch(Exception e){
                    e.printStackTrace();
                }
        
            }
        }.start();
    }


    //func chamada quando inicializa o programa
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        textChat.setText("\n\n\n\n\n\n\n\n\n");
        System.out.println("Funcao inicialize chamada!");

        try {
            notif.notificaMsg();
            leArquivos();
            conectaSocket();
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }


        Platform.runLater(new Runnable(){
            @Override
            public void run(){
                typeChat.requestFocus();
                lendoChat();
            }
        });
    }

}
