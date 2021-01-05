package fr.quatrevieux.araknemu.game.handler.basic;

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.basic.PlayerEmote;

/**
 * Send the player emote to other players
 */
final public class SetEmote implements PacketHandler<GameSession, PlayerEmote>{
    @Override
    public void handle(GameSession session, PlayerEmote packet) throws Exception {
        session.setEmote(packet.emote());

    }

    @Override
    public Class<PlayerEmote> packet() {
        return PlayerEmote.class;
    }
}
