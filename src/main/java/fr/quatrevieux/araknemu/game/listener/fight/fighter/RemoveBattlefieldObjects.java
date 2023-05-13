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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;

/**
 * Remove all battlefield objects created by the fighter on its death
 */
public final class RemoveBattlefieldObjects implements Listener<FighterDie> {
    private final Fight fight;

    public RemoveBattlefieldObjects(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterDie event) {
        fight.map().removeObjectsIf(obj -> event.fighter().equals(obj.caster()));
    }

    @Override
    public Class<FighterDie> event() {
        return FighterDie.class;
    }
}
