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
import fr.quatrevieux.araknemu.common.account.banishment.event.IpBanned;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.living.entity.BanIp;
import fr.quatrevieux.araknemu.data.living.repository.BanIpRepository;
import inet.ipaddr.IPAddressString;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Handle ban ip table
 */
final public class BanIpService<A extends LivingAccount> {
    final public class RuleBuilder {
        final private IPAddressString ipAddress;
        private String cause = "";
        private Duration duration = null;
        private A banisher = null;

        private RuleBuilder(IPAddressString ipAddress) {
            this.ipAddress = ipAddress;
        }

        /**
         * Define the ban cause
         */
        public RuleBuilder cause(String cause) {
            this.cause = cause;
            return this;
        }

        /**
         * Define the ban duration
         */
        public RuleBuilder duration(Duration duration) {
            this.duration = duration;
            return this;
        }

        /**
         * Define the rule creator
         */
        public RuleBuilder banisher(A banisher) {
            this.banisher = banisher;
            return this;
        }

        /**
         * Save and apply the ban ip rule
         *
         * @return The created rule
         */
        public BanIpRule<A> apply() {
            BanIp entity = repository.add(new BanIp(
                ipAddress,
                Instant.now(),
                duration == null ? null : Instant.now().plus(duration),
                cause,
                banisher == null ? -1 : banisher.id()
            ));

            banIps.add(entity);
            BanIpRule<A> rule = new BanIpRule<>(entity, banisher);

            dispatcher.dispatch(new IpBanned<>(rule));

            return rule;
        }
    }

    final private BanIpRepository repository;
    final private Dispatcher dispatcher;
    final private Function<int[], Map<Integer, A>> loadAccountsByIds;

    private Collection<BanIp> banIps = new CopyOnWriteArrayList<>();
    private Instant lastUpdate = Instant.EPOCH;

    public BanIpService(BanIpRepository repository, Dispatcher dispatcher) {
        this(repository, dispatcher, ids -> Collections.emptyMap());
    }

    public BanIpService(BanIpRepository repository, Dispatcher dispatcher, Function<int[], Map<Integer, A>> loadAccountsByIds) {
        this.repository = repository;
        this.dispatcher = dispatcher;
        this.loadAccountsByIds = loadAccountsByIds;
    }

    /**
     * Load (or reload) all the ban ip rules
     * Note: news rules will not trigger a {@link IpBanned} event, it will only overrides current rules
     */
    public void load() {
        lastUpdate = Instant.now();
        banIps = repository.available();
    }

    /**
     * Create a new rule
     *
     * @param ipAddress The IP to ban
     *
     * @return The rule builder
     */
    public RuleBuilder newRule(IPAddressString ipAddress) {
        return new RuleBuilder(ipAddress);
    }

    /**
     * Check if an IP address is banned
     *
     * @param ipAddress IP to check
     *
     * @return true if banned
     */
    public boolean isIpBanned(IPAddressString ipAddress) {
        return findFirstEntry(ipAddress).isPresent();
    }

    /**
     * Get all active ban ip rules
     */
    public Collection<BanIpRule<A>> rules() {
        return rules(banIps);
    }

    /**
     * Get the first matching rule for the given IP address
     *
     * @param ipAddress IP address to check
     *
     * @return The rule, or empty optional if not found
     */
    public Optional<BanIpRule<A>> matching(IPAddressString ipAddress) {
        return findFirstEntry(ipAddress).map(banIp -> new BanIpRule<>(
            banIp,
            loadAccountsByIds.apply(new int[] {banIp.banisherId()}).get(banIp.banisherId())
        ));
    }

    /**
     * Disable all active rules on the given IP address
     *
     * @param ipAddress IP address to disable
     */
    public void disable(IPAddressString ipAddress) {
        repository.disable(ipAddress);
        banIps = banIps.stream().filter(banIp -> !banIp.ipAddress().equals(ipAddress)).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }

    /**
     * Refresh the ban ip table
     * News rules will dispatch a {@link IpBanned} event
     */
    public void refresh() {
        final Instant updateTime = Instant.now();
        final Map<Integer, BanIp> refreshed = banIps.stream().filter(BanIp::active).collect(Collectors.toMap(
            BanIp::id,
            Function.identity()
        ));
        final Collection<BanIp> added = new ArrayList<>();

        repository.updated(lastUpdate).forEach(banIp -> {
            if (banIp.active()) {
                // A new IP is banned
                if (!refreshed.containsKey(banIp.id()) || !refreshed.get(banIp.id()).active()) {
                    added.add(banIp);
                }

                refreshed.put(banIp.id(), banIp);
            } else {
                refreshed.remove(banIp.id());
            }
        });

        banIps = new CopyOnWriteArrayList<>(refreshed.values());
        rules(added).stream().map(IpBanned::new).forEach(dispatcher::dispatch);
        lastUpdate = updateTime;
    }

    /**
     * Find the first matching ban ip entry
     */
    private Optional<BanIp> findFirstEntry(IPAddressString ipAddress) {
        return banIps.stream()
            .filter(BanIp::active)
            .filter(banIp -> banIp.ipAddress().contains(ipAddress))
            .findFirst()
        ;
    }

    /**
     * Convert ban ip entities to BanIpRule
     */
    private Collection<BanIpRule<A>> rules(Collection<BanIp> entities) {
        final Map<Integer, A> accounts = loadAccountsByIds.apply(entities.stream()
            .filter(BanIp::active)
            .mapToInt(BanIp::banisherId)
            .distinct()
            .toArray()
        );

        return entities.stream()
            .filter(BanIp::active)
            .map(banIp -> new BanIpRule<>(banIp, accounts.get(banIp.banisherId())))
            .collect(Collectors.toList())
        ;
    }
}
