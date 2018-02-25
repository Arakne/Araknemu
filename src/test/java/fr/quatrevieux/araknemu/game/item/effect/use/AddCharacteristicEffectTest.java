package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddCharacteristicEffectTest extends GameBaseCase {
    private ExplorationPlayer player;
    private AddCharacteristicEffect effect;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        gamePlayer(true);
        player = explorationPlayer();

        effect = new AddCharacteristicEffect(Characteristic.WISDOM);
        requestStack.clear();
    }

    @Test
    void applyFixedValue() {
        effect.apply(new UseEffect(null, Effect.ADD_CHARACT_WISDOM, new int[] {2, 0, 0}), player);

        assertEquals(2, player.characteristics().base().get(Characteristic.WISDOM));
        requestStack.assertLast(
            Information.characteristicBoosted(Characteristic.WISDOM, 2)
        );
    }

    @Test
    void applyRandomValue() {
        effect.apply(new UseEffect(null, Effect.ADD_CHARACT_WISDOM, new int[] {1, 10, 0}), player);

        int value = player.characteristics().base().get(Characteristic.WISDOM);
        assertBetween(1, 10, value);

        requestStack.assertAll(
            new Stats(player),
            Information.characteristicBoosted(Characteristic.WISDOM, value)
        );
    }

    @Test
    void applyNoMessage() {
        effect = new AddCharacteristicEffect(Characteristic.RESISTANCE_ACTION_POINT);
        effect.apply(new UseEffect(null, Effect.ADD_CHARACT_WISDOM, new int[] {10, 0, 0}), player);

        assertEquals(10, player.characteristics().base().get(Characteristic.RESISTANCE_ACTION_POINT));
        requestStack.assertAll(
            new Stats(player)
        );
    }

    @Test
    void check() {
        assertTrue(effect.check(new UseEffect(null, Effect.ADD_CHARACT_WISDOM, new int[] {2, 0, 0}), player));
        assertFalse(effect.checkTarget(new UseEffect(null, Effect.ADD_CHARACT_WISDOM, new int[] {2, 0, 0}), player, null, 0));
    }
}
