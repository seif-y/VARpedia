package wikispeak;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Bash {
	
	public static Process execute(String directory, String cmd) {		
		try {
			ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
			pb.directory(new File(directory));
			Process process = pb.start();
			return process;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String readOutput(Process process) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        
		String output = "";
        String line = "";
        
        try {
			while ((line = reader.readLine()) != null) { output += line + "\n"; }
			int exitCode = process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        return output;
	}

}
