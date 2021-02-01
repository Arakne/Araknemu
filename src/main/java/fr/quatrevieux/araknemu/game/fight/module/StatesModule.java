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

package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.PushStateHandler;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.misc.RemoveStateHandler;
import fr.quatrevieux.araknemu.game.fight.module.util.ActiveFightSubscriber;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.RefreshStates;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendState;

/**
 * Module for handle fighter states
 */
public final class StatesModule implements FightModule {
    private final Fight fight;
    private final EventsSubscriber subscriber;

    public StatesModule(Fight fight) {
        this.fight = fight;
        this.subscriber = new ActiveFightSubscriber(new Listener[] {
            new RefreshStates(),
            new SendState(fight),
        });
    }

    @Override
    public void effects(EffectsHandler handler) {
        handler.register(950, new PushStateHandler(fight));
        handler.register(951, new RemoveStateHandler());
    }

    @Override
    public Listener[] listeners() {
        return subscriber.listeners();
    }
}
