package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.event.exploration.StartExploration;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreated;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreationError;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Create the game session
 */
final public class CreateGame implements PacketHandler<GameSession, CreateGameRequest> {
    final private ExplorationService service;

    public CreateGame(ExplorationService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, CreateGameRequest packet) throws Exception {
        if (packet.type() != CreateGameRequest.Type.EXPLORATION) {
            throw new ErrorPacket(new GameCreationError());
        }

        ExplorationPlayer exploration = service.create(session.player());

        session.setExploration(exploration);

        session.write(new GameCreated(CreateGameRequest.Type.EXPLORATION));
        session.dispatch(new StartExploration(exploration));
    }

    @Override
    public Class<CreateGameRequest> packet() {
        return CreateGameRequest.class;
    }
}
