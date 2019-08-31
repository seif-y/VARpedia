package wikispeak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Wikit {

    private static Wikit instance = null; 
  
    private String _article;
    private String[] _articleSentences;
    private String caps = "[QWERTYUIOPASDFGHJKLZXCVBNM]";
  
    
    private Wikit() 
    { 
        _article = "Nothing to see here"; 
    } 
    
    
    public static Wikit getInstance() 
    { 
        if (instance == null) 
            instance = new Wikit(); 
  
        return instance; 
    } 
    
    
    public String search(String searchTerm) throws IOException, InterruptedException {
        
    	Process process = Bash.execute("wikit", searchTerm);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output = "";
        String line = "";
        while ((line = reader.readLine()) != null) { output += line + "\n"; }
        int exitCode = process.waitFor();
        
        return output;
    }
    
    
    public void setArticle(String article) {
    	_article = article;
    	
    	_articleSentences = _article.split("(?<=[^ ]\\.) (?=[^a-z])");
    	
    	_article = "";
    	for (String line : _articleSentences) {
    		_article += line + "\n\n";
    	}
    	_article = _article.substring(0, _article.length() - 1);
    }
 
    
    public String getArticle() {
    	return _article;
    }
    
    
    public int getNumSentences() {
    	return _articleSentences.length;
    }
}
