package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.game.event.listener.player.inventory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddInventoryListenersTest extends GameBaseCase {
    private AddInventoryListeners listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new AddInventoryListeners(
            container.get(PlayerItemRepository.class)
        );
    }

    @Test
    void onPlayerLoaded() throws SQLException, ContainerException {
        listener.on(
            new PlayerLoaded(gamePlayer())
        );

        assertTrue(gamePlayer().dispatcher().has(SendItemData.class));
        assertTrue(gamePlayer().dispatcher().has(SendItemPosition.class));
        assertTrue(gamePlayer().dispatcher().has(SendItemQuantity.class));
        assertTrue(gamePlayer().dispatcher().has(SaveNewItem.class));
        assertTrue(gamePlayer().dispatcher().has(SaveItemPosition.class));
        assertTrue(gamePlayer().dispatcher().has(SaveItemQuantity.class));
    }
}
