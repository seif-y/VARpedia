package wikispeak;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import wikispeak.creation.Creation;

public class Creator {

	private static Creator instance = null;
	  
    private String _creationName;
 
    
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
    	
    	if (!(new File("./creations/audiofiles")).exists()) {
    		Process process = Bash.execute(".", "mkdir creations/audiofiles");
    		try {
    			process.waitFor();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	if (!(new File("./creations/images")).exists()) {
    		Process process = Bash.execute(".", "mkdir creations/images");
    		try {
    			process.waitFor();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	if (!(new File("./creations/music")).exists()) {
    		Process process = Bash.execute(".", "mkdir creations/music");
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
    
    
    public String getCurrentCreationName() {
    	return _creationName;
    }
    
    
    /**
     * Return a string that contains the length of an audio file.
     * @param fileName The audio file that we are getting the time for.
     * @return The time of the given audio file
     * @throws InterruptedException 
     */
    public String getTimeOfAudio(String fileName) {
    	Process soxi = Bash.execute("./creations/audiofiles", "soxi -D " + fileName);
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
    	
    	try {
    		Process combine = Bash.execute("./creations/audiofiles", "sox " + list + combinedFileName);
    		combine.waitFor();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}

    }
    
    public void overlayMusic(String music, String voice, String combinedAudio) {
    	
    	try {
    		Process overlay = Bash.execute("./creations", "ffmpeg -i music/" + music + " -i audiofiles/" + voice + " -filter_complex amerge=inputs=2 -ac 2 " + combinedAudio);
    		overlay.waitFor();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}

    }
    
    public void trimMusic(String musicFile, String trimmedMusicFile, String length) {
    	try {
    		Process trim = Bash.execute(".", "ffmpeg -t " + length + " -i music/" + musicFile + " creations/music/" + trimmedMusicFile);
    		trim.waitFor();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
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
    	String vfSettings = "scale=iw*min(1920/iw\\,1080/ih):ih*min(1920/iw\\,1080/ih), pad=1920:1080:(1920-iw*min(1920/iw\\,1080/ih))/2:(1080-ih*min(1920/iw\\,1080/ih))/2,format=yuv420p,drawtext=fontfile=myfont.ttf:fontsize=100: fontcolor=white:shadowx=2:x=(w-text_w)/2:y=(h-text_h)/2:text='" + Wikit.get().getTerm() + "'";
    	
    	String cmd = "cat " + list + "| ffmpeg -framerate " + framerate + " -f image2pipe -i - -vf \"" + vfSettings + "\" -r 25 " + videoName;
    	
    	try {
    		Process create = Bash.execute("./creations/images", cmd);
			create.waitFor();
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
			Process combine = Bash.execute("./creations", "ffmpeg -loglevel panic -y -i " + videoName + " -i " + audioName + " -c:v copy -map 0:video:0 -map 1:audio:0 -strict experimental " + fileName + " >&2");
			combine.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	_creationName = fileName;
    }
    
    
    
    /**
     * Deletes all temporary files
     */
    public void cleanup() {
    	try {
    		Bash.execute("./creations/audiofiles", "rm .* *").waitFor();
    		Bash.execute("./creations/music", "rm .* *").waitFor();
    		Bash.execute("./creations/images", "rm .* *").waitFor();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    	
    }
    
    
    /**
     * Read the list of user creations to get data such as ratings/views
     * @return An ArrayList of Creation objects.
     */
    public ArrayList<Creation> readCreationList() {
    	ArrayList<Creation> list = null;
    	if (new File("./creations/creations.ser").exists()) {
    		try {
    			FileInputStream fileIn = new FileInputStream("./creations/creations.ser");
    			ObjectInputStream in = new ObjectInputStream(fileIn);
    			list = (ArrayList<Creation>) in.readObject();
    			in.close();
    			fileIn.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	} else {
    		list = new ArrayList<Creation>();
    	}
    	
    	return list;
    }
    
    
    
    /**
     * Write the list of creation objects to the file "creations.ser"
     * @param list An ArrayList containing all the Creation objects.
     */
    public void writeCreationList(ArrayList<Creation> list) {
    	try {
    		FileOutputStream fileOut = new FileOutputStream("./creations/creations.ser");
    		ObjectOutputStream out = new ObjectOutputStream(fileOut);
    		out.writeObject(list);
    		out.close();
    		fileOut.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
