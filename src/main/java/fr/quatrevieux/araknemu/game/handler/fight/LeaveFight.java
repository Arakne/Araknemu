package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.fight.state.LeavableState;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.fight.LeaveFightRequest;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Leave the fight
 */
final public class LeaveFight implements PacketHandler<GameSession, LeaveFightRequest> {
    @Override
    public void handle(GameSession session, LeaveFightRequest packet) {
        session.fighter().fight().state(LeavableState.class).leave(session.fighter());
    }

    @Override
    public Class<LeaveFightRequest> packet() {
        return LeaveFightRequest.class;
    }
}
