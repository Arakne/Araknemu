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

package fr.quatrevieux.araknemu.game.listener.player.exchange;

import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.bank.Bank;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendStoragePacketsTest extends GameBaseCase {
    private Bank bank;
    private ItemService itemService;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
            .use(AccountBank.class, BankItem.class)
        ;

        bank = container.get(BankService.class).load(explorationPlayer().account());
        bank.dispatcher().register(new SendStoragePackets(explorationPlayer()));

        itemService = container.get(ItemService.class);
    }

    @Test
    void onObjectAdded() {
        ItemEntry entry = bank.add(itemService.create(39));

        requestStack.assertLast(new StorageObject(entry));
    }

    @Test
    void onObjectQuantityChanged() {
        ItemEntry entry = bank.add(itemService.create(39));
        requestStack.clear();

        entry.add(3);

        requestStack.assertLast(new StorageObject(entry));
    }

    @Test
    void onObjectRemoved() {
        ItemEntry entry = bank.add(itemService.create(39));
        requestStack.clear();

        entry.remove(1);

        requestStack.assertLast(new StorageObject(entry));
    }

    @Test
    void onKamasChanged() {
        bank.addKamas(5000);

        requestStack.assertLast(new StorageKamas(5000));
    }
}
