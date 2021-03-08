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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.player.experience;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.experience.event.PlayerLevelUp;
import fr.quatrevieux.araknemu.game.player.experience.event.PlayerXpChanged;

/**
 * Manage the player level and experience
 */
public final class GamePlayerExperience {
    private final Player entity;
    private final PlayerExperienceService service;
    private final Dispatcher dispatcher;

    public GamePlayerExperience(Player entity, PlayerExperienceService service, Dispatcher dispatcher) {
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
    public long min() {
        return service.byLevel(entity.level()).experience();
    }

    /**
     * Get the current player experience
     */
    public long current() {
        return entity.experience();
    }

    /**
     * Get the next level experience
     */
    public long max() {
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
    public void add(long experience) {
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
