package fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator;

import java.util.Optional;

/**
 * Error during validating a path
 */
public class PathValidationException extends Exception {
    final private Object errorPacket;

    public PathValidationException(Object errorPacket) {
        super();

        this.errorPacket = errorPacket;
    }

    /**
     * Get the error to send
     */
    public Optional<Object> errorPacket() {
        return Optional.ofNullable(errorPacket);
    }
}
