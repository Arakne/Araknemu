package fr.quatrevieux.araknemu.game.handler.dialog;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.dialog.CreateDialogRequest;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Start a new dialog with NPC
 */
final public class StartDialog implements PacketHandler<GameSession, CreateDialogRequest> {
    @Override
    public void handle(GameSession session, CreateDialogRequest packet) {
        final ExplorationPlayer exploration = session.exploration();

        exploration.map().creature(packet.npcId()).apply(new Operation() {
            @Override
            public void onExplorationPlayer(ExplorationPlayer player) {
                throw new IllegalArgumentException("Cannot start a dialog with a player");
            }

            @Override
            public void onNpc(GameNpc npc) {
                exploration.interactions().start(new NpcDialog(exploration, npc));
            }
        });
    }

    @Override
    public Class<CreateDialogRequest> packet() {
        return CreateDialogRequest.class;
    }
}
