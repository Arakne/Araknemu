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
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.info.StartLifeTimer;
import fr.quatrevieux.araknemu.network.game.out.info.StopLifeTimer;


/**
 * This class handles a Player life regeneration
 */
final public class LifeRegeneration implements EventsSubscriber {
    final private GamePlayer player;

    public LifeRegeneration(GamePlayer player) {
        this.player = player;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[]{
            new Listener<ExplorationPlayerCreated>(){
                @Override
                public void on(ExplorationPlayerCreated event) {
                    player.properties().life().startLifeRegeneration(1000);
                    player.send(new StartLifeTimer(1000));
                }
                @Override
                public Class<ExplorationPlayerCreated> event() {
                    return ExplorationPlayerCreated.class;
                }
            },
            new Listener<FightJoined>(){
                @Override
                public void on(FightJoined event) {
                    player.properties().life().stopLifeRegeneration();
                    player.send(new StopLifeTimer());
                }
                @Override
                public Class<FightJoined> event() {
                    return FightJoined.class;
                }
            }
        };
    }
}
