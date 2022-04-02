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

package fr.quatrevieux.araknemu.game.exploration.exchange.player;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangePartyProcessor;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import org.checkerframework.checker.index.qual.Positive;

import java.util.Map;

/**
 * Exchange processor for a player party
 */
public final class PlayerExchangePartyProcessor implements ExchangePartyProcessor {
    private final ExplorationPlayer player;
    private final PlayerExchangeStorage storage;

    public PlayerExchangePartyProcessor(ExplorationPlayer player, PlayerExchangeStorage storage) {
        this.player = player;
        this.storage = storage;
    }

    @Override
    public boolean validate() {
        boolean valid = true;

        if (storage.kamas() > player.inventory().kamas()) {
            storage.setKamas(player.inventory().kamas());

            valid = false;
        }

        for (Map.Entry<ItemEntry, @Positive Integer> entry : storage.items().entrySet()) {
            if (entry.getValue() > entry.getKey().quantity()) {
                storage.setItem(entry.getKey(), entry.getKey().quantity());

                valid = false;
            }
        }

        return valid;
    }

    @Override
    public void process(ExchangePartyProcessor distant) {
        final long kamas = storage.kamas();

        if (kamas > 0) {
            player.inventory().removeKamas(kamas);
            distant.addKamas(kamas);
        }

        storage.items().forEach((entry, quantity) -> {
            if (quantity > 0) {
                entry.remove(quantity);
                distant.addItem(entry.item(), quantity);
            }
        });
    }

    @Override
    public void terminate(boolean accepted) {
        player.interactions().remove();
        player.send(new ExchangeLeaved(accepted));
    }

    @Override
    public boolean accepted() {
        return storage.accepted();
    }

    @Override
    public void resetAccept() {
        storage.setAccepted(false);
    }

    @Override
    public void addKamas(@Positive long kamas) {
        player.inventory().addKamas(kamas);
    }

    @Override
    public void addItem(Item item, @Positive int quantity) {
        player.inventory().add(item, quantity);
    }
}
