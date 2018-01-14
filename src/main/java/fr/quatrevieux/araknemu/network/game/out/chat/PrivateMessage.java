package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Send private message to client
 *
 * @todo Escape message
 */
final public class PrivateMessage {
    final static public char TYPE_FROM = 'F';
    final static public char TYPE_TO   = 'T';

    final private char type;
    final private GamePlayer target;
    final private String message;
    final private String extra;

    public PrivateMessage(char type, GamePlayer target, String message, String extra) {
        this.type = type;
        this.target = target;
        this.message = message;
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "cMK" + type + "|" + target.id() + "|" + target.name() + "|" + message + "|" + extra;
    }
}
