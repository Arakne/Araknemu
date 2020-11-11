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

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.spell.SpellList;

public interface ActiveFighter extends PassiveFighter {
    /**
     * Get the fighter spells
     */
    public SpellList spells();

    /**
     * Get an attachment by its key
     *
     * @param key The attachment key
     *
     * @return The attached value
     *
     * @see Fighter#attach(Object, Object) For set the attachment
     */
    public Object attachment(Object key);

    /**
     * Get attachment by its class
     *
     * @param type The attachment class
     *
     * @return The attachment
     *
     * @see Fighter#attach(Object) Fir set the attachment
     */
    default public <T> T attachment(Class<T> type) {
        return type.cast(attachment((Object) type));
    }
}
