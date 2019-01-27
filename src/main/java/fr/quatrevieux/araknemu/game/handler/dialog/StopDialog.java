package fr.quatrevieux.araknemu.game.handler.dialog;

import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.dialog.LeaveDialogRequest;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Stop the current NPC dialog
 */
final public class StopDialog implements PacketHandler<GameSession, LeaveDialogRequest> {
    @Override
    public void handle(GameSession session, LeaveDialogRequest packet) {
        session.exploration().interactions().get(NpcDialog.class).stop();
    }

    @Override
    public Class<LeaveDialogRequest> packet() {
        return LeaveDialogRequest.class;
    }
}
