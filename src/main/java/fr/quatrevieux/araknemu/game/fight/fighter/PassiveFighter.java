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

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.Team;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * Fighter type which can be a target of spells or other actions
 */
public interface PassiveFighter extends Creature<FightCell> {
    /**
     * Get the fighter id
     */
    public int id();

    /**
     * The fighter cell
     */
    public FightCell cell();

    /**
     * Go to the given cell
     */
    public void move(FightCell cell);

    /**
     * Get the fighter level
     */
    public int level();

    /**
     * Get the fighter life
     */
    public FighterLife life();

    /**
     * Get the current buffs
     */
    public Buffs buffs();

    /**
     * Get the fighter states
     */
    public States states();

    /**
     * Get the fighter total characteristics
     */
    public FighterCharacteristics characteristics();

    /**
     * Get the fighter team
     */
    public Team<? extends PassiveFighter> team();

    /**
     * Check if the player is dead
     */
    public default boolean dead() {
        return life().dead();
    }
}
