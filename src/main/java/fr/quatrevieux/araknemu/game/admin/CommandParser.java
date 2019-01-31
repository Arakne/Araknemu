package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.admin.exception.AdminException;

import java.util.List;

/**
 * Parser for console command line
 */
public interface CommandParser {
    final public class Arguments {
        final private String command;
        final private List<String> arguments;
        final private Context context;

        public Arguments(String command, List<String> arguments, Context context) {
            this.command = command;
            this.arguments = arguments;
            this.context = context;
        }

        public String command() {
            return command;
        }

        public List<String> arguments() {
            return arguments;
        }

        public Context context() {
            return context;
        }
    }

    /**
     * Parse a command line
     */
    public Arguments parse(String line) throws AdminException;
}
