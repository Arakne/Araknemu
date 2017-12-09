package fr.quatrevieux.araknemu.game.account.exception;

/**
 * Throw when error occurs during character creation
 */
public class CharacterCreationException extends Exception {
    /**
     * List of error codes
     * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L580
     */
    public enum Error {
        SUBSCRIPTION_OUT("s"),
        CREATE_CHARACTER_FULL("f"),
        NAME_ALEREADY_EXISTS("a"),
        CREATE_CHARACTER_BAD_NAME("n"),
        CREATE_CHARACTER_ERROR;

        final private String code;

        Error(String code) {
            this.code = code;
        }

        Error() {
            this("");
        }

        public String code() {
            return code;
        }
    }

    final private Error error;

    public CharacterCreationException(Error error) {
        this.error = error;
    }

    public CharacterCreationException(Throwable cause) {
        super(cause);
        error = Error.CREATE_CHARACTER_ERROR;
    }

    public Error error() {
        return error;
    }
}
