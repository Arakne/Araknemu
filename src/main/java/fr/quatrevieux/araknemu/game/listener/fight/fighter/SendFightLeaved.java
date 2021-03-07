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

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.CancelFight;

/**
 * Send to the fighter that he has leave the fight
 */
public final class SendFightLeaved implements Listener<FightLeaved> {
    private final PlayerFighter fighter;

    public SendFightLeaved(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(FightLeaved event) {
        fighter.send(new CancelFight());
    }

    @Override
    public Class<FightLeaved> event() {
        return FightLeaved.class;
    }
}
