package fr.quatrevieux.araknemu.game.handler.dialog;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.dialog.LeaveDialogRequest;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StopDialogTest extends GameBaseCase {
    private ExplorationPlayer player;

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

        requestStack.clear();
    }

    @Test
    void functionalSuccess() throws Exception {
        handlePacket(new LeaveDialogRequest());

        assertFalse(player.interactions().busy());
        requestStack.assertLast(new DialogLeaved());
    }

    @Test
    void functionalNotExploring() {
        session.setExploration(null);

        assertThrows(CloseImmediately.class, () -> handlePacket(new LeaveDialogRequest()));
    }
}
