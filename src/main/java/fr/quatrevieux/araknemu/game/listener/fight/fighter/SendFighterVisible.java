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
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterVisible;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Send to client packets for make fighter visible
 *
 * The fighter position will be sent before to ensure that
 * the fighter will be shown at correct position
 *
 * @see ActionEffect#fighterVisible(PassiveFighter, PassiveFighter)
 */
public final class SendFighterVisible implements Listener<FighterVisible> {
    private final Fight fight;

    public SendFighterVisible(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterVisible event) {
        final PassiveFighter target = event.fighter();

        fight.send(new FighterPositions(target));
        fight.send(ActionEffect.fighterVisible(event.caster(), target));
    }

    @Override
    public Class<FighterVisible> event() {
        return FighterVisible.class;
    }
}
