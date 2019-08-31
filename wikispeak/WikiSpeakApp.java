package wikispeak;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WikiSpeakApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/wikispeak/resources/Main.fxml"));
        Parent layout = loader.load();
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    	
    	/*
    	try {
			Wikit.get().setArticle(Wikit.get().search("apple"));
			String article = Wikit.get().getArticle(3);
			
			Creator.get().makeAudio(article, "audio");
			Creator.get().makeVideo("apple", "video");
			Creator.get().combine("video", "audio", "yes");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }
}
