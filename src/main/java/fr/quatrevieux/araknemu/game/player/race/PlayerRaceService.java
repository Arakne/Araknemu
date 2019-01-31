package fr.quatrevieux.araknemu.game.player.race;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handle player race data
 */
final public class PlayerRaceService implements PreloadableService {
    final private PlayerRaceRepository repository;
    final private SpellService spellService;

    final private Map<Race, GamePlayerRace> races = new EnumMap<>(Race.class);

    public PlayerRaceService(PlayerRaceRepository repository, SpellService spellService) {
        this.repository = repository;
        this.spellService = spellService;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading races...");

        for (PlayerRace race : repository.load()) {
            races.put(race.race(), create(race));
        }

        logger.info("{} races loaded", races.size());
    }

    /**
     * Get a player race data
     */
    public GamePlayerRace get(Race race) {
        if (!races.containsKey(race)) {
            races.put(race, create(repository.get(race)));
        }

        return races.get(race);
    }

    private GamePlayerRace create(PlayerRace entity) {
        return new GamePlayerRace(
            entity,
            Arrays.stream(entity.spells())
                .mapToObj(spellService::get)
                .collect(Collectors.toList())
        );
    }
}
