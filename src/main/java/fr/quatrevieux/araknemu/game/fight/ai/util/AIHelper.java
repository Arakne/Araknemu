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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.util;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.proxy.ProxyAI;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;

/**
 * Utility class for perform common AI operations on the current fighter
 *
 * @see AI#helper()
 */
public final class AIHelper {
    private final ProxyAI ai;
    private final CellsHelper cells;
    private final SpellsHelper spells;
    private final FightersHelper enemies;
    private final FightersHelper allies;

    public AIHelper(AI ai) {
        this(new ProxyAI(ai));
    }

    public AIHelper(ProxyAI ai) {
        this.ai = ai;

        this.cells = new CellsHelper(ai);
        this.spells = new SpellsHelper(this, ai);
        this.enemies = new FightersHelper(this, ai, fighter -> !fighter.team().equals(ai.fighter().team()));
        this.allies = new FightersHelper(this, ai, fighter -> !fighter.equals(ai.fighter()) && fighter.team().equals(ai.fighter().team()));
    }

    /**
     * @return The spells helper
     */
    public SpellsHelper spells() {
        return spells;
    }

    /**
     * @return The cells helper
     */
    public CellsHelper cells() {
        return cells;
    }

    /**
     * @return Get the fighters helper for enemies
     */
    public FightersHelper enemies() {
        return enemies;
    }

    /**
     * @return Get the fighters helper for allies
     */
    public FightersHelper allies() {
        return allies;
    }

    /**
     * @return The current number of movement points
     */
    public int movementPoints() {
        return ai.turn().points().movementPoints();
    }

    /**
     * @return The current number of action points
     */
    public int actionPoints() {
        return ai.turn().points().actionPoints();
    }

    /**
     * Does the fighter has at least 1 AP
     *
     * @return true if has at least 1 AP
     */
    public boolean hasActionPoints() {
        return ai.turn().points().actionPoints() >= 1;
    }

    /**
     * Does the fighter has at least 1 MP
     *
     * @return true if has at least 1 MP
     */
    public boolean hasMovementPoints() {
        return ai.turn().points().movementPoints() >= 1;
    }

    /**
     * Check if the current fighter has movement points, and he's not blocked by enemies on adjacent cells
     */
    public boolean canMove() {
        return hasMovementPoints() && cells().adjacent().anyMatch(FightCell::walkable);
    }

    /**
     * Check if the current fighter can cast a spell
     * Return true only if the fighter has at least one available spell
     */
    public boolean canCast() {
        return hasActionPoints() && spells().hasAvailable();
    }

    /**
     * Simulate a movement to a given cell by changing the current cell of the fighter
     *
     * Note: this method will not modify the current fighter or AI,
     *   but will return a new helper instance with the configured position
     *
     * <pre>{@code
     * // Simulate all possible casts from the given position
     * Collection<CastSimulation> possibleCasts = ai.helper().withPosition(newCell).spells()
     *     .simulate(simulator)
     *     .collect(Collectors.toList())
     * ;
     * }</pre>
     *
     * @param cell The new cell
     *
     * @return The modified AI helper instance
     */
    public AIHelper withPosition(FightCell cell) {
        return new AIHelper(ai.withPosition(cell.id()));
    }
}
