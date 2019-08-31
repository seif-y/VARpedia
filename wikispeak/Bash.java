package wikispeak;

public class Bash {
	
	public static Process execute(String... commands) {		
		try {
			ProcessBuilder pb = new ProcessBuilder(commands);
			Process process = pb.start();
			return process;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
