package fr.quatrevieux.araknemu.network.game.out.exchange;

/**
 * Cannot request the exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L236
 */
final public class ExchangeRequestError {
    static public enum Error {
        ALREADY_EXCHANGE('O'),
        NOT_NEAR_CRAFT_TABLE('T'),
        TOOL_NOT_EQUIPPED('J'),
        OVERWEIGHT('o'),
        NOT_SUBSCRIBED('S'),
        CANT_EXCHANGE('I'),
        ;

        final private char c;

        Error(char c) {
            this.c = c;
        }
    }

    final private Error error;

    public ExchangeRequestError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ERE" + error.c;
    }
}
