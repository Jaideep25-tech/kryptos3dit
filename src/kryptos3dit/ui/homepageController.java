package kryptos3dit.ui;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * FXML Controller class
 *
 * @author Jaideep
 */
public class homepageController implements Initializable {
    
    @FXML
    private Pane mainArea;
    @FXML
    private WebView myweb = new WebView();
    @FXML
    private WebEngine engine;
    @FXML
    private FontAwesomeIcon closeBtn;
    @FXML
    private ImageView about;
    @FXML
    private FontAwesomeIcon closeBtn2;
    @FXML
    public void navigateToFilter(ActionEvent e) throws IOException{
        mainArea.getChildren().removeAll();
        Parent fxml = FXMLLoader.load(getClass().getResource("uifxml.fxml"));
        mainArea.getChildren().addAll(fxml);
    }
    @FXML
    public void navigateToEncryption(ActionEvent e) throws IOException{
        mainArea.getChildren().removeAll();
        Parent fxml = FXMLLoader.load(getClass().getResource("encryption.fxml"));
        mainArea.getChildren().addAll(fxml);
    }
    @FXML
    public void openGoogleImages(ActionEvent e){
        closeBtn.setVisible(true);
        myweb.setVisible(true);
        engine = myweb.getEngine();
        engine.load("https://www.google.com/imghp?hl=EN");
       
    }
    @FXML
    public void closeGI(MouseEvent  e){
        myweb.setVisible(false);
        closeBtn.setVisible(false);
        
    }
    @FXML
    public void aboutMessage(ActionEvent e){
        about.setVisible(true);
        closeBtn2.setVisible(true);
    }
    @FXML
    public void closeAbout(MouseEvent e){
        about.setVisible(false);
        closeBtn2.setVisible(false);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        closeBtn.setVisible(false);
        myweb.setVisible(false);
        about.setVisible(false);
        closeBtn2.setVisible(false);
    }    
    
}