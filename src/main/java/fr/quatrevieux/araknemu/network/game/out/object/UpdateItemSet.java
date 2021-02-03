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

package fr.quatrevieux.araknemu.network.game.out.object;

import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.player.inventory.itemset.PlayerItemSet;

import java.util.stream.Collectors;

/**
 * Send item set data to the client
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L230
 */
public final class UpdateItemSet {
    private static final ItemEffectsTransformer EFFECTS_TRANSFORMER = new ItemEffectsTransformer();

    private final PlayerItemSet itemSet;

    public UpdateItemSet(PlayerItemSet itemSet) {
        this.itemSet = itemSet;
    }

    @Override
    public String toString() {
        if (itemSet.isEmpty()) {
            return "OS-" + itemSet.id();
        }

        return
            "OS+" + itemSet.id() + "|" +
            itemSet.items().stream().map(item -> Integer.toString(item.id())).collect(Collectors.joining(";")) + "|" +
            EFFECTS_TRANSFORMER.serialize(itemSet.bonus().effects())
       ;
    }
}
