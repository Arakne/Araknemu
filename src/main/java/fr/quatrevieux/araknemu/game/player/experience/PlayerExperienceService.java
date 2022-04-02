/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.player.experience;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerExperience;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerExperienceRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.listener.player.RebuildLifePointsOnLevelUp;
import fr.quatrevieux.araknemu.game.listener.player.SendLevelUp;
import fr.quatrevieux.araknemu.game.listener.player.SendPlayerXp;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.index.qual.Positive;

import java.util.ArrayList;
import java.util.List;

/**
 * Handle player experience and levels
 */
public final class PlayerExperienceService implements PreloadableService, EventsSubscriber {
    private final PlayerExperienceRepository repository;
    private final GameConfiguration.PlayerConfiguration configuration;

    private final List<PlayerExperience> levels = new ArrayList<>();

    public PlayerExperienceService(PlayerExperienceRepository repository, GameConfiguration.PlayerConfiguration configuration) {
        this.repository = repository;
        this.configuration = configuration;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading player experience...");

        levels.clear();
        levels.addAll(repository.all());

        logger.info("{} player levels loaded", levels.size());
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<PlayerLoaded>() {
                @Override
                public void on(PlayerLoaded event) {
                    event.player().dispatcher().add(new RebuildLifePointsOnLevelUp(event.player())); // Issue #55 : Before send stats
                    event.player().dispatcher().add(new SendLevelUp(event.player()));
                    event.player().dispatcher().add(new SendPlayerXp(event.player()));
                }

                @Override
                public Class<PlayerLoaded> event() {
                    return PlayerLoaded.class;
                }
            },
        };
    }

    /**
     * Load player level
     *
     * @param dispatcher The event dispatcher
     * @param player Player to load
     */
    public GamePlayerExperience load(Dispatcher dispatcher, Player player) {
        return new GamePlayerExperience(player, this, dispatcher);
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
    void applyLevelUpBonus(Player entity, @Positive int newLevel) {
        final int diff = newLevel - entity.level();

        if (diff > 0) {
            entity.setSpellPoints(entity.spellPoints() + configuration.spellBoostPointsOnLevelUp() * diff);
            entity.setBoostPoints(entity.boostPoints() + configuration.characteristicPointsOnLevelUp() * diff);
        }
    }
}
