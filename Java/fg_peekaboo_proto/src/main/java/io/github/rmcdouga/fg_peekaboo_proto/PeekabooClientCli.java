package io.github.rmcdouga.fg_peekaboo_proto;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import tools.jackson.databind.json.JsonMapper;

public class PeekabooClientCli implements PeekabooClient {
	private static final String FOREGROUND_ARG = "--foreground";
	private static final String JSON_ARG = "--json";
	
	private final Path peekabooCliPath;
	
	public PeekabooClientCli(Path peekabooCliPath) {
		this.peekabooCliPath = peekabooCliPath;
	}

	@Override
	public void image(AppTarget target, ImageFormat format, ImageScale scale, Path outputPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void image(AppTarget target, ImageScale scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void see(AppTarget target, Path outputPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public void click(long pid, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(AppTarget target, int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public long appLaunch(String bundleId) {
		List<String> result = executeCommand(peekabooCliPath, "app", "launch", bundleId, JSON_ARG, FOREGROUND_ARG);
		System.out.println("appLaunch result: \n" + String.join(System.lineSeparator(), result));
		return 0;
	}

	@Override
	public long appFocus(long pid) {
		// TODO Auto-generated method stub
		return 0;
	}


	
    /**
     * Executes a system command and returns the console output.
     * 
     * @param command The base command or executable (e.g., "ping" or "ls")
     * @param arguments Optional flags or arguments for the command
     * @return String containing the standard output from the execution
     */
    private List<String> executeCommand(Path command, String... arguments) {
        StringBuilder output = new StringBuilder();
        
        // Combine command and arguments into a single list
        List<String> commandList = Stream.concat(
        										Stream.of(command.toString()), 
        										arguments != null ? Stream.of(arguments) : Stream.empty()
        										)
        								 .toList();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            
            // Redirect error stream to standard output stream to capture all logs together
            processBuilder.redirectErrorStream(true);
            
            // Start the process
            Process process = processBuilder.start();
            
            List<String> lines = readAllLines(process);
            
            // Wait for the process to finish and check the exit code
            int exitCode = process.waitFor();
            if (exitCode != 0) {
            	throw new PeekabooClientException("Command execution failed with exit code: " + exitCode + ". Output: " + String.join(System.lineSeparator(), lines));
            }
            
            return lines;
        } catch (IOException | InterruptedException e) {
            throw new PeekabooClientException("Execution failed: " + e.getMessage(), e);
        }
    }

	private List<String> readAllLines(Process process) throws IOException {
		// Read the output from the command line execution
		try (BufferedReader reader = process.inputReader()) {
			return reader.lines().toList();
		}
	}

}
