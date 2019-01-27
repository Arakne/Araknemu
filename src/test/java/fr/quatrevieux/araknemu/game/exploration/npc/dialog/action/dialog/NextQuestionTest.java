package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.dialog;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NextQuestionTest extends GameBaseCase {
    private ExplorationPlayer player;
    private NextQuestion.Factory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcs();

        player = explorationPlayer();

        player.interactions().start(
            new NpcDialog(
                player,
                container.get(NpcService.class).get(472)
            )
        );

        factory = new NextQuestion.Factory(container.get(DialogService.class));
    }

    @Test
    void factory() {
        assertEquals("NEXT", factory.type());
        assertInstanceOf(NextQuestion.class, factory.create(new ResponseAction(1, "NEXT", "123")));
    }

    @Test
    void checkNoQuestions() {
        assertFalse(factory.create(new ResponseAction(1, "NEXT", "")).check(player));
    }

    @Test
    void checkSuccess() {
        assertTrue(factory.create(new ResponseAction(1, "NEXT", "3786")).check(player));
    }

    @Test
    void apply() throws ContainerException {
        factory.create(new ResponseAction(1, "NEXT", "3786")).apply(player);

        player.interactions().get(NpcDialog.class).forQuestion(3786);

        NpcQuestion question = container.get(DialogService.class).byIds(new int[] {3786}).stream().findFirst().get();
        requestStack.assertLast(new DialogQuestion(
            question,
            question.responses(player),
            player
        ));
    }
}
