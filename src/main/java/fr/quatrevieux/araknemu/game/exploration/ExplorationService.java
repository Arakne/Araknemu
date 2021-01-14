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

package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.listener.map.SendSpriteRestrictions;
import fr.quatrevieux.araknemu.game.listener.player.InitializeGame;
import fr.quatrevieux.araknemu.game.listener.player.LifeRegeneration;
import fr.quatrevieux.araknemu.game.listener.player.exploration.RefreshExplorationRestrictions;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Base service for handle game exploration
 */
final public class ExplorationService implements EventsSubscriber {
    final private ExplorationMapService mapService;
    final private Dispatcher dispatcher;

    public ExplorationService(ExplorationMapService mapService, Dispatcher dispatcher) {
        this.mapService = mapService;
        this.dispatcher = dispatcher;
    }

    /**
     * Start exploration for a player
     */
    public ExplorationPlayer create(GamePlayer player) {
        ExplorationPlayer exploration = new ExplorationPlayer(player);

        exploration.dispatcher().add(new InitializeGame(exploration, mapService));
        exploration.dispatcher().add(new RefreshExplorationRestrictions(exploration));
        exploration.dispatcher().register(new LifeRegeneration());

        dispatcher.dispatch(new ExplorationPlayerCreated(exploration));

        return exploration;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<MapLoaded>() {
                @Override
                public void on(MapLoaded event) {
                    event.map().dispatcher().add(new SendSpriteRestrictions(event.map()));
                }

                @Override
                public Class<MapLoaded> event() {
                    return MapLoaded.class;
                }
            }
        };
    }
}
