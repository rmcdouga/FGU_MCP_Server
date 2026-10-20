package io.github.rmcdouga.fg_peekaboo_proto;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.rmcdouga.fg_peekaboo_proto.JsonData.JsonDataFactory;
import io.github.rmcdouga.fg_peekaboo_proto.PeekabooClient.AppTarget.AppNameTarget;
import io.github.rmcdouga.fg_peekaboo_proto.PeekabooClient.AppTarget.FrontMostTarget;
import io.github.rmcdouga.fg_peekaboo_proto.PeekabooClient.AppTarget.ProcessTarget;
import tools.jackson.databind.json.JsonMapper;

public class PeekabooClientCli implements PeekabooClient {
	private static final String FOREGROUND_ARG = "--foreground";
	private static final String JSON_ARG = "--json";
	
	private final Path peekabooCliPath;
	private final JsonDataFactory jsonDataFactory;
	
	public PeekabooClientCli(Path peekabooCliPath, JsonDataFactory jsonDataFactory) {
		this.peekabooCliPath = peekabooCliPath;
		this.jsonDataFactory = jsonDataFactory;
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
	public ImageInfo see(AppTarget target, Path outputPath) {
		String cliTargetArg = switch (target) {
			case AppNameTarget appNameTarget -> throw new IllegalArgumentException("AppNameTarget is not supported for move operation. Use ProcessTarget or FrontMostTarget instead.");
			case ProcessTarget processTarget -> "--pid";
			case FrontMostTarget frontMostTarget -> throw new IllegalArgumentException("FrontMostTarget is not supported for move operation. Use ProcessTarget or FrontMostTarget instead.");
		};
		String json = callPeekabooCli("see", cliTargetArg, target.cliValue(), "--path", outputPath.toString(), "--no-elements", JSON_ARG);

		// Parse the JSON output to extract image size information
		JsonData jsonData = jsonDataFactory.fromJsonString(json);
		JsonData observations = jsonData.arrayAt("/data/observations")
										.findFirst()
										.orElseThrow(()->new PeekabooClientException("No observations returned from 'see' tool."));
		Integer imgSizeX = observations.getIntAt("/coordinates/image_size_pixels/width")
									   .orElseThrow(()->new PeekabooClientException("No image width returned from 'see' tool."));
		Integer imgSizeY = observations.getIntAt("/coordinates/image_size_pixels/height")
									   .orElseThrow(()->new PeekabooClientException("No image height returned from 'see' tool."));
	
		return new ImageInfo(imgSizeX, imgSizeY);
	}

	@Override
	public void click(long pid, int x, int y) {
		callPeekabooCli("click", "--pid", Long.toString(pid), "--at", Integer.toString(x) + "," + Integer.toString(y), JSON_ARG, FOREGROUND_ARG);
	}

	@Override
	public void move(AppTarget target, int x, int y) {
		String cliTargetArg = switch (target) {
			case AppNameTarget appNameTarget -> throw new IllegalArgumentException("AppNameTarget is not supported for move operation. Use ProcessTarget or FrontMostTarget instead.");
			case ProcessTarget processTarget -> "--pid";
			case FrontMostTarget frontMostTarget -> throw new IllegalArgumentException("FrontMostTarget is not supported for move operation. Use ProcessTarget or FrontMostTarget instead.");
		};
		callPeekabooCli("move", cliTargetArg, target.cliValue(), "--at", Integer.toString(x) + "," + Integer.toString(y), JSON_ARG, FOREGROUND_ARG);
	}

	@Override
	public long appLaunch(String bundleId) {
		String json = callPeekabooCli("app", "launch", bundleId, JSON_ARG, FOREGROUND_ARG);
		JsonData jsonData = jsonDataFactory.fromJsonString(json);
		return jsonData.getLongAt("/data/pid").orElseThrow(()->new PeekabooClientException("No process ID returned from 'app_launch' tool."));
	}

	@Override
	public long appFocus(long pid) {
		String json = callPeekabooCli("app", "focus", "--pid", Long.toString(pid), JSON_ARG, FOREGROUND_ARG);
		return pid;
	}

	private String callPeekabooCli(String... args) {
		List<String> result = executeCommand(peekabooCliPath, args);
		String json = String.join("\n", result);
		System.out.println("peekaboo json result: \n" + json);
		return json;
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
