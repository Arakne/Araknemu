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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterHidden;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Send to client packet for make fighter invisible
 *
 * @see ActionEffect#fighterHidden(FighterData, FighterData)
 */
public final class SendFighterHidden implements Listener<FighterHidden> {
    private final Fight fight;

    public SendFighterHidden(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterHidden event) {
        fight.send(ActionEffect.fighterHidden(event.caster(), event.fighter()));
    }

    @Override
    public Class<FighterHidden> event() {
        return FighterHidden.class;
    }
}
