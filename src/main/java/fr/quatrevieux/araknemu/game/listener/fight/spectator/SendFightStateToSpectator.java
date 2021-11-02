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

package fr.quatrevieux.araknemu.game.listener.fight.spectator;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.fight.spectator.event.StartWatchFight;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFightAsSpectator;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;

import java.util.stream.Collectors;

/**
 * Send join fight and current state packets to the new spectator
 */
public final class SendFightStateToSpectator implements Listener<StartWatchFight> {
    private final Spectator spectator;

    public SendFightStateToSpectator(Spectator spectator) {
        this.spectator = spectator;
    }

    @Override
    public void on(StartWatchFight event) {
        final Fight fight = spectator.fight();

        spectator.send(new JoinFightAsSpectator(fight));
        spectator.send(
            new AddSprites(
                fight.fighters()
                    .stream()
                    .map(Fighter::sprite)
                    .collect(Collectors.toList())
            )
        );

        spectator.send(new BeginFight());
        spectator.send(new FighterTurnOrder(fight.turnList()));
        fight.turnList().current().map(StartTurn::new).ifPresent(spectator::send);
    }

    @Override
    public Class<StartWatchFight> event() {
        return StartWatchFight.class;
    }
}
