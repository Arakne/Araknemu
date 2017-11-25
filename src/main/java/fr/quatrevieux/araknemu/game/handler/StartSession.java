package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.out.HelloGame;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.in.SessionCreated;

/**
 * Handle session creation
 */
final public class StartSession implements PacketHandler<GameSession, SessionCreated> {
    @Override
    public void handle(GameSession session, SessionCreated packet) {
        session.write(
            new HelloGame()
        );
    }

    @Override
    public Class<SessionCreated> packet() {
        return SessionCreated.class;
    }
}
