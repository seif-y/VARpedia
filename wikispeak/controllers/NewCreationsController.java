package wikispeak.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class NewCreationsController {

    @FXML
    private TextField searchField;
    @FXML
    private Text errorMsg;
    @FXML
    private AnchorPane newCreationPage;
    @FXML
    private ImageView searchingGif;

    @FXML
    private void initialize() {
        searchingGif.setVisible(false);
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();

        if (searchTerm == null || searchTerm.equals("")) {
            errorMsg.setText("Please enter a valid search term");

        } else {
            searchingGif.setVisible(true);

            Thread searchThread = new Thread(wikitSearch);
            searchThread.start();
        }
    }

    private void loadFinishCreationsPage() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/wikispeak/resources/FinishCreationPage.fxml"));
        try {
            AnchorPane finishCreationPage = loader.load();
            newCreationPage.getChildren().clear();
            newCreationPage.getChildren().add(finishCreationPage);

        } catch (IOException e) {
            //TODO: handle exception
        }

    }

    private Task<Void> wikitSearch = new Task<Void>() {

        @Override
        protected Void call() throws Exception {
            //TODO: Search term on wikit
            Thread.sleep(3000);
            return null;
        }

        @Override
        protected void done() {
            Platform.runLater(() -> {
                loadFinishCreationsPage();
            });
        }
    };
}
