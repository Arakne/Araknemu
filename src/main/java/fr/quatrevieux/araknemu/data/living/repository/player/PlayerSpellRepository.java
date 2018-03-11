package fr.quatrevieux.araknemu.data.living.repository.player;

import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;

import java.util.Collection;

/**
 * Repository for {@link PlayerSpell}
 */
public interface PlayerSpellRepository extends MutableRepository<PlayerSpell> {
    /**
     * Get all spells for a player
     *
     * @param player The player to load
     */
    public Collection<PlayerSpell> byPlayer(Player player);
}
