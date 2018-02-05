package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.handler.EnsureAdmin;
import fr.quatrevieux.araknemu.game.handler.basic.admin.ExecuteCommand;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Loader for admin packets
 */
final public class AdminLoader extends AbstractLoader {
    public AdminLoader() {
        super(EnsureAdmin::new);
    }

    @Override
    protected PacketHandler<GameSession, ?>[] handlers(Container container) throws ContainerException {
        return new PacketHandler[] {
            new ExecuteCommand(
                container.get(AdminService.class)
            )
        };
    }
}
