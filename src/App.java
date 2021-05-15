import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) throws Exception {
        
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        File configFile = new File("config/config.txt");
        if(configFile.exists()){
            Parent root = FXMLLoader.load(getClass().getResource("SceneChat.fxml"));
            Scene scene1 = new Scene(root);
            stage.setScene(scene1);
            stage.setTitle("ChatBot");
            stage.setResizable(false);
            stage.show();
        }
        else{

            configFile.createNewFile();
            Parent root = FXMLLoader.load(getClass().getResource("SceneConfig.fxml"));
            Scene scene1 = new Scene(root);
            stage.setScene(scene1);
            stage.setTitle("Configurando");
            stage.show();
        }  
    }

}
