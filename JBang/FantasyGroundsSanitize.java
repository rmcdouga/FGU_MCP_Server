///usr/bin/env jbang
//JAVA 25
//DEPS org.jboss.aesh:aesh:0.66.19

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;

import org.jboss.aesh.cl.CommandLine;
import org.jboss.aesh.cl.internal.OptionType;
import org.jboss.aesh.cl.internal.ProcessedCommand;
import org.jboss.aesh.cl.internal.ProcessedCommandBuilder;
import org.jboss.aesh.cl.internal.ProcessedOptionBuilder;
import org.jboss.aesh.cl.parser.AeshCommandLineParser;
import org.jboss.aesh.console.command.Command;
import org.jboss.aesh.console.command.CommandException;
import org.jboss.aesh.console.command.CommandResult;
import org.jboss.aesh.console.command.invocation.CommandInvocation;
import org.jboss.aesh.console.command.map.MapCommand;

public class FantasyGroundsSanitize {
    private static final String USAGE = "Usage: jbang FantasyGroundsSanitize.java [--help|-h] [--stdin|--clipboard]\n\n" +
            "Options:\n" +
            "  --stdin     Read UTF-8 text from stdin and write sanitized text to stdout.\n" +
            "  --clipboard Read text from the clipboard, sanitize it, and write it back to the clipboard.\n" +
            "  --help, -h  Show this help message.\n";

    public static void main(String[] args) {
        try {
            CommandLine<?> commandLine = parseArgs(args);
            if (commandLine.hasOption("help") || commandLine.hasOption('h')) {
                System.out.print(USAGE);
                return;
            }

            if (commandLine.hasOption("clipboard")) {
                String input = readClipboardText();
                String sanitized = sanitize(input);
                writeClipboardText(sanitized);
                return;
            }

            String input = readUtf8Stdin(System.in);
            String sanitized = sanitize(input);
            System.out.print(sanitized);
        } catch (CharacterCodingException e) {
            System.err.println("Invalid UTF-8 input.");
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.print(USAGE);
            System.exit(1);
        }
    }

    static CommandLine<?> parseArgs(String[] args) throws Exception {
        ProcessedCommand<Command> command = new ProcessedCommandBuilder()
                .name("fantasygrounds-sanitize")
                .description("Sanitize text for Fantasy Grounds")
                .command(new NoopCommand())
                .addOption(new ProcessedOptionBuilder()
                        .shortName('h')
                        .name("help")
                        .description("Show usage information")
                        .type(Boolean.class)
                        .optionType(OptionType.BOOLEAN)
                        .hasValue(false)
                        .create())
                .addOption(new ProcessedOptionBuilder()
                        .shortName('s')
                        .name("stdin")
                        .description("Read UTF-8 text from stdin and sanitize to stdout")
                        .type(Boolean.class)
                        .optionType(OptionType.BOOLEAN)
                        .hasValue(false)
                        .create())
                .addOption(new ProcessedOptionBuilder()
                        .shortName('c')
                        .name("clipboard")
                        .description("Read from clipboard, sanitize, and write back to clipboard")
                        .type(Boolean.class)
                        .optionType(OptionType.BOOLEAN)
                        .hasValue(false)
                        .create())
                .create();

        AeshCommandLineParser<Command> parser = new AeshCommandLineParser<>(command);
        String joinedArgs = "fantasygrounds-sanitize";
        if (args != null && args.length > 0) {
            joinedArgs += " " + String.join(" ", args);
        }
        return parser.parse(joinedArgs);
    }

    static String readUtf8Stdin(InputStream in) throws IOException, CharacterCodingException {
        byte[] bytes = in.readAllBytes();
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        return decoder.decode(ByteBuffer.wrap(bytes)).toString();
    }

    static String readClipboardText() throws IOException, UnsupportedFlavorException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);
        if (transferable == null || !transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return "";
        }
        return (String) transferable.getTransferData(DataFlavor.stringFlavor);
    }

    static void writeClipboardText(String text) throws IOException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);
    }

    static String sanitize(String textIn) {
        if (textIn == null) {
            return "";
        }

        String sanitized = Normalizer.normalize(textIn, Normalizer.Form.NFKC)
                .replace("\u201C", "\"")
                .replace("\u201D", "\"")
                .replace("\u2018", "'")
                .replace("\u2019", "'")
                .replace("\u00A0", " ")
                .replace("\u2007", " ")
                .replace("\u202F", " ");

        StringBuilder out = new StringBuilder(sanitized.length());
        for (int i = 0; i < sanitized.length(); i++) {
            char ch = sanitized.charAt(i);
            if (isControlCharacter(ch)) {
                continue;
            }
            out.append(ch);
        }
        return out.toString();
    }

    private static boolean isControlCharacter(char ch) {
        return ch < 0x20 && ch != '\n' && ch != '\r' && ch != '\t' || ch == 0x7F;
    }

    private static final class NoopCommand extends MapCommand<CommandInvocation> {
        @Override
        public CommandResult execute(CommandInvocation invocation) throws CommandException, InterruptedException {
            return CommandResult.SUCCESS;
        }
    }
}
