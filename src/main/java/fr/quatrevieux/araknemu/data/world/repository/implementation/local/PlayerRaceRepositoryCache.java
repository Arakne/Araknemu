package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;

import java.util.EnumMap;
import java.util.Map;

/**
 * Cache for {@link PlayerRaceRepository}
 */
final public class PlayerRaceRepositoryCache implements PlayerRaceRepository {
    final private PlayerRaceRepository repository;

    final private Map<Race, PlayerRace> races = new EnumMap<>(Race.class);

    public PlayerRaceRepositoryCache(PlayerRaceRepository repository) {
        this.repository = repository;
    }

    @Override
    public PlayerRace get(Race race) throws RepositoryException {
        if (!races.containsKey(race)) {
            races.put(race, repository.get(race));
        }

        return races.get(race);
    }

    @Override
    public void initialize() throws RepositoryException {
        repository.initialize();
    }

    @Override
    public void destroy() throws RepositoryException {
        repository.destroy();
    }

    @Override
    public PlayerRace get(PlayerRace entity) throws RepositoryException {
        return get(entity.race());
    }

    @Override
    public boolean has(PlayerRace entity) throws RepositoryException {
        return races.containsKey(entity.race()) || repository.has(entity);
    }
}
