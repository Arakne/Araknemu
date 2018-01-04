package fr.quatrevieux.araknemu.data.living.repository.player;

import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.ServerCharacters;

import java.util.Collection;

/**
 * Repository for {@link Player} entity
 */
public interface PlayerRepository extends MutableRepository<Player> {
    /**
     * Get list of players by account
     *
     * @param accountId The account race
     * @param serverId The server
     */
    Collection<Player> findByAccount(int accountId, int serverId);

    /**
     * Check if the name is already used into the current server
     *
     * @param player The criteria
     *
     * @return true if exists
     */
    boolean nameExists(Player player);

    /**
     * Get the account characters count
     *
     * @param player The player data
     */
    int accountCharactersCount(Player player);

    /**
     * Get list of servers with characters count
     *
     * @param accountId The account id to find
     *
     * @return The list of servers. The returned server can be down, but cannot have a count of zero
     */
    Collection<ServerCharacters> accountCharactersCount(int accountId);

    /**
     * Get the player entity by race, and ensure that account and server is valid
     */
    Player getForGame(Player player);
}
