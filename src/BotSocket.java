import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class BotSocket {

    private String nickbot;
    private String token;
    private String canal;
    private String msgchat;
    private String TWITCH_HOST;
    private Socket socket;
    private PrintWriter prt;
    private BufferedReader bf;
    private InputStreamReader in;

    Boolean conectado = false;

    public static void main(String args[]) throws UnknownHostException, IOException, InterruptedException{
        BotSocket bot = new BotSocket("<username>", "oauth:<key>", "<user>");
        bot.criaSocket();
        long startTime = System.currentTimeMillis(); //fetch starting time
        bot.enviaMsg("PRIVMSG #"+ bot.canal+" :oie");
        while(false||(System.currentTimeMillis()-startTime)<10000){
            System.out.println(bot.recebeMsg());
            if(bot.recebeMsg().startsWith("PING")){
                bot.respondePong();
            }
        }
        bot.closeSocket();
    }

    public BotSocket(String nickbot, String token, String canal){

        this.nickbot = nickbot;
        this.token = token;
        this.canal = canal;
        this.TWITCH_HOST = "irc.chat.twitch.tv";

    }

    public void criaSocket() throws UnknownHostException, IOException{
        if(conectado){
            closeSocket();
        }
        //conecta ao irc da twitch atraves do socket
        socket = new Socket("irc.chat.twitch.tv", 6667);
        //variavel para conseguir enviar mensagem ao servidor.
        prt = new PrintWriter(socket.getOutputStream());

        in = new InputStreamReader(socket.getInputStream());
        bf = new BufferedReader(in);

        enviaMsg("PASS "+ token);
        enviaMsg("NICK "+ nickbot);
        enviaMsg("USER "+ nickbot+" 0 * "+nickbot);
        enviaMsg("JOIN #"+ canal);

        System.out.println("Conexao feita");
    }

    public void enviaMsg(String mensagem){
        prt.println(mensagem);
        prt.flush();
    }
    public String recebeMsg() throws IOException{
        msgchat = bf.readLine();
        return msgchat;
    }

    public void respondePong(){
        prt.write("PONG "+TWITCH_HOST+"\r\n");
        prt.flush();
    }

    public void closeSocket() throws IOException{
        bf.close();
        prt.close();
        socket.close();
        System.out.println("Conexao encerrada");
    }

    
}
