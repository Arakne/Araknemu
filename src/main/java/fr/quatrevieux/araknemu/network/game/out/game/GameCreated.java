package fr.quatrevieux.araknemu.network.game.out.game;

import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;

/**
 * The game session is successfully created
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L85
 */
final public class GameCreated {
    final private CreateGameRequest.Type type;

    public GameCreated(CreateGameRequest.Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "GCK|" + type.ordinal();
    }
}
