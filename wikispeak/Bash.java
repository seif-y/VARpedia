package wikispeak;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Bash {

	private static Bash instance = null;

	private Bash() {

	}

	public Bash get() {
		if (instance == null)
			instance = new Bash();

		return instance;
	}

	
	/**
	 * Uses ProcessBuilder to run a specified BASH command.
	 * @param directory The directory that the command will be run in
	 * @param cmd The command being executed
	 * @return The Process object returned by the ProcessBuilder's start method, or null if an exception is caught.
	 */
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
	
	/**
	 * Reads and returns the output of a Process object, using BufferedReader
	 * @param process The process object being read
	 * @return A string containing the stdout for the given process
	 */
	public static String readOutput(Process process) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        
		String output = "";
        String line = "";
        
        try {
			while ((line = reader.readLine()) != null) { output += line + "\n"; }
			process.waitFor();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        return output;
	}
	
	
	/**
	 * Checks a string for invalid characters to input as bash commands.
	 * @param text The string to check
	 * @return True if it contains an invalid character, False otherwise
	 */
	public static boolean hasInvalidChars(String text, boolean spaceIsInvalid) {
		String [] invalidChars = { "$", "$", ".", "*", "|", "\\", "<", ">", "?", "/", ":", "\"", "`", "[", "]", "(", ")", "`" };
		
		if (spaceIsInvalid) { invalidChars[0] = " "; }
		
		for (String character : invalidChars) {
    		if (text.contains(character)) {
    			return true;
    		}
    	}
		
		return false;
	}

}
