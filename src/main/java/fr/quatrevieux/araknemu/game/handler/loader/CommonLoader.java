package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.game.account.generator.NameGenerator;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.factory.ActionFactory;
import fr.quatrevieux.araknemu.game.handler.*;
import fr.quatrevieux.araknemu.game.handler.account.GenerateName;
import fr.quatrevieux.araknemu.game.handler.account.Login;
import fr.quatrevieux.araknemu.game.handler.account.SendRegionalVersion;
import fr.quatrevieux.araknemu.game.handler.basic.SendDateAndTime;
import fr.quatrevieux.araknemu.game.handler.fight.TerminateTurnAction;
import fr.quatrevieux.araknemu.game.handler.fight.PerformTurnAction;
import fr.quatrevieux.araknemu.game.handler.game.EndGameAction;
import fr.quatrevieux.araknemu.game.handler.game.ValidateGameAction;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Loader for common packets
 */
final public class CommonLoader implements Loader {
    @Override
    public PacketHandler<GameSession, ?>[] load(Container container) throws ContainerException {
        return new PacketHandler[] {
            new StartSession(),
            new StopSession(),
            new CheckQueuePosition(),
            new Login(
                container.get(TokenService.class),
                container.get(AccountService.class)
            ),
            new SendRegionalVersion(),
            new PongResponse(),
            new SendDateAndTime(),
            new SendPong(),
            new GenerateName(container.get(NameGenerator.class)),
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
