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

import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Cell for a fight map
 * This type should be used only for actual fight and not AI system
 * Mutation operation of fight cell must be declared here instead of {@link BattlefieldCell}
 */
public interface FightCell extends BattlefieldCell {
    @Override
    public FightMap map();

    public @Nullable Fighter fighter();

    /**
     * Set a fighter on this cell
     */
    public void set(Fighter fighter);

    /**
     * Remove the fighter on the cell
     *
     * @throws FightMapException if there is no fighter on the cell
     */
    public void removeFighter();

    /**
     * Remove the fighter on the cell if it's the given fighter
     * If the cell has no fighter, or if the fighter is not the given fighter, this method will do nothing
     *
     * So unlike {@link #removeFighter()}, this method will not throw any exception
     *
     * @param fighter The fighter to remove
     */
    public void removeFighter(Fighter fighter);
}
