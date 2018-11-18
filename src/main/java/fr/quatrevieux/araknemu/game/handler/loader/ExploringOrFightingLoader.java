package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionFactory;
import fr.quatrevieux.araknemu.game.handler.EnsureFighting;
import fr.quatrevieux.araknemu.game.handler.ExploringOrFightingSwitcher;
import fr.quatrevieux.araknemu.game.handler.fight.PerformTurnAction;
import fr.quatrevieux.araknemu.game.handler.fight.TerminateTurnAction;
import fr.quatrevieux.araknemu.game.handler.game.EndGameAction;
import fr.quatrevieux.araknemu.game.handler.game.ValidateGameAction;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Loader for exploring or fighter switch packet handlers
 */
final public class ExploringOrFightingLoader implements Loader {
    @Override
    public PacketHandler<GameSession, ?>[] load(Container container) throws ContainerException {
        return new PacketHandler[] {
            new ExploringOrFightingSwitcher(
                new ValidateGameAction(container.get(ActionFactory.class)),
                new EnsureFighting(new PerformTurnAction())
            ),
            new ExploringOrFightingSwitcher(
                new EndGameAction(),
                new EnsureFighting(new TerminateTurnAction())
            )
        };
    }
}
