package wikispeak.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import wikispeak.Creator;
import wikispeak.creation.Creation;
import wikispeak.creation.RatingComparator;
import wikispeak.creation.ViewsComparator;

public class MainController extends Controller {

    @FXML
    private AnchorPane pane;
    @FXML
    private Text creationsToReview;
    
    
    @FXML
    private void initialize() {
    	List<Creation> creationsSortedByRating = Creator.get().readCreationList();
    	List<Creation> creationsSortedByViews = Creator.get().readCreationList();
    	
    	List<Creation> creationReviewList = new ArrayList<Creation>();
    	
    	if (creationsSortedByViews.isEmpty()) {
    		creationsToReview.setText("You have no creations. Click \"New Creation\" to get started!");
    		return;
    	}
    	if (creationsSortedByViews.size() <= 4) {
    		for (Creation creation : creationsSortedByViews) {
    			creationReviewList.add(creation);
    		}
    	} else {
    		Collections.sort(creationsSortedByRating, new RatingComparator());
    		Collections.sort(creationsSortedByViews, new ViewsComparator());
    		
    		creationReviewList.add(creationsSortedByRating.get(0));
    		creationReviewList.add(creationsSortedByRating.get(1));
    		
    		int ind = 0;
    		while (creationReviewList.size() <= 4)  {
    			if (!creationReviewList.contains(creationsSortedByViews.get(ind))) {
    				creationReviewList.add(creationsSortedByViews.get(ind));
    			}
    			ind++;
    		}
    	}
    	
    	String creationReviewString = "";
    	for (Creation creation : creationReviewList) {
    		creationReviewString += creation.getName();
    		if (creationReviewList.indexOf(creation) < creationReviewList.size() - 1) {
    			creationReviewString += "   |   ";
    		}
    	}
    	creationsToReview.setText(creationReviewString);
    }
    
    
    /**
     * Loads "New Creation" page
     */
    @FXML
    private void handleNewCreation() {
        switchScenes(pane,"NewCreationPage.fxml");
    }

    
    /**
     * Loads "View Creations" page
     */
    @FXML
    private void handleViewCreations() {
        switchScenes(pane,"CreationsPage.fxml");
    }
}
