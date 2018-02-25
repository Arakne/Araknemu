package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddLifeEffectTest extends GameBaseCase {
    private ExplorationPlayer player;
    private AddLifeEffect effect;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        gamePlayer(true);
        player = explorationPlayer();

        effect = new AddLifeEffect();

        requestStack.clear();
    }

    @Test
    void applyFixed() {
        player.life().set(100);
        requestStack.clear();

        effect.apply(new UseEffect(effect, Effect.ADD_LIFE, new int[] {10, 0, 0}), player);

        assertEquals(110, player.life().current());
        requestStack.assertAll(
            new Stats(player),
            Information.heal(10)
        );
    }

    @Test
    void applyRandom() {
        player.life().set(100);
        requestStack.clear();

        effect.apply(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player);

        assertBetween(101, 110, player.life().current());
        requestStack.assertAll(
            new Stats(player),
            Information.heal(player.life().current() - 100)
        );
    }

    @Test
    void applyToTarget() throws Exception {
        ExplorationPlayer target = new ExplorationPlayer(makeOtherPlayer());
        target.life().set(10);

        effect.applyToTarget(new UseEffect(effect, Effect.ADD_LIFE, new int[] {10, 0, 0}), player, target, 0);

        assertEquals(20, target.life().current());
    }

    @Test
    void checkFullLife() {
        assertFalse(effect.check(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player));
    }

    @Test
    void checkOk() {
        player.life().set(10);

        assertTrue(effect.check(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player));
    }

    @Test
    void checkTargetFullLife() throws Exception {
        ExplorationPlayer target = new ExplorationPlayer(makeOtherPlayer());

        assertFalse(effect.checkTarget(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player, target, 0));
    }

    @Test
    void checkTargetNotPlayer() {
        assertFalse(effect.checkTarget(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player, null, 0));
    }

    @Test
    void checkTargetOk() throws Exception {
        ExplorationPlayer target = new ExplorationPlayer(makeOtherPlayer());
        target.life().set(10);

        assertTrue(effect.checkTarget(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player, target, 0));
    }
}
