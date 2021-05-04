import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalTime;
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

    String nickbot = "Bisoidinho";
    String token = "oauth:bc1dgp24hr3evjd2zvhmzckvrzimop";
    String canal = "biscoitinho";

    //notifica
    Notifica notif = new Notifica(nickbot,"/images/icon.png");

    String nomes[] = new String[150];
    Boolean temnome = false;
    Boolean ligado = true;

    String mensagem = "";
    String msgchat = "";
    String nick = "";
    String tratada = "";
    String TWITCH_HOST = "irc.chat.twitch.tv";
    int TWITCH_PORT = 6667;
    Socket socket;
    PrintWriter prt;
    InputStreamReader in;
    BufferedReader bf;
    
    //arquivo
    String stringnomes;
    FileReader reader;
    BufferedReader breader;
    PrintWriter clean;
    FileWriter writer;
    BufferedWriter bwriter;

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

    //cria e verifica arquivo de nomes do bot
    void leArquivoNomes() throws IOException{
        for(int x = 0; x<150; x++){
            nomes[x] = "";
        }
        stringnomes = "";
        reader = new FileReader("nomes.txt");
        breader = new BufferedReader(reader);
        String linha;
        while((linha = breader.readLine())!=null){
            stringnomes += linha+"\n";
        }
        breader.close();
        for(int x = 0; x<stringnomes.split("\n").length; x++){
            nomes[x] = stringnomes.split("\n")[x];
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
            
            for(int x = 0; x<150;x++){
                if(nomes[x] == ""){
                    nomes[x] = ""+nick;
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
                    break;
                }
                if(nomes[x].equals(nick)){
                    break;
                }
            }
            if(tratada.equals("fon")){
                printChat("fon 4Head");
            }
            else if(tratada.indexOf("!troll") !=-1){
                printChat("mds esse OBATMAO é muito ruim.. patético");
            }
            else if(tratada.indexOf("!mendigo") !=-1){
                printChat("só tem gente mendigando nesse jogo.. patético");
            }

        }
    }

    public void closeBot() throws IOException{
        prt.println("PART #"+ canal);
        prt.println("QUIT");
        prt.flush();

        clean = new PrintWriter("nomes.txt");
        clean.close();

        writer = new FileWriter("nomes.txt");
        bwriter = new BufferedWriter(writer);
        
        for(int x = 0; x< 100; x++){
            if(nomes[x].equals("")){
                break;
            }
            bwriter.write(nomes[x]);
            bwriter.newLine();
        }
        bwriter.close();

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
            leArquivoNomes();
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
