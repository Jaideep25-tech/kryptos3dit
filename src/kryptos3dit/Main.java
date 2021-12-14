package kryptos3dit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Jaideep
 */
public class Main extends Application{
    public static Stage stage = null;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
       Parent root = FXMLLoader.load(getClass().getResource("ui/homepage.fxml"));
       Scene s = new Scene(root, 1139, 723);
       s.getStylesheets().add(getClass().getResource("ui/design.css").toExternalForm());
       stage.setScene(s);
       this.stage=stage;
       stage.show();
       
        
    }
}