package fr.quatrevieux.araknemu.game.handler.dialog;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.dialog.ChosenResponse;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Perform actions for the given dialog response
 */
final public class PerformResponseAction implements PacketHandler<GameSession, ChosenResponse> {
    @Override
    public void handle(GameSession session, ChosenResponse packet) {
        final ExplorationPlayer player = session.exploration();

        player.interactions().get(NpcDialog.class)
            .forQuestion(packet.question())
            .answer(packet.response())
        ;
    }

    @Override
    public Class<ChosenResponse> packet() {
        return ChosenResponse.class;
    }
}
