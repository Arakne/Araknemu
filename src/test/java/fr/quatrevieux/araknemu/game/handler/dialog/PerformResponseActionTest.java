package fr.quatrevieux.araknemu.game.handler.dialog;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.network.game.in.dialog.ChosenResponse;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogLeaved;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class PerformResponseActionTest extends GameBaseCase {
    private ExplorationPlayer player;
    private PerformResponseAction handler;

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

        handler = new PerformResponseAction();

        requestStack.clear();
    }

    @Test
    void handleWithNext() throws ContainerException {
        handler.handle(session, new ChosenResponse(3786, 3323));

        NpcQuestion question = container.get(DialogService.class).byIds(new int[] {3787}).stream().findFirst().get();
        requestStack.assertAll(new DialogQuestion(
            question,
            question.responses(player),
            player
        ));

        player.interactions().get(NpcDialog.class).forQuestion(3787);
    }

    @Test
    void handleWithLeave() {
        handler.handle(session, new ChosenResponse(3786, 3324));

        requestStack.assertLast(new DialogLeaved());
        assertFalse(player.interactions().interacting());
    }

    @Test
    void handleBadQuestion() {
        assertThrows(IllegalArgumentException.class, () -> handler.handle(session, new ChosenResponse(404, 3324)));
    }

    @Test
    void handleBadResponse() {
        assertThrows(NoSuchElementException.class, () -> handler.handle(session, new ChosenResponse(3786, 404)));
    }
}
