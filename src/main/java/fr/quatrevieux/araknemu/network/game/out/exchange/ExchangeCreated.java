package fr.quatrevieux.araknemu.network.game.out.exchange;

import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * The exchange is created
 *
 * Note: Do not supports shops or mount park
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L321
 */
final public class ExchangeCreated {
    final private ExchangeType type;
    final private Creature target;

    public ExchangeCreated(ExchangeType type) {
        this(type, null);
    }

    /**
     * @param type The exchange type
     * @param target The exchange target. May be null
     */
    public ExchangeCreated(ExchangeType type, Creature target) {
        this.type = type;
        this.target = target;
    }

    @Override
    public String toString() {
        return "ECK" + type.ordinal() + (target != null ? "|" + target.id() : "");
    }
}
