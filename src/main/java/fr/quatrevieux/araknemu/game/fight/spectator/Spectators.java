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

package fr.quatrevieux.araknemu.game.fight.spectator;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;
import fr.quatrevieux.araknemu.game.fight.spectator.event.SpectatorJoined;
import fr.quatrevieux.araknemu.game.fight.spectator.event.SpectatorLeaved;
import fr.quatrevieux.araknemu.game.listener.fight.spectator.SendSpectatorHasJoined;
import fr.quatrevieux.araknemu.game.world.util.Sender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Handle spectators of a fight
 */
public final class Spectators implements Sender, EventsSubscriber, Dispatcher {
    private final Fight fight;

    private final Set<Spectator> spectators = new HashSet<>();

    public Spectators(Fight fight) {
        this.fight = fight;
        fight.dispatcher().register(this);
    }

    @Override
    public void dispatch(Object event) {
        spectators.forEach(spectator -> spectator.dispatch(event));
    }

    @Override
    public void send(Object packet) {
        spectators.forEach(spectator -> spectator.send(packet));
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FightStopped>() {
                @Override
                public void on(FightStopped event) {
                    spectators.forEach(Spectator::stop);
                }

                @Override
                public Class<FightStopped> event() {
                    return FightStopped.class;
                }
            },
            new Listener<FightCancelled>() {
                @Override
                public void on(FightCancelled event) {
                    // Copy spectators because Spectator::leave will remove the spectator from list
                    new ArrayList<>(spectators).forEach(Spectator::leave);
                }

                @Override
                public Class<FightCancelled> event() {
                    return FightCancelled.class;
                }
            },
            new SendSpectatorHasJoined(fight),
        };
    }

    /**
     * Add a spectator to the fight
     * Will dispatch a {@link SpectatorJoined}
     *
     * @throws IllegalStateException When the spectator is already added
     */
    public void add(Spectator spectator) {
        if (!spectators.add(spectator)) {
            throw new IllegalStateException("Spectator " + spectator.name() + " is already added");
        }

        fight.dispatch(new SpectatorJoined(spectator));
    }

    /**
     * Remove a spectator from the fight
     * Will dispatch a {@link SpectatorLeaved}
     *
     * @throws IllegalStateException When the spectator is not in present
     */
    public void remove(Spectator spectator) {
        if (!spectators.remove(spectator)) {
            throw new IllegalStateException("Spectator " + spectator.name() + " is not present");
        }

        fight.dispatch(new SpectatorLeaved(spectator));
    }

    /**
     * Remove all spectators
     * Should be use only by {@link Fight#destroy()}
     */
    public void clear() {
        spectators.clear();
    }
}
