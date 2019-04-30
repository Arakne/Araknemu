package fr.quatrevieux.araknemu.game.fight.module;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.factory.AiFactory;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AiModuleTest extends FightBaseCase {
    @Test
    void register() throws Exception {
        Fight fight = createPvmFight();
        fight.register(new AiModule(fight, container.get(AiFactory.class)));

        assertInstanceOf(Simulator.class, fight.attachment(Simulator.class));
    }

    @Test
    void fighterInitialized() throws Exception {
        Fight fight = createPvmFight();

        fight.register(new AiModule(fight, container.get(AiFactory.class)));
        fight.nextState();

        for (Fighter monster : fight.team(1).fighters()) {
            assertInstanceOf(AI.class, monster.attachment(AI.class));
        }

        assertNull(player.fighter().attachment(AI.class));
    }

    @Test
    void turnStartedWithoutAi() throws Exception {
        Fight fight = createPvmFight();
        fight.register(new AiModule(fight, container.get(AiFactory.class)));
        fight.nextState();
        fight.turnList().start();

        FightTurn turn = fight.turnList().current().get();

        assertSame(player.fighter(), turn.fighter());
        assertTrue(turn.active());

        // Check if there is no pending action
        Runnable action = Mockito.mock(Runnable.class);
        turn.later(action);

        Mockito.verify(action).run();
    }

    @Test
    void turnStartedWithAi() throws Exception {
        Fight fight = createPvmFight();
        fight.register(new AiModule(fight, container.get(AiFactory.class)));
        fight.nextState();
        fight.turnList().start();
        fight.turnList().current().get().stop();

        requestStack.clear();

        FightTurn turn = fight.turnList().current().get();

        assertTrue(turn.active());
        Thread.sleep(100);

        // Move action started
        requestStack.assertOne("GA0;1;-2;ab-fbGdbU");
    }
}
