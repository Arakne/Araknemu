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

package fr.quatrevieux.araknemu.core.network.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Base dispatcher
 *
 * @param <S> The handled session type
 */
final public class DefaultDispatcher<S> implements Dispatcher<S> {
    final private Map<Class, PacketHandler> handlers = new HashMap<>();

    public DefaultDispatcher(PacketHandler<S, ?>[] handlers) {
        for (PacketHandler<S, ?> handler : handlers) {
            this.handlers.put(handler.packet(), handler);
        }
    }

    @Override
    public void dispatch(S session, Packet packet) throws Exception {
        if (!handlers.containsKey(packet.getClass())) {
            throw new HandlerNotFoundException(packet);
        }

        handlers.get(packet.getClass()).handle(session, packet);
    }
}
