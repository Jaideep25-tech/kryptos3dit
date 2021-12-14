package kryptos3dit.ui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import kryptos3dit.Main;
import kryptos3dit.filters.Filters;

/**
 * FXML Controller class
 *
 * @author Naman Nihal
 */
public class UifxmlController implements Initializable {

    @FXML
    private ImageView imageviewer;
    @FXML
    private ImageView updatedImageView;
    @FXML
    private Slider brightnessSlider;
    @FXML
    private VBox advanceEdits;
    @FXML
    private File file;
    @FXML
    private TextField text;
    private String watermarkText;
    @FXML
    private Slider blurSlider;
    @FXML
    private Slider rotateSlider;
    @FXML
    private Slider sharpenSlider;
    @FXML
    private Slider pixelateSlider;
    @FXML
    private Button chooseImage;
    private BufferedImage input;
    private BufferedImage output;
    private Image img = null;
    @FXML
    private Button imagePreview;
    @FXML
    private Label brightenVal;
    @FXML
    private Label blurVal;
    @FXML
    private Label rotateVal;
    @FXML
    private Label sharpenVal;
    @FXML
    private Label pixelateVal;
    @FXML
    private Button insertImageButton;
    @FXML
    private Button insertNewImageButton;

    @FXML
    public void insertImage(ActionEvent e) throws IOException {
        FileChooser f = new FileChooser();
        file = f.showOpenDialog(null);

        if (file != null) {
            Image img = new Image(file.toURI().toString(), 553, 443, true, true);
            imageviewer.setImage(img);
            imageviewer.setPreserveRatio(false);
            insertImageButton.setVisible(false);
            insertNewImageButton.setVisible(true);
        } else if (file == null) {
            return;
        }
        input = ImageIO.read(file);
        this.output = input;
        chooseImage.setVisible(false);
    }

    void updateImage() {

        imagePreview.setVisible(false);
        File updatedFile = new File("temp/output.png");
        Image updatedImage = new Image(updatedFile.toURI().toString(), 553, 443, true, true);
        updatedImageView.setImage(updatedImage);
        updatedImageView.setPreserveRatio(false);
    }

    @FXML
    public void saveImage(ActionEvent e) throws IOException {
        FileChooser f = new FileChooser();
        File file = f.showSaveDialog(null);
        if (file != null) {
            ImageIO.write(output, "png", file);
        }

    }

    boolean checkImageInput() {
        if (file == null) {
            Alert a1 = new Alert(AlertType.ERROR);
            a1.setTitle("KRYPTOS3DIT");
            a1.setHeaderText("Error! Image not found");
            a1.showAndWait();
            return false;
        }
        return true;
    }

    void displayMessage() {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("KRYPTOS3DIT");
        a.setHeaderText("Filter applied.");
        a.show();
    }

    void displayError() {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("KRYPTOS3DIT");
        a.setHeaderText("Something went wrong. Try Again");
        a.showAndWait();
    }

    @FXML
    void enableEdits() {
        advanceEdits.setVisible(true);
        brightenVal.setVisible(true);
        blurVal.setVisible(true);
        pixelateVal.setVisible(true);
        sharpenVal.setVisible(true);
        rotateVal.setVisible(true);
    }

    @FXML
    void applyWaterMark(ActionEvent e) throws IOException {
        if (!checkImageInput()) {
            return;
        }
        watermarkText = text.getText();
        if ("".equals(watermarkText)) {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("KRYPTOS3DIT");
            a.setHeaderText("Text is empty");
            a.show();
            return;
        }
        try {
            this.output = Filters.addWatermark(this.output, watermarkText);
        } catch (NullPointerException exc) {
            displayError();
        }
        Alert a = new Alert(AlertType.INFORMATION);
        ImageIO.write(output, "png", new File("temp/output.png"));
        a.setTitle("CRYPTOS3DIT");
        a.setHeaderText("Watermark added successfully");
        a.show();
        updateImage();

    }

    @FXML
    void applyGrayscale(ActionEvent e) throws IOException {
        if (!checkImageInput()) {
            return;
        }
        this.output = Filters.grayscale(this.output);
        try {
            ImageIO.write(output, "png", new File("temp/output.png"));
        } catch (ArrayIndexOutOfBoundsException exc) {
            displayError();
        }
        updateImage();
        displayMessage();
    }

    @FXML
    void applySepia(ActionEvent e) throws IOException {
        if (!checkImageInput()) {
            return;
        }

        this.output = Filters.sepia(this.output);
        try {
            ImageIO.write(output, "png", new File("temp/output.png"));
        } catch (ArrayIndexOutOfBoundsException exc) {
            displayError();
        }
        updateImage();
        displayMessage();
    }

    @FXML
    void applyNegative(ActionEvent e) throws IOException {
        if (!checkImageInput()) {
            return;
        }
        try {
            this.output = Filters.negative(this.output);
        } catch (ArrayIndexOutOfBoundsException exc) {
            displayError();
        }
        ImageIO.write(output, "png", new File("temp/output.png"));
        updateImage();
        displayMessage();
    }

    @FXML
    void applyDetectEdges(ActionEvent e) throws IOException {
        if (!checkImageInput()) {
            return;
        }
        try {
            this.output = Filters.detectEdges(this.output);
        } catch (ArrayIndexOutOfBoundsException exc) {
            displayError();
        }
        ImageIO.write(output, "png", new File("temp/output.png"));
        displayMessage();
        updateImage();
    }

    @FXML
    void verticalMirrorImage(ActionEvent e) throws IOException {
        if (!checkImageInput()) {
            return;
        }
        try {
            this.output = Filters.mirror(this.output, true);
        } catch (ArrayIndexOutOfBoundsException exc) {
            displayError();
        }
        ImageIO.write(output, "png", new File("temp/output.png"));
        updateImage();
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("Kryptos3dit");
        a.setHeaderText("Filter applied.");
        a.show();
    }

    @FXML
    void horizontalMirrorImage(ActionEvent e) throws IOException {
        if (!checkImageInput()) {
            return;
        }

        try {
            this.output = Filters.mirror(this.output, false);
        } catch (ArrayIndexOutOfBoundsException exc) {
            displayError();
        }
        ImageIO.write(output, "png", new File("temp/output.png"));
        updateImage();
        displayMessage();
    }

    @FXML
    void posterizeImage(ActionEvent e) throws IOException {
        if (!checkImageInput()) {
            return;
        }
        try {
            this.output = Filters.posterize(this.output);
        } catch (ArrayIndexOutOfBoundsException exc) {
            displayError();
        }
        ImageIO.write(output, "png", new File("temp/output.png"));
        updateImage();
        displayMessage();
    }

    @FXML
    void brighten(ActionEvent e) throws IOException {
        if (!checkImageInput()) {
            return;
        }
        double value = brightnessSlider.getValue();

        if (value >= 0 && value <= 1) {
            this.output = Filters.brighten(this.output, value);

        } else if (value >= -1 && value <= 0) {
            this.output = Filters.darken(this.output, value);

        }
        ImageIO.write(output, "png", new File("temp/output.png"));
        updateImage();
        displayMessage();
    }

    @FXML
    void applyGaussianBlur(ActionEvent e) throws IOException {
        if (!checkImageInput()) {
            return;
        }

        this.output = Filters.gaussianBlur(this.output, (int) blurSlider.getValue());
        ImageIO.write(output, "png", new File("temp/output.png"));
        updateImage();
        displayMessage();

    }

    @FXML
    void rotateImage(ActionEvent e) throws IOException {
        if (!checkImageInput()) {
            return;
        }

        this.output = Filters.rotate(this.output, rotateSlider.getValue());
        ImageIO.write(output, "png", new File("temp/output.png"));
        updateImage();
        displayMessage();
    }

    @FXML
    void sharpenImage() throws IOException {
        if (!checkImageInput()) {
            return;
        }
        this.output = Filters.sharpen(this.output, (int) sharpenSlider.getValue());
        ImageIO.write(output, "png", new File("temp/output.png"));
        updateImage();
        displayMessage();
    }

    @FXML
    void pixelateImage() throws IOException {
        if (!checkImageInput()) {
            return;
        }
        try {
            this.output = Filters.pixelate(this.output, (int) pixelateSlider.getValue());
        } catch (ArrayIndexOutOfBoundsException exc) {
            displayError();
        }
        ImageIO.write(output, "png", new File("temp/output.png"));
        updateImage();
        displayMessage();

    }

    @FXML
    void backToHomepage(MouseEvent e) throws IOException {
        Main.stage.getScene().setRoot(FXMLLoader.load(getClass().getResource("homepage.fxml")));

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        advanceEdits.setVisible(false);
        watermarkText = "";
        brightenVal.textProperty().bind(
                Bindings.format(
                        "%.2f",
                        brightnessSlider.valueProperty()
                )
        );
        blurVal.textProperty().bind(
                Bindings.format(
                        "%.2f",
                        blurSlider.valueProperty()
                )
        );
        pixelateVal.textProperty().bind(
                Bindings.format(
                        "%.2f",
                        pixelateSlider.valueProperty()
                )
        );
        sharpenVal.textProperty().bind(
                Bindings.format(
                        "%.2f",
                        sharpenSlider.valueProperty()
                )
        );
        rotateVal.textProperty().bind(
                Bindings.format(
                        "%.2f",
                        rotateSlider.valueProperty()
                )
        );

        brightenVal.setVisible(false);
        blurVal.setVisible(false);
        pixelateVal.setVisible(false);
        sharpenVal.setVisible(false);
        rotateVal.setVisible(false);
        insertNewImageButton.setVisible(false);

    }

}
