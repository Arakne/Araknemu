package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.dialog;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeaveDialogTest extends GameBaseCase {
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
    }

    @Test
    void factory() {
        LeaveDialog.Factory factory = new LeaveDialog.Factory();

        assertEquals("LEAVE", factory.type());
        assertInstanceOf(LeaveDialog.class, factory.create(new ResponseAction(1, "LEAVE", "")));
        assertSame(factory.create(new ResponseAction(1, "LEAVE", "")), factory.create(new ResponseAction(2, "LEAVE", "")));
    }

    @Test
    void check() {
        assertTrue(new LeaveDialog().check(player));
    }

    @Test
    void apply() {
        new LeaveDialog().apply(player);

        assertFalse(player.interactions().interacting());
        requestStack.assertLast(new DialogLeaved());
    }
}
