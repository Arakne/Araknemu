package fr.quatrevieux.araknemu.network.game.out.account;

/**
 * Error during generating name
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L1205
 */
final public class RandomNameGenerationError {
    public enum Error {
        UNDEFINED,
        CANNOT_GENERATE_PASSWORD
    }

    final private Error error;

    public RandomNameGenerationError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "APE" + (error.ordinal() + 1);
    }
}
