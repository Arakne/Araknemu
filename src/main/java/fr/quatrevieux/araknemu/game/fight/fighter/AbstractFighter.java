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
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterHidden;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterInitialized;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMoved;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterVisible;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for implements a fighter
 * Provide commons attributes and methods
 */
public abstract class AbstractFighter implements Fighter {
    private final ListenerAggregate dispatcher = new DefaultListenerAggregate();
    @SuppressWarnings({"assignment", "argument"})
    private final BuffList buffs = new BuffList(this);
    @SuppressWarnings({"assignment", "argument"})
    private final States states = new FighterStates(this);
    private final Map<Object, Object> attachments = new HashMap<>();

    // Mutable attributes
    private @Nullable FightCell cell;
    private @MonotonicNonNull Fight fight;
    private Direction orientation = Direction.SOUTH_EAST;
    private boolean hidden = false;
    private @Nullable Fighter invoker = null;

    @Override
    public void init() {
        if (fight == null) {
            throw new IllegalStateException("The fighter should join the fight before initialisation");
        }

        fight.dispatch(new FighterInitialized(this));
    }

    @Override
    public final FightCell cell() {
        if (cell == null) {
            throw new IllegalStateException("The fighter is not on a cell");
        }

        return cell;
    }

    @Override
    public final Direction orientation() {
        return orientation;
    }

    @Override
    public final void setOrientation(Direction orientation) {
        this.orientation = orientation;
    }

    @Override
    public final void move(@Nullable FightCell cell) {
        if (this.cell != null) {
            this.cell.removeFighter();
        }

        if (cell != null) {
            cell.set(this);
        }

        this.cell = cell;

        if (cell != null && fight != null) {
            fight.dispatch(new FighterMoved(this, cell));
        }
    }

    @Override
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    public final ListenerAggregate dispatcher() {
        return dispatcher;
    }

    @Override
    public final BuffList buffs() {
        return buffs;
    }

    @Override
    public final States states() {
        return states;
    }

    @Override
    public final Fight fight() {
        if (fight == null) {
            throw new IllegalStateException("The fighter has not join a fight");
        }

        return fight;
    }

    @Override
    public final void joinFight(Fight fight, FightCell startCell) {
        if (this.fight != null) {
            throw new IllegalStateException("A fight is already defined");
        }

        this.fight = fight;
        this.cell = startCell;
        startCell.set(this);
    }

    @Override
    public final void attach(Object key, Object value) {
        attachments.put(key, value);
    }

    @Override
    public final @Nullable Object attachment(Object key) {
        return attachments.get(key);
    }

    @Override
    public final boolean isOnFight() {
        return fight != null;
    }

    @Override
    public boolean hidden() {
        return hidden;
    }

    @Override
    public void setHidden(Fighter caster, boolean hidden) {
        if (this.hidden == hidden) {
            return;
        }

        this.hidden = hidden;

        if (fight != null) {
            fight.dispatch(hidden ? new FighterHidden(this, caster) : new FighterVisible(this, caster));
        }
    }

    @Override
    public final void setInvoker(Fighter invoker) {
        this.invoker = invoker;
    }

    @Override
    public @Nullable FighterData invoker() {
        return invoker;
    }

    @Override
    public final boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof FighterData)) {
            return false;
        }

        return id() == ((FighterData) obj).id();
    }

    @Override
    public final int hashCode() {
        return id();
    }
}
