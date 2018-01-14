package fr.quatrevieux.araknemu.game.chat;

/**
 * Exception when sending message to chat
 */
public class ChatException extends Exception {
    public enum Error {
        DEFAULT(""),
        UNAUTHORIZED(""),
        SYNTAX_ERROR("S"),
        USER_NOT_CONNECTED("f"),
        USER_NOT_CONNECTED_BUT_TRY_SEND_EXTERNAL("e"),
        USER_NOT_CONNECTED_EXTERNAL_NACK("n");

        final private String id;

        Error(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

    final private Error error;

    public ChatException(Error error) {
        this.error = error;
    }

    public Error error() {
        return error;
    }
}
