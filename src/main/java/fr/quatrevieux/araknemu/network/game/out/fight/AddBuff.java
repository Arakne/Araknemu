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

package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.spell.Spell;

/**
 * Add buff to fighter
 */
public final class AddBuff {
    private final Buff buff;

    public AddBuff(Buff buff) {
        this.buff = buff;
    }

    @Override
    public String toString() {
        return "GIE"
            + buff.effect().effect() + ";"
            + buff.target().id() + ";"
            + buff.effect().min() + ";"
            + (buff.effect().max() == 0 ? "" : buff.effect().max()) + ";"
            + buff.effect().special() + ";"
            + buff.effect().text() + ";"
            + buff.remainingTurns() + ";"
            + (buff.action() instanceof Spell ? ((Spell) buff.action()).id() : "")
        ;
    }
}
