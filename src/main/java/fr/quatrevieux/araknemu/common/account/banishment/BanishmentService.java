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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.common.account.banishment;

import fr.quatrevieux.araknemu.common.account.LivingAccount;
import fr.quatrevieux.araknemu.common.account.banishment.event.AccountBanned;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;
import fr.quatrevieux.araknemu.data.living.repository.account.BanishmentRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handle banishment
 */
public final class BanishmentService<A extends LivingAccount> {
    private final BanishmentRepository repository;
    private final Dispatcher dispatcher;
    private final Function<int[], Map<Integer, A>> loadAccountsByIds;

    public BanishmentService(BanishmentRepository repository, Dispatcher dispatcher) {
        this(repository, dispatcher, ids -> Collections.emptyMap());
    }

    public BanishmentService(BanishmentRepository repository, Dispatcher dispatcher, Function<int[], Map<Integer, A>> loadAccountsByIds) {
        this.repository = repository;
        this.dispatcher = dispatcher;
        this.loadAccountsByIds = loadAccountsByIds;
    }

    /**
     * Check if the given account is banned
     *
     * @param account Account to check
     *
     * @return true if banned
     */
    public boolean isBanned(A account) {
        return !account.isMaster() && repository.isBanned(account.id());
    }

    /**
     * Ban an account
     *
     * @param account Account to ban
     * @param duration The banishment duration
     * @param cause The ban cause message
     * @param banisher The account of the banisher
     *
     * @return The created entry
     */
    public BanEntry<A> ban(A account, Duration duration, String cause, A banisher) {
        final Banishment banishment = addBanEntry(account, duration, cause, banisher.id());
        final BanEntry<A> entry = new BanEntry<>(account, banishment, banisher);

        dispatcher.dispatch(new AccountBanned<>(entry));

        return entry;
    }

    /**
     * Ban an account without banisher
     *
     * @param account Account to ban
     * @param duration The banishment duration
     * @param cause The ban cause message
     *
     * @return The created entry
     */
    public BanEntry<A> ban(A account, Duration duration, String cause) {
        final Banishment banishment = addBanEntry(account, duration, cause, -1);
        final BanEntry<A> entry = new BanEntry<>(account, banishment);

        dispatcher.dispatch(new AccountBanned<>(entry));

        return entry;
    }

    /**
     * List all banishment entries for the given account
     *
     * @param account The account to find
     *
     * @return List of banishment entries
     */
    public List<BanEntry<A>> list(A account) {
        final List<Banishment> entities = repository.forAccount(account.id());
        final Map<Integer, A> accounts = loadAccountsByIds.apply(entities.stream()
            .mapToInt(Banishment::banisherId)
            .filter(id -> id != -1)
            .distinct()
            .toArray()
        );

        return entities.stream()
            .map(entity -> new BanEntry<>(account, entity, accounts.get(entity.banisherId())))
            .collect(Collectors.toList())
        ;
    }

    /**
     * Remove all active banishment
     *
     * @param account Account to unban
     */
    public void unban(A account) {
        repository.removeActive(account.id());
    }

    /**
     * Insert the ban banishment to database
     */
    private Banishment addBanEntry(A account, Duration duration, String cause, int banisherId) {
        final Instant now = Instant.now();

        return repository.add(new Banishment(account.id(), now, now.plus(duration), cause, banisherId));
    }
}
