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

package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.builder.FightBuilder;
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;

import java.util.function.Consumer;

/**
 * Handle fight creation
 */
final public class FightHandler<B extends FightBuilder> implements EventsSubscriber {
    final private FightService service;
    final private B builder;

    public FightHandler(FightService service, B builder) {
        this.service = service;
        this.builder = builder;
    }

    /**
     * Create and start the fight
     *
     * @param configuration The fight configuration
     */
    public Fight start(Consumer<B> configuration) {
        configuration.accept(builder);

        Fight fight = builder.build(service.newFightId());

        service.modules(fight).forEach(fight::register);
        fight.nextState();

        fight.dispatcher().register(this);

        service.created(fight);

        return fight;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<FightStopped>() {
                @Override
                public void on(FightStopped event) {
                    service.remove(event.fight());
                }

                @Override
                public Class<FightStopped> event() {
                    return FightStopped.class;
                }
            },
            new Listener<FightCancelled>() {
                @Override
                public void on(FightCancelled event) {
                    service.remove(event.fight());
                }

                @Override
                public Class<FightCancelled> event() {
                    return FightCancelled.class;
                }
            },
        };
    }
}
