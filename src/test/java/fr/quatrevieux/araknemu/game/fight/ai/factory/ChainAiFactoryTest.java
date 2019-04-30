package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class ChainAiFactoryTest extends FightBaseCase {
    private Fighter fighter;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        createFight();

        fighter = player.fighter();
    }

    @Test
    void createEmpty() {
        assertFalse(new ChainAiFactory().create(fighter).isPresent());
    }

    @Test
    void createNoneMatch() {
        assertFalse(new ChainAiFactory(fighter -> Optional.empty()).create(fighter).isPresent());
    }

    @Test
    void createSuccess() {
        final AI ai = new AI(fighter, new ActionGenerator[0]);

        assertSame(ai, new ChainAiFactory(
            fighter -> Optional.empty(),
            fighter -> Optional.of(ai)
        ).create(fighter).get());
    }
}
