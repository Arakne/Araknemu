package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.TurnEnd;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * End the turn of the current fighter
 */
final public class EndFighterTurn implements PacketHandler<GameSession, TurnEnd> {
    @Override
    public void handle(GameSession session, TurnEnd packet) {
        session.fighter().turn().stop();
    }

    @Override
    public Class<TurnEnd> packet() {
        return TurnEnd.class;
    }
}
