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
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;

/**
 * Send to fight the removed fighter
 */
public final class SendFighterRemoved implements Listener<FighterRemoved> {
    private final Fight fight;

    public SendFighterRemoved(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterRemoved event) {
        fight.send(new RemoveSprite(event.fighter().sprite()));
    }

    @Override
    public Class<FighterRemoved> event() {
        return FighterRemoved.class;
    }
}
