package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCommandTest extends GameBaseCase {
    @Test
    void defaults() {
        Command command = new AbstractCommand() {
            @Override
            protected void build(Builder builder) {}

            @Override
            public String name() {
                return "cmd";
            }

            @Override
            public void execute(AdminPerformer output, List<String> arguments) {}
        };

        assertEquals("No description", command.description());
        assertEquals(
            "cmd - No description\n" +
            "========================================\n\n" +
            "No help",
            command.help()
        );
        assertEquals(EnumSet.of(Permission.ACCESS), command.permissions());
    }

    @Test
    void withDescriptionAndHelp() {
        Command command = new AbstractCommand() {
            @Override
            protected void build(Builder builder) {
                builder
                    .description("My very useful command")
                    .help("Do what you wants")
                    .requires(Permission.SUPER_ADMIN)
                ;
            }

            @Override
            public String name() {
                return "cmd";
            }

            @Override
            public void execute(AdminPerformer output, List<String> arguments) {}
        };

        assertEquals("My very useful command", command.description());
        assertEquals(
            "cmd - My very useful command\n" +
                "========================================\n\n" +
                "Do what you wants",
            command.help()
        );
        assertEquals(EnumSet.of(Permission.ACCESS, Permission.SUPER_ADMIN), command.permissions());
    }
}
