package fr.quatrevieux.araknemu.game.handler.emote;

import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.emote.SetOrientationRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Change the exploration player orientation for role player
 *
 * @see SetOrientationRequest
 */
final public class ChangeOrientation implements PacketHandler<GameSession, SetOrientationRequest> {
    @Override
    public void handle(GameSession session, SetOrientationRequest packet) throws Exception {
        if (!session.player().restrictions().canMoveAllDirections() && !packet.orientation().restricted()) {
            throw new ErrorPacket(new Noop());
        }

        session.exploration().setOrientation(packet.orientation());
    }

    @Override
    public Class<SetOrientationRequest> packet() {
        return SetOrientationRequest.class;
    }
}
