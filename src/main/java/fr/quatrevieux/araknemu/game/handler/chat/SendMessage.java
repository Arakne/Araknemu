package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.chat.SendMessageError;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Send a message
 */
final public class SendMessage implements PacketHandler<GameSession, Message> {
    final private ChatService service;

    public SendMessage(ChatService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, Message packet) throws Exception {
        try {
            service.send(
                session.player(),
                packet
            );
        } catch (ChatException e) {
            switch (e.error()) {
                case UNAUTHORIZED:
                    throw new ErrorPacket(Error.cantDoOnServer());
                default:
                    throw new ErrorPacket(new SendMessageError(e.error(), packet.target()));
            }
        }
    }

    @Override
    public Class<Message> packet() {
        return Message.class;
    }
}
