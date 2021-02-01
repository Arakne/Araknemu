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

package fr.quatrevieux.araknemu.game.listener.player.exchange.bank;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.repository.account.BankItemRepository;
import fr.quatrevieux.araknemu.game.account.bank.BankEntry;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;

/**
 * Save the bank on changes
 */
public final class SaveBank implements EventsSubscriber {
    private final BankItemRepository repository;

    public SaveBank(BankItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<ObjectAdded>() {
                @Override
                public void on(ObjectAdded event) {
                    if (!(event.entry() instanceof BankEntry)) {
                        return;
                    }

                    final BankEntry entry = (BankEntry) event.entry();

                    repository.add(entry.entity());
                }

                @Override
                public Class<ObjectAdded> event() {
                    return ObjectAdded.class;
                }
            },

            new Listener<ObjectDeleted>() {
                @Override
                public void on(ObjectDeleted event) {
                    if (!(event.entry() instanceof BankEntry)) {
                        return;
                    }

                    final BankEntry entry = (BankEntry) event.entry();

                    repository.delete(entry.entity());
                }

                @Override
                public Class<ObjectDeleted> event() {
                    return ObjectDeleted.class;
                }
            },

            new Listener<ObjectQuantityChanged>() {
                @Override
                public void on(ObjectQuantityChanged event) {
                    if (!(event.entry() instanceof BankEntry)) {
                        return;
                    }

                    final BankEntry entry = (BankEntry) event.entry();

                    repository.update(entry.entity());
                }

                @Override
                public Class<ObjectQuantityChanged> event() {
                    return ObjectQuantityChanged.class;
                }
            },
        };
    }
}
