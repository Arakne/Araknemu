package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FireworkEffectTest extends GameBaseCase {
    private FireworkEffect effect;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        gamePlayer(true);
        player = explorationPlayer();

        effect = new FireworkEffect();

        requestStack.clear();
    }

    @Test
    void check() {
        assertFalse(effect.check(new UseEffect(effect, Effect.FIREWORK , new int[] {2900, 0, 6}), player));
        assertTrue(effect.checkTarget(new UseEffect(effect, Effect.FIREWORK , new int[] {2900, 0, 6}), player, null, 150));
    }

    @Test
    void applyTarget() {
        effect.applyToTarget(new UseEffect(effect, Effect.FIREWORK , new int[] {6, 0, 2900}), player, null, 150);

        requestStack.assertLast(
            new GameActionResponse(1, ActionType.FIREWORK, player.id(), "150,2900,11,8,6")
        );

        assertTrue(player.interactions().busy());
    }
}