package wikispeak;

import java.io.File;
import java.util.List;

public class Creator {

		private static Creator instance = null;
	  
    private String _time;
 
    
    /**
     * Checks if creations directory exists. If not, it creates the directory
     */
    private Creator() { 
    	if (!(new File("./creations")).exists()) {
    		Process process = Bash.execute(".", "mkdir creations");
    		try {
    			process.waitFor();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    } 
    
    
    /**
     * Gets the only instance of this class
     * @return The instance of this singleton class
     */
    public static Creator get() { 
        if (instance == null) 
            instance = new Creator(); 
  
        return instance; 
    }
    
    
    /**
     * Makes an .wav audio file of the given text being dictated, using text2wave
     * @param text The text to be dictated
     * @param fileName The name of the audio file.
     */
    public void makeAudio(String text, String fileName) {
    	try {
			Process generateAudio = Bash.execute("./creations", "echo \"" + text + "\" | text2wave -o ." + fileName + ".wav");
			generateAudio.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    }
    
    
    /**
     * Return a string that contains the length of an audio file.
     * @param fileName The audio file that we are getting the time for.
     * @return The time of the given audio file
     * @throws InterruptedException 
     */
    public String getTimeOfAudio(String fileName) {
    	Process soxi = Bash.execute("./creations", "soxi -D " + fileName);
    	String time = Bash.readOutput(soxi);
		
		return time;
    }
    
    
    /**
     * Combine multiple audio files by concatenating them, using the sox command. 
     * @param audioFiles The list of names for the audio files being combined.
     * @param combinedFileName
     */
    public void combineAudio(List<String> audioFiles, String combinedFileName) {
    	String list = "";
    	for (String file : audioFiles) {
    		list += file + " ";
    	}
    	System.out.println(list);
    	Bash.execute("./creations", "sox " + list + combinedFileName);
    }
    
    
    /**
     * Uses ffmpeg to make a .mp4 video, consisting of a slideshow of the given images.
     * @param imageFiles
     * @param time
     */
    public void makeSlideshow(List<String> imageFiles, String videoName, String time) {
    	
    	String list = "";
    	for (String file : imageFiles) {
    		list += file + " ";
    	}
    	String framerate = imageFiles.size() + "/" + time;
    	// cat *.jpg | ffmpeg 
    	// -framerate $FRAMERATE 
    	// -f image2pipe -i - 
    	String vfSettings = "scale=iw*min(1920/iw\\,1080/ih):ih*min(1920/iw\\,1080/ih), pad=1920:1080:(1920-iw*min(1920/iw\\,1080/ih))/2:(1080-ih*min(1920/iw\\,1080/ih))/2,format=yuv420p,drawtext=fontfile=myfont.ttf:fontsize=30: fontcolor=white:shadowx=2:x=(w-text_w)/2:y=(h-text_h)/2:text='" + Wikit.get().getTerm() + "'";
    	// -r 25 $VIDEONAME.mp4
    	
    	Bash.execute("./creations", "cat " + list + "| ffmpeg -framerate " + framerate + "-f image2pipe -i - -vf \"" + vfSettings + "\" -r 25 " + videoName);

    }
    
    
    /**
     * Makes a .mp4 video file that displays the given term in white text over a black background, using ffmpeg
     * @param term The term to be displayed
     * @param filename The name of the video file
     */
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
    
    
    /**
     * Combines a video file with an audio file, using ffmpeg
     * @param fileName The name of the final video file, after combining
     */
    public void combine(String videoName, String audioName, String fileName) {
    	try {
			Process combine = Bash.execute("./creations", "ffmpeg -loglevel panic -y -i " + videoName + ".mp4 -i " + audioName + ".wav -c:v copy -map 0:video:0 -map 1:audio:0 -strict experimental " + fileName + ".mp4 >&2");
			combine.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    
    /**
     * Deletes the temporary audio and video files
     */
    public void cleanup(String videoName, String audioName) {
    	try {
    		Process deleteAudio = Bash.execute("./creations", "rm " + videoName + ".mp4");
    		deleteAudio.waitFor();
        	Process deleteVideo = Bash.execute("./creations", "rm " + audioName + ".wav");
        	deleteVideo.waitFor();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }
}
