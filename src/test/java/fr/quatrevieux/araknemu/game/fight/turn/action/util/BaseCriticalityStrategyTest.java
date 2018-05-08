package fr.quatrevieux.araknemu.game.fight.turn.action.util;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseCriticalityStrategyTest extends FightBaseCase {
    private BaseCriticalityStrategy strategy;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        createFight();

        strategy = new BaseCriticalityStrategy(player.fighter());
    }

    @Test
    void hitRateWithoutBonus() {
        assertEquals(25, strategy.hitRate(25));
    }

    @Test
    void hitRateWithFixedBonus() {
        player.characteristics().base().set(Characteristic.CRITICAL_BONUS, 10);

        assertEquals(15, strategy.hitRate(25));
    }

    @Test
    void hitRateWithAgility() {
        player.characteristics().base().set(Characteristic.AGILITY, 100);

        assertEquals(15, strategy.hitRate(25));
    }

    @Test
    void hitRate2() {
        assertEquals(2, strategy.hitRate(2));
    }

    @Test
    void hitRate0() {
        assertEquals(0, strategy.hitRate(0));
    }

    @Test
    void failureRate() {
        assertEquals(100, strategy.failureRate(100));
    }

    @Test
    void failureRateWithMalus() {
        player.characteristics().base().set(Characteristic.FAIL_MALUS, 10);

        assertEquals(90, strategy.failureRate(100));
    }
}
