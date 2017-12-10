package fr.quatrevieux.araknemu.game.account.exception;

import fr.quatrevieux.araknemu.data.living.constraint.player.PlayerConstraints;

/**
 * Throw when error occurs during character creation
 */
public class CharacterCreationException extends Exception {

    final private PlayerConstraints.Error error;

    public CharacterCreationException(PlayerConstraints.Error error) {
        this.error = error;
    }

    public CharacterCreationException(Throwable cause) {
        super(cause);
        error = PlayerConstraints.Error.CREATE_CHARACTER_ERROR;
    }

    public PlayerConstraints.Error error() {
        return error;
    }
}
