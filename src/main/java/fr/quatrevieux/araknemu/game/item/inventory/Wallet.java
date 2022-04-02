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

package fr.quatrevieux.araknemu.game.item.inventory;

import fr.quatrevieux.araknemu.game.item.inventory.event.KamasChanged;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Store kamas
 */
public interface Wallet {
    /**
     * Get the current amount of kamas on the inventory
     *
     * @return The amount of kamas. Always positive
     */
    @Pure
    public @NonNegative long kamas();

    /**
     * Add kamas to the inventory
     * Will dispatch event {@link KamasChanged}
     *
     * @param quantity Quantity of kamas to add. Must be positive
     *
     * @throws IllegalArgumentException When a null or negative quantity is given
     */
    public void addKamas(@Positive long quantity);

    /**
     * Remove kamas from the inventory
     * Will dispatch event {@link KamasChanged}
     *
     * @param quantity Quantity of kamas to remove. Must be positive (and not zero)
     *
     * @throws IllegalArgumentException When an invalid quantity is given
     */
    public void removeKamas(@Positive long quantity);
}
