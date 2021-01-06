package fr.quatrevieux.araknemu.game.handler.basic;

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.basic.SetEmoteRequest;

/**
 * Send the player emote to other players
 */
final public class ChangeEmote implements PacketHandler<GameSession, SetEmoteRequest>{
    @Override
    public void handle(GameSession session, SetEmoteRequest packet) throws Exception {
        session.changeEmote(packet.emote());

    }

    @Override
    public Class<SetEmoteRequest> packet() {
        return SetEmoteRequest.class;
    }
}
