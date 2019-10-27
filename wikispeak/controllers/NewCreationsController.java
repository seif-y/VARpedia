package wikispeak.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import wikispeak.Bash;
import wikispeak.Creator;
import wikispeak.Wikit;


public class NewCreationsController extends Controller {

    
	@FXML
	private AnchorPane pane;
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
        Creator.get().cleanup();
    }
    
    
    @FXML
    private void handleHome() {
    	switchScenes(pane, "HomePage.fxml");
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
        		message = "There was an error with wikit.";
        		successful = false;
        	} else if (output.contains("not found :^(")) {
        		message = "Could not find an article for \"" + Wikit.get().getTerm() + "\"";
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
        			switchScenes(pane, "AudioEditor.fxml");
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
