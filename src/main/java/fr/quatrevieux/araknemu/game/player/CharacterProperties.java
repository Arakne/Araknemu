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

package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.game.player.characteristic.CharacterCharacteristics;
import fr.quatrevieux.araknemu.game.player.experience.GamePlayerExperience;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.world.creature.Life;

/**
 * Define properties and characteristics of the current character
 */
public interface CharacterProperties {
    /**
     * Get the player characteristics
     */
    public CharacterCharacteristics characteristics();

    /**
     * Get the player life
     */
    public Life life();

    /**
     * Get the player spells
     */
    public SpellBook spells();

    /**
     * Get the player level and experience
     */
    public GamePlayerExperience experience();

    /**
     * Current kamas quantity of the player
     *
     * @see fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory#kamas()
     */
    public long kamas();
}
