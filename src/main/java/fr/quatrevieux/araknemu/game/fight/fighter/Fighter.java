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

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * Base fighter
 */
public interface Fighter extends Creature<FightCell>, Dispatcher {
    /**
     * Initialise the fighter when fight started
     */
    public void init();

    /**
     * Change the fighter orientation
     */
    public void setOrientation(Direction orientation);

    /**
     * Go to the given cell
     */
    public void move(FightCell cell);

    /**
     * Get the fighter life
     */
    public FighterLife life();

    /**
     * Get the fighter total characteristics
     */
    public FighterCharacteristics characteristics();

    /**
     * Get the fighter spells
     */
    public SpellList spells();

    /**
     * Get the weapon
     *
     * @throws fr.quatrevieux.araknemu.game.fight.exception.FightException When cannot get any weapon on the fighter
     */
    public CastableWeapon weapon();

    /**
     * Get the current buffs
     */
    public BuffList buffs();

    /**
     * Get the fighter states
     */
    public States states();

    /**
     * Attach an attribute to the fighter
     *
     * @param key The attachment key
     * @param value The attached value
     *
     * @see Fighter#attachment(Object) For get the attachment
     */
    public void attach(Object key, Object value);

    /**
     * Attach an object by its class
     *
     * @param value The attachment
     *
     * @see Fighter#attachment(Class) For get the attachment
     */
    default public void attach(Object value) {
        attach(value.getClass(), value);
    }

    /**
     * Get an attachment by its key
     *
     * @param key The attachment key
     *
     * @return The attached value
     *
     * @see Fighter#attach(Object, Object) For set the attachment
     */
    public Object attachment(Object key);

    /**
     * Get attachment by its class
     *
     * @param type The attachment class
     *
     * @return The attachment
     *
     * @see Fighter#attach(Object) Fir set the attachment
     */
    default public <T> T attachment(Class<T> type) {
        return type.cast(attachment((Object) type));
    }

    /**
     * Get the fighter level
     */
    public int level();

    /**
     * Get the fighter team
     */
    public FightTeam team();

    /**
     * Get the fight
     */
    public Fight fight();

    /**
     * Join the fight
     *
     * @param fight Fight to join
     * @param startCell The start cell
     */
    public void joinFight(Fight fight, FightCell startCell);

    /**
     * Check if the fighter is ready for fight
     */
    public boolean ready();

    /**
     * Check if the player is dead
     */
    default public boolean dead() {
        return life().dead();
    }

    /**
     * Start to play the turn
     *
     * @param turn The fighter turn
     */
    public void play(FightTurn turn);

    /**
     * Stop the turn
     */
    public void stop();

    /**
     * Check if the fighter is on the fight (The fight is set and is on a cell)
     */
    public boolean isOnFight();

    /**
     * Apply the operation on the fighter
     *
     * This method will call the corresponding method on the given operation object
     * Implements the visitor pattern
     *
     * @param <O> The operation type
     *
     * @return The given operation
     */
    public <O extends FighterOperation> O apply(O operation);

    /**
     * Check if the fighter is the team leader
     */
    default public boolean isTeamLeader() {
        return equals(team().leader());
    }
}
