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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.spell;

import fr.arakne.utils.encoding.Base64;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;

/**
 * Update the spell data after upgrade
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Spells.as#L31
 */
final public class UpdateSpell {
    final private SpellBookEntry entry;

    public UpdateSpell(SpellBookEntry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "SUK" + entry.spell().id() + "~" + entry.spell().level() + "~" + Base64.chr(entry.position());
    }
}
