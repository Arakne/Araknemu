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

package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.network.realm.out.BadVersion;
import fr.quatrevieux.araknemu.realm.RealmConfiguration;

/**
 * Check the client version
 */
final public class CheckDofusVersion implements PacketHandler<RealmSession, DofusVersion> {
    final private RealmConfiguration configuration;

    public CheckDofusVersion(RealmConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void handle(RealmSession session, DofusVersion packet) {
        if (packet.version().equals(configuration.clientVersion())) {
            return;
        }

        session.send(new BadVersion(configuration.clientVersion()));
        session.close();
    }

    @Override
    public Class<DofusVersion> packet() {
        return DofusVersion.class;
    }
}
