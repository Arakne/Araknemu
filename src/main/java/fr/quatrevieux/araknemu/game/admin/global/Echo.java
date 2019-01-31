package fr.quatrevieux.araknemu.game.admin.global;

import fr.quatrevieux.araknemu.game.admin.AbstractCommand;
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.LogType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Simple "echo" command
 */
final public class Echo extends AbstractCommand {
    @Override
    protected void build(Builder builder) {
        builder
            .description("Write to console the arguments")
            .help(
                "echo [-i|s|e] [args ...]\n\n" +
                "Options :\n" +
                "-i : Print as information (default)\n" +
                "-s : Print as success\n" +
                "-e : Print as error\n\n" +
                "Example :\n" +
                "echo Hello World ! ==> Print 'Hello World !' in white (info)\n" +
                "echo -e WAKE !!!   ==> Print 'WAKE !!!' in red (error)"
            )
        ;
    }

    @Override
    public String name() {
        return "echo";
    }

    @Override
    public void execute(AdminPerformer performer, List<String> arguments) {
        LogType log = LogType.DEFAULT;
        int start = 2;

        switch (arguments.get(1)) {
            case "-e":
                log = LogType.ERROR;
                break;
            case "-s":
                log = LogType.SUCCESS;
                break;
            case "-i":
                log = LogType.DEFAULT;
                break;
            default:
                start = 1;
        }

        performer.log(log, StringUtils.join(arguments.listIterator(start), " "));
    }
}
