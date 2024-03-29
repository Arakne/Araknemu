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
 * Copyright (c) 2017-2021 Vincent Quatrevieux, Jean-Alexandre Valentin
 */

package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.exploration.event.StartExploration;
import fr.quatrevieux.araknemu.game.exploration.event.StopExploration;
import fr.quatrevieux.araknemu.network.game.out.info.StartLifeTimer;
import fr.quatrevieux.araknemu.network.game.out.info.StopLifeTimer;

/**
 * This class handles a Player life regeneration
 */
public final class LifeRegeneration implements EventsSubscriber {
    private final GameConfiguration.PlayerConfiguration configuration;

    public LifeRegeneration(GameConfiguration.PlayerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<StartExploration>() {
                @Override
                public void on(StartExploration event) {
                    final int rate = configuration.baseLifeRegeneration();

                    if (rate > 0) {
                        event.player().player().properties().life().startLifeRegeneration(rate);
                        event.player().send(new StartLifeTimer(rate));
                    }
                }

                @Override
                public Class<StartExploration> event() {
                    return StartExploration.class;
                }
            },

            new Listener<StopExploration>() {
                @Override
                public void on(StopExploration event) {
                    event.player().player().properties().life().stopLifeRegeneration();
                    event.session().send(new StopLifeTimer());
                }

                @Override
                public Class<StopExploration> event() {
                    return StopExploration.class;
                }
            },
        };
    }
}
