package fr.quatrevieux.araknemu.game.listener.admin;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.admin.AdminUser;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;

/**
 * Remove admin session on disconnect
 */
final public class RemoveAdminSession implements Listener<Disconnected> {
    final private AdminUser user;

    public RemoveAdminSession(AdminUser user) {
        this.user = user;
    }

    @Override
    public void on(Disconnected event) {
        user.logout();
    }

    @Override
    public Class<Disconnected> event() {
        return Disconnected.class;
    }
}
