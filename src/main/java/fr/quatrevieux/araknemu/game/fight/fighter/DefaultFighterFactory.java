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

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.fight.fighter.event.PlayerFighterCreated;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightJoined;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.ApplyEndFightReward;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.ApplyLeaveReward;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.LeaveOnDisconnect;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFightLeaved;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendStats;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.StopFightSession;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Default implementation of the fighter factory
 */
final public class DefaultFighterFactory implements FighterFactory {
    final private Dispatcher dispatcher;

    public DefaultFighterFactory(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public PlayerFighter create(GamePlayer player) {
        final PlayerFighter fighter = new PlayerFighter(player);

        fighter.dispatcher().add(new SendFightJoined(fighter));
        fighter.dispatcher().add(new ApplyEndFightReward(fighter));
        fighter.dispatcher().add(new StopFightSession(fighter));
        fighter.dispatcher().add(new SendFightLeaved(fighter));
        fighter.dispatcher().add(new LeaveOnDisconnect(fighter));
        fighter.dispatcher().add(new ApplyLeaveReward(fighter));
        fighter.dispatcher().add(new SendStats(fighter));

        dispatcher.dispatch(new PlayerFighterCreated(fighter));

        return fighter;
    }
}
