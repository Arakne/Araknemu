package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterReady;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Change the ready state of the fighter
 */
final public class ChangeFighterReadyState implements PacketHandler<GameSession, FighterReady> {
    @Override
    public void handle(GameSession session, FighterReady packet) {
        session.fighter().setReady(packet.ready());
    }

    @Override
    public Class<FighterReady> packet() {
        return FighterReady.class;
    }
}
