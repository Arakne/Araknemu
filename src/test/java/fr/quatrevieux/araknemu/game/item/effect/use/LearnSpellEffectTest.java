package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LearnSpellEffectTest extends GameBaseCase {
    private LearnSpellEffect effect;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSpells();

        effect = new LearnSpellEffect(
            container.get(SpellService.class)
        );

        gamePlayer(true);
        requestStack.clear();
    }

    @Test
    void checkAlreadyLearned() throws SQLException, ContainerException {
        assertFalse(
            effect.check(
                new UseEffect(null, Effect.LEARN_SPELL, new int[] {0, 0, 3}),
                explorationPlayer()
            )
        );

        requestStack.assertLast(
            Error.cantLearnSpell(3)
        );
    }

    @Test
    void checkTooHighLevelSpell() throws SQLException, ContainerException {
        dataSet.pushHighLevelSpells();

        assertFalse(
            effect.check(
                new UseEffect(null, Effect.LEARN_SPELL, new int[] {0, 0, 1908}),
                explorationPlayer()
            )
        );

        requestStack.assertLast(
            Error.cantLearnSpell(1908)
        );
    }

    @Test
    void checkOk() throws SQLException, ContainerException {
        assertTrue(
            effect.check(
                new UseEffect(null, Effect.LEARN_SPELL, new int[] {0, 0, 202}),
                explorationPlayer()
            )
        );
    }

    @Test
    void apply() throws SQLException, ContainerException {
        effect.apply(
            new UseEffect(null, Effect.LEARN_SPELL, new int[] {0, 0, 202}),
            explorationPlayer()
        );

        assertTrue(gamePlayer().spells().has(202));
        assertEquals(1, gamePlayer().spells().entry(202).spell().level());
        assertEquals(63, gamePlayer().spells().entry(202).position());
        assertFalse(gamePlayer().spells().entry(202).classSpell());
    }
}
