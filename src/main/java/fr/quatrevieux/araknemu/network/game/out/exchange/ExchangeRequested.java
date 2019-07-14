package fr.quatrevieux.araknemu.network.game.out.exchange;

import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * Send the exchange request to the parties
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L160
 */
final public class ExchangeRequested {
    final private Creature initiator;
    final private Creature target;
    final private ExchangeType type;

    public ExchangeRequested(Creature initiator, Creature target, ExchangeType type) {
        this.initiator = initiator;
        this.target = target;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ERK" + initiator.id() + "|" + target.id() + "|" + type.ordinal();
    }
}
