package wikispeak.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import wikispeak.Bash;
import wikispeak.Wikit;

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

    
    
    /**
     * Initialize method: sets the searching animation to invisible
     */
    @FXML
    private void initialize() {
        searchingGif.setVisible(false);
    }

    
    /**
     * Executes when "search" button is pressed.
     * Checks if the search term is valid and clear of invalid characters, and if it is, executes the search task in a new thread
     */
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText();

        if (searchTerm == null || searchTerm.equals("")) {
            errorMsg.setText("Please enter a valid search term");

        } else if (Bash.hasInvalidChars(searchTerm, false)) {
        	errorMsg.setText("Search term contains invalid character(s)");
        	
        } else {        	
            searchingGif.setVisible(true);

            Thread searchThread = new Thread(new WikitSearch<Void>());
            searchThread.start();
        }
    }
    
    
    /**
     * Redirects to the "Finish Creations" page.
     */
    private void loadFinishCreationsPage() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/wikispeak/resources/AudioEditor.fxml"));
        try {
            AnchorPane finishCreationPage = loader.load();
            newCreationPage.getChildren().clear();
            newCreationPage.getChildren().add(finishCreationPage);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    
    /**
     * Subclass of Task to handle wikit search.
     */
    @SuppressWarnings("hiding")
	private class WikitSearch<Void> extends Task<Void> {

    	private boolean successful;
    	private String wikitOut;
    	private String message;
    	
    	
    	/**
    	 * Call method: Searches for term using wikit, and updates fields according to success of search.
    	 */
    	@Override
        protected Void call() throws Exception {
           
        	String output = Wikit.get().search(searchField.getText());
        	
        	if (output.equals("")) {
        		message = "There was an error with wikit :L";
        		successful = false;
        	} else if (output.contains("not found :^(")) {
        		message = output;
        		successful = false;
        	} else {
        		wikitOut = output;
        		successful = true;
        	}
        	
        	return null;
        	
        }

    	
    	/**
    	 * Done method:  if successful, redirects to "Finish Creations" page. Otherwise, it displays the error message.
    	 */
        @Override
        protected void done() {
            
        	if (successful) {
        		Platform.runLater(() -> {
        			Wikit.get().setArticle(wikitOut);
        			loadFinishCreationsPage();
        		});
        	} else {
        		Platform.runLater(() -> {
        			errorMsg.setText(message);
        			searchingGif.setVisible(false);
        		});
        	}
        }
    }
}
