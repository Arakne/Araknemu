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

package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;

/**
 * Extends the base sprite for fighters, allowing temporary alterations
 */
public interface FighterSprite extends Sprite {
    /**
     * Duplicate the sprite with the given fighter
     * The returned sprite should be same as current one, but with new fighter cell, orientation, and characteristics.
     *
     * @param fighter The new fighter
     *
     * @return The new sprite instance
     */
    public FighterSprite withFighter(Fighter fighter);
}
