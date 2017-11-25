package fr.quatrevieux.araknemu.network.realm.out;

/**
 * Error during selecting server
 */
final public class SelectServerError {
    public enum Error {
        FULL('F'),
        CANT_SELECT('r')
        ;

        final private char c;

        Error(char c) {
            this.c = c;
        }
    }

    final private Error error;

    public SelectServerError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "AXE" + error.c;
    }
}
