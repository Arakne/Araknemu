package fr.quatrevieux.araknemu.game.admin.exception;

import fr.quatrevieux.araknemu.game.admin.AdminUser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Handle exceptions
 */
final public class ExceptionHandler {
    final private AdminUser user;

    final private Map<Class, Consumer<Throwable>> handlers = new HashMap<>();

    public ExceptionHandler(AdminUser user) {
        this.user = user;

        register(CommandNotFoundException.class, e -> user.error("Command '{}' is not found", e.command()));
        register(CommandException.class, e -> user.error("An error occurs during execution of '{}' : {}", e.command(), e.getMessage()));
        register(CommandPermissionsException.class, e -> user.error("Unauthorized command '{}', you need at least these permissions {}", e.command(), e.permissions()));
        register(ContextException.class, e -> user.error("Error during resolving context : {}", e.getMessage()));
        register(ContextNotFoundException.class, e -> user.error("The context '{}' is not found", e.context()));
    }

    /**
     * Handle the error
     */
    public void handle(Throwable error) {
        if (!handlers.containsKey(error.getClass())) {
            user.error("Error : {}", error.toString());
            return;
        }

        handlers.get(error.getClass()).accept(error);
    }

    private <T extends Throwable> void register(Class<T> type, Consumer<T> consumer) {
        handlers.put(type, (Consumer<Throwable>) consumer);
    }
}
