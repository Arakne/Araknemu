package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandPermissionsException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.admin.exception.ExceptionHandler;
import fr.quatrevieux.araknemu.game.admin.global.GlobalContext;
import fr.quatrevieux.araknemu.game.event.listener.admin.RemoveAdminSession;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.CommandResult;
import org.slf4j.helpers.MessageFormatter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Admin user session
 */
final public class AdminUser implements AdminPerformer {
    final private AdminService service;
    final private GamePlayer player;

    final private AdminUserContext context;
    final private AdminUserCommandParser parser;
    final private ExceptionHandler errorHandler;

    public AdminUser(AdminService service, GamePlayer player, Map<String, ContextResolver> resolvers) throws ContextException {
        this.service = service;
        this.player = player;

        Context globalContext = new GlobalContext(this);
        this.context = new AdminUserContext(
            globalContext,
            resolvers.get("player").resolve(globalContext, player),
            resolvers
        );

        this.parser = new AdminUserCommandParser(this);
        this.errorHandler = new ExceptionHandler(this);

        player.dispatcher().add(new RemoveAdminSession(this));
    }

    /**
     * Get the admin user context handler
     */
    public AdminUserContext context() {
        return context;
    }

    @Override
    public Optional<GameAccount> account() {
        return Optional.of(player.account());
    }

    @Override
    public void execute(String command) throws AdminException {
        execute(parser.parse(command));
    }

    @Override
    public boolean isGranted(Set<Permission> permissions) {
        return player.account().isGranted(permissions);
    }

    @Override
    public void log(LogType type, String message, Object... arguments) {
        player.send(
            new CommandResult(type, MessageFormatter.arrayFormat(message, arguments).getMessage())
        );
    }

    /**
     * Send the exception error to the console
     */
    public void error(Throwable error) {
        errorHandler.handle(error);
    }

    /**
     * Destroy this user session
     */
    public void logout() {
        service.removeSession(this);
    }

    /**
     * Get the admin player id
     */
    public int id() {
        return player.id();
    }

    private void execute(CommandParser.Arguments arguments) throws AdminException {
        Command command = arguments.context().command(arguments.command());

        if (!isGranted(command.permissions())) {
            throw new CommandPermissionsException(command.name(), command.permissions());
        }

        command.execute(this, arguments.arguments());
    }
}
