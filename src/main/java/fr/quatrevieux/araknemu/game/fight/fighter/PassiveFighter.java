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

import java.util.Optional;

import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.Team;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.Nullable;

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
    public void move(@Nullable FightCell cell);

    /**
     * Get the fighter level
     */
    public @Positive int level();

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
     * Check the hidden state of the fighter
     * If true, cell must not be sent to other fighters, movement actions must be hidden
     * and AI should ignore fighter position
     */
    public boolean hidden();

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
    public void setHidden(PassiveFighter caster, boolean hidden);

    /**
     * Check if the player is dead
     */
    public default boolean dead() {
        return life().dead();
    }

    public Optional<PassiveFighter> invoker();
}
