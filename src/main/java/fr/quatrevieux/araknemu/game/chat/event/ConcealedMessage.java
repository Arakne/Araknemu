package fr.quatrevieux.araknemu.game.chat.event;

import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Event trigger when private message is sent
 */
final public class ConcealedMessage {
    final private GamePlayer sender;
    final private GamePlayer receiver;
    final private String message;
    final private String extra;

    public ConcealedMessage(GamePlayer sender, GamePlayer receiver, String message, String extra) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.extra = extra;
    }

    public GamePlayer sender() {
        return sender;
    }

    public GamePlayer receiver() {
        return receiver;
    }

    public String message() {
        return message;
    }

    public String extra() {
        return extra;
    }
}
