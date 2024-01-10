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
import fr.quatrevieux.araknemu.game.fight.FighterSprite;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.castable.closeCombat.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

/**
 * Base fighter
 */
public interface Fighter extends Dispatcher, ActiveFighter {
    /**
     * Initialise the fighter when fight started
     */
    public void init();

    /**
     * Change the fighter orientation
     */
    public void setOrientation(Direction orientation);

    @Override
    public FightCell cell();

    @Override
    public FighterLife life();

    @Override
    public default boolean dead() {
        return life().dead();
    }

    /**
     * Change the fighter cell
     *
     * Unlike {@link #move(FightCell)}, this method will not trigger any event nor set the fighter on the new cell
     * Do not use this method if you don't know what you are doing. For move the fighter, use {@link #move(FightCell)}
     *
     * If the fighter is already on a cell, it will be removed from it
     */
    public void setCell(@Nullable FightCell cell);

    /**
     * Go to the given cell
     *
     * @see fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMoved Trigger when the fighter is actually moved
     */
    public void move(@Nullable FightCell cell);

    /**
     * Change the hidden state of the fighter
     * An event will be dispatched to the fight if effective
     *
     * Note: this method will do nothing if the fighter is already on the requested state
     *
     * @param caster Effect caster
     * @param hidden New hidden state
     *
     * @see fr.quatrevieux.araknemu.game.fight.fighter.event.FighterHidden Trigger when the fighter is actually hidden
     * @see fr.quatrevieux.araknemu.game.fight.fighter.event.FighterVisible Trigger when the fighter is actually visible
     */
    public void setHidden(Fighter caster, boolean hidden);

    /**
     * Get the close combat action
     * Usually, this method will return the {@link CastableWeapon} of the fighter
     *
     * @return The close combat action wrapped into an Optional, or empty Optional if the fighter can't do close combat
     */
    public Optional<Castable> closeCombat();

    /**
     * Does the current fighter can play ?
     * Return true if it's the turn of the current fighter
     *
     * Note: this method can return true only if the fighter is an instance of {@link PlayableFighter}
     *
     * @see PlayableFighter#turn() Can be called if this method return true
     */
    public boolean isPlaying();

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
     * Remove an attachment and get its last value
     *
     * @param key The attachment key
     *
     * @return The last attachment value, or null if not found
     */
    public @Nullable Object detach(Object key);

    /**
     * Remove an attachment by its type and get its last value
     *
     * @param type The attachment type
     *
     * @return The last attachment value, or null if not found
     *
     * @param <T> The attachment type
     */
    public default <T> @Nullable T detach(Class<T> type) {
        return type.cast(detach((Object) type));
    }

    /**
     * Attach an object by its class
     *
     * @param value The attachment
     *
     * @see Fighter#attachment(Class) For get the attachment
     */
    public default void attach(Object value) {
        attach(value.getClass(), value);
    }

    /**
     * Get the related fight
     */
    public Fight fight();

    @Override
    public FightTeam team();

    @Override
    public BuffList buffs();

    @Override
    public FighterSpellList spells();

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
     * Check if the fighter is on the fight (i.e. The fight is set)
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

    @Override
    public FighterSprite sprite();

    /**
     * Check if the fighter is the team leader
     */
    public default boolean isTeamLeader() {
        return equals(team().leader());
    }

    /**
     * Change the invoker of the fighter
     * So, {@code fighter.setInvoker(other); fighter.invoker() == other} will be true
     *
     * Note: the return value of {@link #invoked()} should not change after this method call
     *
     * @param invoker The new invoker
     *
     * @see #invoker() For get the invoker
     */
    public void setInvoker(Fighter invoker);
}
