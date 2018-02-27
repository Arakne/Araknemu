package fr.quatrevieux.araknemu.network.game.out.object;

/**
 * Send to client an error during adding / moving an item
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Items.as#L109
 */
final public class AddItemError {
    public enum Error {
        INVENTORY_FULL('F'),
        TOO_LOW_LEVEL('L'),
        ALREADY_EQUIPED('A');

        final private char c;

        Error(char c) {
            this.c = c;
        }
    }

    final private Error error;

    public AddItemError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "OAE" + error.c;
    }
}
