package fr.quatrevieux.araknemu.game.listener.admin;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveAdminSessionTest extends GameBaseCase {
    private RemoveAdminSession listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new RemoveAdminSession(
            container.get(AdminService.class).user(gamePlayer())
        );
    }

    @Test
    void onDisconnect() {
        listener.on(new Disconnected());
    }
}
