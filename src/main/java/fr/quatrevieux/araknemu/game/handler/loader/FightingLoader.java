package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.game.handler.EnsureFighting;
import fr.quatrevieux.araknemu.game.handler.fight.ChangeFighterReadyState;
import fr.quatrevieux.araknemu.game.handler.fight.ChangeFighterStartPlace;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Loader for fight packets
 */
final public class FightingLoader extends AbstractLoader {
    public FightingLoader() {
        super(EnsureFighting::new);
    }

    @Override
    public PacketHandler<GameSession, ?>[] handlers(Container container) {
        return new PacketHandler[] {
            new ChangeFighterStartPlace(),
            new ChangeFighterReadyState()
        };
    }
}
