package fr.quatrevieux.araknemu.game.player.experience;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerExperience;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerExperienceRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.listener.service.AddLevelListeners;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Handle player experience and levels
 */
final public class PlayerExperienceService implements PreloadableService {
    final private PlayerExperienceRepository repository;
    final private GameConfiguration.PlayerConfiguration configuration;
    final private ListenerAggregate dispatcher;

    final private List<PlayerExperience> levels = new ArrayList<>();

    public PlayerExperienceService(PlayerExperienceRepository repository, GameConfiguration.PlayerConfiguration configuration, ListenerAggregate dispatcher) {
        this.repository = repository;
        this.configuration = configuration;
        this.dispatcher = dispatcher;
    }

    @Override
    public void preload(Logger logger) {
        dispatcher.add(new AddLevelListeners());

        logger.info("Loading player experience...");

        levels.clear();
        levels.addAll(repository.all());

        logger.info("{} player levels loaded", levels.size());
    }

    /**
     * Load player level
     *
     * @param dispatcher The event dispatcher
     * @param player Player to load
     */
    public PlayerLevel load(Dispatcher dispatcher, Player player) {
        return new PlayerLevel(player, this, dispatcher);
    }

    /**
     * Get the experience related to the level
     * If the level is higher than maximum level, the maximum level is returned
     *
     * @param level Level to get
     */
    PlayerExperience byLevel(int level) {
        return level <= levels.size()
            ? levels.get(level - 1)
            : levels.get(levels.size() - 1)
        ;
    }

    /**
     * Get the maximum player level
     */
    int maxLevel() {
        return levels.size();
    }

    /**
     * Apply the level up bonus
     *
     * @param entity The player entity
     * @param newLevel The reached level
     */
    void applyLevelUpBonus(Player entity, int newLevel) {
        int diff = newLevel - entity.level();

        entity.setSpellPoints(entity.spellPoints() + configuration.spellBoostPointsOnLevelUp() * diff);
        entity.setBoostPoints(entity.boostPoints() + configuration.characteristicPointsOnLevelUp() * diff);
    }
}
