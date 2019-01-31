package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Server for admin
 */
final public class AdminService {
    final private Map<String, ContextResolver> resolvers;

    final private Map<Integer, AdminUser> usersById = new HashMap<>();

    public AdminService(Collection<ContextResolver> resolvers) {
        this.resolvers = resolvers
            .stream()
            .collect(
                Collectors.toMap(
                    ContextResolver::type,
                    Function.identity()
                )
            )
        ;
    }

    /**
     * Get an admin user from player
     *
     * @param player The admin player
     */
    public AdminUser user(GamePlayer player) throws ContextException {
        if (!usersById.containsKey(player.id())) {
            usersById.put(
                player.id(),
                new AdminUser(this, player, resolvers)
            );
        }

        return usersById.get(player.id());
    }

    /**
     * Remove the admin session
     */
    void removeSession(AdminUser user) {
        usersById.remove(user.id());
    }
}
