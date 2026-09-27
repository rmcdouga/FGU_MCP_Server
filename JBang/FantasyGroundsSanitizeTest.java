///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.junit.jupiter:junit-jupiter-engine:6.1.3
//DEPS org.junit.platform:junit-platform-console:6.1.3

//SOURCES FantasyGroundsSanitize.java

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.platform.console.ConsoleLauncher;

public class FantasyGroundsSanitizeTest {

    @Test
    public void testSanitizeReplacesCurlyQuotesAndSpecialSpaces() {
        String input = "This is “text” with a non-breaking\u00A0space and ‘single’ quotes.";
        String expected = "This is \"text\" with a non-breaking space and 'single' quotes.";

        assertEquals(expected, FantasyGroundsSanitize.sanitize(input));
    }

    @Test
    public void testSanitizeRemovesControlCharacters() {
        String input = "Line 1\u0001\nLine 2\u007F\u0003";
        String expected = "Line 1\nLine 2";

        assertEquals(expected, FantasyGroundsSanitize.sanitize(input));
    }

    @Test
    public void testParseArgsRecognizesHelpAndStdinOptions() throws Exception {
        var helpLine = FantasyGroundsSanitize.parseArgs(new String[] { "--help" });
        var stdinLine = FantasyGroundsSanitize.parseArgs(new String[] { "--stdin" });

        assertTrue(helpLine.hasOption("help"));
        assertTrue(stdinLine.hasOption("stdin"));
    }

    @Test
    public void testParseArgsRecognizesClipboardOption() throws Exception {
        var line = FantasyGroundsSanitize.parseArgs(new String[] { "--clipboard" });

        assertTrue(line.hasOption("clipboard"));
    }

    @Test
    public void testClipboardRoundTripSanitizesText() throws Exception {
        String input = "This is “text” with ‘quotes’ and a non-breaking\u00A0space.";
        String expected = "This is \"text\" with 'quotes' and a non-breaking space.";

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(input);
        clipboard.setContents(selection, null);

        Process process = new ProcessBuilder(
                "jbang",
                "FantasyGroundsSanitize.java",
                "--clipboard")
                .directory(new File("."))
                .start();

        int exitCode = process.waitFor();
        assertEquals(0, exitCode, "Clipboard sanitization command should exit successfully.");

        String result = (String) clipboard.getContents(null)
                .getTransferData(DataFlavor.stringFlavor);

        assertEquals(expected, result);
    }

    public static void main(final String... args) {
        String jarsList = Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator))
                .filter(path -> path.contains("/cache/jars/"))
                .reduce((a, b) -> a + File.pathSeparator + b)
                .orElse("");

        ConsoleLauncher.main("execute", "--scan-class-path", "-cp", jarsList);
    }
}
