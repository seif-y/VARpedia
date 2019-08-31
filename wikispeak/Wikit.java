package wikispeak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Wikit {

    private static Wikit instance = null; 
  
    private String _term;
    private String _article;
    private String[] _articleSentences;
    private String caps = "[QWERTYUIOPASDFGHJKLZXCVBNM]";
  
    
    private Wikit() { 
        _article = "Nothing to see here"; 
    } 
    
    
    public static Wikit get() { 
        if (instance == null) 
            instance = new Wikit(); 
  
        return instance; 
    }
    
    
    public String search(String searchTerm) throws IOException, InterruptedException {
    	_term = searchTerm;
    	return Bash.readOutput(Bash.execute(".", "wikit " + searchTerm));
    }
    
    
    public String getTerm() {
    	return _term;
    }
    
    
    public void setArticle(String article) {
    	_article = article.substring(1);
    	
    	_articleSentences = _article.split("(?<=[^ ]\\.) (?=[^a-z])");
    	
    	_article = "";
    	for (String line : _articleSentences) {
    		_article += line + "\n\n";
    	}
    	_article = _article.substring(1, _article.length() - 1);
    }
 
    
    public String getArticle() {
    	return _article;
    }
    
    
    public String getArticle(int sentences) {
    	String article = "";
    	for (int i = 0; i < sentences; i++) {
    		article += _articleSentences[i] + " ";
    	}
    	return article;
    }
    
    
    public int getNumSentences() {
    	return _articleSentences.length;
    }
}
