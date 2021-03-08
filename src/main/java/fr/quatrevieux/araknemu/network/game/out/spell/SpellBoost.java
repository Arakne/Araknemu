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

package fr.quatrevieux.araknemu.network.game.out.spell;

import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

/**
 * Set the spell boost modifier
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Spells.as#L69
 */
public final class SpellBoost {
    private final int spellId;
    private final SpellsBoosts.Modifier modifier;
    private final int value;

    public SpellBoost(int spellId, SpellsBoosts.Modifier modifier, int value) {
        this.spellId = spellId;
        this.modifier = modifier;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SB" + modifier.effectId() + ";" + spellId + ";" + value;
    }
}
