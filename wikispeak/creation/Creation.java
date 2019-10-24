package wikispeak.creation;

import java.io.Serializable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Creation implements Serializable {
	
	private String _term;
	private String _fileName;
	private String _name;
	private int _rating;
	private int _views;
	
	private static Creation _creation;
	
	public Creation(String term, String fileName, int rating) {
		_term = term;
		_fileName = fileName;
		_name = fileName.substring(0,fileName.length()-4);
		_rating = rating;
		_views = 0;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getFile() {
		return _fileName;
	}
	
	public int getRating() {
		return _rating;
	}
	
	public void setRating(int rating) {
		_rating = rating;
	}
	
	public int getViews() {
		return _views;
	}
	
	public void incrementViews() {
		_views++;
	}
	
	public static void setInstance(Creation creation) {
        _creation = creation;
    }

    public static Creation getInstance() {
        return _creation;
    }

}
