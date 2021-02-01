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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.module.util;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;

/**
 * Subscribe to events when fight is started, and remove the listener when stopped
 */
public final class ActiveFightSubscriber implements EventsSubscriber {
    private final Listener[] listeners;

    public ActiveFightSubscriber(Listener[] listeners) {
        this.listeners = listeners;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FightStarted>() {
                @Override
                public void on(FightStarted event) {
                    final ListenerAggregate dispatcher = event.fight().dispatcher();

                    for (Listener listener : listeners) {
                        dispatcher.add(listener);
                    }
                }

                @Override
                public Class<FightStarted> event() {
                    return FightStarted.class;
                }
            },

            new Listener<FightStopped>() {
                @Override
                public void on(FightStopped event) {
                    final ListenerAggregate dispatcher = event.fight().dispatcher();

                    for (Listener listener : listeners) {
                        dispatcher.remove(listener.getClass());
                    }
                }

                @Override
                public Class<FightStopped> event() {
                    return FightStopped.class;
                }
            },
        };
    }
}
