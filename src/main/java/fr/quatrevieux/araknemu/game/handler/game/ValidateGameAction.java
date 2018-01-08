package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.exploration.action.ActionType;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Validate and start a game action
 */
final public class ValidateGameAction implements PacketHandler<GameSession, GameActionRequest> {
    final private ExplorationService service;

    public ValidateGameAction(ExplorationService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, GameActionRequest packet) throws Exception {
        try {
            service.action(session.exploration(), packet);
        } catch (Exception e) {
            throw new ErrorPacket(
                new GameActionResponse(0, ActionType.NONE, 0)
            );
        }
    }

    @Override
    public Class<GameActionRequest> packet() {
        return GameActionRequest.class;
    }
}
