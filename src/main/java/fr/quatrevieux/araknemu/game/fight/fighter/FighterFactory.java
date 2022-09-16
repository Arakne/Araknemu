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

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Factory for make fighters
 */
public interface FighterFactory {
    /**
     * Create a PlayerFighter from a game player
     *
     * @param player The player
     *
     * @return The PlayerFighter
     */
    public PlayerFighter create(GamePlayer player);

    /**
     * Create a monster fighter
     * An unique ID will be generated for the fighter
     */
    public Fighter create(Monster monster, FightTeam team);

    /**
     * Generate a fighter with a unique ID
     * The generated ID will be a negative integer
     */
    public Fighter generate(FighterGenerator generator);

    public static interface FighterGenerator {
        /**
         * Create a fighter with a generated ID
         */
        public Fighter create(int id);
    }
}
