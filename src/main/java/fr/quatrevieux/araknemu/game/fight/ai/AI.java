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

package fr.quatrevieux.araknemu.game.fight.ai;

import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base type for AI
 * Use by action generators as data accessor
 *
 * @param <F> Current fighter type
 */
public interface AI<F extends ActiveFighter> {
    /**
     * Start the AI
     * The AI will be pushed into the fight to be executed
     *
     * @param turn The current turn
     */
    public void start(Turn turn);

    /**
     * Get the fighter controlled by the AI
     */
    public F fighter();

    /**
     * Get the current fight map
     */
    public BattlefieldMap map();

    /**
     * Get the current turn
     */
    public Turn turn();

    /**
     * Get all fighter of the current fight
     */
    public Stream<? extends FighterData> fighters();

    /**
     * Get the best enemy
     * This method behavior can change, depending on the AI resolution strategy
     *
     * An empty optional can be returned, if there is no enemy which match
     */
    public Optional<? extends FighterData> enemy();

    /**
     * Get helper for the current AI
     */
    public default AIHelper helper() {
        return new AIHelper(this);
    }
}
