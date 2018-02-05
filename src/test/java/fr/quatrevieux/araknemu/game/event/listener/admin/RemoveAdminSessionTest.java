package fr.quatrevieux.araknemu.game.event.listener.admin;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.event.common.Disconnected;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
