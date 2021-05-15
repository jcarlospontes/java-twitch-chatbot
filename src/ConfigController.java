
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

public class ConfigController {

    private Parent root;
    private Scene scene;
    private Stage stage;

    String canal;
    String token;
    String nickbot;

    @FXML
    private TextField inputBotName;

    @FXML
    private TextField inputToken;

    @FXML
    private TextField inputChannel;

    @FXML
    private Button btnConecta;

    @FXML
    void setParametros(ActionEvent event) throws IOException {
        if(!(inputBotName.getText().equals("") || inputChannel.getText().equals("") || inputToken.getText().equals(""))){
            nickbot = inputBotName.getText();
            token = inputToken.getText();
            canal = inputChannel.getText();

            writeConfig("config/config.txt");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SceneChat.fxml"));
            root = loader.load();

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setTitle("ChatBot");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        }
    }
    private void writeConfig(String localarquivo) throws IOException{
        //limpa o arquivo
        PrintWriter clean = new PrintWriter(localarquivo);
        clean.close();

        //abre o arquivo para escrita
        FileWriter fwriter = new FileWriter(localarquivo);
        BufferedWriter bufwriter = new BufferedWriter(fwriter);

        //escreve 3 linhas
        bufwriter.write(this.nickbot);
        bufwriter.newLine();
        bufwriter.write(this.token);
        bufwriter.newLine();
        bufwriter.write(this.canal);
        bufwriter.newLine();
        
        bufwriter.close();
    }


}
