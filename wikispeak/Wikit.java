package wikispeak;

import java.io.IOException;

public class Wikit {

    private static Wikit instance = null; 
  
    private String _term;
    private String _article;
    private String[] _articleSentences;
  
    
    private Wikit() { 
        _article = "Nothing to see here"; 
    } 
    
    
    /**
     * Returns the only instance of this class
     */
    public static Wikit get() { 
        if (instance == null) 
            instance = new Wikit(); 
  
        return instance; 
    }
    
    
    /**
     * Searches for a term using the wikit command.
     * @param searchTerm The term being searched for
     * @return The stdout after the command is run.
     */
    public String search(String searchTerm) throws IOException, InterruptedException {
    	_term = searchTerm;
    	return Bash.readOutput(Bash.execute(".", "wikit \"" + searchTerm + "\""));
    }
    
    
    /**
     * Gets the latest term that was searched
     */
    public String getTerm() {
    	return _term;
    }
    
    
    /**
     * Sets the article to the given string. The article is then separated by sentence and formatted.
     */
    public void setArticle(String article) {
    	_article = article.substring(1);
    	
    	_articleSentences = _article.split("(?<=[^ ]\\.) (?=[^a-z])");
    	
    	_article = "";
    	for (int i = 0; i < _articleSentences.length; i++) {
    		_article += "(" + (i+1) + ") " + _articleSentences[i] + "\n\n";
    	}
    	_article = _article.substring(0, _article.length() - 1);
    }
 
    
    /**
     * Returns the current article, formatted so that each sentence is separated with a new line character, and lines are numbered.
     */
    public String getFormattedArticle() {
    	return _article;
    }
    
    
    /**
     * Gets the first x sentences of the current article, where x is the number specified by the user.
     * @param sentences The number of sentences we want
     */
    public String getArticle(int sentences) {
    	String article = "";
    	for (int i = 0; i < sentences; i++) {
    		article += _articleSentences[i] + " ";
    	}
    	return article;
    }
    
    
    /**
     * Gets the number of sentences in the current article.
     */
    public int getNumSentences() {
    	return _articleSentences.length;
    }
}
