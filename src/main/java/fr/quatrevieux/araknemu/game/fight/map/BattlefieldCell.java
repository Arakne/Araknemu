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

package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Base cell type for a battlefield
 * This type is shared between actual fight and AI system
 * Mutation operation are not allowed on this interface, and must be added on a sub interface like {@link FightCell}
 */
public interface BattlefieldCell extends fr.arakne.utils.maps.BattlefieldCell<BattlefieldCell> {
    @Override
    public BattlefieldMap map();

    /**
     * Check if the cell is walkable, ignoring current fighter
     */
    public boolean walkableIgnoreFighter();

    /**
     * Get the fighter on the cell
     * Will return null if the cell has no fighter
     */
    public @Nullable FighterData fighter();

    /**
     * Check if the cell contains a fighter
     * This is equivalent to call {@code cell.fighter() != null}
     */
    public default boolean hasFighter() {
        return fighter() != null;
    }
}
