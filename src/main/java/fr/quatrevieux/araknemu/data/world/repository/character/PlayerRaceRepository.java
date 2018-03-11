package fr.quatrevieux.araknemu.data.world.repository.character;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;

import java.util.Collection;

/**
 * Read only repository for {@link PlayerRace}
 */
public interface PlayerRaceRepository extends Repository<PlayerRace> {
    /**
     * Get a race entity by its Race enum value
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When cannot found the player race
     * @throws RepositoryException When an error occurs during retrieving data
     */
    PlayerRace get(Race race) throws RepositoryException;

    /**
     * Load all player races
     */
    public Collection<PlayerRace> load();
}
