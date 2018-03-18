package fr.quatrevieux.araknemu.game.player.experience;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.common.PlayerLevelUp;
import fr.quatrevieux.araknemu.game.event.common.PlayerXpChanged;

/**
 * Manage the player level and experience
 */
final public class PlayerLevel {
    final private Player entity;
    final private PlayerExperienceService service;
    final private Dispatcher dispatcher;

    public PlayerLevel(Player entity, PlayerExperienceService service, Dispatcher dispatcher) {
        this.entity = entity;
        this.service = service;
        this.dispatcher = dispatcher;
    }

    /**
     * Get the current player level
     */
    public int level() {
        return entity.level();
    }

    /**
     * Get the minimal experience for current level
     */
    public long minExperience() {
        return service.byLevel(entity.level()).experience();
    }

    /**
     * Get the current player experience
     */
    public long currentExperience() {
        return entity.experience();
    }

    /**
     * Get the next level experience
     */
    public long maxExperience() {
        return service.byLevel(entity.level() + 1).experience();
    }

    /**
     * Check if the player has reached the maximum level
     */
    public boolean maxLevelReached() {
        return entity.level() >= service.maxLevel();
    }

    /**
     * Add experience for the player and check for level up
     *
     * @param experience Experience to add
     */
    public void addExperience(long experience) {
        entity.setExperience(entity.experience() + experience);

        if (maxLevelReached()) {
            dispatcher.dispatch(new PlayerXpChanged());
            return;
        }

        int level = entity.level();

        while (
            level + 1 <= service.maxLevel()
            && entity.experience() >= service.byLevel(level + 1).experience()
        ) {
            ++level;
        }

        if (level == entity.level()) {
            dispatcher.dispatch(new PlayerXpChanged());
            return;
        }

        service.applyLevelUpBonus(entity, level);
        entity.setLevel(level);

        dispatcher.dispatch(new PlayerLevelUp(level));
    }
}
