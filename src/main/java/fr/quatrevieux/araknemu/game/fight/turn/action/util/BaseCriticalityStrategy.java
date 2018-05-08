package fr.quatrevieux.araknemu.game.fight.turn.action.util;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Random;

/**
 * Base algorithm for compute criticality
 */
final public class BaseCriticalityStrategy implements CriticalityStrategy {
    final static private Random RANDOM = new Random();

    final private Fighter fighter;

    public BaseCriticalityStrategy(Fighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public int hitRate(int base) {
        if (base <= 2) {
            return base;
        }

        base -= fighter.characteristics().get(Characteristic.CRITICAL_BONUS);
        int agility = fighter.characteristics().get(Characteristic.AGILITY);

        if (agility < 0) {
            agility = 0;
        }

        int rate = Math.min((int) ((base * 2.9901) / Math.log(agility + 12)), base);

        return Math.max(rate, 2);
    }

    @Override
    public boolean hit(int baseRate) {
        if (baseRate < 2) { // No criticality
            return false;
        }

        return RANDOM.nextInt(hitRate(baseRate)) == 0;
    }

    @Override
    public int failureRate(int base) {
        return Math.max(base - fighter.characteristics().get(Characteristic.FAIL_MALUS), 2);
    }

    @Override
    public boolean failed(int baseRate) {
        return baseRate > 0 && RANDOM.nextInt(failureRate(baseRate)) == 0;
    }
}
