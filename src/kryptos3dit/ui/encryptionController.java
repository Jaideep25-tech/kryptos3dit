package kryptos3dit.ui;


import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.BufferOverflowException;
import java.nio.ReadOnlyBufferException;
import java.nio.file.InvalidPathException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import kryptos3dit.Main;
import kryptos3dit.crypto.AES256CTR;


/**
 * FXML Controller class
 *
 * @author Jaideep
 */
public class encryptionController implements Initializable {

    private File file;
    @FXML
    private FontAwesomeIcon toggleOff, toggleOn;
    @FXML
    private FontAwesomeIcon decryptionToggleOff, decryptionToggleOnn;
    @FXML
    private PasswordField encryptKey;
    @FXML
    private PasswordField  encryptKey1;
    @FXML
    private PasswordField  decryptKey;
    @FXML
    private PasswordField  decryptKey1;
    @FXML
    private TextField encryptyTextField;
    @FXML
    private TextField encryptTextField1;
    @FXML
    private TextField decryptTextField;
    @FXML
    private TextField decryptTextField1;
    boolean encryptionToggle, decryptionToggle;
    @FXML
    private Button importMessage;
    @FXML
    private Button importButton;
    @FXML
    private Button importButton2;
    @FXML
    private Button importButton3;
    @FXML
    void importFile(ActionEvent e){
        FileChooser filechooser = new FileChooser();
        file = filechooser.showOpenDialog(null);
        if(file!=null){
            importMessage.setVisible(true);
            importButton.setVisible(false);
            importButton3.setVisible(true);
            importButton2.setVisible(false);
        }
    }
    
    boolean checkFile(){
          if(file!=null){
              return true;
          }  
          Alert a = new Alert(AlertType.ERROR);
          a.setTitle("KRYPTOS3DIT");
          a.setHeaderText("File not found");
          a.setContentText("Import file to encrypt or decrypt");
          a.show();
          return false;
    }
    boolean checkPassword(String s1, String s2){
        if(!s1.equals(s2)){
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("KRYPTOS3DIT");
            a.setContentText("Passwords didn't match. Try Again");
            a.showAndWait();
            return false;
        }
        return true;
    }
    void displayError(){
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("KRYPTOS3DIT");
        a.setHeaderText("Something went wrong. Try again");
        a.showAndWait();
    }
    @FXML
    void encrypt(ActionEvent e){
        if(encryptionToggle){
            encryptKey.setText(encryptyTextField.getText());
            encryptKey1.setText(encryptTextField1.getText());
        }
        if(!checkFile() || !checkPassword(encryptKey.getText(),encryptKey1.getText()) || !checkFile()){
            return;
        }
        try {
            Alert a1 = new Alert(AlertType.INFORMATION);
            a1.getButtonTypes().removeAll(ButtonType.OK);
            a1.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            a1.setTitle("KRYPTOS3DIT");
            a1.setHeaderText("Remember to store your password.");
            a1.setContentText("Decrypting files with wrong password can \npermanently encrypt the file. "
                    + "\nContinue with Encryption? ");
            
            Optional<ButtonType> choice = a1.showAndWait();
            if (choice.get() == ButtonType.YES) {
                String temp = encryptKey1.getText();
                AES256CTR enc = new AES256CTR(temp);
                AES256CTR.encrypt(enc, file.getPath());
                Alert a = new Alert(AlertType.INFORMATION);
                a.setTitle("KRYPTOS3DIT");
                a.setHeaderText("File Encrypted");
                a.showAndWait();
            } else if (choice.get() == ButtonType.NO) {
                Alert a2 = new Alert(AlertType.INFORMATION);
                a2.setTitle("KRYPTOS3DIT");
                a2.setHeaderText("Encryption Aborted");
                a2.show();
            }
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            displayError();
            return;
        } catch (IOException iOException) {
            displayError();
            return;
        } catch (OutOfMemoryError outOfMemoryError) {
            displayError();
            return;
        } catch (SecurityException securityException) {
            displayError();
            return;
        } catch (InvalidPathException invalidPathException) {
            displayError();
            return;
        } catch (ReadOnlyBufferException readOnlyBufferException) {
            displayError();
            return;
        } catch (BufferOverflowException bufferOverflowException) {
            displayError();
            return;
        }
    }
    @FXML
    void decrypt(ActionEvent e) throws NoSuchAlgorithmException, IOException{
        if(decryptionToggle){
            decryptKey.setText(decryptTextField.getText());
            decryptKey1.setText(decryptTextField1.getText());
        }
        if(!checkFile() || !checkPassword(decryptKey.getText(),decryptKey1.getText())){
            return;
        }
        try {
            Alert a1 = new Alert(AlertType.INFORMATION);
            a1.getButtonTypes().removeAll(ButtonType.OK);
            a1.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            
            a1.setTitle("KRYPTOS3DIT");
            a1.setHeaderText("Ensure password is correct");
            a1.setContentText("Decrypting files with wrong password can \npermanently encrypt the file. "
                    + "\nContinue with Decryption? ");
            Optional<ButtonType> choice = a1.showAndWait();
            if (choice.get() == ButtonType.YES) {
                AES256CTR dec = new AES256CTR(decryptKey.getText());
                AES256CTR.decryption(dec, file.getPath());
                Alert a = new Alert(AlertType.INFORMATION);
                a.setTitle("KRYPTOS3DIT");
                a.setHeaderText("File Decrypted");
                a.showAndWait();
            } else if (choice.get() == ButtonType.NO) {
                Alert a2 = new Alert(AlertType.INFORMATION);
                a2.setTitle("KRYPTOS3DIT");
                a2.setHeaderText("Decryption Aborted");
                a2.show();
            }
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            displayError();
            return;
        } catch (IOException iOException) {
            displayError();
            return;
        } catch (OutOfMemoryError outOfMemoryError) {
            displayError();
            return;
        } catch (SecurityException securityException) {
            displayError();
            return;
        } catch (InvalidPathException invalidPathException) {
            displayError();
            return;
        } catch (ReadOnlyBufferException readOnlyBufferException) {
            displayError();
            return;
        } catch (BufferOverflowException bufferOverflowException) {
            displayError();
            return;
        }   
    }
    @FXML
    void viewPassword(MouseEvent m){
        if(encryptionToggle==false){
            toggleOff.setVisible(false);
            toggleOn.setVisible(true);
            encryptyTextField.setText(encryptKey.getText());
            encryptyTextField.setVisible(true);
            encryptTextField1.setText(encryptKey1.getText());
            encryptTextField1.setVisible(true);
            encryptionToggle =true;
        
        }else if(encryptionToggle==true){
            toggleOff.setVisible(true);
            toggleOn.setVisible(false);
            encryptKey.setText(encryptyTextField.getText());
            encryptKey1.setText(encryptTextField1.getText());
            encryptTextField1.setVisible(false);
            encryptyTextField.setVisible(false);
            encryptionToggle=false;
        }
    }
    @FXML
    void viewPasswordDecryption(MouseEvent m){
        
        if(decryptionToggle==false){
            decryptionToggleOff.setVisible(false);
            decryptionToggleOnn.setVisible(true);
            decryptTextField.setText(decryptKey.getText());
            decryptTextField1.setText(decryptKey1.getText());
            decryptTextField.setVisible(true);
            decryptTextField1.setVisible(true);
            decryptionToggle=true;
        }else if(decryptionToggle==true){
            decryptionToggleOff.setVisible(true);
            decryptionToggleOnn.setVisible(false);
            decryptKey.setText(decryptTextField.getText());
            decryptKey1.setText(decryptTextField1.getText());
            decryptTextField.setVisible(false);
            decryptTextField1.setVisible(false);
            decryptionToggle=false;
        }
        
    }
    @FXML
    void backToHomepage(MouseEvent e) throws IOException{
        Main.stage.getScene().setRoot(FXMLLoader.load(getClass().getResource("homepage.fxml")));
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        toggleOn.setVisible(false);
        decryptionToggleOnn.setVisible(false);
        encryptyTextField.setVisible(false);
        encryptTextField1.setVisible(false);
        decryptTextField.setVisible(false);
        decryptTextField1.setVisible(false);
        encryptionToggle=false;
        decryptionToggle=false;
        importMessage.setVisible(false);
        importButton3.setVisible(false);
       
    }
}    
    
