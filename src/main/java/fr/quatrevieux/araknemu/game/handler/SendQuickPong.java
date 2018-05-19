package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.Ping;
import fr.quatrevieux.araknemu.network.game.in.QuickPing;
import fr.quatrevieux.araknemu.network.game.out.Pong;
import fr.quatrevieux.araknemu.network.game.out.QuickPong;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Send pong for qping request
 */
final public class SendQuickPong implements PacketHandler<GameSession, QuickPing> {
    @Override
    public void handle(GameSession session, QuickPing packet) {
        session.write(new QuickPong());
    }

    @Override
    public Class<QuickPing> packet() {
        return QuickPing.class;
    }
}
