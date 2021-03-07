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

package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFight;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Send fight joined packets
 */
public final class SendFightJoined implements Listener<FightJoined> {
    private final PlayerFighter fighter;

    public SendFightJoined(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(FightJoined event) {
        fighter.send(new JoinFight(event.fight()));

        fighter.send(
            new AddSprites(
                event.fight().fighters()
                    .stream()
                    .map(Fighter::sprite)
                    .collect(Collectors.toList())
            )
        );

        fighter.send(
            new FightStartPositions(
                new List[] {
                    event.fight().team(0).startPlaces(),
                    event.fight().team(1).startPlaces(),
                },
                fighter.team().number()
            )
        );
    }

    @Override
    public Class<FightJoined> event() {
        return FightJoined.class;
    }
}
