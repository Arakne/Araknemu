package fr.quatrevieux.araknemu.network.game.out.basic;

/**
 * Do nothing packet (no-operation)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/DataProcessor.as#L84
 */
final public class Noop {
    @Override
    public String toString() {
        return "BN";
    }
}
