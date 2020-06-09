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

package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.in.FriendSearch;
import fr.quatrevieux.araknemu.network.realm.out.FriendServerList;
import fr.quatrevieux.araknemu.realm.host.HostService;

/**
 * List the friend servers and character count
 */
final public class SearchFriend implements PacketHandler<RealmSession, FriendSearch> {
    final private HostService service;

    public SearchFriend(HostService service) {
        this.service = service;
    }

    @Override
    public void handle(RealmSession session, FriendSearch packet) {
        session.send(new FriendServerList(service.searchFriendServers(packet.pseudo())));
    }

    @Override
    public Class<FriendSearch> packet() {
        return FriendSearch.class;
    }
}
