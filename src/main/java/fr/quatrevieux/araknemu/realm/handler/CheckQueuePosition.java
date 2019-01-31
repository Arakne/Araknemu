package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.network.in.AskQueuePosition;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.out.QueuePosition;
import fr.quatrevieux.araknemu.network.realm.RealmSession;

/**
 * Check the queue position (Af packet)
 * @todo to implements
 *
 * Empty class to not fail for not found handler
 */
final public class CheckQueuePosition implements PacketHandler<RealmSession, AskQueuePosition> {
    @Override
    public void handle(RealmSession session, AskQueuePosition packet) {
        session.write(new QueuePosition(1));
    }

    @Override
    public Class<AskQueuePosition> packet() {
        return AskQueuePosition.class;
    }
}
