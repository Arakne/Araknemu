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

package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.team.event.AllowJoinTeamChanged;
import fr.quatrevieux.araknemu.game.fight.team.event.AllowSpectatorChanged;
import fr.quatrevieux.araknemu.game.fight.team.event.NeedHelpChanged;
import fr.quatrevieux.araknemu.network.game.out.fight.FightOption;

/**
 * Send to the map the changed team fight option
 */
public final class SendTeamOptionChanged implements EventsSubscriber {
    private final ExplorationMap map;

    public SendTeamOptionChanged(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<AllowSpectatorChanged>() {
                @Override
                public void on(AllowSpectatorChanged event) {
                    map.send(FightOption.blockSpectators(event.options()));
                }

                @Override
                public Class<AllowSpectatorChanged> event() {
                    return AllowSpectatorChanged.class;
                }
            },
            new Listener<AllowJoinTeamChanged>() {
                @Override
                public void on(AllowJoinTeamChanged event) {
                    map.send(FightOption.blockJoiner(event.options()));
                }

                @Override
                public Class<AllowJoinTeamChanged> event() {
                    return AllowJoinTeamChanged.class;
                }
            },
            new Listener<NeedHelpChanged>() {
                @Override
                public void on(NeedHelpChanged event) {
                    map.send(FightOption.needHelp(event.options()));
                }

                @Override
                public Class<NeedHelpChanged> event() {
                    return NeedHelpChanged.class;
                }
            },
        };
    }
}
