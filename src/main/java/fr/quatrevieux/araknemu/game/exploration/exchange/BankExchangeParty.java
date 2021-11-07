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

package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.account.bank.Bank;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.listener.player.exchange.SendStoragePackets;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import fr.quatrevieux.araknemu.network.game.out.exchange.StorageList;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageMovementError;

/**
 * Exchange party for store items into the bank
 */
public final class BankExchangeParty implements ExchangeParty {
    private final ExplorationPlayer player;
    private final Bank bank;

    public BankExchangeParty(ExplorationPlayer player, Bank bank) {
        this.player = player;
        this.bank = bank;
    }

    @Override
    public ExchangeType type() {
        return ExchangeType.BANK;
    }

    @Override
    public Creature actor() {
        return player;
    }

    @Override
    public Creature target() {
        // Bank do not have target
        return null;
    }

    @Override
    public ExchangeInteraction dialog() {
        return new ExchangeDialog(this);
    }

    @Override
    public void initialize() {
        bank.dispatcher().register(new SendStoragePackets(this));
        send(new StorageList(bank));
    }

    @Override
    public void leave() {
        bank.save();
        player.interactions().remove();
        send(ExchangeLeaved.accepted());
    }

    @Override
    public void toggleAccept() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void kamas(long quantity) {
        try {
            if (quantity > 0) {
                addKamasToBank(quantity);
            } else {
                removeKamasFromBank(-quantity);
            }
        } catch (IllegalArgumentException e) {
            send(new StorageMovementError());
        }
    }

    @Override
    public void item(int itemEntryId, int quantity) {
        try {
            if (quantity > 0) {
                addItemToBank(player.inventory().get(itemEntryId), quantity);
            } else {
                removeItemFromBank(bank.get(itemEntryId), -quantity);
            }
        } catch (InventoryException e) {
            send(new StorageMovementError());
        }
    }

    @Override
    public void send(Object packet) {
        player.send(packet);
    }

    /**
     * Remove kamas from inventory and store into bank
     */
    private void addKamasToBank(long quantity) {
        quantity = Math.min(quantity, player.inventory().kamas());

        player.inventory().removeKamas(quantity);
        bank.addKamas(quantity);
    }

    /**
     * Retrieve kamas from the bank, and add into inventory
     */
    private void removeKamasFromBank(long quantity) {
        quantity = Math.min(quantity, bank.kamas());

        bank.removeKamas(quantity);
        player.inventory().addKamas(quantity);
    }

    /**
     * Add an inventory item to the bank
     *
     * @param entry The player's inventory item
     * @param quantity The quantity to add
     */
    private void addItemToBank(ItemEntry entry, int quantity) {
        if (!entry.isDefaultPosition()) {
            throw new InventoryException("The item should be in default position");
        }

        quantity = Math.min(entry.quantity(), quantity);

        entry.remove(quantity);
        bank.add(entry.item(), quantity);
    }

    /**
     * Remove an item from bank and add into player's inventory
     *
     * @param entry The bank item to move
     * @param quantity The quantity to move
     */
    private void removeItemFromBank(ItemEntry entry, int quantity) {
        quantity = Math.min(entry.quantity(), quantity);

        entry.remove(quantity);
        player.inventory().add(entry.item(), quantity);
    }
}
