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

package fr.quatrevieux.araknemu.realm.host;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.value.ServerCharacters;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.host.event.HostsUpdated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handle game server hosts
 */
public final class HostService {
    private final PlayerRepository playerRepository;
    private final Dispatcher dispatcher;

    private final ConcurrentMap<Integer, GameHost> hosts = new ConcurrentHashMap<>();

    public HostService(PlayerRepository playerRepository, Dispatcher dispatcher) {
        this.playerRepository = playerRepository;
        this.dispatcher = dispatcher;
    }

    /**
     * Get all declared hosts
     */
    public Collection<GameHost> all() {
        return hosts.values();
    }

    /**
     * Declare an host
     */
    public void declare(GameHost host) {
        hosts.put(host.id(), host);
        dispatcher.dispatch(new HostsUpdated(hosts.values()));
    }

    /**
     * Update the host status
     *
     * @param id Host race
     * @param state Host state
     * @param canLog Can log into server
     */
    public void updateHost(int id, GameHost.State state, boolean canLog) {
        final GameHost host = hosts.get(id);

        if (host == null) {
            throw new NoSuchElementException("Host " + id + " is not found");
        }

        host.setState(state);
        host.setCanLog(canLog);
        dispatcher.dispatch(new HostsUpdated(hosts.values()));
    }

    /**
     * Check if the requested host is available for login
     * @param id The host race
     */
    public boolean isAvailable(int id) {
        final GameHost host = hosts.get(id);

        return host != null && host.canLog();
    }

    /**
     * Get a game host
     * @param id The host race
     */
    public GameHost get(int id) {
        final GameHost host = hosts.get(id);

        if (host == null) {
            throw new NoSuchElementException("Host " + id + " is not found");
        }

        return host;
    }

    /**
     * Check if the account is logged into one of the game servers
     *
     * @param account Account to check
     * @param response The response callback
     */
    public void checkLogin(AuthenticationAccount account, GameConnector.HostResponse<Boolean> response) {
        if (hosts.isEmpty()) {
            response.response(false);
            return;
        }

        new Runnable() {
            private final Set<GameHost> pending = Collections.synchronizedSet(new HashSet<>(all()));
            private boolean result = false;

            @Override
            public void run() {
                for (GameHost host : new ArrayList<>(pending)) {
                    host.connector().checkLogin(account, r -> {
                        result |= r;

                        pending.remove(host);

                        if (pending.isEmpty()) {
                            response.response(result);
                        }
                    });
                }
            }
        }.run();
    }

    /**
     * Get characters count for each hosts
     */
    public Collection<ServerCharacters> charactersByHost(AuthenticationAccount account) {
        return playerRepository.accountCharactersCount(
            account.id()
        );
    }

    /**
     * Search for the friend servers
     *
     * @param pseudo The friend account pseudo
     */
    public Collection<ServerCharacters> searchFriendServers(String pseudo) {
        return playerRepository.serverCharactersCountByAccountPseudo(pseudo);
    }
}
