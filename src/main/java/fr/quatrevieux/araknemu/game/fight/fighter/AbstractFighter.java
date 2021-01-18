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
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterInitialized;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Base class for implements a fighter
 * Provide commons attributes and methods
 */
abstract public class AbstractFighter implements Fighter {
    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();
    final private BuffList buffs = new BuffList(this);
    final private States states = new FighterStates(this);
    final private Map<Object, Object> attachments = new HashMap<>();

    // Mutable attributes
    private FightCell cell;
    private Fight fight;
    private FightTurn turn;
    private Direction orientation = Direction.SOUTH_EAST;
    private Optional<Fighter> invoker = Optional.empty();

    @Override
    public void init() {
        fight.dispatch(new FighterInitialized(this));
    }

    @Override
    final public FightCell cell() {
        return cell;
    }

    @Override
    final public Direction orientation() {
        return orientation;
    }

    @Override
    final public void setOrientation(Direction orientation) {
        this.orientation = orientation;
    }

    @Override
    final public void move(FightCell cell) {
        if (this.cell != null) {
            this.cell.removeFighter();
        }

        if (cell != null) {
            cell.set(this);
        }

        this.cell = cell;
    }

    @Override
    public void dispatch(Object event) {
        dispatcher.dispatch(event);
    }

    final public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    @Override
    final public BuffList buffs() {
        return buffs;
    }

    @Override
    final public States states() {
        return states;
    }

    @Override
    final public Fight fight() {
        return fight;
    }

    @Override
    final public void joinFight(Fight fight, FightCell startCell) {
        if (this.fight != null) {
            throw new IllegalStateException("A fight is already defined");
        }

        this.fight = fight;
        this.cell = startCell;
        startCell.set(this);
    }

    @Override
    public void play(FightTurn turn) {
        this.turn = turn;
    }

    @Override
    public void stop() {
        turn = null;
    }

    /**
     * Get the current fighter turn
     */
    final public FightTurn turn() {
        if (turn == null) {
            throw new FightException("It's not your turn");
        }

        return turn;
    }

    @Override
    final public void attach(Object key, Object value) {
        attachments.put(key, value);
    }

    @Override
    final public Object attachment(Object key) {
        return attachments.get(key);
    }

    @Override
    final public boolean isOnFight() {
        return fight != null && cell != null;
    }

    @Override
    final public boolean equals(Object obj) {
        return obj != null && getClass().equals(obj.getClass()) && id() == ((Fighter) obj).id();
    }

    @Override
    final public int hashCode() {
        return id();
    }

    /**
     * Clear fighter data
     */
    public void destroy() {
        this.fight = null;
        this.attachments.clear();
    }

    @Override
    public void addInvocation(Fighter fighter, FightCell cell) {
        fighter.joinFight(fight, cell);
        fighter.setInvoker(this);
        fight.turnList().currentFighter().team().join(fighter);
        fight.turnList().add(fighter);

        fighter.init();
    }

    @Override
    public Optional<Fighter> invoker() {
        return invoker;
    }

    @Override
    public void setInvoker(Fighter invoker) {
        this.invoker = Optional.of(invoker);
    }

}
