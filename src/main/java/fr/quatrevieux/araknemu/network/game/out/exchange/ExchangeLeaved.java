package fr.quatrevieux.araknemu.network.game.out.exchange;

/**
 * The exchange is terminated (cancelled or accepted)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L284
 */
final public class ExchangeLeaved {
    final private boolean accepted;

    public ExchangeLeaved(boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public String toString() {
        return "EV" + (accepted ? "a" : "");
    }

    /**
     * The exchange is cancelled
     */
    static public ExchangeLeaved cancelled() {
        return new ExchangeLeaved(false);
    }

    /**
     * The exchange is accepted
     */
    static public ExchangeLeaved accepted() {
        return new ExchangeLeaved(true);
    }
}
