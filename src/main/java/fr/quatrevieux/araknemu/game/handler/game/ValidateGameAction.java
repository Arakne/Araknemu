package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.factory.ActionFactory;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Validate and start a game action
 */
final public class ValidateGameAction implements PacketHandler<GameSession, GameActionRequest> {
    final private ActionFactory factory;

    public ValidateGameAction(ActionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void handle(GameSession session, GameActionRequest packet) throws Exception {
        try {
            session.exploration().interactions().push(
                factory.create(
                    session.exploration(),
                    ActionType.byId(packet.type()),
                    packet.arguments()
                )
            );
        } catch (Exception e) {
            throw new ErrorPacket(new GameActionResponse(0, ActionType.NONE, 0), e);
        }
    }

    @Override
    public Class<GameActionRequest> packet() {
        return GameActionRequest.class;
    }
}
