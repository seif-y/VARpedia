package wikispeak;

import java.io.File;
import java.io.IOException;

public class Creator {

	private static Creator instance = null; 
	  
    private String _time;
 
    
    private Creator() { 
    	if (!(new File("./creations")).exists()) {
    		Bash.execute(".", "mkdir creations");
    	}
    } 
    
    
    public static Creator get() { 
        if (instance == null) 
            instance = new Creator(); 
  
        return instance; 
    }
    
    public void makeAudio(String text, String fileName) {
    	try {
			Process generateAudio = Bash.execute("./creations", "echo \"" + text + "\" | text2wave -o " + fileName + ".wav");
			generateAudio.waitFor();
			_time = Bash.readOutput(Bash.execute("./creations", "soxi -D " + fileName + ".wav"));
			_time = _time.substring(0, _time.length() - 1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    }
    
    public void makeVideo(String term, String filename) {
    	
    	String fontSettings = "\"drawtext=fontfile=myfont.ttf:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='" + term + "'\"";
    	String bgSettings = "color=c=black:s=320x240:d=" + _time;
    	
    	try {
    		Process generateVideo = Bash.execute("./creations", "ffmpeg -loglevel panic -f lavfi -i " + bgSettings + " -vf " + fontSettings + " " + filename + ".mp4");
			generateVideo.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void combine(String videoName, String audioName, String fileName) {
    	try {
			Process combine = Bash.execute("./creations", "ffmpeg -loglevel panic -y -i " + videoName + ".mp4 -i " + audioName + ".wav -c:v copy -map 0:video:0 -map 1:audio:0 -strict experimental " + fileName + ".mp4 >&2");
			combine.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void cleanup(String videoName, String audioName) {
    	try {
    		Process deleteAudio = Bash.execute("./creations", "rm " + videoName);
    		deleteAudio.waitFor();
        	Process deleteVideo = Bash.execute("./creations", "rm " + audioName);
        	deleteVideo.waitFor();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }
}
