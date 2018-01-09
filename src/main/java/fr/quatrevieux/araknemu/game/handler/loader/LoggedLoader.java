package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.handler.EnsureLogged;
import fr.quatrevieux.araknemu.game.handler.account.CreateCharacter;
import fr.quatrevieux.araknemu.game.handler.account.ListCharacters;
import fr.quatrevieux.araknemu.game.handler.account.SelectCharacter;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Loader for logged packets
 */
final public class LoggedLoader extends AbstractLoader {
    public LoggedLoader() {
        super(EnsureLogged::new);
    }

    @Override
    protected PacketHandler<GameSession, ?>[] handlers(Container container) throws ContainerException {
        return new PacketHandler[] {
            new ListCharacters(
                container.get(CharactersService.class)
            ),
            new CreateCharacter(
                container.get(CharactersService.class)
            ),
            new SelectCharacter(
                container.get(PlayerService.class)
            )
        };
    }
}
