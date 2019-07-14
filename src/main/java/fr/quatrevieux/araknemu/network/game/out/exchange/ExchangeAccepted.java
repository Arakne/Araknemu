package fr.quatrevieux.araknemu.network.game.out.exchange;

import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * Change the accepted state of the exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L277
 */
final public class ExchangeAccepted {
    final private boolean accepted;
    final private Creature who;

    public ExchangeAccepted(boolean accepted, Creature who) {
        this.accepted = accepted;
        this.who = who;
    }

    @Override
    public String toString() {
        return "EK" + (accepted ? "1" : "0") + who.id();
    }
}
