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

package fr.quatrevieux.araknemu.data.world.entity.monster;

/**
 * Store Pvm item drop by a monster
 */
public final class MonsterRewardItem {
    private final int monsterId;
    private final int itemTemplateId;
    private final int quantity;
    private final int discernment;
    private final double rate;

    public MonsterRewardItem(int monsterId, int itemTemplateId, int quantity, int discernment, double rate) {
        this.monsterId = monsterId;
        this.itemTemplateId = itemTemplateId;
        this.quantity = quantity;
        this.discernment = discernment;
        this.rate = rate;
    }

    /**
     * The monster template id (part of the primary key)
     *
     * @see MonsterTemplate#id()
     */
    public int monsterId() {
        return monsterId;
    }

    /**
     * The drop item id (part of the primary key)
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate#id()
     */
    public int itemTemplateId() {
        return itemTemplateId;
    }

    /**
     * Maximum item quantity dropped by the monster
     */
    public int quantity() {
        return quantity;
    }

    /**
     * Minimal discernment value for drop the item
     */
    public int discernment() {
        return discernment;
    }

    /**
     * The drop chance in percent
     *
     * The rate is a double value in interval ]0, 100], where :
     * - 100 means always dropped
     * - 0 means never dropped (cannot be reached)
     */
    public double rate() {
        return rate;
    }
}
