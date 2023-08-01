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
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.team.Team;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.creature.Life;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Fighter type which contains all necessary data for perform computation on fight
 * This type is read only : no modification can be performed following this interface
 */
public interface FighterData extends Creature<BattlefieldCell> {
    /**
     * Get the fighter id
     */
    public int id();

    /**
     * The fighter cell
     */
    public BattlefieldCell cell();

    /**
     * Get the fighter level
     */
    public @Positive int level();

    /**
     * Get the fighter life
     */
    public Life life();

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
    public Team<? extends FighterData> team();

    /**
     * Check the hidden state of the fighter
     * If true, cell must not be sent to other fighters, movement actions must be hidden
     * and AI should ignore fighter position
     */
    public boolean hidden();

    /**
     * Check if the player is dead
     */
    public boolean dead();

    /**
     * Get the invoker fighter
     * This value is null if the fighter is not an invocation
     */
    public @Nullable FighterData invoker();

    /**
     * Does the current fighter is an invocation ?
     * If this value is true, {@link #invoker()} will return a non null value
     *
     * Note: the fighter may have an invoker (i.e. {@link #invoker()} is not null) but this value is false.
     *       in this case, the fighter has not been invoked initially but has been summoned by another fighter (e.g. spiritual leash)
     */
    public boolean invoked();
}
