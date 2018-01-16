package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChatException;

/**
 * Cannot send the message
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Chat.as#L227
 */
final public class SendMessageError {
    final private ChatException.Error error;
    final private String target;

    public SendMessageError(ChatException.Error error, String target) {
        this.error = error;
        this.target = target;
    }

    @Override
    public String toString() {
        return "cME" + error.id() + target;
    }
}
