package fr.quatrevieux.araknemu.game.handler.basic.admin;

import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.basic.admin.AdminCommand;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Execute an admin command
 */
final public class ExecuteCommand implements PacketHandler<GameSession, AdminCommand> {
    final private AdminService service;

    public ExecuteCommand(AdminService service) {
        this.service = service;
    }

    @Override
    public void handle(GameSession session, AdminCommand packet) throws Exception {
        AdminUser user = service.user(session.player());

        try {
            user.execute(packet.command());
        } catch (AdminException e) {
            user.error(e);
        }
    }

    @Override
    public Class<AdminCommand> packet() {
        return AdminCommand.class;
    }
}
