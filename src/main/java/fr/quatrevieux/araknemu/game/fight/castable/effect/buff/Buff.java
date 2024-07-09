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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.buff;

import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Base type for buff applied to a fighter
 *
 * This interface is used by both AI and fight system,
 * and its intended to be overridden to use appropriate types (e.g. fighter type)
 */
public interface Buff {
    /**
     * Get the buff effect
     * For most cases, {@link SpellEffect#min()} is used as the buff value, and {@link SpellEffect#max()} set to 0
     */
    public SpellEffect effect();

    /**
     * Get the buff caster (i.e. the fighter who cast the spell that applied the buff)
     */
    public FighterData caster();

    /**
     * Get the buff target (i.e. the fighter who is affected by the buff)
     */
    public FighterData target();
}
