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

package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.exchange;

import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.bank.Bank;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.StorageList;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenBankTest extends GameBaseCase {
    private OpenBank.Factory factory;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
            .use(AccountBank.class, BankItem.class)
        ;

        factory = new OpenBank.Factory(container.get(BankService.class));
        player = explorationPlayer();

        requestStack.clear();
    }

    @Test
    void factory() {
        assertInstanceOf(OpenBank.class, factory.create(new ResponseAction(1, "BANK", "")));
    }

    @Test
    void check() {
        assertTrue(factory.create(new ResponseAction(1, "BANK", "")).check(player));
    }

    @Test
    void applySuccess() {
        requestStack.clear();
        factory.create(new ResponseAction(1, "BANK", "")).apply(player);

        requestStack.assertAll(
            new ExchangeCreated(ExchangeType.BANK),
            new StorageList(container.get(BankService.class).load(player.account()))
        );

        assertTrue(player.interactions().busy());
        assertInstanceOf(ExchangeDialog.class, player.interactions().get(Interaction.class));
    }

    @Test
    void applyWithCostPayedByPlayer() {
        Bank bank = container.get(BankService.class).load(player.account());

        bank.add(container.get(ItemService.class).create(39));
        bank.add(container.get(ItemService.class).create(40));
        bank.add(container.get(ItemService.class).create(2422));

        requestStack.clear();
        factory.create(new ResponseAction(1, "BANK", "")).apply(player);

        requestStack.assertOne(Information.bankTaxPayed(3));
        assertTrue(player.interactions().busy());
        assertEquals(15222, player.inventory().kamas());
    }

    @Test
    void applyWithCostPayedByBank() {
        Bank bank = container.get(BankService.class).load(player.account());

        bank.addKamas(1000);

        bank.add(container.get(ItemService.class).create(39));
        bank.add(container.get(ItemService.class).create(40));
        bank.add(container.get(ItemService.class).create(2422));

        bank.save();

        player.inventory().removeKamas(15225);

        requestStack.clear();
        factory.create(new ResponseAction(1, "BANK", "")).apply(player);

        assertTrue(player.interactions().busy());
        assertEquals(0, player.inventory().kamas());

        player.interactions().stop();
        assertEquals(997, container.get(BankService.class).load(player.account()).kamas());
    }

    @Test
    void applyWithCostPayedByBothPlayerAndBank() {
        Bank bank = container.get(BankService.class).load(player.account());

        bank.addKamas(2);

        bank.add(container.get(ItemService.class).create(39));
        bank.add(container.get(ItemService.class).create(40));
        bank.add(container.get(ItemService.class).create(2422));

        bank.save();

        player.inventory().removeKamas(15223);

        requestStack.clear();
        factory.create(new ResponseAction(1, "BANK", "")).apply(player);

        assertTrue(player.interactions().busy());
        requestStack.assertOne(Information.bankTaxPayed(1));
        assertEquals(1, player.inventory().kamas());

        player.interactions().stop();
        assertEquals(0, container.get(BankService.class).load(player.account()).kamas());
    }

    @Test
    void applyNotEnoughKamas() {
        Bank bank = container.get(BankService.class).load(player.account());

        bank.add(container.get(ItemService.class).create(39));
        bank.add(container.get(ItemService.class).create(40));
        bank.add(container.get(ItemService.class).create(2422));

        bank.save();

        player.inventory().removeKamas(15223);

        requestStack.clear();
        factory.create(new ResponseAction(1, "BANK", "")).apply(player);

        assertFalse(player.interactions().busy());
        requestStack.assertOne(ServerMessage.notEnoughKamasForBank(3));
    }
}
