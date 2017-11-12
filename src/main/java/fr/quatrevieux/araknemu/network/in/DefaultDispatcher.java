package fr.quatrevieux.araknemu.network.in;

import java.util.HashMap;
import java.util.Map;

/**
 * Base dispatcher
 * @param <S>
 */
final public class DefaultDispatcher<S> implements Dispatcher<S> {
    final private Map<Class, PacketHandler> handlers = new HashMap<>();

    public DefaultDispatcher(PacketHandler<S, ?>[] handlers) {
        for (PacketHandler<S, ?> handler : handlers) {
            this.handlers.put(handler.packet(), handler);
        }
    }

    @Override
    public void dispatch(S session, Packet packet) throws HandlerNotFoundException {
        if (!handlers.containsKey(packet.getClass())) {
            throw new HandlerNotFoundException(packet);
        }

        handlers.get(packet.getClass()).handle(session, packet);
    }
}
