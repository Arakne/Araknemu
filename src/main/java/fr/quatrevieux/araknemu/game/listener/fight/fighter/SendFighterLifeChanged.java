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
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterLifeChanged;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Send the damage or heal
 */
public final class SendFighterLifeChanged implements Listener<FighterLifeChanged> {
    private final Fight fight;

    public SendFighterLifeChanged(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterLifeChanged event) {
        fight.send(ActionEffect.alterLifePoints(event.caster(), event.fighter(), event.value()));
    }

    @Override
    public Class<FighterLifeChanged> event() {
        return FighterLifeChanged.class;
    }
}
