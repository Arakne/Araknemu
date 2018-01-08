package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.MapReady;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

import java.util.Collections;

/**
 * Load map extra info for join the map
 */
final public class LoadExtraInfo implements PacketHandler<GameSession, AskExtraInfo> {
    @Override
    public void handle(GameSession session, AskExtraInfo packet) throws Exception {
        if (session.exploration().map() == null) {
            throw new CloseImmediately("A map should be loaded before get extra info");
        }

        session.write(
            new AddSprites(
                session.exploration().map().sprites()
            )
        );
        session.write(new MapReady());
    }

    @Override
    public Class<AskExtraInfo> packet() {
        return AskExtraInfo.class;
    }
}
