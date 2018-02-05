package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import org.slf4j.helpers.MessageFormatter;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

abstract public class CommandTestCase extends GameBaseCase {
    protected Command command;
    protected PerformerWrapper performer;

    static public class PerformerWrapper implements AdminPerformer {
        class Entry {
            final public LogType type;
            final public String message;

            public Entry(LogType type, String message) {
                this.type = type;
                this.message = message;
            }
        }

        final private AdminPerformer performer;
        public List<Entry> logs = new ArrayList<>();

        public PerformerWrapper(AdminPerformer performer) {
            this.performer = performer;
        }

        @Override
        public void execute(String command) throws AdminException {
            performer.execute(command);
        }

        @Override
        public boolean isGranted(Set<Permission> permissions) {
            return performer.isGranted(permissions);
        }

        @Override
        public Optional<GameAccount> account() {
            return performer.account();
        }

        @Override
        public void log(LogType type, String message, Object... arguments) {
            logs.add(
                new Entry(
                    type,
                    MessageFormatter.arrayFormat(message, arguments).getMessage()
                )
            );
        }
    }

    public AdminUser user() throws ContainerException, SQLException, ContextException {
        return container.get(AdminService.class).user(gamePlayer(true));
    }

    public void execute(AdminPerformer performer, String... arguments) {
        command.execute(
            this.performer = new PerformerWrapper(performer),
            Arrays.asList(arguments)
        );
    }

    public void execute(String... arguments) throws ContainerException, SQLException, ContextException {
        execute(user(), arguments);
    }

    public void assertOutput(String... lines) {
        assertArrayEquals(
            lines,
            performer.logs
                .stream()
                .map(entry -> entry.message)
                .toArray()
        );
    }

    public void assertOutputContains(String line) {
        assertContains(
            line,
            performer.logs
                .stream()
                .map(entry -> entry.message)
                .collect(Collectors.toList())
        );
    }

    public void assertSuccess(String line) {
        assertLogType(LogType.SUCCESS, line);
    }

    public void assertError(String line) {
        assertLogType(LogType.ERROR, line);
    }

    public void assertInfo(String line) {
        assertLogType(LogType.DEFAULT, line);
    }

    public void assertLogType(LogType type, String line) {
        Optional<PerformerWrapper.Entry> log = performer.logs
            .stream()
            .filter(entry -> entry.message.equals(line))
            .findFirst()
        ;

        assertTrue(log.isPresent(), "Line not found");
        assertEquals(type, log.get().type, "Invalid log type");
    }
}
